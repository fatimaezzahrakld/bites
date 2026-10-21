package studio.uncommon.bites;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

@Configuration class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean SecurityFilterChain filterChain(HttpSecurity http,TokenFilter filter) throws Exception {
  return http.csrf(csrf->csrf.disable()) // Bearer tokens only; no cookie authentication.
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a.requestMatchers("/api/auth/login","/api/auth/register","/api/menu","/actuator/health","/","/index.html","/favicon.ico","/assets/**").permitAll().anyRequest().authenticated())
   .addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class)
   .httpBasic(h->h.disable()).formLogin(f->f.disable()).build();
 }
}
@Component class TokenFilter extends OncePerRequestFilter {
 private final TokenRepo tokens;
 TokenFilter(TokenRepo t){tokens=t;}
 static String hash(String input){try{byte[] digest=MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));return HexFormat.of().formatHex(digest);}catch(Exception e){throw new IllegalStateException(e);}}
 static String randomToken(){byte[] b=new byte[32];new SecureRandom().nextBytes(b);return Base64.getUrlEncoder().withoutPadding().encodeToString(b);}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {
  String header=request.getHeader(HttpHeaders.AUTHORIZATION);
  if(header!=null&&header.startsWith("Bearer ")){
   String raw=header.substring(7);
   if(raw.length()<=200){tokens.findById(hash(raw)).filter(t->t.expiresAt.isAfter(Instant.now())&&t.user.enabled).ifPresent(t->{
    var auth=new UsernamePasswordAuthenticationToken(t.user,null,List.of(new SimpleGrantedAuthority("ROLE_"+t.user.role.name())));
    SecurityContextHolder.getContext().setAuthentication(auth);
   });}
  }
  chain.doFilter(request,response);
 }
}
