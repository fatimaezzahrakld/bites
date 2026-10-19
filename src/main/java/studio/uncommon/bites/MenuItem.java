package studio.uncommon.bites;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
public class MenuItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String name;
 public String category;
 public String description;
 public String emoji;
 @Column(precision=10,scale=2) public BigDecimal price;
 public boolean available=true;
 protected MenuItem(){}
 public MenuItem(String name,String category,String description,String emoji,String price){
 this.name=name;this.category=category;this.description=description;this.emoji=emoji;this.price=new BigDecimal(price);
 }
}
