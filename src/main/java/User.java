import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private RestClient restClient;
	
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
			return res.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
