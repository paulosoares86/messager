import org.json.JSONException;
import org.json.JSONObject;

public class Message {

	private int chatRoomId;
	private String text;
	
	public Message(int chatRoomId, String text) {
		setChatRoomId(chatRoomId);
		setText(text);
	}
	
	public Message(JSONObject message) throws JSONException {
		this(message.getInt("chat_room_id"), message.getString("text"));
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
	
	
}
