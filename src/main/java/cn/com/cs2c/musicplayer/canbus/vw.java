package cn.com.cs2c.musicplayer.canbus;

import android.content.Context;
import android.cs2c.ICanBusService;
import android.os.RemoteException;

public class vw implements ICanbus {
    private ICanBusService mCanbus;
    private Context mContext;

    public void SetContext(Context ctx) {
        this.mContext = ctx;
        if (this.mContext != null) {
            this.mCanbus = (ICanBusService) this.mContext.getSystemService("canbus");
        }
    }

    private void sendSrcToCan(int src, int type) {
        try {
            this.mCanbus.sendPacketForCanbus(new byte[]{-64, 2, (byte) src, (byte) type});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendMediaToCan(int d1, int d2, int d3, int d4, int d5, int d6) {
        try {
            this.mCanbus.sendPacketForCanbus(new byte[]{-61, 6, (byte) d1, (byte) d2, (byte) d3, (byte) d4, (byte) d5, (byte) d6});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void closeSrcCanbus() {
        sendSrcToCan(0, 17);
        sendMediaToCan(0, 0, 0, 0, 0, 0);
    }

    public void ReportStatusToCan(int fileNum, int min, int sec) {
        sendSrcToCan(8, 17);
        sendMediaToCan(1, 0, (byte) (fileNum & 255), (byte) ((fileNum >> 8) & 255), min, sec);
    }
}
