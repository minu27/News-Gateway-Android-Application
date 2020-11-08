package edu.iit.itmd555.newsgateway;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AsyncArticleLoader extends AsyncTask<String, Integer, String> {

    public MainActivity mainActivity;

    private static final String TAG = "AsyncArticleLoader";

    private static final String DATA_URL = "https://newsapi.org/v2/everything?sources=%s&language=en&pageSize=10&apiKey=4f3d59f6450f46e18ddca10cef55b87b";

    public String source;
    private NewsReceiver newsReceiver;

    AsyncArticleLoader(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(String... strings) {
        Uri dataUri = Uri.parse(String.format(DATA_URL, source));
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

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

        List<Article> articles = parseJSON(s);
        NewsService.newArticles = articles;

    }

    private List<Article> parseJSON(String s) {

        ArrayList<Article> articles = new ArrayList<>();
        try {
            JSONObject result = new JSONObject(s);
            JSONArray articlesJSON = (JSONArray) result.get("articles");
            for (int i=0; i<articlesJSON.length(); i++) {
                JSONObject articleJSON = (JSONObject) articlesJSON.get(i);
                Article article = new Article();
                article.setAuthor(articleJSON.getString("author"));
                article.setTitle(articleJSON.getString("title"));
                article.setDescription(articleJSON.getString("description"));
                article.setUrl(articleJSON.getString("url"));
                article.setUrlToImage(articleJSON.getString("urlToImage"));
                article.setPublishedAt(articleJSON.getString("publishedAt"));
                articles.add(article);
            }
            return articles;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}
