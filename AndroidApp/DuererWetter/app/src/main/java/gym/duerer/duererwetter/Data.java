package gym.duerer.duererwetter;

import android.content.Context;
import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Mathias on 11.10.15.
 */
public class Data {

    public static Map<String, String> parse(String json) {
        Map<String, String> result = new HashMap<>();
        String trueJson = json.replace("{", "");
        trueJson = trueJson.replace("}", "");
        String[] splitted = trueJson.split(",");

        for (String s : splitted) {
            String[] temp = s.split(":");
            result.put(temp[0].replace("\"", ""), temp[1].replace("\"", ""));
        }

        return result;
    }

    public static Map<String, String> getAndParseData() throws Exception {
        StringBuilder result = new StringBuilder();

        URL url = new URL("http://wetterstation-duerer.rhcloud.com/getWeather");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            result.append(line);
        }
        bufferedReader.close();

        return Data.parse(result.toString());
    }

    public static Map<String, String> renameEntries(Map<String, String> tempResult) {
        Map<String, String> result = new TreeMap<>();
        result.put("Temperatur", tempResult.get("temp"));
        result.put("Luftfeuchtigkeit", tempResult.get("humidity"));
        result.put("Luftqualität", tempResult.get("airQ"));
        result.put("Luftdruck", tempResult.get("airP"));
        result.put("Niederschlag", tempResult.get("rain"));
        result.put("Windrichtung", tempResult.get("windDir"));
        result.put("Windstärke", tempResult.get("wind"));
        return result;
    }

    public static final int NORMAL = -1, COLD = 0, WARM = 1, RAIN = 2;

    public static int interpretValues(Map<String, String> map, Context context) {
        Resources res = context.getResources();

        if (Integer.valueOf(map.get("rain")) > 0) {
            return RAIN;
        }

        if (Integer.valueOf(map.get("temp")) <= res.getInteger(R.integer.zuKalt)) {
            return COLD;
        }

        if (Integer.valueOf(map.get("temp")) >= res.getInteger(R.integer.zuWarm)) {
            return WARM;
        }

        return NORMAL;
    }

}
