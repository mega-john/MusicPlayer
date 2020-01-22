package cn.com.cs2c.musicplayer.canbus;

import android.content.Context;

public interface ICanbus {
    void ReportStatusToCan(int i, int i2, int i3);

    void SetContext(Context context);

    void closeSrcCanbus();
}
