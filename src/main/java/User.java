import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private RestClient restClient;
	private static String authToken;
	
	public User(RestClient restClient) {
		this.restClient = restClient;
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

	public boolean logIn(String email, String password) {
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

	public boolean connectTo(String otherEmail) {
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
	
	public static boolean isLoggedIn() {
		return authToken != null;
	}

	public static String getAuthToken() {
		return authToken;
	}
	

}
