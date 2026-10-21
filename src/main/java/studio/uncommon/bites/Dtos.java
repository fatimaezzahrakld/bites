package studio.uncommon.bites;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

record LoginRequest(@NotBlank String email,@NotBlank String password){}
record RegisterRequest(@NotBlank @Size(min=2,max=100) String name,@NotBlank @Email String email,@NotBlank @Size(min=10,max=100) String password){}
record Identity(Long id,String name,String email,String role){}
record AuthResult(String token,Identity user){}
record MenuView(Long id,Long restaurantId,String restaurant,String name,String category,String description,String emoji,BigDecimal price,boolean available){}
record MenuInput(@NotBlank @Size(max=110) String name,@NotBlank @Size(max=40) String category,@NotBlank @Size(max=500) String description,@NotBlank @Size(max=10) String emoji,@NotNull @DecimalMin("1.00") @Digits(integer=8,fraction=2) BigDecimal price,boolean available){}
record LineInput(@NotNull Long menuItemId,@Min(1) @Max(20) int quantity){}
record OrderInput(@NotBlank @Size(max=250) String address,@Size(max=500) String notes,@NotEmpty @Size(max=30) List<@Valid LineInput> items){}
record LineView(String name,int quantity,BigDecimal unitPrice){}
record OrderView(Long id,String customer,String restaurant,String driver,Long restaurantId,String address,String notes,String status,BigDecimal total,Instant createdAt,List<LineView> lines){}
record StatusInput(@NotBlank String status){}
record StatsView(long customers,long restaurants,long drivers,long orders,BigDecimal revenueDelivered){}
