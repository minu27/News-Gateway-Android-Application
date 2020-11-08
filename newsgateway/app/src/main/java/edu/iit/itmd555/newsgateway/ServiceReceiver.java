package edu.iit.itmd555.newsgateway;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "ServiceReceiver";
    private MainActivity mainActivity;

    public ServiceReceiver(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent == null || intent.getAction() == null)
            return;

        switch (intent.getAction()) {
            case MainActivity.SERVICE_BROADCAST:
                String source;
                if(intent.hasExtra("source")) {
                    Log.d(TAG, "onReceive: broadcast received");
                    source = intent.getStringExtra("source");
                    AsyncArticleLoader asyncArticleLoader = new AsyncArticleLoader(mainActivity);
                    asyncArticleLoader.source = source;
                    asyncArticleLoader.execute();
                }
                break;
        }

    }

}
