package cn.com.cs2c.musicplayer.canbus;

import android.content.Context;
import android.cs2c.ICanBusService;
import android.os.RemoteException;

public class golf implements ICanbus {
    private ICanBusService mCanbus;
    private Context mContext;

    public void SetContext(Context ctx) {
        this.mContext = ctx;
        if (this.mContext != null) {
            this.mCanbus = (ICanBusService) this.mContext.getSystemService("canbus");
        }
    }

    private void sendMediaToCan(int src, int type, int i1, int i2, int i3, int i4, int i5, int i6) {
        try {
            this.mCanbus.sendPacketForCanbus(new byte[]{-64, 8, (byte) src, (byte) type, (byte) i1, (byte) i2, (byte) i3, (byte) i4, (byte) i5, (byte) i6});
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void closeSrcCanbus() {
        sendMediaToCan(0, 17, 0, 0, 0, 0, 0, 0);
    }

    public void ReportStatusToCan(int fileNum, int min, int sec) {
        sendMediaToCan(8, 17, 1, 0, (byte) (fileNum & 255), (byte) ((fileNum >> 8) & 255), min, sec);
    }
}
