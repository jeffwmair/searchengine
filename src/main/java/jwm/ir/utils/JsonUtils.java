package jwm.ir.utils;

public class JsonUtils {

	public static String getJsonItem(String key, String val) {
		return "\"" + key + "\":\"" + val + "\""; 
	}
	public static String getJsonItem(String key, int val) {
		return getJsonItem(key, Integer.toString(val));
	}
	public static String getJsonItem(String key, double val) {
		return getJsonItem(key, Double.toString(val));
	}
}
