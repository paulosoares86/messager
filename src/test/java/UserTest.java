import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import org.json.JSONException;
import org.json.JSONObject;

public class UserTest {	

	String loginUrl = "http://localhost:8000/users/sign_in";
	String name = "Test User";
	String email = "test@example.com";
	String password = "password320";
	JSONObject successJSON;
	
	ArgumentCaptor<JSONObject> jsonCaptor;
	
	@Before
	public void setup() throws JSONException {
		jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		successJSON = new JSONObject("{\"success\":\"true\"}");
	}

	@Test
	public void signUp() throws JSONException {
		RestClient rc = Mockito.mock(RestClient.class);
		User user = new User(rc);
		Mockito.when(rc.post(Mockito.eq("/users/sign_up"), Mockito.any(JSONObject.class))).thenReturn(successJSON);
		    
		boolean signedUp = user.signUp(name, email);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_up"), jsonCaptor.capture());
		
		assertEquals(name, jsonCaptor.getValue().get("name"));
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertTrue(signedUp);
	}
	
	@Test
	public void login() throws JSONException {
		RestClient rc = Mockito.mock(RestClient.class);
		User user = new User(rc);
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(successJSON);
		
		boolean logedIn = user.logIn(email, password);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_in"), jsonCaptor.capture());
		
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertEquals(password, jsonCaptor.getValue().get("password"));
		assertTrue(logedIn);
	}
}
