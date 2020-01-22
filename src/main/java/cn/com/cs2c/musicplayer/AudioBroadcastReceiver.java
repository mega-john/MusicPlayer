package cn.com.cs2c.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class AudioBroadcastReceiver extends BroadcastReceiver {
    private Handler mHandlerMain;
    private String mPackageName;

    AudioBroadcastReceiver(Handler handler, String packageName) {
        this.mHandlerMain = handler;
        this.mPackageName = packageName;
    }

    public void onReceive(Context context, Intent intent) {
        if ("cn.com.cs2c.android.vehicle.action.PAUSE_MUSIC".equalsIgnoreCase(intent.getAction())) {
            String packageName = intent.getStringExtra("mPackageName");
            if (packageName == null || !packageName.equalsIgnoreCase(this.mPackageName)) {
                this.mHandlerMain.sendEmptyMessage(Constants.RequestPauseTrack);
            }
        }
    }
}
