package cn.com.cs2c.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MusicPlayerBootCompleteBroadcastReceiver extends BroadcastReceiver {
    public static boolean bootCompleteFlag = false;
    public static boolean firstTimeRunFlag = true;

    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equalsIgnoreCase(intent.getAction())) {
            bootCompleteFlag = true;
            Log.i("zys1", "MusicPlayer_Boot_Completed!");
        }
    }
}
