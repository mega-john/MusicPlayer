package cn.com.cs2c.musicplayer.canbus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.cs2c.ICanBusService;
import android.os.RemoteException;

public class BagooAudi implements ICanbus {
    /* access modifiers changed from: private */
    public boolean mBtPhoneStatus = false;
    BroadcastReceiver mReverseReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.bts.device.action.DISCONNECT".equals(intent.getAction())) {
                boolean unused = BagooAudi.this.mBtPhoneStatus = false;
            } else if ("android.bts.device.action.PHONE_HANGUP".equals(intent.getAction())) {
                boolean unused2 = BagooAudi.this.mBtPhoneStatus = false;
            } else if ("android.bts.device.action.PHONE_CALLOUT".equals(intent.getAction())) {
                boolean unused3 = BagooAudi.this.mBtPhoneStatus = true;
            } else if ("android.bts.device.action.PHONE_CALLIN".equals(intent.getAction())) {
                boolean unused4 = BagooAudi.this.mBtPhoneStatus = true;
            }
        }
    };
    private ICanBusService mCanbus;
    private Context mContext;

    public void SetContext(Context ctx) {
        this.mContext = ctx;
        if (this.mContext != null) {
            this.mCanbus = (ICanBusService) this.mContext.getSystemService("canbus");
        }
        regReceiver();
    }

    public void closeSrcCanbus() {
        sendSrcToCan(0, 17);
        sendMediaToCan(0, 0, 0, 0, 0, 0);
    }

    public void ReportStatusToCan(int fileNum, int min, int sec) {
        sendSrcToCan(8, 17);
        sendMediaToCan(1, 0, (byte) (fileNum & 255), (byte) ((fileNum >> 8) & 255), min, sec);
    }

    private void sendSrcToCan(int src, int type) {
        byte[] data = {-64, 2, (byte) src, (byte) type};
        try {
            if (!this.mBtPhoneStatus) {
                this.mCanbus.sendPacketForCanbus(data);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendMediaToCan(int d1, int d2, int d3, int d4, int d5, int d6) {
        byte[] data = {-61, 6, (byte) d1, (byte) d2, (byte) d3, (byte) d4, (byte) d5, (byte) d6};
        try {
            if (!this.mBtPhoneStatus) {
                this.mCanbus.sendPacketForCanbus(data);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.bts.device.action.DISCONNECT");
        filter.addAction("android.bts.device.action.PHONE_HANGUP");
        filter.addAction("android.bts.device.action.PHONE_CALLOUT");
        filter.addAction("android.bts.device.action.PHONE_CALLIN");
        this.mContext.registerReceiver(this.mReverseReceiver, filter);
    }
}
