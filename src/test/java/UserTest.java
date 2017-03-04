import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class UserTest {	

	String loginUrl = "http://localhost:8000/users/sign_in";
	String name = "Test User";
	String email = "test@example.com";
	String otherEmail = "other@example.com";
	String password = "password320";
	
	HashMap<String, JSONObject> jsonMessages = new HashMap<String, JSONObject>();
	
	RestClient rc;
	User user;
	
	ArgumentCaptor<JSONObject> jsonCaptor;
	
	@Before
	public void setup() throws JSONException {
		jsonCaptor = ArgumentCaptor.forClass(JSONObject.class);
		jsonMessages.put("success", new JSONObject("{\"success\": true}"));
		jsonMessages.put("loggedIn", new JSONObject("{\"success\": true, \"auth_token\": \"123123123123\" }"));
		jsonMessages.put("invitations", new JSONObject("{\"success\": true, \"invitations\": [ { \"name\": \"Friend Name\", \"email\": \"other@example.com\" } ] } "));
		jsonMessages.put("contacts", new JSONObject("{\"success\": true, \"contacts\": [ { \"name\": \"Contact Name\", \"email\": \"contact@example.com\" } ] } "));
		jsonMessages.put("chatRooms", new JSONObject("{\"success\": true, \"chat_rooms\": [ { \"name\": \"Contact Name\", \"id\": 111 }, { \"name\": \"Contact2\", \"id\": 123 }, { \"name\": \"Contact 3\", \"id\": 333 } ] } "));
		jsonMessages.put("messages", new JSONObject("{\"success\": true, \"messages\": [ { \"chat_room_id\": 123, \"text\": \"hello\" }, { \"chat_room_id\": 111, \"text\": \"world!\" } ] } "));
		jsonMessages.put("error", new JSONObject("{\"success\": false}"));
		
		rc = Mockito.mock(RestClient.class);
		User.setRestClient(rc);
		user = new User();
	}

	@Test
	public void signUp() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_up"), Mockito.any(JSONObject.class))).thenReturn(jsonMessages.get("success"));
		    
		boolean signedUp = user.signUp(name, email);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_up"), jsonCaptor.capture());
		
		assertEquals(name, jsonCaptor.getValue().get("name"));
		assertEquals(email, jsonCaptor.getValue().get("email"));
		assertTrue(signedUp);
	}
	
	@Test
	public void failLogin() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(jsonMessages.get("error"));
		
		boolean logedIn = user.login(email, password);
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/sign_in"), jsonCaptor.capture());
		
		assertFalse(logedIn);
		assertFalse(User.isLoggedIn());
	}
	
	@Test
	public void successLogin() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(jsonMessages.get("loggedIn"));
		
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
		
		Mockito.when(rc.post(Mockito.eq("/users/connect"), Mockito.any(JSONObject.class))).thenReturn(jsonMessages.get("success"));
		boolean isConnected = user.connectTo(otherEmail);
		
		Mockito.verify(rc, Mockito.times(1)).post(Mockito.eq("/users/connect"), jsonCaptor.capture());
		
		assertTrue(isConnected);
		assertEquals(otherEmail, jsonCaptor.getValue().get("friend_email"));
	}
	
	@Test
	public void authorizedCheckInvitations() throws JSONException {
		signIn();
		
		Mockito.when(rc.get(Mockito.eq("/users/invitations"))).thenReturn(jsonMessages.get("invitations"));
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
		
		Mockito.when(rc.get(Mockito.eq("/users/contacts"))).thenReturn(jsonMessages.get("contacts"));
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
	
	@Test
	public void authorizedGetChatRooms() throws JSONException {
		signIn();
		
		Mockito.when(rc.get(Mockito.eq("/users/chat_rooms"))).thenReturn(jsonMessages.get("chatRooms"));
		List<ChatRoom> rooms = user.getChatRooms();
		
		Mockito.verify(rc, Mockito.times(1)).get("/users/chat_rooms");
		
		assertEquals(3, rooms.size());
		assertEquals(111, rooms.get(0).getId());
		assertEquals(123, rooms.get(1).getId());
		assertEquals(333, rooms.get(2).getId());
	}
	
	@Test(expected=UnauthorizedException.class)
	public void unauthorizedGetChatRooms() throws JSONException {
		signOut();
		user.getChatRooms();
	}
	
	@Test
	public void authorizedGetMessages() throws JSONException {
		signIn();
		
		Mockito.when(rc.get(Mockito.eq("/users/messages"))).thenReturn(jsonMessages.get("messages"));
		List<Message> messages = user.getMessages();
		
		Mockito.verify(rc, Mockito.times(1)).get("/users/messages");
		
		assertEquals(2, messages.size());
		
		assertEquals(123, messages.get(0).getChatRoomId());
		assertEquals("hello", messages.get(0).getText());
		
		assertEquals(111, messages.get(1).getChatRoomId());
		assertEquals("world!", messages.get(1).getText());
	}
	
	@Test(expected=UnauthorizedException.class)
	public void unauthorizedGetMessages() throws JSONException {
		signOut();
		user.getMessages();
	}
	
	private void signIn() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(jsonMessages.get("loggedIn"));
		user.login(email, password);
	}
	
	private void signOut() throws JSONException {
		Mockito.when(rc.post(Mockito.eq("/users/sign_in"), Mockito.any(JSONObject.class))).thenReturn(jsonMessages.get("loggedIn"));
		user.logout();
	}
}
