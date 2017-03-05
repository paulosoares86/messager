import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {

	private static RestClient restClient = new RestClient();
	private static String authToken;

	private String name;
	private String email;
	
	public User() {}

	public User(String name, String email) {
		setName(name);
		setEmail(email);
	}
	
	public User(JSONObject user) throws JSONException {
		this(user.getString("name"), user.getString("email"));
	}

	public boolean signUp(String name, String email) {
		try {
			User user = new User(name, email);
			JSONObject res = restClient.post("/users/sign_up", user.toJSON());
			return res.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean login(String email, String password) {
		try {
			JSONObject payload = new JSONObject();
			payload.put("email", email);
			payload.put("password", password);
			JSONObject res = restClient.post("/users/sign_in", payload);

			authToken = res.getBoolean("success") ? res.getString("auth_token") : null;
			return isLoggedIn();
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean logout() {
		authToken = null;
		return true;
	}

	public boolean connectTo(String otherEmail) {
		checkIsLoggedIn("connectTo");
		try {
			JSONObject payload = new JSONObject();
			payload.put("friend_email", otherEmail);
			JSONObject res = restClient.post("/users/connect", payload);
			return res.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<User> checkInvitations() {
		checkIsLoggedIn("checkInvitations");
		return getUsersForResource("invitations");
	}
	
	public List<User> getContactList() {
		checkIsLoggedIn("getContactList");
		return getUsersForResource("contacts");
	}
	
	public List<ChatRoom> getChatRooms() {
		checkIsLoggedIn("getChatRooms");
		List<ChatRoom> ret = new ArrayList<ChatRoom>();
		try {
			for (JSONObject chatRoom : getResourceList("chat_rooms")) 
				ret.add(new ChatRoom(chatRoom));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public List<Message> getMessages() {
		checkIsLoggedIn("getMessages");
		List<Message> ret = new ArrayList<Message>();
		try {
			for (JSONObject message : getResourceList("messages")) 
				ret.add(new Message(message));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public ChatRoom createChatRoom(ArrayList<User> users) {
		checkIsLoggedIn("createChatRoom");
		try {
			JSONObject payload = new JSONObject();
			payload.put("users", users);
			JSONObject res = restClient.post("/users/chat_rooms", payload);
			if (!res.getBoolean("success")) {
				return null;
			} else {
				return new ChatRoom((JSONObject) res.get("chat_room"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static boolean isLoggedIn() {
		return authToken != null;
	}

	public static String getAuthToken() {
		return authToken;
	}
	
	private List<User> getUsersForResource(String userResource) {
		List<User> ret = new ArrayList<User>();
		try {
			for (JSONObject user : getResourceList(userResource)) 
				ret.add(new User(user));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	private List<JSONObject> getResourceList(String resource) {
		try {
			JSONObject res = restClient.get("/users/" + resource);
			if (!res.getBoolean("success")) {
				return new ArrayList<JSONObject>();
			}
			return JSONUtil.toList(res.getJSONArray(resource));
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<JSONObject>();
		}
	}

	public static void checkIsLoggedIn(String methodName) {
		if (isLoggedIn()) return; 
		String msg = "Unauthorized call for " + methodName;
		throw new UnauthorizedException(msg);
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static void setRestClient(RestClient _restClient) {
		restClient = _restClient;
	}
	
	private JSONObject toJSON() throws JSONException {
		JSONObject payload = new JSONObject();
		payload.put("email", this.email);
		payload.put("name", this.name);
		return payload;
	}

}
