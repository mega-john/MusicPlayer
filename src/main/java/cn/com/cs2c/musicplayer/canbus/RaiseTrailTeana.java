package cn.com.cs2c.musicplayer.canbus;

import android.content.Context;
import android.cs2c.ICanBusService;
import android.os.RemoteException;

public class RaiseTrailTeana implements ICanbus {
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
            this.mCanbus.sendPacketForCanbus(new byte[]{-64, 8, (byte) src, (byte) type, 0, 0, 0, 0, 0, 0});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void sendMediaToCan(int d1, int d2, int d3, int d4, int d5, int d6, boolean bClose) {
        int i = 0;
        byte[] data = new byte[10];
        data[0] = -64;
        data[1] = 8;
        data[2] = (byte) (bClose ? 0 : 8);
        if (!bClose) {
            i = 17;
        }
        data[3] = (byte) i;
        data[4] = (byte) d1;
        data[5] = (byte) d2;
        data[6] = (byte) d3;
        data[7] = (byte) d4;
        data[8] = (byte) d5;
        data[9] = (byte) d6;
        try {
            this.mCanbus.sendPacketForCanbus(data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void closeSrcCanbus() {
        sendSrcToCan(0, 0);
        sendMediaToCan(0, 0, 0, 0, 0, 0, true);
    }

    public void ReportStatusToCan(int fileNum, int min, int sec) {
        sendSrcToCan(8, 17);
        sendMediaToCan(1, 0, (byte) (fileNum & 255), (byte) ((fileNum >> 8) & 255), min, sec, false);
    }
}
