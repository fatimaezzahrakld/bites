package studio.uncommon.bites;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

@RestController @RequestMapping("/api/auth") class AuthController {
 private final UserRepo users;private final TokenRepo tokens;private final PasswordEncoder encoder;
 AuthController(UserRepo u,TokenRepo t,PasswordEncoder e){users=u;tokens=t;encoder=e;}
 static Identity view(AppUser u){return new Identity(u.id,u.name,u.email,u.role.name());}
 private AuthResult issue(AppUser u){String raw=TokenFilter.randomToken();tokens.save(new ApiToken(TokenFilter.hash(raw),u,Instant.now().plus(12,ChronoUnit.HOURS)));return new AuthResult(raw,view(u));}
 @PostMapping("/register") AuthResult register(@Valid @RequestBody RegisterRequest data){
  String email=data.email().trim().toLowerCase(Locale.ROOT);
  if(users.findByEmailIgnoreCase(email).isPresent())throw new ResponseStatusException(HttpStatus.CONFLICT,"Email already registered");
  return issue(users.save(new AppUser(data.name().trim(),email,encoder.encode(data.password()),Role.CUSTOMER)));
 }
 @PostMapping("/login") AuthResult login(@Valid @RequestBody LoginRequest data){
  AppUser user=users.findByEmailIgnoreCase(data.email().trim()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials"));
  if(!user.enabled||!encoder.matches(data.password(),user.passwordHash))throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Invalid credentials");
  return issue(user);
 }
 @GetMapping("/me") Identity me(Authentication auth){return view((AppUser)auth.getPrincipal());}
 @PostMapping("/logout") ResponseEntity<Void> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String header){
  if(header.startsWith("Bearer "))tokens.deleteById(TokenFilter.hash(header.substring(7)));
  return ResponseEntity.noContent().build();
 }
}
