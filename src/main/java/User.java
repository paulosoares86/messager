import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private RestClient restClient;
	private static String authToken;
	
	private String name;
	private String email;

	public User(RestClient restClient) {
		this.restClient = restClient;
	}
	
	public User(String name, String email) {
		this.name = name;
		this.email = email;
	}
	
	public boolean signUp(String name, String email) {
		try {
			JSONObject payload = new JSONObject();
			payload.put("email", email);
			payload.put("name", name);
			JSONObject res = restClient.post("/users/sign_up", payload);
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
			return authToken != null;
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
		List<User> ret = new ArrayList<User>();		
		try {
			JSONObject res = restClient.get("/users/invitations");
			if (!res.getBoolean("success")) {
				return ret;
			} 
			List<Object> invitations = JSONUtil.toList(res.getJSONArray("invitations"));
			for (Object invitation : invitations) {
				JSONObject userJson = new JSONObject(invitation);
				String name = userJson.getString("name");
				String email = userJson.getString("email");
				invitations.add(new User(name, email));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public static boolean isLoggedIn() {
		return authToken != null;
	}

	public static String getAuthToken() {
		return authToken;
	}
	
	private void checkIsLoggedIn(String methodName) {
		if (!isLoggedIn()) {
			String msg = "Unauthorized call for " + methodName;
			throw new UnauthorizedException(msg);
		}
	}
	
	// getters and setters
	
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
}
