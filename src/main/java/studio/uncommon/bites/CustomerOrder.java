package studio.uncommon.bites;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
@Entity
@Table(name="customer_orders")
public class CustomerOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String customerName;
 public String customerEmail;
 public String status="PLACED";
 @Column(precision=10,scale=2) public BigDecimal total;
 public Instant createdAt=Instant.now();
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER)
 public List<OrderLine> lines=new ArrayList<>();
}
