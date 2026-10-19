package studio.uncommon.bites;
import jakarta.persistence.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
public class OrderLine {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public Long menuItemId;
 public String name;
 public int quantity;
 @Column(precision=10,scale=2) public BigDecimal unitPrice;
 @JsonIgnore @ManyToOne(fetch=FetchType.LAZY) public CustomerOrder order;
}
