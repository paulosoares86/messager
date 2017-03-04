import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import net.sf.corn.httpclient.*;
import net.sf.corn.httpclient.HttpResponse;

public class RestClient {

	public JSONObject get(String url) {
		return sendData(HttpClient.HTTP_METHOD.GET, url, null);
	}
	
	public JSONObject post(String url, JSONObject json) {
		return sendData(HttpClient.HTTP_METHOD.POST, url, json);
	}
	
	private JSONObject sendData(HttpClient.HTTP_METHOD method, String url, JSONObject body) {
		try {
			HttpClient client = new HttpClient(new URI(url));
			HttpResponse res = body == null ? client.sendData(method) : client.sendData(method, body.toString());
			return new JSONObject(res.getData());
		} catch (IOException e) {
			return buildErrorJSON("Could not connect to server: " + e.getMessage());
		} catch (URISyntaxException e) {
			return buildErrorJSON("Invalid URI: " + e.getMessage());
		} catch (JSONException e) {
			return buildErrorJSON("Invalid JSON response from server: " + e.getMessage());
		}
	}
	
	private JSONObject buildErrorJSON(String message) {
		JSONObject ret = new JSONObject();
		try {
			ret.put("success", false);
			ret.put("error", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ret;	
	}
}
