package studio.uncommon.bites;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import java.util.Map;
@RestControllerAdvice class Errors {
 @ExceptionHandler(ResponseStatusException.class) ResponseEntity<Map<String,String>> response(ResponseStatusException e){return ResponseEntity.status(e.getStatusCode()).body(Map.of("error",e.getReason()==null?"Request failed":e.getReason()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("error","Please check the required fields and values"));}
 @ExceptionHandler(OptimisticLockingFailureException.class) ResponseEntity<Map<String,String>> race(Exception e){return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error","This order changed. Refresh and try again."));}
}
