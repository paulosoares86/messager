import org.json.JSONException;
import org.json.JSONObject;

public class User {
	
	private RestClient restClient;
	
	public User(RestClient restClient) {
		this.restClient = restClient;
	}
	
	public boolean signUp(String name, String email) {
		try {
			MyJSON payload = new MyJSON();
			payload.put("email", email);
			payload.put("name", name);
			MyJSON res = restClient.post("/users/sign_up", payload);
			return res.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
}
