package se.ox.helper;

import java.util.HashMap;
import java.util.Map;

public class JsonHelper {

    private JsonHelper() {
    }

    public static Map<String, String> readJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.isBlank()) return map;

        String body = json.strip().replaceAll("[{}\"]", "");
        for (String pair : body.split(",")) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                String key = kv[0].trim();
                String value = kv[1].trim();
                map.put(key, value);
            }
        }
        return map;
    }

    public static String toJson(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (var entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append("\"").append(escape(entry.getValue())).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    public static Integer getInt(Map<String, String> map, String key) {
        try {
            return Integer.parseInt(map.getOrDefault(key, "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String getString(Map<String, String> map, String key) {
        String val = map.get(key);
        return (val == null || val.isBlank()) ? null : val;
    }

    private static String escape(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
