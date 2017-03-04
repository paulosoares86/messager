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
	String otherEmail = "other@example.com";
	String password = "password320";
	
	JSONObject successJSON;
	JSONObject loggedInJSON;
	JSONObject loginErrorJSON;
	
	RestClient rc;
	User user;
	
	ArgumentCaptor<JSONObject> jsonCaptor;
	
	@Before
	public void setup() throws JSONException {
		jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		successJSON = new JSONObject("{\"success\": true}");
		loggedInJSON = new JSONObject("{\"success\": true, \"auth_token\": \"123123123123\" }");
		loginErrorJSON = new JSONObject("{\"success\": false}");
		
		rc = Mockito.mock(RestClient.class);
		user = new User(rc);
	}

	@Test
	public void signUp() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_up"), Mockito.any(JSONObject.class))).thenReturn(successJSON);
		    
		boolean signedUp = user.signUp(name, email);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_up"), jsonCaptor.capture());
		
		assertEquals(name, jsonCaptor.getValue().get("name"));
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertTrue(signedUp);
	}
	
	@Test
	public void failLogin() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(loginErrorJSON);
		
		boolean logedIn = user.logIn(email, password);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_in"), jsonCaptor.capture());
		
		assertFalse(logedIn);
		assertFalse(User.isLoggedIn());
	}
	
	@Test
	public void successLogin() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(loggedInJSON);
		
		boolean logedIn = user.logIn(email, password);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_in"), jsonCaptor.capture());
		
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertEquals(password, jsonCaptor.getValue().get("password"));
		assertTrue(logedIn);
		assertTrue(User.isLoggedIn());
		assertEquals(User.getAuthToken(), "123123123123");
	}

	@Test
	public void connectTo() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/connect"), Mockito.any(JSONObject.class))).thenReturn(successJSON);
		boolean isConnected = user.connectTo(otherEmail);
		
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/connect"), jsonCaptor.capture());
		
		assertTrue(isConnected);
		assertEquals(otherEmail, jsonCaptor.getValue().get("friend_email"));
	}
}
