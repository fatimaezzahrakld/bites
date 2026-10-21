package studio.uncommon.bites;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

// Package-private persistence models: all external API responses use explicit DTOs.
enum Role { CUSTOMER, RESTAURANT, DRIVER, ADMIN }
enum OrderStatus { PLACED, ACCEPTED, PREPARING, READY, PICKED_UP, DELIVERED, CANCELLED }
@Entity @Table(name="app_users") class AppUser {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Column(nullable=false,length=100) String name;
 @Column(nullable=false,unique=true,length=190) String email;
 @Column(nullable=false,name="password_hash") String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) Role role;
 boolean enabled=true;
 protected AppUser(){}
 AppUser(String name,String email,String hash,Role role){this.name=name;this.email=email;passwordHash=hash;this.role=role;}
}
@Entity @Table(name="api_tokens") class ApiToken {
 @Id @Column(length=64) String hash;
 @ManyToOne(optional=false,fetch=FetchType.EAGER) AppUser user;
 @Column(nullable=false) Instant expiresAt;
 protected ApiToken(){}
 ApiToken(String hash,AppUser user,Instant expiry){this.hash=hash;this.user=user;this.expiresAt=expiry;}
}
@Entity @Table(name="menu_items") class MenuItem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false,fetch=FetchType.EAGER) AppUser restaurant;
 @Column(nullable=false,length=110) String name;
 @Column(nullable=false,length=40) String category;
 @Column(nullable=false,length=500) String description;
 @Column(nullable=false,length=10) String emoji;
 @Column(nullable=false,precision=10,scale=2) BigDecimal price;
 boolean available=true;
 protected MenuItem(){}
 MenuItem(AppUser restaurant,String name,String cat,String description,String emoji,String price){this.restaurant=restaurant;this.name=name;category=cat;this.description=description;this.emoji=emoji;this.price=new BigDecimal(price);}
}
@Entity @Table(name="customer_orders") class CustomerOrder {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @Version long version;
 @ManyToOne(optional=false) AppUser customer;
 @ManyToOne(optional=false) AppUser restaurant;
 @ManyToOne AppUser driver;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=30) OrderStatus status=OrderStatus.PLACED;
 @Column(nullable=false,length=250) String address;
 @Column(nullable=false,length=500) String notes="";
 @Column(nullable=false,precision=10,scale=2) BigDecimal total;
 @Column(nullable=false) Instant createdAt=Instant.now();
 @OneToMany(mappedBy="order",cascade=CascadeType.ALL,orphanRemoval=true,fetch=FetchType.EAGER) List<OrderLine> lines=new ArrayList<>();
}
@Entity @Table(name="order_lines") class OrderLine {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) CustomerOrder order;
 Long menuItemId;
 @Column(nullable=false,length=110) String name;
 @Column(nullable=false) int quantity;
 @Column(nullable=false,precision=10,scale=2) BigDecimal unitPrice;
}
