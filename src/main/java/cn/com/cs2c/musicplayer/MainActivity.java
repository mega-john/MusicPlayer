package cn.com.cs2c.musicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.cs2c.ISystemAppService;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import cn.com.cs2c.musicplayer.canbus.BagooAudi;
import cn.com.cs2c.musicplayer.canbus.BagooBenz;
import cn.com.cs2c.musicplayer.canbus.ICanbus;
import cn.com.cs2c.musicplayer.canbus.RaiseTrailTeana;
import cn.com.cs2c.musicplayer.canbus.RaiseVw;
import cn.com.cs2c.musicplayer.canbus.SimpleHyundai;
import cn.com.cs2c.musicplayer.canbus.crv;
import cn.com.cs2c.musicplayer.canbus.golf;
import cn.com.cs2c.musicplayer.canbus.opel;
import cn.com.cs2c.musicplayer.canbus.vw;

public class MainActivity extends Activity implements AudioManager.OnAudioFocusChangeListener, Handler.Callback, View.OnClickListener, View.OnTouchListener, AbsListView.OnScrollListener, AdapterView.OnItemClickListener, SeekBar.OnSeekBarChangeListener {
    public static boolean firstTimeRunFlag = true;
    private SimpleAdapter mAdapterAlbum;
    private SimpleAdapter mAdapterAlbumTrack;
    private SimpleAdapter mAdapterArtist;
    private SimpleAdapter mAdapterArtistTrack;
    private SimpleAdapter mAdapterLrc;
    private SimpleAdapter mAdapterTrack;
    private int mAlbumCount;
    private int mAlbumFirstVisibleItemPosition;
    private int mAlbumLastVisibleItemPosition;
    private int mAlbumSelectionPosition;
    private boolean mAlbumSelectionValid;
    private int mAlbumTrackCount;
    private int mArtistCount;
    private int mArtistSelectionPosition;
    private boolean mArtistSelectionValid;
    private int mArtistTrackCount;
    private AudioBroadcastReceiver mAudioBroadcastReceiver;
    private AudioManager mAudioManager;
    private Bundle mBundle;
    private Button mButtonPopupSearch;
    private ICanbus mCanbusHelper;
    private int mCategoryIndex;
    private int mDisplayHeight;
    private DisplayMetrics mDisplayMetrics;
    private int mDisplayWidth;
    private EditText mEditTextPopupSearch;
    private EditText mEditTextQuickSearch;
    private ListView mGridViewAlbumList;
    private ListView mGridViewAlbumTrackList;
    private ListView mGridViewArtistList;
    private ListView mGridViewArtistTrackList;
    private ListView mGridViewTrackList;
    private Handler mHandlerMain;
    private ImageView mImageViewHeadlineFunction;
    private ImageView mImageViewHeadlineIcon;
    private ImageView mImageViewPlayLRC;
    private ImageView mImageViewPlayList;
    private ImageView mImageViewPlayLoop;
    private ImageView mImageViewPlayNext;
    private ImageView mImageViewPlayPause;
    private ImageView mImageViewPlayPrevious;
    private ImageView mImageViewPlayShuffle;
    private ImageView mImageViewPopupCategory;
    private ImageView mImageViewPopupCategoryAlbum;
    private ImageView mImageViewPopupCategoryArtist;
    private ImageView mImageViewPopupCategoryPlaying;
    private ImageView mImageViewPopupCategoryTrack;
    private ImageView mImageViewPopupSearch;
    private ImageView mImageViewPopupSetting;
    private ImageView mImageViewPopupSettingPartyShuffle;
    private ImageView mImageViewPopupSettingPlayAll;
    private ImageView mImageViewPopupSettingShuffleAll;
    private ImageView mImageViewPopupSort;
    private ImageView mImageViewPopupSortSize;
    private ImageView mImageViewPopupSortTime;
    private ImageView mImageViewPopupSortTitle;
    private ImageView mImageViewPopupSortType;
    private ImageView mImageViewQuickSearch;
    private ImageView mImageViewStartEq;
    private ImageView mImageViewThumbnail;
    private InputMethodManager mInputMethodManager;
    private Intent mIntent;
    private IntentFilter mIntentFilter;
    private int mKnobLeftCount;
    private int mKnobRightCount;
    private List<Map<String, Object>> mListAlbum;
    private List<Map<String, Object>> mListAlbumBackup;
    private List<Map<String, Object>> mListAlbumTrack;
    private List<Map<String, Object>> mListAlbumTrackBackup;
    private List<Map<String, Object>> mListArtist;
    private List<Map<String, Object>> mListArtistBackup;
    private List<Map<String, Object>> mListArtistTrack;
    private List<Map<String, Object>> mListArtistTrackBackup;
    private List<String> mListPlayTrack;
    private List<Map<String, Object>> mListTrack;
    private List<Map<String, Object>> mListTrackBackup;
    private ListView mListViewLrc;
    private boolean mLrcFlag;
    private List<String> mLrcKey;
    private List<Map<String, Object>> mLrcValue;
    private MediaBroadcastReceiver mMediaBroadcastReceiver;
    private MediaManager mMediaManager;
    private Message mMessage;
    private PanelBroadcastReceiver mPanelBroadcastReceiver;
    private int mParentIndex;
    private int mPlayPosition;
    private PopupWindow mPopupWindow;
    private boolean mProcessPanelKeyFlag;
    private Bitmap mRecordBitmap;
    private int mRecordCount;
    private int mRecordIndex;
    private Map<String, Object> mRecordMap;
    private String mRecordName;
    private String mRecordPath;
    private int mRecordSize;
    private String mRecordTime;
    private String mRecordType;
    private int mRecoverCount;
    private int mRecoverState;
    private Boolean mRestoreValid;
    private ISystemAppService mSAS;
    private SeekBar mSeekBarPosition;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;
    private TextView mTextViewHeadlineName;
    private TextView mTextViewLength;
    private TextView mTextViewParentTitle;
    private TextView mTextViewPopupCategory;
    private TextView mTextViewPopupCategoryAlbum;
    private TextView mTextViewPopupCategoryArtist;
    private TextView mTextViewPopupCategoryPlaying;
    private TextView mTextViewPopupCategoryTrack;
    private TextView mTextViewPopupSearch;
    private TextView mTextViewPopupSetting;
    private TextView mTextViewPopupSettingPartyShuffle;
    private TextView mTextViewPopupSettingPlayAll;
    private TextView mTextViewPopupSettingShuffleAll;
    private TextView mTextViewPopupSort;
    private TextView mTextViewPopupSortSize;
    private TextView mTextViewPopupSortTime;
    private TextView mTextViewPopupSortTitle;
    private TextView mTextViewPopupSortType;
    private TextView mTextViewProgress;
    private TextView mTextViewTrackTitle;
    private int mTrackCount;
    private int mTrackFirstVisibleItemPosition;
    private int mTrackIndex;
    private int mTrackLastVisibleItemPosition;
    private View mViewAlbumList;
    private View mViewAlbumTrackList;
    private View mViewArtistList;
    private View mViewArtistTrackList;
    private ViewFlipper mViewFlipperList;
    private ViewFlipper mViewFlipperMain;
    private ViewFlipper mViewFlipperPlay;
    private ViewFlipper mViewFlipperPopupContent;
    private View mViewHint;
    private View mViewList;
    private View mViewPlay;
    private boolean mViewPlayValid;
    private View mViewPopup;
    private View mViewPopupCategory;
    private View mViewPopupSearch;
    private View mViewPopupSetting;
    private View mViewPopupSort;
    private View mViewTrackList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setEnvironment();
//        loadCanbus();
    }

    private void MediaManagerSendEmptyMessage(int msg) {
        if (this.mMediaManager == null || this.mMediaManager.getHandler() == null)
            return;

        this.mMediaManager.getHandler().sendEmptyMessage(msg);
    }

    private void MediaManagerSendMessage(int what, int arg1) {
        this.MediaManagerSendMessage(what, arg1, 0, null);
    }

    private void MediaManagerSendMessage(int what, int arg1, Object obj) {
        this.MediaManagerSendMessage(what, arg1, 0, obj);
    }

    private void MediaManagerSendMessage(int what, int arg1, int arg2) {
        this.MediaManagerSendMessage(what, arg1, arg2, null);
    }

    private void MediaManagerSendMessage(int what, int arg1, int arg2, Object obj) {
        if (this.mMediaManager == null || this.mMediaManager.getHandler() == null)
            return;

        this.mMessage = Message.obtain();
        this.mMessage.what = what;
        this.mMessage.arg1 = arg1;
        this.mMessage.arg1 = arg2;
        this.mMessage.obj = obj;
        this.mMediaManager.getHandler().sendMessage(this.mMessage);
    }

    protected void onResume() {
        super.onResume();
        this.mEditTextQuickSearch.setFocusable(false);
        this.mEditTextQuickSearch.setFocusableInTouchMode(false);
        this.mIntent = new Intent("cn.com.cs2c.android.vehicle.action.PAUSE_MUSIC");
        this.mIntent.putExtra("mPackageName", getPackageName());
        sendBroadcast(this.mIntent);
        this.mAudioManager.requestAudioFocus(this, 3, 1);
        try {
//            this.mSAS.requestRespondPanelKey(getPackageName());
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onResume " + e.getMessage());
        }
        if (this.mViewPlayValid) {
            MediaManagerSendEmptyMessage(Constants.RequestResumeTrack);
        }
        if (this.mCanbusHelper != null) {
            this.mCanbusHelper.ReportStatusToCan(0, 0, 0);
        }
    }

    public void onBackPressed() {
        switch (this.mViewFlipperMain.getDisplayedChild()) {
            case 0:
                switch (this.mViewFlipperList.getDisplayedChild()) {
                    case 0:
                    case 1:
                    case 2:
                        this.MediaManagerSendEmptyMessage(Constants.RequestCloseDatabase);
                        break;
                    case 3:
                        this.mViewFlipperList.showPrevious();
                        this.mViewFlipperList.showPrevious();
                        break;
                    case 4:
                        this.mViewFlipperList.showPrevious();
                        this.mViewFlipperList.showPrevious();
                        break;
                }
                updateListHeadline();
                return;
            case 1:
                this.mViewFlipperMain.showPrevious();
                return;
            default:
                return;
        }
    }

    protected void onDestroy() {
        if (this.mCanbusHelper != null) {
            this.mCanbusHelper.closeSrcCanbus();
        }
        this.mPopupWindow.dismiss();
        this.mAudioManager.abandonAudioFocus(this);
        unregisterReceiver(this.mAudioBroadcastReceiver);
        unregisterReceiver(this.mPanelBroadcastReceiver);
        unregisterReceiver(this.mMediaBroadcastReceiver);
        super.onDestroy();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /* JADX WARNING: type inference failed for: r11v0, types: [cn.com.cs2c.musicplayer.MainActivity] */
    public void onClick(View view) {
        try {
            if (view.getId() != R.id.EditTextQuickSearch && this.mInputMethodManager.isActive(this.mEditTextQuickSearch)) {
                this.mInputMethodManager.hideSoftInputFromWindow(this.mEditTextQuickSearch.getWindowToken(), 2);
            }
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onClick " + e.getMessage());
        }
        try {
            if (view.getId() != R.id.EditTextPopupSearch && this.mInputMethodManager.isActive(this.mEditTextPopupSearch)) {
                this.mInputMethodManager.hideSoftInputFromWindow(this.mEditTextPopupSearch.getWindowToken(), 2);
            }
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onClick " + e.getMessage());
        }
        switch (view.getId()) {
            case R.id.ImageViewHeadlineIcon /*2131165200*/:
                if (this.mViewFlipperList.getCurrentView().getId() == R.id.LinearLayoutArtistTrackList ||
                        this.mViewFlipperList.getCurrentView().getId() == R.id.LinearLayoutAlbumTrackList) {
                    onBackPressed();
                    return;
                }
                while (this.mViewFlipperList.getCurrentView().getId() != R.id.LinearLayoutTrackList) {
                    this.mViewFlipperList.showNext();
                }
                updateListHeadline();
                return;
            case R.id.EditTextQuickSearch /*2131165202*/:
                this.mEditTextQuickSearch.setFocusable(true);
                this.mEditTextQuickSearch.setFocusableInTouchMode(true);
                this.mEditTextQuickSearch.clearFocus();
                this.mEditTextQuickSearch.requestFocus();
                this.mInputMethodManager.showSoftInput(this.mEditTextQuickSearch, 1);
                return;
            case R.id.ImageViewQuickSearch /*2131165203*/:
                this.mEditTextPopupSearch.setText(this.mEditTextQuickSearch.getText().toString());
                onClick(this.mButtonPopupSearch);
                return;
            case R.id.ImageViewHeadlineFunction /*2131165204*/:
                this.mImageViewPopupCategory.setImageResource(R.drawable.popup_category_over);
                this.mImageViewPopupSort.setImageResource(R.drawable.popup_sort);
                this.mImageViewPopupSearch.setImageResource(R.drawable.popup_search);
                this.mImageViewPopupSetting.setImageResource(R.drawable.popup_setting);
                this.mTextViewPopupCategory.setTextColor(-16742657);
                this.mTextViewPopupSort.setTextColor(-1);
                this.mTextViewPopupSearch.setTextColor(-1);
                this.mTextViewPopupSetting.setTextColor(-1);
                this.mPopupWindow.showAtLocation(this.mViewList, 17, 0, 0);
                while (this.mViewFlipperPopupContent.getCurrentView().getId() != this.mViewPopupCategory.getId()) {
                    this.mViewFlipperPopupContent.showNext();
                }
                return;
            case R.id.ImageViewPlayShuffle /*2131165224*/:
                MediaManagerSendEmptyMessage(Constants.RequestPlayShuffle);
                return;
            case R.id.ImageViewPlayLoop /*2131165225*/:
                MediaManagerSendEmptyMessage(Constants.RequestPlayLoop);
                return;
            case R.id.ImageViewPlayPrevious /*2131165226*/:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.RequestPlayPrevious;
//                this.mMessage.arg1 = 1;
                this.MediaManagerSendMessage(Constants.RequestPlayPrevious, 1);
                return;
            case R.id.ImageViewPlayPause /*2131165227*/:
                MediaManagerSendEmptyMessage(Constants.RequestPlayPause);
                return;
            case R.id.ImageViewPlayNext /*2131165228*/:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.RequestPlayNext;
//                this.mMessage.arg1 = 1;
                this.MediaManagerSendMessage(Constants.RequestPlayNext, 1);
                return;
            case R.id.ImageViewPlayList /*2131165229*/:
                this.mViewFlipperMain.showPrevious();
                return;
            case R.id.ImageViewPlayLRC /*2131165234*/:
                if (this.mViewFlipperPlay.getCurrentView().getId() == R.id.ImageViewThumbnail) {
                    this.mImageViewPlayLRC.setImageResource(R.drawable.play_lrc_over);
                } else {
                    this.mImageViewPlayLRC.setImageResource(R.drawable.play_lrc);
                }
                this.mViewFlipperPlay.showNext();
                return;
            case R.id.ImageViewPopupCategory /*2131165238*/:
            case R.id.TextViewPopupCategory /*2131165239*/:
                this.mImageViewPopupCategory.setImageResource(R.drawable.popup_category_over);
                this.mImageViewPopupSort.setImageResource(R.drawable.popup_sort);
                this.mImageViewPopupSearch.setImageResource(R.drawable.popup_search);
                this.mImageViewPopupSetting.setImageResource(R.drawable.popup_setting);
                this.mTextViewPopupCategory.setTextColor(-16742657);
                this.mTextViewPopupSort.setTextColor(-1);
                this.mTextViewPopupSearch.setTextColor(-1);
                this.mTextViewPopupSetting.setTextColor(-1);
                while (this.mViewFlipperPopupContent.getCurrentView().getId() != this.mViewPopupCategory.getId()) {
                    this.mViewFlipperPopupContent.showNext();
                }
                return;
            case R.id.ImageViewPopupSort /*2131165241*/:
            case R.id.TextViewPopupSort /*2131165242*/:
                this.mImageViewPopupCategory.setImageResource(R.drawable.popup_category);
                this.mImageViewPopupSort.setImageResource(R.drawable.popup_sort_over);
                this.mImageViewPopupSearch.setImageResource(R.drawable.popup_search);
                this.mImageViewPopupSetting.setImageResource(R.drawable.popup_setting);
                this.mTextViewPopupCategory.setTextColor(-1);
                this.mTextViewPopupSort.setTextColor(-16742657);
                this.mTextViewPopupSearch.setTextColor(-1);
                this.mTextViewPopupSetting.setTextColor(-1);
                while (this.mViewFlipperPopupContent.getCurrentView().getId() != this.mViewPopupSort.getId()) {
                    this.mViewFlipperPopupContent.showNext();
                }
                return;
            case R.id.ImageViewPopupSearch /*2131165244*/:
            case R.id.TextViewPopupSearch /*2131165245*/:
                this.mImageViewPopupCategory.setImageResource(R.drawable.popup_category);
                this.mImageViewPopupSort.setImageResource(R.drawable.popup_sort);
                this.mImageViewPopupSearch.setImageResource(R.drawable.popup_search_over);
                this.mImageViewPopupSetting.setImageResource(R.drawable.popup_setting);
                this.mTextViewPopupCategory.setTextColor(-1);
                this.mTextViewPopupSort.setTextColor(-1);
                this.mTextViewPopupSearch.setTextColor(-16742657);
                this.mTextViewPopupSetting.setTextColor(-1);
                while (this.mViewFlipperPopupContent.getCurrentView().getId() != this.mViewPopupSearch.getId()) {
                    this.mViewFlipperPopupContent.showNext();
                }
                return;
            case R.id.ImageViewPopupSetting /*2131165247*/:
            case R.id.TextViewPopupSetting /*2131165248*/:
                this.mImageViewPopupCategory.setImageResource(R.drawable.popup_category);
                this.mImageViewPopupSort.setImageResource(R.drawable.popup_sort);
                this.mImageViewPopupSearch.setImageResource(R.drawable.popup_search);
                this.mImageViewPopupSetting.setImageResource(R.drawable.popup_setting_over);
                this.mTextViewPopupCategory.setTextColor(-1);
                this.mTextViewPopupSort.setTextColor(-1);
                this.mTextViewPopupSearch.setTextColor(-1);
                this.mTextViewPopupSetting.setTextColor(-16742657);
                while (this.mViewFlipperPopupContent.getCurrentView().getId() != this.mViewPopupSetting.getId()) {
                    this.mViewFlipperPopupContent.showNext();
                }
                return;
            case R.id.ImageViewPopupCategoryPlaying /*2131165253*/:
            case R.id.TextViewPopupCategoryPlaying /*2131165258*/:
                this.mPopupWindow.dismiss();
                if (this.mViewPlayValid) {
                    this.mViewFlipperMain.showNext();
                    return;
                }
                return;
            case R.id.ImageViewPopupCategoryTrack /*2131165254*/:
            case R.id.TextViewPopupCategoryTrack /*2131165259*/:
                this.mPopupWindow.dismiss();
                if (this.mListTrack.size() != this.mListTrackBackup.size()) {
                    this.mListTrack.clear();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListTrackBackup.size()) {
                        this.mListTrack.add(this.mListTrackBackup.get(this.mRecordIndex));
                        this.mRecordIndex++;
                    }
                    this.mAdapterTrack.notifyDataSetChanged();
                }
                while (this.mViewFlipperList.getCurrentView().getId() != this.mViewTrackList.getId()) {
                    this.mViewFlipperList.showNext();
                }
                updateListHeadline();
                return;
            case R.id.ImageViewPopupCategoryArtist /*2131165255*/:
            case R.id.TextViewPopupCategoryArtist /*2131165260*/:
                this.mPopupWindow.dismiss();
                if (this.mListArtist.size() != this.mListArtistBackup.size()) {
                    this.mListArtist.clear();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListArtistBackup.size()) {
                        this.mListArtist.add(this.mListArtistBackup.get(this.mRecordIndex));
                        this.mRecordIndex++;
                    }
                    this.mAdapterArtist.notifyDataSetChanged();
                }
                while (this.mViewFlipperList.getCurrentView().getId() != this.mViewArtistList.getId()) {
                    this.mViewFlipperList.showNext();
                }
                updateListHeadline();
                return;
            case R.id.ImageViewPopupCategoryAlbum /*2131165256*/:
            case R.id.TextViewPopupCategoryAlbum /*2131165261*/:
                this.mPopupWindow.dismiss();
                if (this.mListAlbum.size() != this.mListAlbumBackup.size()) {
                    this.mListAlbum.clear();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListAlbumBackup.size()) {
                        this.mListAlbum.add(this.mListAlbumBackup.get(this.mRecordIndex));
                        this.mRecordIndex++;
                    }
                    this.mAdapterAlbum.notifyDataSetChanged();
                }
                while (this.mViewFlipperList.getCurrentView().getId() != this.mViewAlbumList.getId()) {
                    this.mViewFlipperList.showNext();
                }
                updateListHeadline();
                return;
            case R.id.EditTextPopupSearch /*2131165263*/:
                this.mInputMethodManager.showSoftInput(this.mEditTextQuickSearch, 1);
                return;
            case R.id.ButtonPopupSearch /*2131165264*/:
                this.mEditTextQuickSearch.setText(this.mEditTextPopupSearch.getText().toString());
                if (this.mEditTextPopupSearch.getText().toString().isEmpty()) {
                    switch (this.mViewFlipperList.getCurrentView().getId()) {
                        case R.id.LinearLayoutAlbumList /*2131165184*/:
                            if (this.mListAlbum.size() != this.mListAlbumBackup.size()) {
                                this.mListAlbum.clear();
                                this.mListAlbum.addAll(this.mListAlbumBackup);
                                this.mAdapterAlbum.notifyDataSetChanged();
                                this.mAlbumCount = this.mListAlbum.size();
                                break;
                            }
                            break;
                        case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                            if (this.mListAlbumTrack.size() != this.mListAlbumTrackBackup.size()) {
                                this.mListAlbumTrack.clear();
                                this.mListAlbumTrack.addAll(this.mListAlbumTrackBackup);
                                this.mAdapterAlbumTrack.notifyDataSetChanged();
                                this.mAlbumTrackCount = this.mListAlbumTrack.size();
                                break;
                            }
                            break;
                        case R.id.LinearLayoutArtistList /*2131165188*/:
                            if (this.mListArtist.size() != this.mListArtistBackup.size()) {
                                this.mListArtist.clear();
                                this.mListArtist.addAll(this.mListArtistBackup);
                                this.mAdapterArtist.notifyDataSetChanged();
                                this.mArtistCount = this.mListArtist.size();
                                break;
                            }
                            break;
                        case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                            if (this.mListArtistTrack.size() != this.mListArtistTrackBackup.size()) {
                                this.mListArtistTrack.clear();
                                this.mListArtistTrack.addAll(this.mListArtistTrackBackup);
                                this.mAdapterArtistTrack.notifyDataSetChanged();
                                this.mArtistTrackCount = this.mListArtistTrack.size();
                                break;
                            }
                            break;
                        case R.id.LinearLayoutTrackList /*2131165284*/:
                            if (this.mListTrack.size() != this.mListTrackBackup.size()) {
                                this.mListTrack.clear();
                                this.mListTrack.addAll(this.mListTrackBackup);
                                this.mAdapterTrack.notifyDataSetChanged();
                                this.mTrackCount = this.mListTrack.size();
                                break;
                            }
                            break;
                    }
                } else {
                    this.mRecordName = this.mEditTextPopupSearch.getText().toString();
                    switch (this.mViewFlipperList.getCurrentView().getId()) {
                        case R.id.LinearLayoutAlbumList /*2131165184*/:
                            this.mListAlbum.clear();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListAlbumBackup.size()) {
                                if (this.mListAlbumBackup.get(this.mRecordIndex).get("ListItemName").toString().toLowerCase().contains(this.mRecordName.toLowerCase())) {
                                    this.mListAlbum.add(this.mListAlbumBackup.get(this.mRecordIndex));
                                }
                                this.mRecordIndex++;
                            }
                            this.mAdapterAlbum.notifyDataSetChanged();
                            this.mAlbumCount = this.mListAlbum.size();
                            break;
                        case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                            this.mListAlbumTrack.clear();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListAlbumTrackBackup.size()) {
                                if (this.mListAlbumTrackBackup.get(this.mRecordIndex).get("ListItemName").toString().toLowerCase().contains(this.mRecordName.toLowerCase())) {
                                    this.mListAlbumTrack.add(this.mListAlbumTrackBackup.get(this.mRecordIndex));
                                }
                                this.mRecordIndex++;
                            }
                            this.mAdapterAlbumTrack.notifyDataSetChanged();
                            this.mAlbumTrackCount = this.mListAlbumTrack.size();
                            break;
                        case R.id.LinearLayoutArtistList /*2131165188*/:
                            this.mListArtist.clear();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListArtistBackup.size()) {
                                if (this.mListArtistBackup.get(this.mRecordIndex).get("ListItemName").toString().toLowerCase().contains(this.mRecordName.toLowerCase())) {
                                    this.mListArtist.add(this.mListArtistBackup.get(this.mRecordIndex));
                                }
                                this.mRecordIndex++;
                            }
                            this.mAdapterArtist.notifyDataSetChanged();
                            this.mArtistCount = this.mListArtist.size();
                            break;
                        case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                            this.mListArtistTrack.clear();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListArtistTrackBackup.size()) {
                                if (this.mListArtistTrackBackup.get(this.mRecordIndex).get("ListItemName").toString().toLowerCase().contains(this.mRecordName.toLowerCase())) {
                                    this.mListArtistTrack.add(this.mListArtistTrackBackup.get(this.mRecordIndex));
                                }
                                this.mRecordIndex++;
                            }
                            this.mAdapterArtistTrack.notifyDataSetChanged();
                            this.mArtistTrackCount = this.mListArtistTrack.size();
                            break;
                        case R.id.LinearLayoutTrackList /*2131165284*/:
                            this.mListTrack.clear();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListTrackBackup.size()) {
                                if (this.mListTrackBackup.get(this.mRecordIndex).get("ListItemName").toString().toLowerCase().contains(this.mRecordName.toLowerCase())) {
                                    this.mListTrack.add(this.mListTrackBackup.get(this.mRecordIndex));
                                }
                                this.mRecordIndex++;
                            }
                            this.mAdapterTrack.notifyDataSetChanged();
                            this.mTrackCount = this.mListTrack.size();
                            break;
                    }
                }
                updateListHeadline();
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSettingPlayAll /*2131165266*/:
            case R.id.TextViewPopupSettingPlayAll /*2131165270*/:
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        if (this.mListAlbumTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListAlbumTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListAlbumTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayAlbumTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayAlbumTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        if (this.mListArtistTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListArtistTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListArtistTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayArtistTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayArtistTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        if (this.mListTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSettingPartyShuffle /*2131165267*/:
            case R.id.TextViewPopupSettingPartyShuffle /*2131165271*/:
                int randomInteger = Math.abs(new Random().nextInt());
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        if (this.mListAlbumTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListAlbumTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListAlbumTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
                            Collections.shuffle(this.mListPlayTrack);
                            this.mRecordIndex = (randomInteger % this.mListTrack.size()) + 1;
                            while (this.mRecordIndex < this.mListPlayTrack.size()) {
                                this.mListPlayTrack.remove(this.mRecordIndex);
                                this.mRecordIndex++;
                            }
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayAlbumTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayAlbumTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        if (this.mListArtistTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListArtistTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListArtistTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
                            Collections.shuffle(this.mListPlayTrack);
                            this.mRecordIndex = (randomInteger % this.mListTrack.size()) + 1;
                            while (this.mRecordIndex < this.mListPlayTrack.size()) {
                                this.mListPlayTrack.remove(this.mRecordIndex);
                                this.mRecordIndex++;
                            }
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayArtistTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayArtistTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        if (this.mListTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
                            Collections.shuffle(this.mListPlayTrack);
                            this.mRecordIndex = (randomInteger % this.mListTrack.size()) + 1;
                            while (this.mRecordIndex < this.mListPlayTrack.size()) {
                                this.mListPlayTrack.remove(this.mRecordIndex);
                                this.mRecordIndex++;
                            }
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSettingShuffleAll /*2131165268*/:
            case R.id.TextViewPopupSettingShuffleAll /*2131165272*/:
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        if (this.mListAlbumTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListAlbumTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListAlbumTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
                            Collections.shuffle(this.mListPlayTrack);
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayAlbumTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayAlbumTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        if (this.mListArtistTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListArtistTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListArtistTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
                            Collections.shuffle(this.mListPlayTrack);
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayArtistTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayArtistTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        if (this.mListTrack.size() > 0) {
                            this.mViewPlayValid = true;
                            this.mListPlayTrack = new ArrayList();
                            this.mRecordIndex = 0;
                            while (this.mRecordIndex < this.mListTrack.size()) {
                                this.mListPlayTrack.add((String) this.mListTrack.get(this.mRecordIndex).get("ListItemPath"));
                                this.mRecordIndex++;
                            }
                            Collections.shuffle(this.mListPlayTrack);
//                            this.mMessage = Message.obtain();
//                            this.mMessage.what = Constants.RequestPlayTrack;
//                            this.mMessage.arg1 = 0;
//                            this.mMessage.obj = this.mListPlayTrack;
                            this.MediaManagerSendMessage(Constants.RequestPlayTrack, 0, this.mListPlayTrack);
                            this.mViewFlipperMain.showNext();
                            break;
                        }
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSortTitle /*2131165275*/:
            case R.id.TextViewPopupSortTitle /*2131165280*/:
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumList /*2131165184*/:
                        Collections.sort(this.mListAlbum, new NameComparator());
                        this.mAdapterAlbum.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        Collections.sort(this.mListAlbumTrack, new NameComparator());
                        this.mAdapterAlbumTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutArtistList /*2131165188*/:
                        Collections.sort(this.mListArtist, new NameComparator());
                        this.mAdapterArtist.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        Collections.sort(this.mListArtistTrack, new NameComparator());
                        this.mAdapterArtistTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        Collections.sort(this.mListTrack, new NameComparator());
                        this.mAdapterTrack.notifyDataSetChanged();
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSortType /*2131165276*/:
            case R.id.TextViewPopupSortType /*2131165281*/:
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        Collections.sort(this.mListAlbumTrack, new TypeComparator());
                        this.mAdapterAlbumTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        Collections.sort(this.mListArtistTrack, new TypeComparator());
                        this.mAdapterArtistTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        Collections.sort(this.mListTrack, new TypeComparator());
                        this.mAdapterTrack.notifyDataSetChanged();
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSortSize /*2131165277*/:
            case R.id.TextViewPopupSortSize /*2131165282*/:
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        Collections.sort(this.mListAlbumTrack, new SizeComparator());
                        this.mAdapterAlbumTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        Collections.sort(this.mListArtistTrack, new SizeComparator());
                        this.mAdapterArtistTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        Collections.sort(this.mListTrack, new SizeComparator());
                        this.mAdapterTrack.notifyDataSetChanged();
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewPopupSortTime /*2131165278*/:
            case R.id.TextViewPopupSortTime /*2131165283*/:
                switch (this.mViewFlipperList.getCurrentView().getId()) {
                    case R.id.LinearLayoutAlbumTrackList /*2131165186*/:
                        Collections.sort(this.mListAlbumTrack, new TimeComparator());
                        this.mAdapterAlbumTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutArtistTrackList /*2131165190*/:
                        Collections.sort(this.mListArtistTrack, new TimeComparator());
                        this.mAdapterArtistTrack.notifyDataSetChanged();
                        break;
                    case R.id.LinearLayoutTrackList /*2131165284*/:
                        Collections.sort(this.mListTrack, new TimeComparator());
                        this.mAdapterTrack.notifyDataSetChanged();
                        break;
                }
                this.mPopupWindow.dismiss();
                return;
            case R.id.ImageViewpinPuButton /*2131165286*/:
                startEQActivity();
                return;
            default:
                return;
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            if (view.getId() != R.id.EditTextQuickSearch && this.mInputMethodManager.isActive(this.mEditTextQuickSearch)) {
                this.mInputMethodManager.hideSoftInputFromWindow(this.mEditTextQuickSearch.getWindowToken(), 2);
            }
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onItemClick " + e.getMessage());
        }
        try {
            if (view.getId() != R.id.EditTextPopupSearch && this.mInputMethodManager.isActive(this.mEditTextPopupSearch)) {
                this.mInputMethodManager.hideSoftInputFromWindow(this.mEditTextPopupSearch.getWindowToken(), 2);
            }
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onItemClick " + e.getMessage());
        }
        switch (parent.getId()) {
            case R.id.GridViewAlbumList /*2131165185*/:
                if (!this.mAlbumSelectionValid || this.mAlbumSelectionPosition != position) {
                    this.mAlbumSelectionValid = true;
                    this.mAlbumSelectionPosition = position;
                    this.mListAlbumTrack.clear();
                    this.mAdapterAlbumTrack.notifyDataSetChanged();
                    this.mMessage = Message.obtain();
                    this.mMessage.what = Constants.RequestRetrieveAlbumTrackDatabase;
                    this.mMessage.arg1 = position;
                    this.MediaManagerSendMessage(Constants.RequestRetrieveAlbumTrackDatabase, position);
                    return;
                }
                if (this.mListAlbumTrack.size() != this.mListAlbumTrackBackup.size()) {
                    this.mListAlbumTrack.clear();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListAlbumTrackBackup.size()) {
                        this.mListAlbumTrack.add(this.mListAlbumTrackBackup.get(this.mRecordIndex));
                        this.mRecordIndex++;
                    }
                }
                this.mViewFlipperList.showNext();
                this.mViewFlipperList.showNext();
                updateListHeadline();
                return;
            case R.id.GridViewAlbumTrackList /*2131165187*/:
                if (position < this.mListAlbumTrack.size() && this.mListAlbumTrack.get(position).get("ListItemState").equals(0)) {
                    this.mViewPlayValid = true;
                    this.mListPlayTrack = new ArrayList();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListAlbumTrack.size()) {
                        this.mListPlayTrack.add((String) this.mListAlbumTrack.get(this.mRecordIndex).get("ListItemPath"));
                        this.mRecordIndex++;
                    }
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestPlayAlbumTrack;
//                    this.mMessage.arg1 = position;
//                    this.mMessage.obj = this.mListPlayTrack;
                    this.MediaManagerSendMessage(Constants.RequestPlayAlbumTrack, position, this.mListPlayTrack);
                    Log.d("MusicPlayer : ", "Send message: request song from album");
                }
                this.mViewFlipperMain.showNext();
                return;
            case R.id.GridViewArtistList /*2131165189*/:
                if (!this.mArtistSelectionValid || this.mArtistSelectionPosition != position) {
                    this.mArtistSelectionValid = true;
                    this.mArtistSelectionPosition = position;
                    this.mListArtistTrack.clear();
                    this.mAdapterArtistTrack.notifyDataSetChanged();
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestRetrieveArtistTrackDatabase;
//                    this.mMessage.arg1 = position;
                    this.MediaManagerSendMessage(Constants.RequestRetrieveArtistTrackDatabase, position);
                    return;
                }
                if (this.mListArtistTrack.size() != this.mListArtistTrackBackup.size()) {
                    this.mListArtistTrack.clear();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListArtistTrackBackup.size()) {
                        this.mListArtistTrack.add(this.mListArtistTrackBackup.get(this.mRecordIndex));
                        this.mRecordIndex++;
                    }
                }
                this.mViewFlipperList.showNext();
                this.mViewFlipperList.showNext();
                updateListHeadline();
                return;
            case R.id.GridViewArtistTrackList /*2131165191*/:
                if (position < this.mListArtistTrack.size() && this.mListArtistTrack.get(position).get("ListItemState").equals(0)) {
                    this.mViewPlayValid = true;
                    this.mListPlayTrack = new ArrayList();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListArtistTrack.size()) {
                        this.mListPlayTrack.add((String) this.mListArtistTrack.get(this.mRecordIndex).get("ListItemPath"));
                        this.mRecordIndex++;
                    }
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestPlayArtistTrack;
//                    this.mMessage.arg1 = position;
//                    this.mMessage.obj = this.mListPlayTrack;
                    this.MediaManagerSendMessage(Constants.RequestPlayArtistTrack, position, this.mListPlayTrack);
                    Log.d("MusicPlayer : ", "Send message: request an artist song");
                }
                this.mViewFlipperMain.showNext();
                return;
            case R.id.GridViewTrackList /*2131165285*/:
                if (position < this.mListTrack.size() && this.mListTrack.get(position).get("ListItemState").equals(0)) {
                    this.mViewPlayValid = true;
                    this.mListPlayTrack = new ArrayList();
                    this.mRecordIndex = 0;
                    while (this.mRecordIndex < this.mListTrack.size()) {
                        this.mListPlayTrack.add((String) this.mListTrack.get(this.mRecordIndex).get("ListItemPath"));
                        this.mRecordIndex++;
                    }
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestPlayTrack;
//                    this.mMessage.arg1 = position;
//                    this.mMessage.obj = this.mListPlayTrack;
                    this.MediaManagerSendMessage(Constants.RequestPlayTrack, position, this.mListPlayTrack);
                    Log.d("MusicPlayer : ", "Send message: request song");
                }
                this.mViewFlipperMain.showNext();
                return;
            default:
                return;
        }
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.RequestChangeProgress;
//        this.mMessage.arg1 = seekBar.getProgress();
        this.MediaManagerSendMessage(Constants.RequestChangeProgress, seekBar.getProgress());
    }

    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case -1:
                MediaManagerSendEmptyMessage(Constants.RequestPauseTrack);
                return;
            default:
                return;
        }
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case 0:
                if (view.getId() == this.mGridViewTrackList.getId()) {
                    for (int index = this.mTrackFirstVisibleItemPosition; index <= this.mTrackLastVisibleItemPosition; index++) {
                        if ((index < this.mGridViewTrackList.getFirstVisiblePosition() || index > this.mGridViewTrackList.getLastVisiblePosition()) && index >= 0 && index < this.mListTrack.size()) {
                            this.mListTrack.get(index).put("ListItemImage", Integer.valueOf(R.drawable.style_tracklist_item));
                        }
                    }
                    this.mAdapterTrack.notifyDataSetChanged();
                    this.mTrackFirstVisibleItemPosition = this.mGridViewTrackList.getFirstVisiblePosition();
                    this.mTrackLastVisibleItemPosition = this.mGridViewTrackList.getLastVisiblePosition();
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestUpdateTrackBitmap;
//                    this.mMessage.arg1 = this.mTrackFirstVisibleItemPosition;
//                    this.mMessage.arg2 = this.mTrackLastVisibleItemPosition;
                    this.MediaManagerSendMessage(Constants.RequestUpdateTrackBitmap, this.mTrackFirstVisibleItemPosition, this.mTrackLastVisibleItemPosition);
                    return;
                } else if (view.getId() == this.mGridViewAlbumList.getId()) {
                    for (int index2 = this.mAlbumFirstVisibleItemPosition; index2 <= this.mAlbumLastVisibleItemPosition; index2++) {
                        if ((index2 < this.mGridViewAlbumList.getFirstVisiblePosition() || index2 > this.mGridViewAlbumList.getLastVisiblePosition()) && index2 >= 0 && index2 < this.mListAlbum.size()) {
                            this.mListAlbum.get(index2).put("ListItemImage", Integer.valueOf(R.drawable.style_albumlist_item));
                        }
                    }
                    this.mAdapterAlbum.notifyDataSetChanged();
                    this.mAlbumFirstVisibleItemPosition = this.mGridViewAlbumList.getFirstVisiblePosition();
                    this.mAlbumLastVisibleItemPosition = this.mGridViewAlbumList.getLastVisiblePosition();
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestUpdateAlbumBitmap;
//                    this.mMessage.arg1 = this.mAlbumFirstVisibleItemPosition;
//                    this.mMessage.arg2 = this.mAlbumLastVisibleItemPosition;
                    this.MediaManagerSendMessage(Constants.RequestUpdateAlbumBitmap, this.mAlbumFirstVisibleItemPosition, this.mAlbumLastVisibleItemPosition);
                    return;
                } else {
                    return;
                }
            default:
                return;
        }
    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    }

    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() != R.id.GridViewTrackList && view.getId() != R.id.GridViewArtistList && view.getId() != R.id.GridViewAlbumList && view.getId() != R.id.GridViewArtistTrackList && view.getId() != R.id.GridViewAlbumTrackList) {
            return false;
        }
        try {
            if (this.mInputMethodManager.isActive(this.mEditTextQuickSearch)) {
                this.mInputMethodManager.hideSoftInputFromWindow(this.mEditTextQuickSearch.getWindowToken(), 2);
            }
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onTouch " + e.getMessage());
        }
        try {
            if (!this.mInputMethodManager.isActive(this.mEditTextPopupSearch)) {
                return false;
            }
            this.mInputMethodManager.hideSoftInputFromWindow(this.mEditTextPopupSearch.getWindowToken(), 2);
            return false;
        } catch (Exception e) {
            Log.e("MusicPlayer : ", "onTouch " + e.getMessage());
            return false;
        }
    }

    @SuppressLint("WrongConstant")
    private void setEnvironment() {
//        this.mSAS = (ISystemAppService) getSystemService("systemapp");
        setContentView(R.layout.activity_main);
        this.mViewFlipperMain = findViewById(R.id.ViewFlipperMain);
        this.mViewList = getLayoutInflater().inflate(R.layout.activity_list, null);
        this.mImageViewHeadlineIcon = this.mViewList.findViewById(R.id.ImageViewHeadlineIcon);
        this.mTextViewHeadlineName = this.mViewList.findViewById(R.id.TextViewHeadlineName);
        this.mEditTextQuickSearch = this.mViewList.findViewById(R.id.EditTextQuickSearch);
        this.mEditTextQuickSearch.setOnClickListener(this);
        this.mInputMethodManager = (InputMethodManager) getSystemService("input_method");
        this.mImageViewQuickSearch = this.mViewList.findViewById(R.id.ImageViewQuickSearch);
        this.mImageViewQuickSearch.setOnClickListener(this);
        this.mImageViewHeadlineFunction = this.mViewList.findViewById(R.id.ImageViewHeadlineFunction);
        this.mViewFlipperList = this.mViewList.findViewById(R.id.ViewFlipperList);
        this.mImageViewHeadlineIcon.setOnClickListener(this);
        this.mImageViewHeadlineFunction.setOnClickListener(this);
        this.mViewPlay = getLayoutInflater().inflate(R.layout.activity_play, null);
        this.mViewFlipperPlay = this.mViewPlay.findViewById(R.id.ViewFlipperPlay);
        this.mImageViewThumbnail = this.mViewPlay.findViewById(R.id.ImageViewThumbnail);
        this.mListViewLrc = this.mViewPlay.findViewById(R.id.ListViewLrc);
        this.mLrcValue = new ArrayList();
        this.mAdapterLrc = new SimpleAdapter(this, this.mLrcValue, R.layout.activity_lrc_line, new String[]{"Content"}, new int[]{R.id.TextViewLrcLine});
        this.mTextViewTrackTitle = this.mViewPlay.findViewById(R.id.TextViewTrackTitle);
        this.mTextViewParentTitle = this.mViewPlay.findViewById(R.id.TextViewParentTitle);
        this.mTextViewProgress = this.mViewPlay.findViewById(R.id.TextViewProgress);
        this.mSeekBarPosition = this.mViewPlay.findViewById(R.id.SeekBarPosition);
        this.mTextViewLength = this.mViewPlay.findViewById(R.id.TextViewLength);
        this.mImageViewPlayShuffle = this.mViewPlay.findViewById(R.id.ImageViewPlayShuffle);
        this.mImageViewPlayLoop = this.mViewPlay.findViewById(R.id.ImageViewPlayLoop);
        this.mImageViewPlayLRC = this.mViewPlay.findViewById(R.id.ImageViewPlayLRC);
        this.mImageViewPlayPrevious = this.mViewPlay.findViewById(R.id.ImageViewPlayPrevious);
        this.mImageViewPlayPause = this.mViewPlay.findViewById(R.id.ImageViewPlayPause);
        this.mImageViewStartEq = this.mViewPlay.findViewById(R.id.ImageViewpinPuButton);
        this.mImageViewPlayNext = this.mViewPlay.findViewById(R.id.ImageViewPlayNext);
        this.mImageViewPlayList = this.mViewPlay.findViewById(R.id.ImageViewPlayList);
        this.mListViewLrc.setDivider(null);
        this.mSeekBarPosition.setOnSeekBarChangeListener(this);
        this.mImageViewPlayShuffle.setOnClickListener(this);
        this.mImageViewPlayLoop.setOnClickListener(this);
        this.mImageViewPlayLRC.setOnClickListener(this);
        this.mImageViewPlayPrevious.setOnClickListener(this);
        this.mImageViewPlayPause.setOnClickListener(this);
        this.mImageViewStartEq.setOnClickListener(this);
        this.mImageViewPlayNext.setOnClickListener(this);
        this.mImageViewPlayList.setOnClickListener(this);
        this.mViewPlayValid = false;
        this.mViewPopup = getLayoutInflater().inflate(R.layout.activity_popup, null);
        this.mImageViewPopupCategory = this.mViewPopup.findViewById(R.id.ImageViewPopupCategory);
        this.mImageViewPopupSort = this.mViewPopup.findViewById(R.id.ImageViewPopupSort);
        this.mImageViewPopupSearch = this.mViewPopup.findViewById(R.id.ImageViewPopupSearch);
        this.mImageViewPopupSetting = this.mViewPopup.findViewById(R.id.ImageViewPopupSetting);
        this.mViewFlipperPopupContent = this.mViewPopup.findViewById(R.id.ViewFlipperPopupContent);
        this.mTextViewPopupCategory = this.mViewPopup.findViewById(R.id.TextViewPopupCategory);
        this.mTextViewPopupSort = this.mViewPopup.findViewById(R.id.TextViewPopupSort);
        this.mTextViewPopupSearch = this.mViewPopup.findViewById(R.id.TextViewPopupSearch);
        this.mTextViewPopupSetting = this.mViewPopup.findViewById(R.id.TextViewPopupSetting);
        this.mImageViewPopupCategory.setOnClickListener(this);
        this.mImageViewPopupSort.setOnClickListener(this);
        this.mImageViewPopupSearch.setOnClickListener(this);
        this.mImageViewPopupSetting.setOnClickListener(this);
        this.mTextViewPopupCategory.setOnClickListener(this);
        this.mTextViewPopupSort.setOnClickListener(this);
        this.mTextViewPopupSearch.setOnClickListener(this);
        this.mTextViewPopupSetting.setOnClickListener(this);
        this.mViewPopupCategory = getLayoutInflater().inflate(R.layout.activity_popup_category, null);
        this.mViewPopupSort = getLayoutInflater().inflate(R.layout.activity_popup_sort, null);
        this.mViewPopupSearch = getLayoutInflater().inflate(R.layout.activity_popup_search, null);
        this.mViewPopupSetting = getLayoutInflater().inflate(R.layout.activity_popup_setting, null);
        this.mViewFlipperPopupContent.addView(this.mViewPopupCategory);
        this.mViewFlipperPopupContent.addView(this.mViewPopupSort);
        this.mViewFlipperPopupContent.addView(this.mViewPopupSearch);
        this.mViewFlipperPopupContent.addView(this.mViewPopupSetting);
        this.mImageViewPopupCategoryPlaying = this.mViewPopupCategory.findViewById(R.id.ImageViewPopupCategoryPlaying);
        this.mImageViewPopupCategoryTrack = this.mViewPopupCategory.findViewById(R.id.ImageViewPopupCategoryTrack);
        this.mImageViewPopupCategoryArtist = this.mViewPopupCategory.findViewById(R.id.ImageViewPopupCategoryArtist);
        this.mImageViewPopupCategoryAlbum = this.mViewPopupCategory.findViewById(R.id.ImageViewPopupCategoryAlbum);
        this.mTextViewPopupCategoryPlaying = this.mViewPopupCategory.findViewById(R.id.TextViewPopupCategoryPlaying);
        this.mTextViewPopupCategoryTrack = this.mViewPopupCategory.findViewById(R.id.TextViewPopupCategoryTrack);
        this.mTextViewPopupCategoryArtist = this.mViewPopupCategory.findViewById(R.id.TextViewPopupCategoryArtist);
        this.mTextViewPopupCategoryAlbum = this.mViewPopupCategory.findViewById(R.id.TextViewPopupCategoryAlbum);
        this.mImageViewPopupCategoryPlaying.setOnClickListener(this);
        this.mImageViewPopupCategoryTrack.setOnClickListener(this);
        this.mImageViewPopupCategoryArtist.setOnClickListener(this);
        this.mImageViewPopupCategoryAlbum.setOnClickListener(this);
        this.mTextViewPopupCategoryPlaying.setOnClickListener(this);
        this.mTextViewPopupCategoryTrack.setOnClickListener(this);
        this.mTextViewPopupCategoryArtist.setOnClickListener(this);
        this.mTextViewPopupCategoryAlbum.setOnClickListener(this);
        this.mImageViewPopupSortTitle = this.mViewPopupSort.findViewById(R.id.ImageViewPopupSortTitle);
        this.mImageViewPopupSortType = this.mViewPopupSort.findViewById(R.id.ImageViewPopupSortType);
        this.mImageViewPopupSortSize = this.mViewPopupSort.findViewById(R.id.ImageViewPopupSortSize);
        this.mImageViewPopupSortTime = this.mViewPopupSort.findViewById(R.id.ImageViewPopupSortTime);
        this.mTextViewPopupSortTitle = this.mViewPopupSort.findViewById(R.id.TextViewPopupSortTitle);
        this.mTextViewPopupSortType = this.mViewPopupSort.findViewById(R.id.TextViewPopupSortType);
        this.mTextViewPopupSortSize = this.mViewPopupSort.findViewById(R.id.TextViewPopupSortSize);
        this.mTextViewPopupSortTime = this.mViewPopupSort.findViewById(R.id.TextViewPopupSortTime);
        this.mImageViewPopupSortTitle.setOnClickListener(this);
        this.mImageViewPopupSortType.setOnClickListener(this);
        this.mImageViewPopupSortSize.setOnClickListener(this);
        this.mImageViewPopupSortTime.setOnClickListener(this);
        this.mTextViewPopupSortTitle.setOnClickListener(this);
        this.mTextViewPopupSortType.setOnClickListener(this);
        this.mTextViewPopupSortSize.setOnClickListener(this);
        this.mTextViewPopupSortTime.setOnClickListener(this);
        this.mEditTextPopupSearch = this.mViewPopupSearch.findViewById(R.id.EditTextPopupSearch);
        this.mButtonPopupSearch = this.mViewPopupSearch.findViewById(R.id.ButtonPopupSearch);
        this.mButtonPopupSearch.setOnClickListener(this);
        this.mImageViewPopupSettingPlayAll = this.mViewPopupSetting.findViewById(R.id.ImageViewPopupSettingPlayAll);
        this.mImageViewPopupSettingPartyShuffle = this.mViewPopupSetting.findViewById(R.id.ImageViewPopupSettingPartyShuffle);
        this.mImageViewPopupSettingShuffleAll = this.mViewPopupSetting.findViewById(R.id.ImageViewPopupSettingShuffleAll);
        this.mTextViewPopupSettingPlayAll = this.mViewPopupSetting.findViewById(R.id.TextViewPopupSettingPlayAll);
        this.mTextViewPopupSettingPartyShuffle = this.mViewPopupSetting.findViewById(R.id.TextViewPopupSettingPartyShuffle);
        this.mTextViewPopupSettingShuffleAll = this.mViewPopupSetting.findViewById(R.id.TextViewPopupSettingShuffleAll);
        this.mImageViewPopupSettingPlayAll.setOnClickListener(this);
        this.mImageViewPopupSettingPartyShuffle.setOnClickListener(this);
        this.mImageViewPopupSettingShuffleAll.setOnClickListener(this);
        this.mTextViewPopupSettingPlayAll.setOnClickListener(this);
        this.mTextViewPopupSettingPartyShuffle.setOnClickListener(this);
        this.mTextViewPopupSettingShuffleAll.setOnClickListener(this);
        this.mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.mDisplayMetrics);
        this.mDisplayWidth = this.mDisplayMetrics.widthPixels;
        this.mDisplayHeight = this.mDisplayMetrics.heightPixels;
        if (this.mDisplayWidth == 1024 || this.mDisplayHeight == 600) {
            this.mPopupWindow = new PopupWindow(this.mViewPopup, 790, 420);
        } else {
            this.mPopupWindow = new PopupWindow(this.mViewPopup, 617, 336);
        }
        this.mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup_background));
        this.mPopupWindow.setFocusable(true);
        this.mViewFlipperMain.addView(this.mViewList);
        this.mViewFlipperMain.addView(this.mViewPlay);
        this.mViewHint = getLayoutInflater().inflate(R.layout.activity_dummy, null);
        this.mListTrack = new ArrayList();
        this.mListTrackBackup = new ArrayList();
        this.mAdapterTrack = new SimpleAdapter(this, this.mListTrack, R.layout.activity_item,
                new String[]{"ListItemImage", "ListItemName", "ListItemState"},
                new int[]{R.id.ImageViewItemIcon, R.id.MarqueeTextViewItemName, R.id.ImageViewItemState});
        this.mAdapterTrack.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (!(view instanceof ImageView) || !(data instanceof Bitmap)) {
                    return false;
                }
                ((ImageView) view).setImageBitmap((Bitmap) data);
                return true;
            }
        });
        this.mViewTrackList = getLayoutInflater().inflate(R.layout.activity_track_list, null);
        this.mGridViewTrackList = this.mViewTrackList.findViewById(R.id.GridViewTrackList);
        this.mGridViewTrackList.setAdapter(this.mAdapterTrack);
        this.mGridViewTrackList.setOnItemClickListener(this);
        this.mGridViewTrackList.setOnScrollListener(this);
        this.mGridViewTrackList.setOnTouchListener(this);
        this.mListArtist = new ArrayList();
        this.mListArtistBackup = new ArrayList();
        this.mAdapterArtist = new SimpleAdapter(this, this.mListArtist, R.layout.activity_item, new String[]{"ListItemImage", "ListItemName", "ListItemState"}, new int[]{R.id.ImageViewItemIcon, R.id.MarqueeTextViewItemName, R.id.ImageViewItemState});
        this.mViewArtistList = getLayoutInflater().inflate(R.layout.activity_artist_list, null);
        this.mGridViewArtistList = this.mViewArtistList.findViewById(R.id.GridViewArtistList);
        this.mGridViewArtistList.setAdapter(this.mAdapterArtist);
        this.mGridViewArtistList.setOnItemClickListener(this);
        this.mGridViewArtistList.setOnTouchListener(this);
        this.mArtistSelectionValid = false;
        this.mArtistSelectionPosition = -1;
        this.mListAlbum = new ArrayList();
        this.mListAlbumBackup = new ArrayList();
        this.mAdapterAlbum = new SimpleAdapter(this, this.mListAlbum, R.layout.activity_item, new String[]{"ListItemImage", "ListItemName", "ListItemState"}, new int[]{R.id.ImageViewItemIcon, R.id.MarqueeTextViewItemName, R.id.ImageViewItemState});
        this.mAdapterAlbum.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (!(view instanceof ImageView) || !(data instanceof Bitmap)) {
                    return false;
                }
                ((ImageView) view).setImageBitmap((Bitmap) data);
                return true;
            }
        });
        this.mViewAlbumList = getLayoutInflater().inflate(R.layout.activity_album_list, null);
        this.mGridViewAlbumList = this.mViewAlbumList.findViewById(R.id.GridViewAlbumList);
        this.mGridViewAlbumList.setAdapter(this.mAdapterAlbum);
        this.mGridViewAlbumList.setOnItemClickListener(this);
        this.mGridViewAlbumList.setOnScrollListener(this);
        this.mGridViewAlbumList.setOnTouchListener(this);
        this.mAlbumSelectionValid = false;
        this.mAlbumSelectionPosition = -1;
        this.mListArtistTrack = new ArrayList();
        this.mListArtistTrackBackup = new ArrayList();
        this.mAdapterArtistTrack = new SimpleAdapter(this, this.mListArtistTrack, R.layout.activity_item, new String[]{"ListItemImage", "ListItemName", "ListItemState"}, new int[]{R.id.ImageViewItemIcon, R.id.MarqueeTextViewItemName, R.id.ImageViewItemState});
        this.mViewArtistTrackList = getLayoutInflater().inflate(R.layout.activity_artist_track_list, null);
        this.mGridViewArtistTrackList = this.mViewArtistTrackList.findViewById(R.id.GridViewArtistTrackList);
        this.mGridViewArtistTrackList.setAdapter(this.mAdapterArtistTrack);
        this.mGridViewArtistTrackList.setOnItemClickListener(this);
        this.mGridViewArtistTrackList.setOnTouchListener(this);
        this.mListAlbumTrack = new ArrayList();
        this.mListAlbumTrackBackup = new ArrayList();
        this.mAdapterAlbumTrack = new SimpleAdapter(this, this.mListAlbumTrack, R.layout.activity_item, new String[]{"ListItemImage", "ListItemName", "ListItemState"}, new int[]{R.id.ImageViewItemIcon, R.id.MarqueeTextViewItemName, R.id.ImageViewItemState});
        this.mViewAlbumTrackList = getLayoutInflater().inflate(R.layout.activity_album_track_list, null);
        this.mGridViewAlbumTrackList = this.mViewAlbumTrackList.findViewById(R.id.GridViewAlbumTrackList);
        this.mGridViewAlbumTrackList.setAdapter(this.mAdapterAlbumTrack);
        this.mGridViewAlbumTrackList.setOnItemClickListener(this);
        this.mGridViewAlbumTrackList.setOnTouchListener(this);
        this.mViewFlipperList.addView(this.mViewHint);
        this.mViewFlipperList.addView(this.mViewTrackList);
        this.mViewFlipperList.addView(this.mViewArtistList);
        this.mViewFlipperList.addView(this.mViewAlbumList);
        this.mViewFlipperList.addView(this.mViewArtistTrackList);
        this.mViewFlipperList.addView(this.mViewAlbumTrackList);
        this.mListViewLrc.setAdapter(this.mAdapterLrc);
        this.mHandlerMain = new Handler(this);
        this.mMediaManager = new MediaManager(getApplicationContext(), this.mHandlerMain, getContentResolver());
        this.mMediaManager.start();
        this.mMediaBroadcastReceiver = new MediaBroadcastReceiver(this.mHandlerMain);
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("android.intent.action.MEDIA_SCANNER_FINISHED");
        this.mIntentFilter.addDataScheme("file");
        registerReceiver(this.mMediaBroadcastReceiver, this.mIntentFilter);
        this.mAudioBroadcastReceiver = new AudioBroadcastReceiver(this.mHandlerMain, getPackageName());
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("cn.com.cs2c.android.vehicle.action.PAUSE_MUSIC");
        registerReceiver(this.mAudioBroadcastReceiver, this.mIntentFilter);
        this.mPanelBroadcastReceiver = new PanelBroadcastReceiver(this.mHandlerMain, getApplicationContext());
        this.mIntentFilter = new IntentFilter();
        this.mIntentFilter.addAction("cn.com.cs2c.android.vehicle.action.PREVIEW_KEY");
        this.mIntentFilter.addAction("cn.com.cs2c.android.vehicle.action.PLAYPAUSE_KEY");
        this.mIntentFilter.addAction("cn.com.cs2c.android.vehicle.action.NEXT_KEY");
        this.mIntentFilter.addAction("cn.com.cs2c.android.vehicle.action.RADIO_PREVIEW_KEY");
        this.mIntentFilter.addAction("cn.com.cs2c.android.vehicle.action.RADIO_NEXT_KEY");
        registerReceiver(this.mPanelBroadcastReceiver, this.mIntentFilter);
        this.mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        this.mSharedPreferences = getSharedPreferences(getResources().getString(R.string.Music_Trace), 0);
        this.mSharedPreferencesEditor = this.mSharedPreferences.edit();
        this.mRestoreValid = false;
        this.mProcessPanelKeyFlag = false;
        this.mTrackCount = 0;
        this.mArtistCount = 0;
        this.mAlbumCount = 0;
        this.mArtistTrackCount = 0;
        this.mAlbumTrackCount = 0;
        this.mTrackFirstVisibleItemPosition = 0;
        this.mTrackLastVisibleItemPosition = 8;
        this.mAlbumFirstVisibleItemPosition = 0;
        this.mAlbumLastVisibleItemPosition = 8;
        this.mTextViewHeadlineName.setEnabled(false);
        this.mEditTextQuickSearch.setEnabled(false);
        this.mImageViewQuickSearch.setEnabled(false);
        this.mImageViewHeadlineFunction.setEnabled(false);
        this.mKnobLeftCount = 0;
        this.mKnobRightCount = 0;
    }

    private void updateListHeadline() {
        if (this.mViewFlipperList.getCurrentView().getId() == this.mViewHint.getId()) {
            this.mViewFlipperList.removeView(this.mViewHint);
        }
        switch (this.mViewFlipperList.getDisplayedChild()) {
            case 0:
                this.mImageViewHeadlineIcon.setImageResource(R.drawable.list_icon_track);
                this.mTextViewHeadlineName.setText(getResources().getString(R.string.Track_List) + " - " + this.mTrackCount + " ");
                return;
            case 1:
                this.mImageViewHeadlineIcon.setImageResource(R.drawable.list_icon_artist);
                this.mTextViewHeadlineName.setText(getResources().getString(R.string.Artist_List) + " - " + this.mArtistCount + " ");
                return;
            case 2:
                this.mImageViewHeadlineIcon.setImageResource(R.drawable.list_icon_album);
                this.mTextViewHeadlineName.setText(getResources().getString(R.string.Album_List) + " - " + this.mAlbumCount + " ");
                return;
            case 3:
                this.mImageViewHeadlineIcon.setImageResource(R.drawable.sub_track);
                this.mTextViewHeadlineName.setText(getResources().getString(R.string.Artist_Track_List) + " - " + this.mArtistTrackCount + " ");
                return;
            case 4:
                this.mImageViewHeadlineIcon.setImageResource(R.drawable.sub_track);
                this.mTextViewHeadlineName.setText(getResources().getString(R.string.Album_Track_List) + " - " + this.mAlbumTrackCount + " ");
                return;
            default:
                return;
        }
    }

    private void displayLrc() {
        this.mLrcValue.clear();
        if (this.mLrcFlag) {
            this.mLrcKey = this.mMediaManager.getLrcKey();
            this.mLrcValue.addAll(this.mMediaManager.getLrcValue());
        }
        this.mAdapterLrc.notifyDataSetChanged();
    }

    private void updateLrc(int time) {
        if (this.mLrcFlag) {
            int index = 0;
            while (index < this.mLrcKey.size()) {
                String line = this.mLrcKey.get(index);
                if (line.matches("\\d+") && Integer.parseInt(line) > time) {
                    break;
                }
                index++;
            }
            if (index > 0) {
                index--;
            }
            this.mListViewLrc.setSelection(index);
        }
    }

    public void startEQActivity() {
        Intent intentEQ = new Intent();
        intentEQ.setComponent(new ComponentName("cs2c.EQ", "cs2c.EQ.EQActivity"));
        intentEQ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intentEQ);
    }

    private int recoverPlayMemory() {
        final File file = new File(this.mRecordPath);
        int n;
        if (file != null && file.exists() && file.isFile()) {
            n = 0;
            switch (this.mCategoryIndex) {
                case 0: {
                    Log.i("MusicPlayer", "this.mListTrack.size()=" + this.mListTrack.size());
                    if (this.mTrackIndex < this.mListTrack.size()) {
                        this.onItemClick(this.mGridViewTrackList, this.mGridViewTrackList.getChildAt(this.mTrackIndex), this.mTrackIndex, this.mTrackIndex);
                    }
                    Log.d("MusicPlayer : ", "Restore playback information of all song lists");
                    break;
                }
                case 1: {
                    while (this.mViewFlipperList.getCurrentView().getId() != this.mViewArtistList.getId()) {
                        this.mViewFlipperList.showNext();
                    }
                    this.mRestoreValid = false;
                    if (this.mParentIndex < this.mListArtist.size()) {
                        this.onItemClick(this.mGridViewArtistList, this.mGridViewArtistList.getChildAt(this.mParentIndex), this.mParentIndex, this.mParentIndex);
                    }
                    Log.d("MusicPlayer : ", "Resume playback information for artist list");
                    break;
                }
                case 2: {
                    while (this.mViewFlipperList.getCurrentView().getId() != this.mViewAlbumList.getId()) {
                        this.mViewFlipperList.showNext();
                    }
                    this.mRestoreValid = false;
                    if (this.mParentIndex < this.mListAlbum.size()) {
                        this.onItemClick(this.mGridViewAlbumList, this.mGridViewAlbumList.getChildAt(this.mParentIndex), this.mParentIndex, this.mParentIndex);
                    }
                    Log.d("MusicPlayer : ", "Resume playback information of album list");
                    break;
                }
            }
        } else {
            n = 1;
        }
        return n;
    }

    public boolean handleMessage(final Message message) {
        switch (message.what) {
            case -4: {
                this.mTextViewHeadlineName.setEnabled(true);
                this.mEditTextQuickSearch.setEnabled(true);
                this.mImageViewQuickSearch.setEnabled(true);
                this.mImageViewHeadlineFunction.setEnabled(true);
                this.mHandlerMain.sendEmptyMessage(0);
                break;
            }
            case -3: {
                while (this.mViewFlipperMain.getCurrentView().getId() != this.mViewList.getId()) {
                    this.mViewFlipperMain.showNext();
                }
                Toast.makeText(this, getResources().getString(R.string.File_Not_Exist), Toast.LENGTH_SHORT).show();
                break;
            }
            case -2: {
                this.mProcessPanelKeyFlag = false;
                if (this.mKnobLeftCount > 0 || this.mKnobRightCount > 0) {
//                    this.mMessage = Message.obtain();
                    if (this.mKnobLeftCount > this.mKnobRightCount) {
//                        this.mMessage.what = Constants.RequestPlayPrevious;
//                        this.mMessage.arg1 = this.mKnobLeftCount - this.mKnobRightCount;
                        this.MediaManagerSendMessage(Constants.RequestPlayPrevious, this.mKnobLeftCount - this.mKnobRightCount);
                    } else {
//                        this.mMessage.what = Constants.RequestPlayNext;
//                        this.mMessage.arg1 = this.mKnobRightCount - this.mKnobLeftCount;
                        this.MediaManagerSendMessage(Constants.RequestPlayNext, this.mKnobRightCount - this.mKnobLeftCount);
                    }
                    this.mKnobLeftCount = 0;
                    this.mKnobRightCount = 0;
                    break;
                }
                break;
            }
            case -1: {
                MediaManagerSendEmptyMessage(Constants.RequestUpdateCount);
                break;
            }
            case Constants.RequestRetrieveDatabase: {
                this.handleRequestRetrieveDatabase();
                break;
            }
            case Constants.ResponseRetrieveDatabase: {
                this.handleResponseRetrieveDatabase();
                break;
            }
            case Constants.ResponseGetTrackCount: {
                this.handleResponseGetTrackCount(message);
                break;
            }
            case Constants.ResponseGetTrackTitle: {
                this.handleResponseGetTrackTitle(message);
                break;
            }
            case Constants.ResponseGetArtistCount: {
                this.handleResponseGetArtistCount(message);
                break;
            }
            case Constants.ResponseGetArtistTitle: {
                this.handleResponseGetArtistTitle(message);
                break;
            }
            case Constants.ResponseGetAlbumtCount: {
                this.handleResponseGetAlbumtCount(message);
                break;
            }
            case Constants.ResponseGetAlbumTitle: {
                this.handleResponseGetAlbumTitle(message);
                break;
            }
            case Constants.ResponseGetArtistTrackCount: {
                this.handleResponseGetArtistTrackCount(message);
                break;
            }
            case Constants.ResponseGetArtistTrackTitle: {
                this.handleResponseGetArtistTrackTitle(message);
                break;
            }
            case Constants.ResponseGetAlbumTrackCount: {
                this.handleResponseGetAlbumTrackCount(message);
                break;
            }
            case Constants.ResponseGetAlbumTrackTitle: {
                this.handleResponseGetAlbumTrackTitle(message);
                break;
            }
            case Constants.ResponsePlayTrack: {
                this.handleResponsePlayTrack(message);
                break;
            }
            case Constants.ResponsePlayArtistTrack: {
                this.handleResponsePlayArtistTrack(message);
                break;
            }
            case Constants.ResponsePlayAlbumTrack: {
                this.handleResponsePlayAlbumTrack(message);
                break;
            }
            case Constants.ResponseGetTrackPlayProgress: {
                this.handleResponseGetTrackPlayProgress(message);
                break;
            }
            case Constants.TrackPlayComplete: {
                this.handleTrackPlayComplete(message);
                break;
            }
            case Constants.ArtistTrackPlayComplete: {
                this.handleArtistTrackPlayComplete(message);
                break;
            }
            case Constants.AlbumTrackPlayComplete: {
                this.handleAlbumTrackPlayComplete(message);
                break;
            }
            case Constants.AllTrackComplete: {
                this.handleAllTrackComplete();
                break;
            }
            case Constants.ResponsePlayLoop: {
                this.handleResponsePlayLoop(message);
                break;
            }
            case Constants.ResponsePlayPause: {
                this.handleResponsePlayPause(message);
                break;
            }
            case Constants.ResponseChangeProgress: {
                this.handleResponseChangeProgress();
                break;
            }
            case Constants.ResponsePlayShuffle: {
                this.handleResponsePlayShuffle(message);
                break;
            }
            case Constants.RequestPauseTrack: {
                this.onAudioFocusChange(-1);
                break;
            }
            case Constants.ResponsePauseTrack: {
                this.handleResponsePauseTrack();
                break;
            }
            case Constants.ResponseResumeTrack: {
                this.handleResponseResumeTrack();
                break;
            }
            case Constants.ResponseCloseDatabase: {
                this.handleResponseCloseDatabase();
                break;
            }
            case Constants.ResponseRestart: {
                this.handleResponseRestart();
                break;
            }
            case Constants.ResponseRetrieveTrackDatabase: {
                this.handleResponseRetrieveTrackDatabase();
                break;
            }
            case Constants.ResponseRetrieveArtistDatabase: {
                this.handleResponseRetrieveArtistDatabase();
                break;
            }
            case Constants.ResponseRetrieveAlbumDatabase: {
                this.handleResponseRetrieveAlbumDatabase();
                break;
            }
            case Constants.ResponseRetrieveArtistTrackDatabase: {
                this.handleResponseRetrieveArtistTrackDatabase();
                break;
            }
            case Constants.ResponseRetrieveAlbumTrackDatabase: {
                this.handleResponseRetrieveAlbumTrackDatabase();
                break;
            }
            case Constants.ResponseUpdateTrackBitmap: {
                this.handleResponseUpdateTrackBitmap(message);
                break;
            }
            case Constants.ResponseUpdateAlbumBitmap: {
                this.handleResponseUpdateAlbumBitmap(message);
                break;
            }
            case Constants.ResponseUpdateCount: {
                this.handleResponseUpdateCount();
                break;
            }
            case Constants.RequestRecoverPlay: {
                this.handleRequestRecoverPlay();
                break;
            }
            case Constants.ResponseRecoverPlay: {
                this.handleResponseRecoverPlay(message);
                break;
            }
            case 72: {
                if (this.mViewPlayValid && !this.mProcessPanelKeyFlag) {
                    this.mProcessPanelKeyFlag = true;
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestPlayPrevious;
//                    this.mMessage.arg1 = 1;
                    this.MediaManagerSendMessage(Constants.RequestPlayPrevious, 1);
                    this.mHandlerMain.sendEmptyMessageDelayed(-2, 150L);
                    break;
                }
                break;
            }
            case 73: {
                if (this.mViewPlayValid && !this.mProcessPanelKeyFlag) {
                    this.mProcessPanelKeyFlag = true;
                    MediaManagerSendEmptyMessage(Constants.RequestPlayPause);
                    this.mHandlerMain.sendEmptyMessageDelayed(-2, 150L);
                    break;
                }
                break;
            }
            case 74: {
                if (this.mViewPlayValid && !this.mProcessPanelKeyFlag) {
                    this.mProcessPanelKeyFlag = true;
//                    this.mMessage = Message.obtain();
//                    this.mMessage.what = Constants.RequestPlayNext;
//                    this.mMessage.arg1 = 1;
                    this.MediaManagerSendMessage(Constants.RequestPlayNext, 1);
                    this.mHandlerMain.sendEmptyMessageDelayed(-2, 150L);
                    break;
                }
                break;
            }
            case 75: {
                if (this.mViewPlayValid) {
                    if (this.mKnobLeftCount == 0) {
                        this.mHandlerMain.sendEmptyMessageDelayed(-2, 300L);
                    }
                    ++this.mKnobLeftCount;
                    break;
                }
                break;
            }
            case 76: {
                if (this.mViewPlayValid) {
                    if (this.mKnobRightCount == 0) {
                        this.mHandlerMain.sendEmptyMessageDelayed(-2, 300L);
                    }
                    ++this.mKnobRightCount;
                    break;
                }
                break;
            }
        }
        return true;
    }

    private void handleRequestRetrieveDatabase() {
        MediaManagerSendEmptyMessage(Constants.RequestRetrieveTrackDatabase);
        MediaManagerSendEmptyMessage(Constants.RequestRetrieveArtistDatabase);
        MediaManagerSendEmptyMessage(Constants.RequestRetrieveAlbumDatabase);
    }

    private void handleResponseRetrieveDatabase() {
        this.mTextViewHeadlineName.setText(getResources().getString(R.string.Track_List) + " - " + this.mTrackCount + " ");
        updateListHeadline();
        this.mRecoverCount = 0;
        this.mRecoverState = 0;
        this.mHandlerMain.sendEmptyMessage(Constants.RequestRecoverPlay);
    }

    private void handleResponseGetTrackCount(Message message) {
        this.mListTrack.clear();
        this.mAdapterTrack.notifyDataSetChanged();
        this.mRecordCount = message.arg1;
        if (this.mRecordCount > 0) {
            this.mRecordIndex = 0;
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetTrackTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetTrackTitle, this.mRecordIndex);
        } else {
            MediaManagerSendEmptyMessage(Constants.RequestGetArtistCount);
        }
        this.mTrackCount = this.mRecordCount;
    }

    private void handleResponseGetTrackTitle(Message message) {
        this.mBundle = message.getData();
        this.mRecordName = this.mBundle.getString("Track_Name");
        this.mRecordType = this.mBundle.getString("Track_Type");
        this.mRecordSize = this.mBundle.getInt("Track_Size");
        this.mRecordTime = this.mBundle.getString("Track_Time");
        this.mRecordPath = this.mBundle.getString("Track_Path");
        this.mRecordMap = new HashMap();
        this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_tracklist_item));
        this.mRecordMap.put("ListItemName", this.mRecordName);
        this.mRecordMap.put("ListItemState", 0);
        this.mRecordMap.put("ListItemType", this.mRecordType);
        this.mRecordMap.put("ListItemSize", Integer.valueOf(this.mRecordSize));
        this.mRecordMap.put("ListItemTime", this.mRecordTime);
        this.mRecordMap.put("ListItemPath", this.mRecordPath);
        String artistName = this.mBundle.getString("Artist_Name");
        if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
            artistName = getResources().getString(R.string.Unknown_Name);
        }
        this.mRecordMap.put("ListItemArtistName", artistName);
        this.mRecordBitmap = (Bitmap) message.obj;
        if (this.mRecordBitmap != null) {
            this.mRecordMap.put("ListItemImage", this.mRecordBitmap);
        }
        this.mListTrack.add(this.mRecordMap);
        this.mAdapterTrack.notifyDataSetChanged();
        this.mTextViewHeadlineName.setText(getResources().getString(R.string.Track_List) + " - " + (this.mRecordIndex + 1) + " ");
        this.mRecordIndex++;
        if (this.mRecordIndex < this.mRecordCount) {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetTrackTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetTrackTitle, this.mRecordIndex);
            return;
        }
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListTrack.size()) {
            this.mListTrackBackup.add(this.mListTrack.get(this.mRecordIndex));
            this.mRecordIndex++;
        }
        MediaManagerSendEmptyMessage(Constants.RequestGetArtistCount);
    }

    private void handleResponseGetArtistCount(Message message) {
        this.mListArtist.clear();
        this.mAdapterArtist.notifyDataSetChanged();
        this.mRecordCount = message.arg1;
        if (this.mRecordCount > 0) {
            this.mRecordIndex = 0;
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetArtistTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetArtistTitle, this.mRecordIndex);
        } else {
            MediaManagerSendEmptyMessage(Constants.RequestGetAlbumCount);
        }
        this.mArtistCount = this.mRecordCount;
    }

    private void handleResponseGetArtistTitle(Message message) {
        this.mRecordName = (String) message.obj;
        if (this.mRecordName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
            this.mRecordName = getResources().getString(R.string.Unknown_Name);
        }
        this.mRecordMap = new HashMap();
        this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_artistlist_item));
        this.mRecordMap.put("ListItemName", this.mRecordName);
        this.mRecordMap.put("ListItemState", 0);
        this.mListArtist.add(this.mRecordMap);
        this.mAdapterArtist.notifyDataSetChanged();
        this.mTextViewHeadlineName.setText(getResources().getString(R.string.Artist_List) + " - " + (this.mRecordIndex + 1) + " ");
        this.mRecordIndex++;
        if (this.mRecordIndex < this.mRecordCount) {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetArtistTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetArtistTitle, this.mRecordIndex);
            return;
        }
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListArtist.size()) {
            this.mListArtistBackup.add(this.mListArtist.get(this.mRecordIndex));
            this.mRecordIndex++;
        }
        MediaManagerSendEmptyMessage(Constants.RequestGetAlbumCount);
    }

    private void handleResponseGetAlbumtCount(Message message) {
        this.mListAlbum.clear();
        this.mAdapterAlbum.notifyDataSetChanged();
        this.mRecordCount = message.arg1;
        if (this.mRecordCount > 0) {
            this.mRecordIndex = 0;
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetAlbumTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetAlbumTitle, this.mRecordIndex);
        }
        this.mAlbumCount = this.mRecordCount;
    }

    private void handleResponseGetAlbumTitle(Message message) {
        this.mBundle = message.getData();
        this.mRecordName = this.mBundle.getString("Album_Name");
        if (this.mRecordName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
            this.mRecordName = getResources().getString(R.string.Unknown_Name);
        }
        this.mRecordBitmap = (Bitmap) message.obj;
        this.mRecordMap = new HashMap();
        if (this.mRecordBitmap != null) {
            this.mRecordMap.put("ListItemImage", this.mRecordBitmap);
        } else {
            this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_albumlist_item));
        }
        this.mRecordMap.put("ListItemName", this.mRecordName);
        this.mRecordMap.put("ListItemState", 0);
        this.mListAlbum.add(this.mRecordMap);
        this.mAdapterAlbum.notifyDataSetChanged();
        this.mTextViewHeadlineName.setText(getResources().getString(R.string.Album_List) + " - " + (this.mRecordIndex + 1) + " ");
        this.mRecordIndex++;
        if (this.mRecordIndex < this.mRecordCount) {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetAlbumTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetAlbumTitle, this.mRecordIndex);
            return;
        }
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListAlbum.size()) {
            this.mListAlbumBackup.add(this.mListAlbum.get(this.mRecordIndex));
            this.mRecordIndex++;
        }
        this.mHandlerMain.sendEmptyMessage(1);
    }

    private void handleResponseGetArtistTrackCount(Message message) {
        this.mRecordCount = message.arg1;
        if (this.mRecordCount > 0) {
            this.mRecordIndex = 0;
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetArtistTrackTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetArtistTrackTitle, this.mRecordIndex);
        } else {
            this.mViewFlipperList.showNext();
            this.mViewFlipperList.showNext();
        }
        this.mArtistTrackCount = this.mRecordCount;
    }

    private void handleResponseGetArtistTrackTitle(Message message) {
        this.mBundle = message.getData();
        this.mRecordName = this.mBundle.getString("Track_Name");
        this.mRecordType = this.mBundle.getString("Track_Type");
        this.mRecordSize = this.mBundle.getInt("Track_Size");
        this.mRecordTime = this.mBundle.getString("Track_Time");
        this.mRecordPath = this.mBundle.getString("Track_Path");
        this.mRecordMap = new HashMap();
        this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_tracklist_item));
        this.mRecordMap.put("ListItemName", this.mRecordName);
        this.mRecordMap.put("ListItemState", 0);
        this.mRecordMap.put("ListItemType", this.mRecordType);
        this.mRecordMap.put("ListItemSize", Integer.valueOf(this.mRecordSize));
        this.mRecordMap.put("ListItemTime", this.mRecordTime);
        this.mRecordMap.put("ListItemPath", this.mRecordPath);
        String artistName = this.mBundle.getString("Artist_Name");
        if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
            artistName = getResources().getString(R.string.Unknown_Name);
        }
        this.mRecordMap.put("ListItemArtistName", artistName);
        this.mListArtistTrack.add(this.mRecordMap);
        this.mAdapterArtistTrack.notifyDataSetChanged();
        this.mTextViewHeadlineName.setText(getResources().getString(R.string.Artist_Track_List) + " - " + (this.mRecordIndex + 1) + " ");
        this.mRecordIndex++;
        if (this.mRecordIndex < this.mRecordCount) {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetArtistTrackTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetArtistTrackTitle, this.mRecordIndex);
            return;
        }
        this.mListArtistTrackBackup.clear();
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListArtistTrack.size()) {
            this.mListArtistTrackBackup.add(this.mListArtistTrack.get(this.mRecordIndex));
            this.mRecordIndex++;
        }
        this.mViewFlipperList.showNext();
        this.mViewFlipperList.showNext();
        updateListHeadline();
        if (!this.mRestoreValid.booleanValue()) {
            this.mRestoreValid = true;
            onItemClick(this.mGridViewArtistTrackList, this.mGridViewArtistTrackList.getChildAt(this.mTrackIndex), this.mTrackIndex, (long) this.mTrackIndex);
        }
    }

    private void handleResponseGetAlbumTrackCount(Message message) {
        this.mRecordCount = message.arg1;
        if (this.mRecordCount > 0) {
            this.mRecordIndex = 0;
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetAlbumTrackTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetAlbumTrackTitle, this.mRecordIndex);
        } else {
            this.mViewFlipperList.showNext();
            this.mViewFlipperList.showNext();
        }
        this.mAlbumTrackCount = this.mRecordCount;
    }

    private void handleResponseGetAlbumTrackTitle(Message message) {
        this.mBundle = message.getData();
        this.mRecordName = this.mBundle.getString("Track_Name");
        this.mRecordType = this.mBundle.getString("Track_Type");
        this.mRecordSize = this.mBundle.getInt("Track_Size");
        this.mRecordTime = this.mBundle.getString("Track_Time");
        this.mRecordPath = this.mBundle.getString("Track_Path");
        this.mRecordMap = new HashMap();
        this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_tracklist_item));
        this.mRecordMap.put("ListItemName", this.mRecordName);
        this.mRecordMap.put("ListItemState", 0);
        this.mRecordMap.put("ListItemType", this.mRecordType);
        this.mRecordMap.put("ListItemSize", Integer.valueOf(this.mRecordSize));
        this.mRecordMap.put("ListItemTime", this.mRecordTime);
        this.mRecordMap.put("ListItemPath", this.mRecordPath);
        String artistName = this.mBundle.getString("Artist_Name");
        if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
            artistName = getResources().getString(R.string.Unknown_Name);
        }
        this.mRecordMap.put("ListItemArtistName", artistName);
        this.mListAlbumTrack.add(this.mRecordMap);
        this.mAdapterAlbumTrack.notifyDataSetChanged();
        this.mTextViewHeadlineName.setText(getResources().getString(R.string.Album_Track_List) + " - " + (this.mRecordIndex + 1) + " ");
        this.mRecordIndex++;
        if (this.mRecordIndex < this.mRecordCount) {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.RequestGetAlbumTrackTitle;
//            this.mMessage.arg1 = this.mRecordIndex;
            this.MediaManagerSendMessage(Constants.RequestGetAlbumTrackTitle, this.mRecordIndex);
            return;
        }
        this.mListAlbumTrackBackup.clear();
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListAlbumTrack.size()) {
            this.mListAlbumTrackBackup.add(this.mListAlbumTrack.get(this.mRecordIndex));
            this.mRecordIndex++;
        }
        this.mViewFlipperList.showNext();
        this.mViewFlipperList.showNext();
        updateListHeadline();
        if (!this.mRestoreValid.booleanValue()) {
            this.mRestoreValid = true;
            onItemClick(this.mGridViewAlbumTrackList, this.mGridViewAlbumTrackList.getChildAt(this.mTrackIndex), this.mTrackIndex, (long) this.mTrackIndex);
        }
    }

    private void handleResponsePlayTrack(Message message) {
        this.mBundle = message.getData();
        this.mRecordPath = this.mBundle.getString("Track_Path");
        this.mLrcFlag = this.mBundle.getBoolean("LRC_Path_Valid");
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListTrack.size()) {
            if (this.mListTrack.get(this.mRecordIndex).get("ListItemPath").equals(this.mRecordPath)) {
                this.mPlayPosition = this.mRecordIndex;
                this.mListTrack.get(this.mRecordIndex).put("ListItemState", Integer.valueOf(R.drawable.playing));
                this.mSharedPreferencesEditor.putInt("Track_Category", 0);
                this.mSharedPreferencesEditor.putInt("Track_Index", this.mRecordIndex);
                this.mSharedPreferencesEditor.putString("Track_Path", this.mRecordPath);
                this.mSharedPreferencesEditor.commit();
            } else {
                this.mListTrack.get(this.mRecordIndex).put("ListItemState", 0);
            }
            this.mRecordIndex++;
        }
        this.mAdapterTrack.notifyDataSetChanged();
        displayLrc();
        if (message.obj != null) {
            this.mImageViewThumbnail.setImageBitmap((Bitmap) message.obj);
        } else {
            this.mImageViewThumbnail.setImageResource(R.drawable.music_list_track);
        }
        this.mTextViewTrackTitle.setText((String) this.mListTrack.get(this.mPlayPosition).get("ListItemName"));
        this.mTextViewParentTitle.setText((String) this.mListTrack.get(this.mPlayPosition).get("ListItemArtistName"));
        this.mImageViewPlayPause.setImageResource(R.drawable.play_pause_over);
        this.mViewPlayValid = true;
        MediaManagerSendEmptyMessage(Constants.RequestGetTrackPlayProgress);
    }

    private void handleResponsePlayArtistTrack(Message message) {
        this.mBundle = message.getData();
        this.mRecordPath = this.mBundle.getString("Track_Path");
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListArtistTrack.size()) {
            if (this.mListArtistTrack.get(this.mRecordIndex).get("ListItemPath").equals(this.mRecordPath)) {
                this.mPlayPosition = this.mRecordIndex;
                this.mListArtistTrack.get(this.mRecordIndex).put("ListItemState", Integer.valueOf(R.drawable.playing));
                this.mSharedPreferencesEditor.putInt("Track_Category", 1);
                this.mSharedPreferencesEditor.putInt("Track_Index", this.mRecordIndex);
                this.mSharedPreferencesEditor.putInt("Parent_Index", this.mArtistSelectionPosition);
                this.mSharedPreferencesEditor.putString("Track_Path", this.mRecordPath);
                this.mSharedPreferencesEditor.commit();
            } else {
                this.mListArtistTrack.get(this.mRecordIndex).put("ListItemState", 0);
            }
            this.mRecordIndex++;
        }
        this.mAdapterArtistTrack.notifyDataSetChanged();
        if (message.obj != null) {
            this.mImageViewThumbnail.setImageBitmap((Bitmap) message.obj);
        } else {
            this.mImageViewThumbnail.setImageResource(R.drawable.music_list_track);
        }
        this.mTextViewTrackTitle.setText((String) this.mListArtistTrack.get(this.mPlayPosition).get("ListItemName"));
        this.mTextViewParentTitle.setText((String) this.mListArtistTrack.get(this.mPlayPosition).get("ListItemArtistName"));
        this.mImageViewPlayPause.setImageResource(R.drawable.play_pause_over);
        this.mViewPlayValid = true;
        MediaManagerSendEmptyMessage(Constants.RequestGetTrackPlayProgress);
    }

    private void handleResponsePlayAlbumTrack(Message message) {
        this.mBundle = message.getData();
        this.mRecordPath = this.mBundle.getString("Track_Path");
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListAlbumTrack.size()) {
            if (this.mListAlbumTrack.get(this.mRecordIndex).get("ListItemPath").equals(this.mRecordPath)) {
                this.mPlayPosition = this.mRecordIndex;
                this.mListAlbumTrack.get(this.mRecordIndex).put("ListItemState", Integer.valueOf(R.drawable.playing));
                this.mSharedPreferencesEditor.putInt("Track_Category", 2);
                this.mSharedPreferencesEditor.putInt("Track_Index", this.mRecordIndex);
                this.mSharedPreferencesEditor.putInt("Parent_Index", this.mAlbumSelectionPosition);
                this.mSharedPreferencesEditor.putString("Track_Path", this.mRecordPath);
                this.mSharedPreferencesEditor.commit();
            } else {
                this.mListAlbumTrack.get(this.mRecordIndex).put("ListItemState", 0);
            }
            this.mRecordIndex++;
        }
        this.mAdapterAlbumTrack.notifyDataSetChanged();
        if (message.obj != null) {
            this.mImageViewThumbnail.setImageBitmap((Bitmap) message.obj);
        } else {
            this.mImageViewThumbnail.setImageResource(R.drawable.music_list_track);
        }
        this.mTextViewTrackTitle.setText((String) this.mListAlbumTrack.get(this.mPlayPosition).get("ListItemName"));
        this.mTextViewParentTitle.setText((String) this.mListAlbumTrack.get(this.mPlayPosition).get("ListItemArtistName"));
        this.mImageViewPlayPause.setImageResource(R.drawable.play_pause_over);
        this.mViewPlayValid = true;
        MediaManagerSendEmptyMessage(Constants.RequestGetTrackPlayProgress);
    }

    private void handleResponseGetTrackPlayProgress(Message message) {
        String trackProgress;
        String trackProgress2;
        String trackProgress3;
        String trackDuration;
        String trackDuration2;
        String trackDuration3;
        updateLrc(message.arg1);
        int hour = message.arg1 / 3600000;
        int minute = (message.arg1 - (((hour * 60) * 60) * 1000)) / 60000;
        int second = ((message.arg1 - (((hour * 60) * 60) * 1000)) - ((minute * 60) * 1000)) / 1000;
        if (this.mCanbusHelper != null) {
            this.mCanbusHelper.ReportStatusToCan(this.mPlayPosition + 1, message.arg1 / 60000, second);
        }
        if (hour < 10) {
            trackProgress = "0" + hour;
        } else {
            trackProgress = Integer.toString(hour);
        }
        if (minute < 10) {
            trackProgress2 = trackProgress + ":0" + minute;
        } else {
            trackProgress2 = trackProgress + ":" + minute;
        }
        if (second < 10) {
            trackProgress3 = trackProgress2 + ":0" + second;
        } else {
            trackProgress3 = trackProgress2 + ":" + second;
        }
        int hour2 = message.arg2 / 3600000;
        int minute2 = (message.arg2 - (((hour2 * 60) * 60) * 1000)) / 60000;
        int second2 = ((message.arg2 - (((hour2 * 60) * 60) * 1000)) - ((minute2 * 60) * 1000)) / 1000;
        if (hour2 < 10) {
            trackDuration = "0" + hour2;
        } else {
            trackDuration = Integer.toString(hour2);
        }
        if (minute2 < 10) {
            trackDuration2 = trackDuration + ":0" + minute2;
        } else {
            trackDuration2 = trackDuration + ":" + minute2;
        }
        if (second2 < 10) {
            trackDuration3 = trackDuration2 + ":0" + second2;
        } else {
            trackDuration3 = trackDuration2 + ":" + second2;
        }
        this.mTextViewProgress.setText(trackProgress3);
        this.mTextViewLength.setText(trackDuration3);
        this.mSeekBarPosition.setProgress((message.arg1 * 100) / message.arg2);
        this.mSharedPreferencesEditor.putInt("Track_Position", message.arg1);
        this.mSharedPreferencesEditor.commit();
        if (this.mViewPlayValid) {
            this.mMediaManager.getHandler().removeMessages(Constants.RequestGetTrackPlayProgress);
            this.mMediaManager.getHandler().sendEmptyMessageDelayed(28, 1000);
        } else if (this.mViewFlipperMain.getCurrentView().getId() == R.id.LinearLayoutPlay) {
            this.mViewFlipperMain.showPrevious();
        }
    }

    private void handleTrackPlayComplete(Message message) {
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListTrack.size()) {
            if (message.arg1 >= 0 &&
                    message.arg1 < this.mListPlayTrack.size() &&
                    this.mListTrack.get(this.mRecordIndex).get("ListItemPath").equals(this.mListPlayTrack.get(message.arg1))) {
                this.mListTrack.get(this.mRecordIndex).put("ListItemState", 0);
            }
            this.mRecordIndex++;
        }
        this.mAdapterTrack.notifyDataSetChanged();
        this.mImageViewThumbnail.setImageResource(0);
        this.mTextViewTrackTitle.setText(R.string.Track_Name);
        this.mTextViewParentTitle.setText(R.string.Artist_Name);
        this.mTextViewProgress.setText(R.string.Track_Time);
        this.mTextViewLength.setText(R.string.Track_Time);
        this.mSeekBarPosition.setProgress(0);
        this.mSharedPreferencesEditor.putInt("Track_Position", 0);
        this.mSharedPreferencesEditor.commit();
    }

    private void handleArtistTrackPlayComplete(Message message) {
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListArtistTrack.size()) {
            if (message.arg1 >= 0 && message.arg1 < this.mListPlayTrack.size() && this.mListArtistTrack.get(this.mRecordIndex).get("ListItemPath").equals(this.mListPlayTrack.get(message.arg1))) {
                this.mListArtistTrack.get(this.mRecordIndex).put("ListItemState", 0);
            }
            this.mRecordIndex++;
        }
        this.mAdapterArtistTrack.notifyDataSetChanged();
        this.mImageViewThumbnail.setImageResource(0);
        this.mTextViewTrackTitle.setText(R.string.Track_Name);
        this.mTextViewParentTitle.setText(R.string.Artist_Name);
        this.mTextViewProgress.setText(R.string.Track_Time);
        this.mTextViewLength.setText(R.string.Track_Time);
        this.mSeekBarPosition.setProgress(0);
        this.mSharedPreferencesEditor.putInt("Track_Position", 0);
        this.mSharedPreferencesEditor.commit();
    }

    private void handleAlbumTrackPlayComplete(Message message) {
        this.mRecordIndex = 0;
        while (this.mRecordIndex < this.mListAlbumTrack.size()) {
            if (message.arg1 >= 0 && message.arg1 < this.mListPlayTrack.size() && this.mListAlbumTrack.get(this.mRecordIndex).get("ListItemPath").equals(this.mListPlayTrack.get(message.arg1))) {
                this.mListAlbumTrack.get(this.mRecordIndex).put("ListItemState", 0);
            }
            this.mRecordIndex++;
        }
        this.mAdapterAlbumTrack.notifyDataSetChanged();
        this.mImageViewThumbnail.setImageResource(0);
        this.mTextViewTrackTitle.setText(R.string.Track_Name);
        this.mTextViewParentTitle.setText(R.string.Artist_Name);
        this.mTextViewProgress.setText(R.string.Track_Time);
        this.mTextViewLength.setText(R.string.Track_Time);
        this.mSeekBarPosition.setProgress(0);
        this.mSharedPreferencesEditor.putInt("Track_Position", 0);
        this.mSharedPreferencesEditor.commit();
    }

    private void handleAllTrackComplete() {
        this.mSharedPreferencesEditor.putInt("Track_Category", -1);
        this.mSharedPreferencesEditor.putInt("Track_Index", -1);
        this.mSharedPreferencesEditor.putInt("Parent_Index", -1);
        this.mSharedPreferencesEditor.putString("Track_Path", null);
        this.mSharedPreferencesEditor.putInt("Track_Position", 0);
        this.mSharedPreferencesEditor.commit();
        this.mViewPlayValid = false;
        while (this.mViewFlipperMain.getCurrentView().getId() != this.mViewList.getId()) {
            this.mViewFlipperMain.showNext();
        }
    }

    private void handleResponsePlayLoop(Message message) {
        switch (message.arg1) {
            case Constants.PlayMode_loopOff:
                this.mImageViewPlayLoop.setImageResource(R.drawable.play_loop);
                return;
            case Constants.PlayMode_loopOver:
                this.mImageViewPlayLoop.setImageResource(R.drawable.play_loop_over);
                return;
            case Constants.PlayMode_loopOverSingle:
                this.mImageViewPlayLoop.setImageResource(R.drawable.play_single_loop_over);
                return;
            default:
                return;
        }
    }

    private void handleResponsePlayPause(Message message) {
        if (message.arg1 == 1) {
            this.mImageViewPlayPause.setImageResource(R.drawable.play_pause_over);
        } else {
            this.mImageViewPlayPause.setImageResource(R.drawable.play_play_over);
        }
    }

    private void handleResponseChangeProgress() {
    }

    private void handleResponsePlayShuffle(Message message) {
        if (message.arg1 == Constants.ShuffleMode_Off) {
            this.mImageViewPlayShuffle.setImageResource(R.drawable.play_shuffle);
        } else {
            this.mImageViewPlayShuffle.setImageResource(R.drawable.play_shuffle_over);
        }
    }

    private void handleResponsePauseTrack() {
        this.mImageViewPlayPause.setImageResource(R.drawable.play_play_over);
    }

    private void handleResponseResumeTrack() {
        this.mImageViewPlayPause.setImageResource(R.drawable.play_pause_over);
    }

    private void handleResponseCloseDatabase() {
        finish();
    }

    private void handleResponseRestart() {
        this.mIntent = new Intent(this, MainActivity.class);
        startActivity(this.mIntent);
        finish();
    }

    private void handleResponseRetrieveTrackDatabase() {
        this.mListTrack.clear();
        this.mListTrack.addAll(this.mMediaManager.getTrackList());
        this.mAdapterTrack.notifyDataSetChanged();
        for (int index = 0; index < this.mListTrack.size(); index++) {
            String artistName = (String) this.mListTrack.get(index).get("Artist_Name");
            if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
                artistName = getResources().getString(R.string.Unknown_Name);
            }
            this.mListTrack.get(index).put("ListItemArtistName", artistName);
        }
        this.mListTrackBackup.clear();
        this.mListTrackBackup.addAll(this.mListTrack);
        this.mTrackCount = this.mListTrack.size();
        Log.i("MusicPlayer", "this.mTrackCount=" + this.mListTrack.size());
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.RequestUpdateTrackBitmap;
//        this.mMessage.arg1 = 0;
//        this.mMessage.arg2 = Constants.RequestGetArtistTitle;
        this.MediaManagerSendMessage(Constants.RequestUpdateTrackBitmap, 0, Constants.RequestGetArtistTitle);
    }

    private void handleResponseRetrieveArtistDatabase() {
        this.mListArtist.clear();
        this.mListArtist.addAll(this.mMediaManager.getArtistList());
        this.mAdapterArtist.notifyDataSetChanged();
        for (int index = 0; index < this.mListArtist.size(); index++) {
            String artistName = (String) this.mListArtist.get(index).get("ListItemName");
            if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
                artistName = getResources().getString(R.string.Unknown_Name);
            }
            this.mListArtist.get(index).put("ListItemName", artistName);
        }
        this.mListArtistBackup.clear();
        this.mListArtistBackup.addAll(this.mListArtist);
        this.mArtistCount = this.mListArtist.size();
    }

    private void handleResponseRetrieveAlbumDatabase() {
        this.mListAlbum.clear();
        this.mListAlbum.addAll(this.mMediaManager.getAlbumList());
        this.mAdapterAlbum.notifyDataSetChanged();
        for (int index = 0; index < this.mListAlbum.size(); index++) {
            String albumName = (String) this.mListAlbum.get(index).get("ListItemName");
            if (albumName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
                albumName = getResources().getString(R.string.Unknown_Name);
            }
            this.mListAlbum.get(index).put("ListItemName", albumName);
        }
        this.mListAlbumBackup.clear();
        this.mListAlbumBackup.addAll(this.mListAlbum);
        this.mAlbumCount = this.mListAlbum.size();
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.RequestUpdateAlbumBitmap;
//        this.mMessage.arg1 = 0;
//        this.mMessage.arg2 = Constants.RequestGetArtistTitle;
        this.MediaManagerSendMessage(Constants.RequestUpdateAlbumBitmap, 0, Constants.RequestGetArtistTitle);
        this.mHandlerMain.sendEmptyMessage(1);
    }

    private void handleResponseRetrieveArtistTrackDatabase() {
        this.mListArtistTrack.clear();
        this.mListArtistTrack.addAll(this.mMediaManager.getArtistTrackList());
        this.mAdapterArtistTrack.notifyDataSetChanged();
        for (int index = 0; index < this.mListArtistTrack.size(); index++) {
            String artistName = (String) this.mListArtistTrack.get(index).get("Artist_Name");
            if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
                artistName = getResources().getString(R.string.Unknown_Name);
            }
            this.mListArtistTrack.get(index).put("ListItemArtistName", artistName);
        }
        this.mListArtistTrackBackup.clear();
        this.mListArtistTrackBackup.addAll(this.mListArtistTrack);
        this.mArtistTrackCount = this.mListArtistTrack.size();
        this.mViewFlipperList.showNext();
        this.mViewFlipperList.showNext();
        updateListHeadline();
        if (!this.mRestoreValid.booleanValue()) {
            this.mRestoreValid = true;
            onItemClick(this.mGridViewArtistTrackList, this.mGridViewArtistTrackList.getChildAt(this.mTrackIndex), this.mTrackIndex, (long) this.mTrackIndex);
        }
    }

    private void handleResponseRetrieveAlbumTrackDatabase() {
        this.mListAlbumTrack.clear();
        this.mListAlbumTrack.addAll(this.mMediaManager.getAlbumTrackList());
        this.mAdapterAlbumTrack.notifyDataSetChanged();
        for (int index = 0; index < this.mListAlbumTrack.size(); index++) {
            String artistName = (String) this.mListAlbumTrack.get(index).get("Artist_Name");
            if (artistName.equalsIgnoreCase(getResources().getString(R.string.Invalid_Name))) {
                artistName = getResources().getString(R.string.Unknown_Name);
            }
            this.mListAlbumTrack.get(index).put("ListItemArtistName", artistName);
        }
        this.mListAlbumTrackBackup.clear();
        this.mListAlbumTrackBackup.addAll(this.mListAlbumTrack);
        this.mAlbumTrackCount = this.mListAlbumTrack.size();
        this.mViewFlipperList.showNext();
        this.mViewFlipperList.showNext();
        updateListHeadline();
        if (!this.mRestoreValid.booleanValue()) {
            this.mRestoreValid = true;
            onItemClick(this.mGridViewAlbumTrackList, this.mGridViewAlbumTrackList.getChildAt(this.mTrackIndex), this.mTrackIndex, (long) this.mTrackIndex);
        }
    }

    private void handleResponseUpdateTrackBitmap(Message message) {
        List<Bitmap> list = this.mMediaManager.getTrackBitmapList();
        for (int index = message.arg1; index <= message.arg2; index++) {
            if (index - message.arg1 < list.size() && index < this.mListTrack.size()) {
                this.mRecordBitmap = list.get(index - message.arg1);
                if (this.mRecordBitmap != null) {
                    this.mListTrack.get(index).put("ListItemImage", this.mRecordBitmap);
                } else {
                    this.mListTrack.get(index).put("ListItemImage", Integer.valueOf(R.drawable.style_tracklist_item));
                }
            }
        }
        this.mAdapterTrack.notifyDataSetChanged();
    }

    private void handleResponseUpdateAlbumBitmap(Message message) {
        List<Bitmap> list = this.mMediaManager.getAlbumBitmapList();
        for (int index = message.arg1; index <= message.arg2; index++) {
            if (index - message.arg1 < list.size() && index < this.mListAlbum.size()) {
                this.mRecordBitmap = list.get(index - message.arg1);
                if (this.mRecordBitmap != null) {
                    this.mListAlbum.get(index).put("ListItemImage", this.mRecordBitmap);
                } else {
                    this.mListAlbum.get(index).put("ListItemImage", Integer.valueOf(R.drawable.style_albumlist_item));
                }
            }
        }
        this.mAdapterAlbum.notifyDataSetChanged();
    }

    private void handleResponseUpdateCount() {
        Log.i("MusicPlayer", "this.mMediaManager.getTrackCount()=" + this.mMediaManager.getTrackCount());
        Log.i("MusicPlayer", "this.mListTrack.size()=" + this.mListTrack.size());
        if (this.mMediaManager.getTrackCount() != this.mListTrack.size()) {
            MediaManagerSendEmptyMessage(Constants.RequestRetrieveTrackDatabase);
        }
        if (this.mMediaManager.getArtistCount() != this.mListArtist.size()) {
            MediaManagerSendEmptyMessage(Constants.RequestRetrieveArtistDatabase);
        }
        if (this.mMediaManager.getAlbumCount() != this.mListAlbum.size()) {
            MediaManagerSendEmptyMessage(Constants.RequestRetrieveAlbumDatabase);
        }
    }

    private void handleRequestRecoverPlay() {
        if (!this.mRestoreValid.booleanValue()) {
            this.mRestoreValid = true;
            this.mCategoryIndex = this.mSharedPreferences.getInt("Track_Category", -1);
            this.mParentIndex = this.mSharedPreferences.getInt("Parent_Index", -1);
            this.mTrackIndex = this.mSharedPreferences.getInt("Track_Index", -1);
            this.mRecordPath = this.mSharedPreferences.getString("Track_Path", null);
            Log.d("MusicPlayer : ", "Resume playback information");
            if (this.mCategoryIndex < 0 || this.mTrackIndex < 0 || this.mRecordPath == null) {
                this.mRecoverState = 1;
            } else {
                this.mRecoverState = recoverPlayMemory();
            }
            this.mRecoverCount++;
            if (this.mRecoverState == 0 || (this.mRecoverState == 1 && this.mRecoverCount == 3)) {
                this.mMessage = Message.obtain();
                this.mMessage.what = Constants.ResponseRecoverPlay;
                this.mMessage.arg1 = this.mRecoverState;
                this.mHandlerMain.sendMessage(this.mMessage);
                return;
            }
            this.mHandlerMain.sendEmptyMessageDelayed(Constants.RequestRecoverPlay, 3000);
        }
    }

    private void handleResponseRecoverPlay(Message message) {
        if (message.arg1 != 0) {
            this.mRestoreValid = true;
            onItemClick(this.mGridViewTrackList, this.mGridViewTrackList.getChildAt(0), 0, 0);
        }
    }

    private void loadCanbus() {
        String[] carBrand = {"vw", "crv", "golf", "opel", "RaiseVw", "BagooBenz", "BagooAudi", "RaiseTrailTeana", "SimpleHyundai"};
        String[] classArray = {vw.class.getName(), crv.class.getName(), golf.class.getName(), opel.class.getName(), RaiseVw.class.getName(), BagooBenz.class.getName(), BagooAudi.class.getName(), RaiseTrailTeana.class.getName(), SimpleHyundai.class.getName()};
        String mCarBrand = SystemProperties.get("persist.sys.carBrand");
        int i = 0;
        while (i < carBrand.length) {
            try {
                if (mCarBrand.equals(carBrand[i])) {
                    this.mCanbusHelper = (ICanbus) getClassLoader().loadClass(classArray[i]).newInstance();
                }
                i++;
            } catch (InstantiationException e) {
                Log.e("MusicPlayer : ", "loadCanbus " + e.getMessage());
            } catch (IllegalAccessException e2) {
                Log.e("MusicPlayer : ", "loadCanbus " + e2.getMessage());
            } catch (ClassNotFoundException e3) {
                Log.e("MusicPlayer : ", "loadCanbus " + e3.getMessage());
            }
        }
        if (this.mCanbusHelper != null) {
            this.mCanbusHelper.SetContext(this);
        }
    }

    private class NameComparator implements Comparator<Map<String, Object>> {
        private NameComparator() {
        }

        public int compare(Map<String, Object> source, Map<String, Object> destination) {
            return Collator.getInstance(Locale.US).compare((String) source.get("ListItemName"), (String) destination.get("ListItemName"));
        }
    }

    private class TypeComparator implements Comparator<Map<String, Object>> {
        private TypeComparator() {
        }

        public int compare(Map<String, Object> source, Map<String, Object> destination) {
            return ((String) source.get("ListItemType")).compareTo((String) destination.get("ListItemType"));
        }
    }

    private class SizeComparator implements Comparator<Map<String, Object>> {
        private SizeComparator() {
        }

        public int compare(Map<String, Object> source, Map<String, Object> destination) {
            return ((Long) source.get("ListItemSize")).compareTo((Long) destination.get("ListItemSize"));
        }
    }

    private class TimeComparator implements Comparator<Map<String, Object>> {
        private TimeComparator() {
        }

        public int compare(Map<String, Object> source, Map<String, Object> destination) {
            return ((Long) source.get("ListItemTime")).compareTo((Long) destination.get("ListItemTime"));
        }
    }
}
