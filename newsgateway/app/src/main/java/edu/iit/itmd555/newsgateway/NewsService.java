package edu.iit.itmd555.newsgateway;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.List;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    private boolean running = true;
    public static List<Article> newArticles = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (running) {

                    if(NewsService.newArticles != null) {
                        sendNewsBroadcast(NewsService.newArticles);
                        NewsService.newArticles = null;
                    }

                }
                Log.d(TAG, "run: Ending loop");

            }
        }).start();

        return Service.START_NOT_STICKY;
    }

    private void sendNewsBroadcast(List<Article> articles) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.NEWS_BROADCAST);
        intent.putExtra("articles", (Serializable) articles);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

}
