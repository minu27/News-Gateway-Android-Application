package edu.iit.itmd555.newsgateway;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AsyncSourceLoader extends AsyncTask<String, Integer, String> {

    private static final String TAG = "AsyncSourceLoader";

    private MainActivity mainActivity;

    private static final String DATA_URL = "https://newsapi.org/v2/sources?language=en&country=us&category=&apiKey=4f3d59f6450f46e18ddca10cef55b87b";

    AsyncSourceLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {

        Uri dataUri = Uri.parse(DATA_URL);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            Log.d(TAG, "doInBackground: ResponseCode: " + conn.getResponseCode());

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {

        ArrayList<Category> categories = parseJSON(s);
        mainActivity.updateData(categories);

    }

    private ArrayList<Category> parseJSON(String s) {

        ArrayList<Category> categories = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(s);
            JSONArray sourcesJSON = (JSONArray) result.get("sources");
            for (int i=0; i<sourcesJSON.length(); i++) {
                JSONObject sourceJSON = (JSONObject) sourcesJSON.get(i);
                Source source = new Source();
                source.setId((String) sourceJSON.get("id"));
                source.setName((String) sourceJSON.get("name"));
                boolean exists = false;
                for (Category category : categories) {
                    if (category.getCategory().equals(sourceJSON.getString("category"))) {
                        category.getSources().add(source);
                        exists = true;
                        break;
                    }
                }
                if(!exists) {
                    Category category = new Category(sourceJSON.getString("category"));
                    category.getSources().add(source);
                    categories.add(category);
                }
            }
            return categories;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
