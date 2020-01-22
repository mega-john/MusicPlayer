package cn.com.cs2c.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class MediaBroadcastReceiver extends BroadcastReceiver {
    private Handler mHandlerMain;

    MediaBroadcastReceiver(Handler handler) {
        this.mHandlerMain = handler;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("android.intent.action.MEDIA_SCANNER_FINISHED")) {
            Log.i("MusicPlayer", "android.intent.action.MEDIA_SCANNER_FINISHED");
            this.mHandlerMain.removeMessages(-1);
            this.mHandlerMain.sendEmptyMessageDelayed(-1, 3000);
        }
    }
}
