package edu.iit.itmd555.newsgateway;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

public class NewsReceiver extends BroadcastReceiver {

    private static final String TAG = "NewsReceiver";
    private MainActivity mainActivity;

    public NewsReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null || intent.getAction() == null)
            return;

        switch (intent.getAction()) {
            case MainActivity.NEWS_BROADCAST:
                List<Article> articles;
                if(intent.hasExtra("articles")) {
                    Log.d(TAG, "onReceive: broadcast received");
                    articles = (List<Article>) intent.getSerializableExtra("articles");
                    mainActivity.setArticles(articles);
                }
                break;
        }

    }
}
