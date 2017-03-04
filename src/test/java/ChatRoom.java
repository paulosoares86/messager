import org.json.JSONException;
import org.json.JSONObject;

public class ChatRoom {
	
	private int id;
	private String name;
	private RestClient rc = new RestClient();
	
	public ChatRoom(int id, String name) {
		setId(id);
		setName(name);
	}
	
	public ChatRoom(JSONObject chatRoomJson) throws JSONException {
		setId(chatRoomJson.getInt("id"));
		setName(chatRoomJson.getString("name"));
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
