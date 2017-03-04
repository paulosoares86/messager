import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;

public class UserTest {	
	User user;
	String loginUrl = "http://localhost:8000/users/sign_in";
	String name = "Test User";
	String email = "test@example.com";
	
	ArgumentCaptor<MyJSON> jsonCaptor;
	
	@Before
	public void setup() {
		jsonCaptor = ArgumentCaptor.forClass(MyJSON.class);
	}

	@Test
	public void signUp() throws JSONException {
		RestClient rc = Mockito.mock(RestClient.class);
		
		User user = new User(rc);
		MyJSON payload = new MyJSON();
		payload.put("name", name);
		payload.put("email", email);
		Mockito.when(rc.post(Mockito.eq("/users/sign_up"), Mockito.any(MyJSON.class))).thenReturn(new MyJSON("{\"success\":\"true\"}"));
		    
		boolean signedUp = user.signUp(name, email);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_up"), jsonCaptor.capture());
		
		assertEquals(name, jsonCaptor.getValue().get("name"));
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertTrue(signedUp);
	}
}
