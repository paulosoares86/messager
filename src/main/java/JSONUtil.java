import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtil {
	public static List<JSONObject> toList(JSONArray jsonArray) {
		List<JSONObject> list = new ArrayList<JSONObject>();
		try {
			int index = 0;
			while(jsonArray.opt(index) != null) {
				list.add((JSONObject) jsonArray.get(index));
				index++;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
