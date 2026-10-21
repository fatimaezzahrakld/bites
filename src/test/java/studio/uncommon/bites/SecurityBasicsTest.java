package studio.uncommon.bites;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
class SecurityBasicsTest {
 @Test void randomTokensAreDifferentAndHashesAreFixedLength(){
  String a=TokenFilter.randomToken(),b=TokenFilter.randomToken();
  assertNotEquals(a,b);assertEquals(64,TokenFilter.hash(a).length());
  assertEquals(TokenFilter.hash(a),TokenFilter.hash(a));assertNotEquals(a,TokenFilter.hash(a));
 }
 @Test void passwordsAreHashed(){
  var encoder=new BCryptPasswordEncoder();String hash=encoder.encode("StrongPass123!");
  assertNotEquals("StrongPass123!",hash);assertTrue(encoder.matches("StrongPass123!",hash));
  assertFalse(encoder.matches("incorrect",hash));
 }
}
