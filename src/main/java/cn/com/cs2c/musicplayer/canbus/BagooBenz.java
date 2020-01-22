package cn.com.cs2c.musicplayer.canbus;

import android.content.Context;
import android.cs2c.BTManager;
import android.cs2c.ICanBusService;
import android.cs2c.ISystemAppService;
import android.os.RemoteException;

public class BagooBenz implements ICanbus {
    private BTManager mBTManager;
    private ICanBusService mCanbus;
    private Context mContext;
    private ISystemAppService mSystemAppService;

    public void SetContext(Context ctx) {
        this.mContext = ctx;
        if (this.mContext != null) {
            this.mCanbus = (ICanBusService) this.mContext.getSystemService("canbus");
//            this.mSystemAppService = (ISystemAppService) this.mContext.getSystemService("systemapp");
            this.mBTManager = (BTManager) this.mContext.getSystemService("VehicleBT");
        }
    }

    public void closeSrcCanbus() {
        sendMediaToCan(15, 255, 255, 255, 255);
    }

    public void ReportStatusToCan(int fileNum, int min, int sec) {
        sendMediaToCan(4, (byte) (fileNum & 255), (byte) ((fileNum >> 8) & 255), min, sec);
    }

    private void sendMediaToCan(int src, int i1, int i2, int i3, int i4) {
        byte state = 0;
        byte[] data = new byte[11];
        data[0] = -126;
        data[1] = 9;
        data[2] = (byte) src;
        data[3] = -1;
        data[4] = -1;
        data[5] = (byte) i2;
        data[6] = (byte) i1;
        data[7] = -1;
        try {
            if (this.mSystemAppService.getMuteStatus()) {
                state = (byte) 8;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (this.mBTManager.isBTConnected()) {
            state = (byte) (state | 4);
        }
        data[8] = state;
        data[9] = (byte) i3;
        data[10] = (byte) i4;
        try {
            this.mCanbus.sendPacketForCanbus(data);
        } catch (RemoteException e2) {
            e2.printStackTrace();
        }
    }
}
