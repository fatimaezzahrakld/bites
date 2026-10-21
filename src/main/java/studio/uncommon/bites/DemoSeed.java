package studio.uncommon.bites;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
@Configuration class DemoSeed {
 @Bean CommandLineRunner seed(UserRepo users,MenuRepo menu,PasswordEncoder encoder,@Value("${DEMO_SEED:false}") boolean enabled,@Value("${DEMO_PASSWORD:}") String password){return args->{
  if(!enabled)return;
  if(password.length()<10)throw new IllegalStateException("DEMO_PASSWORD must contain at least 10 characters");
  AppUser restaurant=ensure(users,encoder,"Bites Kitchen","restaurant@bites.local",password,Role.RESTAURANT);
  ensure(users,encoder,"Alex Customer","customer@bites.local",password,Role.CUSTOMER);
  ensure(users,encoder,"Sam Driver","driver@bites.local",password,Role.DRIVER);
  ensure(users,encoder,"Bites Admin","admin@bites.local",password,Role.ADMIN);
  if(menu.count()==0){
   menu.save(new MenuItem(restaurant,"Smash Theory","Burgers","Double beef, melted cheddar, secret sauce","🍔","79.00"));
   menu.save(new MenuItem(restaurant,"Hot Honey Bird","Burgers","Crunchy chicken, hot honey, pickles","🍗","72.00"));
   menu.save(new MenuItem(restaurant,"Truffle Shuffle","Burgers","Umami mushrooms, truffle aioli","🍔","89.00"));
   menu.save(new MenuItem(restaurant,"Golden Hour","Sides","Crispy fries with parmesan and herbs","🍟","29.00"));
   menu.save(new MenuItem(restaurant,"Firecracker Wings","Sides","Sweet heat glazed chicken wings","🍗","49.00"));
   menu.save(new MenuItem(restaurant,"Green Goddess","Bowls","Fresh greens, grains, avocado, herbs","🥗","65.00"));
   menu.save(new MenuItem(restaurant,"Pink Lemonade","Drinks","Berries, lemon and sparkling water","🍋","25.00"));
   menu.save(new MenuItem(restaurant,"Midnight Brownie","Desserts","Rich chocolate, gooey center","🍫","39.00"));
  }
 };}
 private static AppUser ensure(UserRepo users,PasswordEncoder e,String name,String email,String pw,Role role){return users.findByEmailIgnoreCase(email).orElseGet(()->users.save(new AppUser(name,email,e.encode(pw),role)));}
}
