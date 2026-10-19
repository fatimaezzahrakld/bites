package studio.uncommon.bites;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderRepository extends JpaRepository<CustomerOrder,Long>{}
