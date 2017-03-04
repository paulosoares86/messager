import org.json.JSONException;
import org.json.JSONObject;

public class MyJSON extends JSONObject {
	
	public MyJSON(String data) throws JSONException {
		super(data);
	}

	public MyJSON() {
		super();
	}
	
	public int hashCode() {
		return 1;
	}

	public boolean equals(MyJSON other) {
		return this.toString().equals(other.toString());
	}
}
