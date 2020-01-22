package cn.com.cs2c.musicplayer.canbus;

import android.content.Context;
import android.cs2c.ICanBusService;
import android.os.RemoteException;

public class opel implements ICanbus {
    private ICanBusService mCanbus;
    private Context mContext;

    public void SetContext(Context ctx) {
        this.mContext = ctx;
        if (this.mContext != null) {
            this.mCanbus = (ICanBusService) this.mContext.getSystemService("canbus");
        }
    }

    public void closeSrcCanbus() {
        sendTextToCan("CLOSE");
    }

    public void ReportStatusToCan(int fileNum, int min, int sec) {
        sendTextToCan("MUSIC");
        byte b = (byte) (fileNum & 255);
        byte b2 = (byte) ((fileNum >> 8) & 255);
        int i = min;
        int i2 = sec;
        if (fileNum > 9999) {
            fileNum = 9999;
        }
        sendTextToCan("MP3 " + fileNum);
    }

    private void sendTextToCan(String str) {
        int sum = 0;
        byte[] data = new byte[13];
        byte[] data2 = str.getBytes();
        data[0] = -3;
        data[1] = 12;
        data[2] = 0;
        for (int i = 0; i < data2.length; i++) {
            data[i + 3] = data2[i];
        }
        for (int i2 = 1; i2 < 11; i2++) {
            sum += data[i2];
        }
        data[11] = (byte) (sum / 256);
        data[12] = (byte) (sum & 255);
        try {
            this.mCanbus.sendEx(data);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
