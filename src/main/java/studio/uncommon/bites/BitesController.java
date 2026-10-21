package studio.uncommon.bites;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.*;
import java.math.BigDecimal;
import java.util.*;

@RestController @RequestMapping("/api") class BitesController {
 private final MenuRepo menu;private final OrderRepo orders;private final UserRepo users;
 BitesController(MenuRepo m,OrderRepo o,UserRepo u){menu=m;orders=o;users=u;}
 private AppUser caller(Authentication auth){return (AppUser)auth.getPrincipal();}
 private void role(AppUser u,Role... allowed){if(Arrays.stream(allowed).noneMatch(r->r==u.role))throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Not allowed");}
 static MenuView menuView(MenuItem i){return new MenuView(i.id,i.restaurant.id,i.restaurant.name,i.name,i.category,i.description,i.emoji,i.price,i.available);}
 static OrderView orderView(CustomerOrder o){return new OrderView(o.id,o.customer.name,o.restaurant.name,o.driver==null?null:o.driver.name,o.restaurant.id,o.address,o.notes,o.status.name(),o.total,o.createdAt,o.lines.stream().map(l->new LineView(l.name,l.quantity,l.unitPrice)).toList());}
 @GetMapping("/menu") List<MenuView> publicMenu(){return menu.findByAvailableTrueOrderByIdAsc().stream().map(BitesController::menuView).toList();}
 @GetMapping("/restaurant/menu") List<MenuView> ownMenu(Authentication auth){AppUser u=caller(auth);role(u,Role.RESTAURANT);return menu.findByRestaurantIdOrderByIdAsc(u.id).stream().map(BitesController::menuView).toList();}
 @PostMapping("/restaurant/menu") @Transactional MenuView addMenu(Authentication auth,@Valid @RequestBody MenuInput input){AppUser u=caller(auth);role(u,Role.RESTAURANT);MenuItem i=new MenuItem(u,input.name().trim(),input.category().trim(),input.description().trim(),input.emoji().trim(),input.price().toString());i.available=input.available();return menuView(menu.save(i));}
 @PutMapping("/restaurant/menu/{id}") @Transactional MenuView editMenu(Authentication auth,@PathVariable Long id,@Valid @RequestBody MenuInput input){AppUser u=caller(auth);role(u,Role.RESTAURANT);MenuItem i=menu.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));if(!i.restaurant.id.equals(u.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN);i.name=input.name().trim();i.category=input.category().trim();i.description=input.description().trim();i.emoji=input.emoji().trim();i.price=input.price();i.available=input.available();return menuView(i);}
 @PostMapping("/orders") @Transactional ResponseEntity<OrderView> order(Authentication auth,@Valid @RequestBody OrderInput request){
  AppUser customer=caller(auth);role(customer,Role.CUSTOMER);
  Map<Long,Integer> quantities=new LinkedHashMap<>();for(LineInput line:request.items())quantities.merge(line.menuItemId(),line.quantity(),Integer::sum);
  if(quantities.values().stream().anyMatch(q->q>20))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Maximum 20 per item");
  List<MenuItem> products=new ArrayList<>();for(Long id:quantities.keySet()){MenuItem item=menu.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown item"));if(!item.available)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unavailable item");products.add(item);}
  Long restaurant=products.getFirst().restaurant.id;if(products.stream().anyMatch(p->!p.restaurant.id.equals(restaurant)))throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Choose products from one restaurant per order");
  CustomerOrder o=new CustomerOrder();o.customer=customer;o.restaurant=products.getFirst().restaurant;o.address=request.address().trim();o.notes=request.notes()==null?"":request.notes().trim();o.total=BigDecimal.ZERO;
  for(MenuItem p:products){OrderLine line=new OrderLine();line.order=o;line.menuItemId=p.id;line.name=p.name;line.quantity=quantities.get(p.id);line.unitPrice=p.price;o.lines.add(line);o.total=o.total.add(p.price.multiply(BigDecimal.valueOf(line.quantity)));}
  return ResponseEntity.status(HttpStatus.CREATED).body(orderView(orders.save(o)));
 }
 @GetMapping("/orders") List<OrderView> myOrders(Authentication auth){AppUser u=caller(auth);role(u,Role.CUSTOMER);return orders.findByCustomerIdOrderByCreatedAtDesc(u.id).stream().map(BitesController::orderView).toList();}
 @GetMapping("/restaurant/orders") List<OrderView> restaurantOrders(Authentication auth){AppUser u=caller(auth);role(u,Role.RESTAURANT);return orders.findByRestaurantIdOrderByCreatedAtDesc(u.id).stream().map(BitesController::orderView).toList();}
 @PostMapping("/restaurant/orders/{id}/status") @Transactional OrderView updateRestaurant(Authentication auth,@PathVariable Long id,@Valid @RequestBody StatusInput payload){
  AppUser u=caller(auth);role(u,Role.RESTAURANT);CustomerOrder o=fetch(id);if(!o.restaurant.id.equals(u.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  OrderStatus next=parse(payload.status());boolean ok=switch(o.status){case PLACED->next==OrderStatus.ACCEPTED||next==OrderStatus.CANCELLED;case ACCEPTED->next==OrderStatus.PREPARING||next==OrderStatus.CANCELLED;case PREPARING->next==OrderStatus.READY;default->false;};
  if(!ok)throw new ResponseStatusException(HttpStatus.CONFLICT,"Invalid status transition");o.status=next;return orderView(o);
 }
 @GetMapping("/driver/available") List<OrderView> available(Authentication auth){role(caller(auth),Role.DRIVER);return orders.findByStatusAndDriverIsNullOrderByCreatedAtAsc(OrderStatus.READY).stream().map(BitesController::orderView).toList();}
 @GetMapping("/driver/orders") List<OrderView> driverOrders(Authentication auth){AppUser u=caller(auth);role(u,Role.DRIVER);return orders.findByDriverIdOrderByCreatedAtDesc(u.id).stream().map(BitesController::orderView).toList();}
 @PostMapping("/driver/orders/{id}/claim") @Transactional OrderView claim(Authentication auth,@PathVariable Long id){AppUser u=caller(auth);role(u,Role.DRIVER);CustomerOrder o=fetch(id);if(o.status!=OrderStatus.READY||o.driver!=null)throw new ResponseStatusException(HttpStatus.CONFLICT,"Order already claimed or not ready");o.driver=u;return orderView(orders.saveAndFlush(o));}
 @PostMapping("/driver/orders/{id}/status") @Transactional OrderView driverStatus(Authentication auth,@PathVariable Long id,@Valid @RequestBody StatusInput input){AppUser u=caller(auth);role(u,Role.DRIVER);CustomerOrder o=fetch(id);if(o.driver==null||!o.driver.id.equals(u.id))throw new ResponseStatusException(HttpStatus.FORBIDDEN);
  OrderStatus next=parse(input.status());if(!((o.status==OrderStatus.READY&&next==OrderStatus.PICKED_UP)||(o.status==OrderStatus.PICKED_UP&&next==OrderStatus.DELIVERED)))throw new ResponseStatusException(HttpStatus.CONFLICT,"Invalid delivery transition");o.status=next;return orderView(o);
 }
 @GetMapping("/admin/users") List<Identity> allUsers(Authentication auth){role(caller(auth),Role.ADMIN);return users.findAll().stream().map(AuthController::view).toList();}
 @GetMapping("/admin/orders") List<OrderView> allOrders(Authentication auth){role(caller(auth),Role.ADMIN);return orders.findAllByOrderByCreatedAtDesc().stream().map(BitesController::orderView).toList();}
 @GetMapping("/admin/stats") StatsView stats(Authentication auth){role(caller(auth),Role.ADMIN);var all=orders.findAll();return new StatsView(users.countByRole(Role.CUSTOMER),users.countByRole(Role.RESTAURANT),users.countByRole(Role.DRIVER),all.size(),all.stream().filter(o->o.status==OrderStatus.DELIVERED).map(o->o.total).reduce(BigDecimal.ZERO,BigDecimal::add));}
 private CustomerOrder fetch(Long id){return orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found"));}
 private OrderStatus parse(String s){try{return OrderStatus.valueOf(s.toUpperCase(Locale.ROOT));}catch(IllegalArgumentException e){throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid status");}}
}
