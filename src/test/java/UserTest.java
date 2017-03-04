import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import java.util.List;

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
	JSONObject invitationsJSON;
	JSONObject contactsJSON;
	JSONObject errorJSON;
	
	RestClient rc;
	User user;
	
	ArgumentCaptor<JSONObject> jsonCaptor;
	
	@Before
	public void setup() throws JSONException {
		jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		successJSON = new JSONObject("{\"success\": true}");
		loggedInJSON = new JSONObject("{\"success\": true, \"auth_token\": \"123123123123\" }");
		invitationsJSON = new JSONObject("{\"success\": true, \"invitations\": [ { \"name\": \"Friend Name\", \"email\": \"other@example.com\" } ] } ");
		contactsJSON = new JSONObject("{\"success\": true, \"contacts\": [ { \"name\": \"Contact Name\", \"email\": \"contact@example.com\" } ] } ");
		errorJSON = new JSONObject("{\"success\": false}");
		
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
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(errorJSON);
		
		boolean logedIn = user.login(email, password);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_in"), jsonCaptor.capture());
		
		assertFalse(logedIn);
		assertFalse(User.isLoggedIn());
	}
	
	@Test
	public void successLogin() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(loggedInJSON);
		
		boolean logedIn = user.login(email, password);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_in"), jsonCaptor.capture());
		
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertEquals(password, jsonCaptor.getValue().get("password"));
		assertTrue(logedIn);
		assertTrue(User.isLoggedIn());
		assertEquals(User.getAuthToken(), "123123123123");
	}
	
	@Test
	public void logout() throws JSONException {
		boolean loggedOut = user.logout();
		assertTrue(loggedOut);
		assertFalse(User.isLoggedIn());
		assertEquals(User.getAuthToken(), null);
	}

	@Test(expected=UnauthorizedException.class)
	public void unauthorizedConnectTo() throws JSONException {
		signOut();
		user.connectTo(otherEmail);
	}
	
	@Test
	public void authorizedConnectTo() throws JSONException {
		signIn();
		
		Mockito.when(rc.post(Mockito.eq("/users/connect"), Mockito.any(JSONObject.class))).thenReturn(successJSON);
		boolean isConnected = user.connectTo(otherEmail);
		
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/connect"), jsonCaptor.capture());
		
		assertTrue(isConnected);
		assertEquals(otherEmail, jsonCaptor.getValue().get("friend_email"));
	}
	
	@Test
	public void authorizedCheckInvitations() throws JSONException {
		signIn();
		
		Mockito.when(rc.get(Mockito.eq("/users/invitations"))).thenReturn(invitationsJSON);
		List<User> invitations = user.checkInvitations();
		
		Mockito.verify(rc, Mockito.times(1)).get("/users/invitations");
		
		assertEquals(1, invitations.size());
		assertEquals("Friend Name", invitations.get(0).getName());
		assertEquals("other@example.com", invitations.get(0).getEmail());
	}
	
	@Test(expected=UnauthorizedException.class)
	public void unauthorizedCheckInvitations() throws JSONException {
		signOut();
		user.checkInvitations();
	}
	
	@Test
	public void authorizedGetContactList() throws JSONException {
		signIn();
		
		Mockito.when(rc.get(Mockito.eq("/users/contacts"))).thenReturn(contactsJSON);
		List<User> contacts = user.getContactList();
		
		Mockito.verify(rc, Mockito.times(1)).get("/users/contacts");
		
		assertEquals(1, contacts.size());
		assertEquals("Contact Name", contacts.get(0).getName());
		assertEquals("contact@example.com", contacts.get(0).getEmail());
	}
	
	@Test(expected=UnauthorizedException.class)
	public void unauthorizedGetContactList() throws JSONException {
		signOut();
		user.getContactList();
	}
	
	private void signIn() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(loggedInJSON);
		user.login(email, password);
	}
	
	private void signOut() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(loggedInJSON);
		user.logout();
	}
}
