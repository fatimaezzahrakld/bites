package studio.uncommon.bites;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.*;
@RestController
@RequestMapping("/api")
public class ApiController {
 private final MenuRepository menu;
 private final OrderRepository orders;
 public ApiController(MenuRepository menu,OrderRepository orders){this.menu=menu;this.orders=orders;}
 @GetMapping("/menu") public List<MenuItem> menu(){return menu.findAll();}
 public record LineRequest(@NotNull Long menuItemId,@Min(1) @Max(20) int quantity){}
 public record OrderRequest(@NotBlank @Size(max=80) String customerName,
  @NotBlank @Email String customerEmail,@NotEmpty List<@Valid LineRequest> items){}
 @PostMapping("/orders")
 public ResponseEntity<CustomerOrder> place(@Valid @RequestBody OrderRequest request){
  if(request.items().size()>30) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Too many items");
  CustomerOrder order=new CustomerOrder();
  order.customerName=request.customerName().trim();order.customerEmail=request.customerEmail().trim();
  BigDecimal total=BigDecimal.ZERO;
  for(LineRequest input:request.items()){
   MenuItem product=menu.findById(input.menuItemId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unknown item"));
   if(!product.available)throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Unavailable item");
   OrderLine line=new OrderLine();line.menuItemId=product.id;line.name=product.name;
   line.quantity=input.quantity();line.unitPrice=product.price;line.order=order;
   order.lines.add(line);total=total.add(product.price.multiply(BigDecimal.valueOf(input.quantity())));
  }
  order.total=total;
  return ResponseEntity.status(HttpStatus.CREATED).body(orders.save(order));
 }
 @GetMapping("/orders/{id}")
 public CustomerOrder get(@PathVariable Long id,@RequestParam String email){
  CustomerOrder order=orders.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
  if(!order.customerEmail.equalsIgnoreCase(email.trim()))throw new ResponseStatusException(HttpStatus.NOT_FOUND);
  return order;
 }
}
