import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class JSONUtil {
	public static List<Object> toList(JSONArray jsonArray) {
		List<Object> list = new ArrayList<Object>();
		try {
			int index = 0;
			while(jsonArray.opt(index) != null) {
				list.add(jsonArray.get(index));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
}
