package studio.uncommon.bites;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
@Configuration
public class SeedData {
 @Bean CommandLineRunner seed(MenuRepository repo){return args->{
  if(repo.count()>0)return;
  repo.save(new MenuItem("Smash Theory","Burgers","Double smashed beef, cheddar, secret sauce","🍔","79.00"));
  repo.save(new MenuItem("Hot Honey Bird","Burgers","Crispy chicken, spicy honey glaze","🍗","72.00"));
  repo.save(new MenuItem("Truffle Shuffle","Burgers","Beef patty, mushrooms, truffle aioli","🍔","89.00"));
  repo.save(new MenuItem("Golden Hour","Sides","Crispy seasoned fries, parmesan dust","🍟","29.00"));
  repo.save(new MenuItem("Firecracker Wings","Sides","Six wings tossed in signature hot sauce","🍗","49.00"));
  repo.save(new MenuItem("Green Goddess","Bowls","Avocado, greens, grains and herby dressing","🥗","65.00"));
  repo.save(new MenuItem("Pink Lemonade","Drinks","Fresh lemon, berries, sparkling water","🍋","25.00"));
  repo.save(new MenuItem("Midnight Brownie","Desserts","Warm dark chocolate brownie","🍫","39.00"));
 }; }
}
