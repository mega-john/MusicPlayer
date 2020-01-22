package cn.com.cs2c.musicplayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.cs2c.ISystemAppService;
import android.os.Handler;

public class PanelBroadcastReceiver extends BroadcastReceiver {
    private Handler mHandlerMain;
    private String mPackageName;
    private ISystemAppService mSAS;

    @SuppressLint("WrongConstant")
    PanelBroadcastReceiver(Handler handler, Context context) {
        this.mHandlerMain = handler;
        this.mPackageName = context.getPackageName();
//        this.mSAS = (ISystemAppService) context.getSystemService("systemapp");
    }

    private boolean whetherRespondPanelKey() {
        try {
            return this.mSAS.whetherRespondPanelKey(this.mPackageName);
        } catch (Exception e) {
            return false;
        }
    }

    public void onReceive(Context context, Intent intent) {
        if (!whetherRespondPanelKey()) {
            return;
        }
        if ("cn.com.cs2c.android.vehicle.action.PREVIEW_KEY".equalsIgnoreCase(intent.getAction())) {
            this.mHandlerMain.sendEmptyMessage(72);
        } else if ("cn.com.cs2c.android.vehicle.action.RADIO_PREVIEW_KEY".equalsIgnoreCase(intent.getAction())) {
            this.mHandlerMain.sendEmptyMessage(75);
        } else if ("cn.com.cs2c.android.vehicle.action.PLAYPAUSE_KEY".equalsIgnoreCase(intent.getAction())) {
            this.mHandlerMain.sendEmptyMessage(73);
        } else if ("cn.com.cs2c.android.vehicle.action.NEXT_KEY".equalsIgnoreCase(intent.getAction())) {
            this.mHandlerMain.sendEmptyMessage(74);
        } else if ("cn.com.cs2c.android.vehicle.action.RADIO_NEXT_KEY".equalsIgnoreCase(intent.getAction())) {
            this.mHandlerMain.sendEmptyMessage(76);
        }
    }
}
