package studio.uncommon.bites;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
interface UserRepo extends JpaRepository<AppUser,Long>{Optional<AppUser> findByEmailIgnoreCase(String email);long countByRole(Role role);}
interface TokenRepo extends JpaRepository<ApiToken,String>{void deleteByUser(AppUser user);}
interface MenuRepo extends JpaRepository<MenuItem,Long>{List<MenuItem> findByAvailableTrueOrderByIdAsc();List<MenuItem> findByRestaurantIdOrderByIdAsc(Long id);}
interface OrderRepo extends JpaRepository<CustomerOrder,Long>{
 List<CustomerOrder> findByCustomerIdOrderByCreatedAtDesc(Long id);
 List<CustomerOrder> findByRestaurantIdOrderByCreatedAtDesc(Long id);
 List<CustomerOrder> findByDriverIdOrderByCreatedAtDesc(Long id);
 List<CustomerOrder> findByStatusAndDriverIsNullOrderByCreatedAtAsc(OrderStatus status);
 List<CustomerOrder> findAllByOrderByCreatedAtDesc();
}
