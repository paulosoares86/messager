import org.json.JSONException;
import org.json.JSONObject;

public class Message {

	private int chatRoomId;
	private String text;
	private static RestClient restClient = new RestClient();
	
	public Message(int chatRoomId, String text) {
		setChatRoomId(chatRoomId);
		setText(text);
	}
	
	public Message(JSONObject message) throws JSONException {
		this(message.getInt("chat_room_id"), message.getString("text"));
	}
	
	public static boolean send(int id, String text) {
		User.checkIsLoggedIn("Message.send");
		JSONObject payload = new JSONObject();
		try {
			payload.put("chat_room_id", id);
			payload.put("text", text);
			JSONObject res = restClient.post("/messages", payload);
			return res.getBoolean("success");
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int getChatRoomId() {
		return chatRoomId;
	}
	public void setChatRoomId(int chatRoomId) {
		this.chatRoomId = chatRoomId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}

	public static void setRestClient(RestClient restClient) {
		Message.restClient = restClient;
	}
	
}
