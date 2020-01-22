package cn.com.cs2c.musicplayer;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaManager extends Thread implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, Handler.Callback {
    public static final Uri ALBUM_CONTENT_URI = Uri.parse("content://media/external/audio/albumart");
    private Pattern albumPattern = Pattern.compile("\\[al:(\\w*)\\]", Pattern.CASE_INSENSITIVE);
    private String[] albumPrejection = {"album"};
    private Pattern artistPattern = Pattern.compile("\\[ar:(\\w*)\\]", Pattern.CASE_INSENSITIVE);
    private String[] artistPrejection = {"artist"};
    private Pattern editorPattern = Pattern.compile("\\[by:(\\w*)\\]", Pattern.CASE_INSENSITIVE);
    private List<Bitmap> mAlbumBitmapList;
    private int mAlbumCount;
    private List<Map<String, Object>> mAlbumList;
    private List<Map<String, Object>> mAlbumTrackList;
    private int mArtistCount;
    private List<Map<String, Object>> mArtistList;
    private List<Map<String, Object>> mArtistTrackList;
    private AudioManager mAudioManager;
    private BufferedReader mBufferedReader;
    private Bundle mBundle;
    private ContentResolver mContentResolverMain;
    private Context mContext;
    private Cursor mCursor;
    private Handler mHandlerMain;
    private Handler mHandlerMediaManager;
    /**
     * play list with filenames
     */
    private List<String> mListPlayTrack;
    private File mLrcFile;
    private boolean mLrcFlag;
    private List<String> mLrcKey;
    private String mLrcLine;
    private List<Map<String, Object>> mLrcValue;
    private MediaPlayer mMediaPlayer;
    private Message mMessage;
    private int mPlayCommand;
    /**
     * PlayMode_loopOff, PlayMode_loopOver, PlayMode_loopOverSingle
     */
    private int mPlayMode;
    /**
     * index of currently playing item in a playlist
     */
    private int mPlayPosition;
    private Bitmap mRecordBitmap;
    private int mRecordCount;
    private long mRecordID;
    private int mRecordIndex;
    private Map<String, Object> mRecordMap;
    private String mRecordName;
    private String mRecordPath;
    private int mRecordSize;
    private String mRecordTime;
    private String mRecordType;
    private Uri mRecordUri;
    private SharedPreferences mSharedPreferences;
    /**
     * ShuffleMode_Off = 0; ShuffleMode_On = 1;
     */
    private int mShuffleMode;
    private List<Bitmap> mTrackBitmapList;
    private int mTrackCount;
    private List<Map<String, Object>> mTrackList;
    private int mVolume;
    private Pattern timePattern = Pattern.compile("\\[(\\d+):(\\d+)[.|:](\\d+)\\]([\\w\\W]*)", Pattern.CASE_INSENSITIVE);
    private Pattern titlePattern = Pattern.compile("\\[ti:(\\w*)\\]", Pattern.CASE_INSENSITIVE);
    private long trackLengthLimitation = 20000;
    private String[] trackPrejection = {"_display_name", "mime_type", "_size", "date_modified", "_data", "artist", "duration"};
    private String trackSelection = "duration>?";
    private String[] trackSelectionArgs = {"20000"};

    MediaManager(Context context, Handler handler, ContentResolver contentResolver) {
        this.mContext = context;
        this.mHandlerMain = handler;
        this.mContentResolverMain = contentResolver;
        this.mSharedPreferences = this.mContext.getSharedPreferences(this.mContext.getResources().getString(R.string.Music_Trace), 0);
        this.mShuffleMode = Constants.ShuffleMode_On;
        this.mAudioManager = (AudioManager) this.mContext.getSystemService(Context.AUDIO_SERVICE);
        this.mVolume = 14;
    }

    private void HandlerMainSendEmptyMessage(int msg) {
        if (this.mHandlerMain == null)
            return;

        this.mHandlerMain.sendEmptyMessage(msg);
    }

    private void HandlerMainSendMessage(int what, int arg1) {
        this.HandlerMainSendMessage(what, arg1, 0, null);
    }

    private void HandlerMainSendMessage(int what, int arg1, Object obj) {
        this.HandlerMainSendMessage(what, arg1, 0, obj);
    }

    private void HandlerMainSendMessage(int what, int arg1, int arg2) {
        this.HandlerMainSendMessage(what, arg1, arg2, null);
    }

    private void HandlerMainSendMessage(int what, int arg1, int arg2, Object obj) {
        if (this.mHandlerMain == null)
            return;

        this.mMessage = Message.obtain();
        this.mMessage.what = what;
        this.mMessage.arg1 = arg1;
        this.mMessage.arg2 = arg2;
        this.mMessage.obj = obj;
        this.mHandlerMain.sendMessage(this.mMessage);
    }

    private void HandlerMainSendMessageWithData(int what, Object obj) {
        if (this.mHandlerMain == null)
            return;

        this.mMessage = Message.obtain();
        this.mMessage.what = what;
//        this.mMessage.arg1 = arg1;
//        this.mMessage.arg1 = arg2;
        this.mMessage.obj = obj;

        if (this.mBundle != null)
            this.mMessage.setData(this.mBundle);

        this.mHandlerMain.sendMessage(this.mMessage);
    }

    public void run() {
//        String storageState;
        super.run();
//        do {
//            storageState = Environment.getStorageState(new File("/mnt/internal_sd"));
//            if (storageState.equalsIgnoreCase("mounted") || storageState.equalsIgnoreCase("mounted_ro")) {
//                this.mMediaPlayer = new MediaPlayer();
//                this.mMediaPlayer.setOnErrorListener(this);
//                this.mMediaPlayer.setOnCompletionListener(this);
//                this.mPlayMode = Constants.PlayMode_loopOver;
//                this.mTrackList = new ArrayList();
//                this.mArtistList = new ArrayList();
//                this.mAlbumList = new ArrayList();
//                this.mArtistTrackList = new ArrayList();
//                this.mAlbumTrackList = new ArrayList();
//                this.mAlbumBitmapList = new ArrayList();
//                this.mTrackBitmapList = new ArrayList();
//                Looper.prepare();
//                this.mHandlerMediaManager = new Handler(this);
//                break;//TODO: ?
//            }
////            storageState = Environment.getStorageState(new File("/mnt/internal_sd"));
////            break;
//        } while (storageState.equalsIgnoreCase("mounted_ro"));
        this.mMediaPlayer = new MediaPlayer();
        this.mMediaPlayer.setOnErrorListener(this);
        this.mMediaPlayer.setOnCompletionListener(this);
        this.mPlayMode = Constants.PlayMode_loopOff;
        this.mTrackList = new ArrayList();
        this.mArtistList = new ArrayList();
        this.mAlbumList = new ArrayList();
        this.mArtistTrackList = new ArrayList();
        this.mAlbumTrackList = new ArrayList();
        this.mAlbumBitmapList = new ArrayList();
        this.mTrackBitmapList = new ArrayList();
        Looper.prepare();
        this.mHandlerMediaManager = new Handler(this);
        if (MusicPlayerBootCompleteBroadcastReceiver.firstTimeRunFlag) {
            MusicPlayerBootCompleteBroadcastReceiver.firstTimeRunFlag = false;
            if (MusicPlayerBootCompleteBroadcastReceiver.bootCompleteFlag) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "run " + e.getMessage());
                }
            } else {
                try {
                    Thread.sleep(4000);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "run " + e.getMessage());
                }
            }
        } else {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                Log.e("MusicPlayer", "run " + e.getMessage());
            }
        }
        this.HandlerMainSendEmptyMessage(-4);
        Looper.loop();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        MediaPlayerStop();
        this.mMediaPlayer.reset();
        this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
        this.HandlerMainSendEmptyMessage(-3);
        return true;
    }

    public void onCompletion(MediaPlayer mediaPlayer) {
        switch (this.mPlayCommand) {
            case Constants.RequestPlayTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.TrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
                break;
            case Constants.RequestPlayArtistTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.ArtistTrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.ArtistTrackPlayComplete, this.mPlayPosition);
                break;
            case Constants.RequestPlayAlbumTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.AlbumTrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.AlbumTrackPlayComplete, this.mPlayPosition);
                break;
        }
        switch (this.mPlayMode) {
            case Constants.PlayMode_loopOff:
                this.mPlayPosition++;
                if (this.mPlayPosition < this.mListPlayTrack.size()) {
                    while (true) {
                        try {
                            if (!new File(this.mListPlayTrack.get(this.mPlayPosition)).exists()) {
//                                this.mMessage = Message.obtain();
//                                this.mMessage.what = Constants.TrackPlayComplete;
//                                this.mMessage.arg1 = this.mPlayPosition;
                                this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
                                this.mPlayPosition++;
                                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            return;
                        }
                    }
                    if (this.mPlayPosition < this.mListPlayTrack.size()) {
                        this.mMediaPlayer.reset();
                        try {
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                        } catch (IOException e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        try {
                            this.mMediaPlayer.prepare();
                        } catch (IOException e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        MediaPlayerStart();
                        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                        this.mCursor.moveToFirst();
                        this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                        while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                            this.mCursor.moveToNext();
                        }
                        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                        this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                        this.mRecordBitmap = null;
                        try {
                            this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                        } catch (Exception e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        this.mBundle = new Bundle();
                        this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
//                        this.mMessage = Message.obtain();
                        switch (this.mPlayCommand) {
                            case Constants.RequestPlayTrack:
//                                this.mMessage.what = Constants.ResponsePlayTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                break;
                            case Constants.RequestPlayArtistTrack:
//                                this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                break;
                            case Constants.RequestPlayAlbumTrack:
//                                this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                break;
                        }
//                        this.mMessage.obj = this.mRecordBitmap;
//                        this.mMessage.setData(this.mBundle);
//                        this.mHandlerMain.sendMessage(this.mMessage);
                        return;
                    }
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    this.HandlerMainSendEmptyMessage(-3);
                    return;
                }
                MediaPlayerStop();
                this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                return;
            case Constants.PlayMode_loopOver:
                this.mPlayPosition = (this.mPlayPosition + 1) % this.mListPlayTrack.size();
                if (this.mPlayPosition < this.mListPlayTrack.size()) {
                    while (true) {
                        try {
                            if (!new File(this.mListPlayTrack.get(this.mPlayPosition)).exists()) {
//                                this.mMessage = Message.obtain();
//                                this.mMessage.what = Constants.TrackPlayComplete;
//                                this.mMessage.arg1 = this.mPlayPosition;
                                this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
                                this.mPlayPosition++;
                                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                            return;
                        }
                    }
                    if (this.mPlayPosition < this.mListPlayTrack.size()) {
                        this.mMediaPlayer.reset();
                        try {
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                        } catch (IOException e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        try {
                            this.mMediaPlayer.prepare();
                        } catch (IOException e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        MediaPlayerStart();
                        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                        this.mCursor.moveToFirst();
                        this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                        while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                            this.mCursor.moveToNext();
                        }
                        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                        this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                        this.mRecordBitmap = null;
                        try {
                            this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                        } catch (Exception e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        this.mBundle = new Bundle();
                        this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
//                        this.mMessage = Message.obtain();
                        switch (this.mPlayCommand) {
                            case Constants.RequestPlayTrack:
//                                this.mMessage.what = Constants.ResponsePlayTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                break;
                            case Constants.RequestPlayArtistTrack:
//                                this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                break;
                            case Constants.RequestPlayAlbumTrack:
//                                this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                break;
                        }
//                        this.mMessage.obj = this.mRecordBitmap;
//                        this.mMessage.setData(this.mBundle);
//                        this.mHandlerMain.sendMessage(this.mMessage);
                        return;
                    }
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    this.HandlerMainSendEmptyMessage(-3);
                    return;
                }
                MediaPlayerStop();
                this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                return;
            case Constants.PlayMode_loopOverSingle:
                if (this.mPlayPosition < this.mListPlayTrack.size()) {
                    while (true) {
                        try {
                            if (!new File(this.mListPlayTrack.get(this.mPlayPosition)).exists()) {
//                                this.mMessage = Message.obtain();
//                                this.mMessage.what = Constants.TrackPlayComplete;
//                                this.mMessage.arg1 = this.mPlayPosition;
                                this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
                                this.mPlayPosition++;
                                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                            return;
                        }
                    }
                    if (this.mPlayPosition < this.mListPlayTrack.size()) {
                        this.mMediaPlayer.reset();
                        try {
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                        } catch (IOException e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        try {
                            this.mMediaPlayer.prepare();
                        } catch (IOException e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        MediaPlayerStart();
                        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                        this.mCursor.moveToFirst();
                        this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                        while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                            this.mCursor.moveToNext();
                        }
                        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                        this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                        this.mRecordBitmap = null;
                        try {
                            this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                        } catch (Exception e) {
                            Log.e("MusicPlayer", "onCompletion " + this.mPlayMode);
                        }
                        this.mBundle = new Bundle();
                        this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
//                        this.mMessage = Message.obtain();
                        switch (this.mPlayCommand) {
                            case Constants.RequestPlayTrack:
//                                this.mMessage.what = Constants.ResponsePlayTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                break;
                            case Constants.RequestPlayArtistTrack:
//                                this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                break;
                            case Constants.RequestPlayAlbumTrack:
//                                this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                break;
                        }
//                        this.mMessage.obj = this.mRecordBitmap;
//                        this.mMessage.setData(this.mBundle);
//                        this.mHandlerMain.sendMessage(this.mMessage);
                        return;
                    }
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    this.HandlerMainSendEmptyMessage(-3);
                    return;
                }
                MediaPlayerStop();
                this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                return;
            default:
                return;
        }
    }

    private void MediaPlayerStart() {
        decreaseVolume();
        this.mMediaPlayer.start();
        increaseVolume();
    }

    private void MediaPlayerStop() {
        decreaseVolume();
        this.mMediaPlayer.stop();
        increaseVolume();
    }

    public Handler getHandler() {
        return this.mHandlerMediaManager;
    }

    public List<Map<String, Object>> getTrackList() {
        return this.mTrackList;
    }

    public List<Map<String, Object>> getArtistList() {
        return this.mArtistList;
    }

    public List<Map<String, Object>> getAlbumList() {
        return this.mAlbumList;
    }

    public List<Map<String, Object>> getArtistTrackList() {
        return this.mArtistTrackList;
    }

    public List<Map<String, Object>> getAlbumTrackList() {
        return this.mAlbumTrackList;
    }

    public List<Bitmap> getAlbumBitmapList() {
        return this.mAlbumBitmapList;
    }

    public List<Bitmap> getTrackBitmapList() {
        return this.mTrackBitmapList;
    }

    public int getTrackCount() {
        return this.mTrackCount;
    }

    public int getArtistCount() {
        return this.mArtistCount;
    }

    public int getAlbumCount() {
        return this.mAlbumCount;
    }

    public List<String> getLrcKey() {
        return this.mLrcKey;
    }

    public List<Map<String, Object>> getLrcValue() {
        return this.mLrcValue;
    }

    private void getLrc(File file) {
        String lrcPath;
        int minute;
        int second;
        int percentileSecond;
        int dotPosition = file.getAbsolutePath().lastIndexOf(46);
        if (dotPosition > -1) {
            lrcPath = file.getAbsolutePath().substring(0, dotPosition) + ".lrc";
        } else {
            lrcPath = null;
        }
        if (lrcPath != null) {
            this.mLrcFile = new File(lrcPath);
            if (!this.mLrcFile.isFile() || !this.mLrcFile.exists()) {
                this.mLrcFlag = false;
                return;
            }
            this.mLrcFlag = true;
            try {
                this.mBufferedReader = new BufferedReader(new FileReader(this.mLrcFile));
                this.mLrcKey = new ArrayList();
                this.mLrcValue = new ArrayList();
                do {
                    try {
                        this.mLrcLine = this.mBufferedReader.readLine();
                    } catch (Exception e) {
                        this.mLrcLine = null;
                        Log.e("MusicPlayer", "getLrc " + this.mPlayMode);
                    }
                    if (this.mLrcLine != null && this.mLrcLine.length() > 0) {
                        Matcher titleMatcher = this.titlePattern.matcher(this.mLrcLine);
                        Matcher artistMatcher = this.artistPattern.matcher(this.mLrcLine);
                        Matcher albumMatcher = this.albumPattern.matcher(this.mLrcLine);
                        Matcher editorMatcher = this.editorPattern.matcher(this.mLrcLine);
                        Matcher timeMatcher = this.timePattern.matcher(this.mLrcLine);
                        if (titleMatcher.find()) {
                            String content = titleMatcher.group(1);
                            this.mLrcKey.add("Title");
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Content", content);
                            this.mLrcValue.add(hashMap);
                        }
                        if (artistMatcher.find()) {
                            String content2 = artistMatcher.group(1);
                            this.mLrcKey.add("Artist");
                            HashMap<String, Object> hashMap2 = new HashMap<>();
                            hashMap2.put("Content", content2);
                            this.mLrcValue.add(hashMap2);
                        }
                        if (albumMatcher.find()) {
                            String content3 = albumMatcher.group(1);
                            this.mLrcKey.add("Album");
                            HashMap<String, Object> hashMap3 = new HashMap<>();
                            hashMap3.put("Content", content3);
                            this.mLrcValue.add(hashMap3);
                        }
                        if (editorMatcher.find()) {
                            String content4 = editorMatcher.group(1);
                            this.mLrcKey.add("Editor");
                            HashMap<String, Object> hashMap4 = new HashMap<>();
                            hashMap4.put("Content", content4);
                            this.mLrcValue.add(hashMap4);
                        }
                        if (timeMatcher.find() && timeMatcher.groupCount() >= 3) {
                            try {
                                minute = Integer.parseInt(timeMatcher.group(1));
                            } catch (Exception e) {
                                minute = 0;
                                Log.e("MusicPlayer", "getLrc " + this.mPlayMode);
                            }
                            try {
                                second = Integer.parseInt(timeMatcher.group(2));
                            } catch (Exception e) {
                                second = 0;
                                Log.e("MusicPlayer", "getLrc " + this.mPlayMode);
                            }
                            try {
                                percentileSecond = Integer.parseInt(timeMatcher.group(3));
                            } catch (Exception e) {
                                percentileSecond = 0;
                                Log.e("MusicPlayer", "getLrc " + this.mPlayMode);
                            }
                            String time = Integer.toString((minute * 60 * 1000) + (second * 1000) + (percentileSecond * 10));
                            String content5 = timeMatcher.group(4);
                            this.mLrcKey.add(time);
                            HashMap<String, Object> hashMap5 = new HashMap<>();
                            hashMap5.put("Content", content5);
                            this.mLrcValue.add(hashMap5);
                        }
                    }
                } while (this.mLrcLine != null);
                try {
                    this.mBufferedReader.close();
                } catch (IOException e) {
                    Log.e("MusicPlayer", "getLrc " + this.mPlayMode);
                }
            } catch (FileNotFoundException e) {
                this.mLrcFlag = false;
                Log.e("MusicPlayer", "getLrc " + this.mPlayMode);
            }
        } else {
            this.mLrcFlag = false;
        }
    }

    private void decreaseVolume() {
        int volume = this.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        while (volume != 0) {
            Log.d("zys", "volume - " + volume);
            volume--;
            this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e("MusicPlayer", "decreaseVolume " + this.mPlayMode);
            }
        }
    }

    private void increaseVolume() {
        int volume = 0;
        while (volume != this.mVolume) {
            Log.d("zys", "volume + " + volume);
            volume++;
            this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e("MusicPlayer", "increaseVolume " + this.mPlayMode);
            }
        }
        Log.d("zys", "volume - " + volume);
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case Constants.RequestGetTrackCount://2
                handleRequestGetTrackCount();
                return true;
            case Constants.RequestGetTrackTitle://4
                handleRequestGetTrackTitle(message);
                return true;
            case Constants.RequestGetArtistCount://6
                handleRequestGetArtistCount();
                return true;
            case Constants.RequestGetArtistTitle://8
                handleRequestGetArtistTitle(message);
                return true;
            case Constants.RequestGetAlbumCount://10
                handleRequestGetAlbumCount();
                return true;
            case Constants.RequestGetAlbumTitle://12
                handleRequestGetAlbumTitle(message);
                return true;
            case Constants.RequestGetArtistTrackCount://14
                handleRequestGetArtistTrackCount(message);
                return true;
            case Constants.RequestGetArtistTrackTitle://16
                handleRequestGetArtistTrackTitle(message);
                return true;
            case Constants.RequestGetAlbumTrackCount://18
                handleRequestGetAlbumTrackCount(message);
                return true;
            case Constants.RequestGetAlbumTrackTitle://20
                handleRequestGetAlbumTrackTitle(message);
                return true;
            case Constants.RequestPlayTrack://22
                handleRequestPlayTrack(message);
                return true;
            case Constants.RequestPlayArtistTrack://24
                handleRequestPlayArtistTrack(message);
                return true;
            case Constants.RequestPlayAlbumTrack://26
                handleRequestPlayAlbumTrack(message);
                return true;
            case Constants.RequestGetTrackPlayProgress://28
                handleRequestGetTrackPlayProgress();
                return true;
            case Constants.RequestPlayLoop://34
                handleRequestPlayLoop();
                return true;
            case Constants.RequestPlayPrevious://36
                handleRequestPlayPrevious(message);
                return true;
            case Constants.RequestPlayPause://38
                handleRequestPlayPause();
                return true;
            case Constants.RequestPlayNext://40
                handleRequestPlayNext(message);
                return true;
            case Constants.RequestChangeProgress://42
                handleRequestChangeProgress(message);
                return true;
            case Constants.RequestPlayShuffle://44
                handleRequestPlayShuffle();
                return true;
            case Constants.RequestPauseTrack://46
                handleRequestPauseTrack();
                return true;
            case Constants.RequestResumeTrack://48
                handleRequestResumeTrack();
                return true;
            case Constants.RequestCloseDatabase://50
                handleRequestCloseDatabase();
                return true;
            case Constants.RequestRestart://52
                handleRequestRestart();
                return true;
            case Constants.RequestRetrieveTrackDatabase://54
                handleRequestRetrieveTrackDatabase();
                return true;
            case Constants.RequestRetrieveArtistDatabase://56
                handleRequestRetrieveArtistDatabase();
                return true;
            case Constants.RequestRetrieveAlbumDatabase://58
                handleRequestRetrieveAlbumDatabase();
                return true;
            case Constants.RequestRetrieveArtistTrackDatabase://60
                handleRequestRetrieveArtistTrackDatabase(message);
                return true;
            case Constants.RequestRetrieveAlbumTrackDatabase://62
                handleRequestRetrieveAlbumTrackDatabase(message);
                return true;
            case Constants.RequestUpdateTrackBitmap://64
                handleRequestUpdateTrackBitmap(message);
                return true;
            case Constants.RequestUpdateAlbumBitmap://66
                handleRequestUpdateAlbumBitmap(message);
                return true;
            case Constants.RequestUpdateCount://68
                handleRequestUpdateCount();
                return true;
            default:
                return true;
        }
    }

    private void handleRequestGetTrackCount() {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseGetTrackCount;
//        this.mMessage.arg1 = this.mRecordCount;
        this.HandlerMainSendMessage(Constants.ResponseGetTrackCount, this.mRecordCount);
    }

    private void handleRequestGetTrackTitle(Message message) {
        this.mRecordIndex = message.arg1;
        if (this.mRecordIndex < this.mRecordCount) {
            this.mRecordName = this.mCursor.getString(this.mCursor.getColumnIndex("_display_name"));
            this.mRecordType = this.mCursor.getString(this.mCursor.getColumnIndex("mime_type"));
            this.mRecordSize = this.mCursor.getInt(this.mCursor.getColumnIndex("_size"));
            this.mRecordTime = this.mCursor.getString(this.mCursor.getColumnIndex("date_modified"));
            this.mRecordPath = this.mCursor.getString(this.mCursor.getColumnIndex("_data"));
            this.mBundle = new Bundle();
            this.mBundle.putString("Track_Name", this.mRecordName);
            this.mBundle.putString("Track_Type", this.mRecordType);
            this.mBundle.putInt("Track_Size", this.mRecordSize);
            this.mBundle.putString("Track_Time", this.mRecordTime);
            this.mBundle.putString("Track_Path", this.mRecordPath);
            this.mBundle.putString("Artist_Name", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
            this.mRecordBitmap = null;
            try {
                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
            } catch (Exception e) {
                Log.e("MusicPlayer", "RequestGetTrackTitle " + this.mPlayMode);
            }
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetTrackTitle;
//            this.mMessage.setData(this.mBundle);
//            this.mMessage.obj = this.mRecordBitmap;
//            this.mHandlerMain.sendMessage(this.mMessage);
            HandlerMainSendMessageWithData(Constants.ResponseGetTrackTitle, this.mRecordBitmap);
            if (this.mRecordIndex != this.mRecordCount - 1) {
                this.mCursor.moveToNext();
            }
        }
    }

    private void handleRequestGetArtistCount() {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null, "artist_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseGetArtistCount;
//        this.mMessage.arg1 = this.mRecordCount;
        this.HandlerMainSendMessage(Constants.ResponseGetArtistCount, this.mRecordCount);
    }

    private void handleRequestGetArtistTitle(Message message) {
        this.mRecordIndex = message.arg1;
        if (this.mRecordIndex < this.mRecordCount) {
            this.mRecordName = this.mCursor.getString(this.mCursor.getColumnIndex("artist"));
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetArtistTitle;
//            this.mMessage.obj = this.mRecordName;
            this.HandlerMainSendMessage(Constants.ResponseGetArtistTitle, 0, this.mRecordName);
            if (this.mRecordIndex != this.mRecordCount - 1) {
                this.mCursor.moveToNext();
            }
        }
    }

    private void handleRequestGetAlbumCount() {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, "album_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseGetAlbumtCount;
//        this.mMessage.arg1 = this.mRecordCount;
        this.HandlerMainSendMessage(Constants.ResponseGetAlbumtCount, this.mRecordCount);
    }

    private void handleRequestGetAlbumTitle(Message message) {
        this.mRecordIndex = message.arg1;
        if (this.mRecordIndex < this.mRecordCount) {
            this.mRecordName = this.mCursor.getString(this.mCursor.getColumnIndex("album"));
            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("_id"));
            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
            this.mRecordBitmap = null;
            try {
                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
            } catch (Exception e) {
                Log.e("MusicPlayer", "RequestGetAlbumTitle " + this.mPlayMode);
            }
            this.mBundle = new Bundle();
            this.mBundle.putString("Album_Name", this.mRecordName);
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetAlbumTitle;
//            this.mMessage.obj = this.mRecordBitmap;
//            this.mMessage.setData(this.mBundle);
//            this.mHandlerMain.sendMessage(this.mMessage);
            HandlerMainSendMessageWithData(Constants.ResponseGetAlbumTitle, this.mRecordBitmap);
            if (this.mRecordIndex != this.mRecordCount - 1) {
                this.mCursor.moveToNext();
            }
        }
    }

    private void handleRequestGetArtistTrackCount(Message message) {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, null, null, null, "artist_key");
        this.mCursor.moveToPosition(message.arg1);
        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("_id"));
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "artist_id=" + this.mRecordID, null, "title_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseGetArtistTrackCount;
//        this.mMessage.arg1 = this.mRecordCount;
        this.HandlerMainSendMessage(Constants.ResponseGetArtistTrackCount, this.mRecordCount);
    }

    private void handleRequestGetArtistTrackTitle(Message message) {
        this.mRecordIndex = message.arg1;
        if (this.mRecordIndex < this.mRecordCount) {
            this.mRecordName = this.mCursor.getString(this.mCursor.getColumnIndex("title"));
            this.mRecordType = this.mCursor.getString(this.mCursor.getColumnIndex("mime_type"));
            this.mRecordSize = this.mCursor.getInt(this.mCursor.getColumnIndex("_size"));
            this.mRecordTime = this.mCursor.getString(this.mCursor.getColumnIndex("date_modified"));
            this.mRecordPath = this.mCursor.getString(this.mCursor.getColumnIndex("_data"));
            this.mBundle = new Bundle();
            this.mBundle.putString("Track_Name", this.mRecordName);
            this.mBundle.putString("Track_Type", this.mRecordType);
            this.mBundle.putInt("Track_Size", this.mRecordSize);
            this.mBundle.putString("Track_Time", this.mRecordTime);
            this.mBundle.putString("Track_Path", this.mRecordPath);
            this.mBundle.putString("Artist_Name", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetArtistTrackTitle;
//            this.mMessage.setData(this.mBundle);
//            this.mHandlerMain.sendMessage(this.mMessage);
            HandlerMainSendMessageWithData(Constants.ResponseGetArtistTrackTitle, null);
            this.mCursor.moveToNext();
        }
    }

    private void handleRequestGetAlbumTrackCount(Message message) {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, "album_key");
        this.mCursor.moveToPosition(message.arg1);
        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("_id"));
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "album_id=" + this.mRecordID, null, "title_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseGetAlbumTrackCount;
//        this.mMessage.arg1 = this.mRecordCount;
        this.HandlerMainSendMessage(Constants.ResponseGetAlbumTrackCount, this.mRecordCount);
    }

    private void handleRequestGetAlbumTrackTitle(Message message) {
        this.mRecordIndex = message.arg1;
        if (this.mRecordIndex < this.mRecordCount) {
            this.mRecordName = this.mCursor.getString(this.mCursor.getColumnIndex("title"));
            this.mRecordType = this.mCursor.getString(this.mCursor.getColumnIndex("mime_type"));
            this.mRecordSize = this.mCursor.getInt(this.mCursor.getColumnIndex("_size"));
            this.mRecordTime = this.mCursor.getString(this.mCursor.getColumnIndex("date_modified"));
            this.mRecordPath = this.mCursor.getString(this.mCursor.getColumnIndex("_data"));
            this.mBundle = new Bundle();
            this.mBundle.putString("Track_Name", this.mRecordName);
            this.mBundle.putString("Track_Type", this.mRecordType);
            this.mBundle.putInt("Track_Size", this.mRecordSize);
            this.mBundle.putString("Track_Time", this.mRecordTime);
            this.mBundle.putString("Track_Path", this.mRecordPath);
            this.mBundle.putString("Artist_Name", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetAlbumTrackTitle;
//            this.mMessage.setData(this.mBundle);
//            this.mHandlerMain.sendMessage(this.mMessage);
            HandlerMainSendMessageWithData(Constants.ResponseGetAlbumTrackTitle, null);
            this.mCursor.moveToNext();
        }
    }

    private void handleRequestPlayTrack(Message message) {
        int trackPosition;
        this.mListPlayTrack = (ArrayList) message.obj;
        this.mPlayPosition = message.arg1;
        this.mPlayCommand = Constants.RequestPlayTrack;
        File file = new File(this.mListPlayTrack.get(this.mPlayPosition));
        if (file.exists()) {
            String trackPath = this.mSharedPreferences.getString("Track_Path", null);
            if (trackPath == null || !trackPath.equalsIgnoreCase(file.getAbsolutePath())) {
                trackPosition = 0;
            } else {
                trackPosition = this.mSharedPreferences.getInt("Track_Position", 0);
            }
            getLrc(file);
            try {
                this.mMediaPlayer.reset();
                this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.seekTo(trackPosition);
                MediaPlayerStart();
                this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                this.mCursor.moveToFirst();
                this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                    this.mCursor.moveToNext();
                }
                this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                this.mRecordBitmap = null;
                try {
                    this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "RequestPlayTrack " + this.mPlayMode);
                }
                this.mBundle = new Bundle();
                this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.ResponsePlayTrack;
//                this.mMessage.obj = this.mRecordBitmap;
//                this.mMessage.setData(this.mBundle);
//                this.mHandlerMain.sendMessage(this.mMessage);
                HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
            } catch (Exception e) {
                Log.e("MusicPlayer", "RequestPlayTrack " + this.mPlayMode);
            }
        } else {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.TrackPlayComplete;
//            this.mMessage.arg1 = this.mPlayPosition;
            this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
            this.HandlerMainSendEmptyMessage(-3);
        }
    }

    private void handleRequestPlayArtistTrack(Message message) {
        int trackPosition;
        this.mListPlayTrack = (ArrayList) message.obj;
        this.mPlayPosition = message.arg1;
        this.mPlayCommand = Constants.RequestPlayArtistTrack;
        File file = new File(this.mListPlayTrack.get(this.mPlayPosition));
        if (file.exists()) {
            String trackPath = this.mSharedPreferences.getString("Track_Path", null);
            if (trackPath == null || !trackPath.equalsIgnoreCase(file.getAbsolutePath())) {
                trackPosition = 0;
            } else {
                trackPosition = this.mSharedPreferences.getInt("Track_Position", 0);
            }
            getLrc(file);
            try {
                this.mMediaPlayer.reset();
                this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.seekTo(trackPosition);
                MediaPlayerStart();
                this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                this.mCursor.moveToFirst();
                this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                    this.mCursor.moveToNext();
                }
                this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                this.mRecordBitmap = null;
                try {
                    this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "RequestPlayArtistTrack " + this.mPlayMode);
                }
                this.mBundle = new Bundle();
                this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.ResponsePlayArtistTrack;
//                this.mMessage.obj = this.mRecordBitmap;
//                this.mMessage.setData(this.mBundle);
//                this.mHandlerMain.sendMessage(this.mMessage);
                HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
            } catch (Exception e) {
                Log.e("MusicPlayer", "RequestPlayArtistTrack " + this.mPlayMode);
            }
        } else {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ArtistTrackPlayComplete;
//            this.mMessage.arg1 = this.mPlayPosition;
            this.HandlerMainSendMessage(Constants.ArtistTrackPlayComplete, this.mPlayPosition);
            this.HandlerMainSendEmptyMessage(-3);
        }
    }

    private void handleRequestPlayAlbumTrack(Message message) {
        int trackPosition;
        this.mListPlayTrack = (ArrayList) message.obj;
        this.mPlayPosition = message.arg1;
        this.mPlayCommand = Constants.RequestPlayAlbumTrack;
        try {
            File file = new File(this.mListPlayTrack.get(this.mPlayPosition));
            if (file.exists()) {
                String trackPath = this.mSharedPreferences.getString("Track_Path", null);
                if (trackPath == null || !trackPath.equalsIgnoreCase(file.getAbsolutePath())) {
                    trackPosition = 0;
                } else {
                    trackPosition = this.mSharedPreferences.getInt("Track_Position", 0);
                }
                getLrc(file);
                this.mMediaPlayer.reset();
                this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                this.mMediaPlayer.prepare();
                this.mMediaPlayer.seekTo(trackPosition);
                MediaPlayerStart();
                this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                this.mCursor.moveToFirst();
                this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                    this.mCursor.moveToNext();
                }
                this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                this.mRecordBitmap = null;
                try {
                    this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "RequestPlayAlbumTrack " + this.mPlayMode);
                }
                this.mBundle = new Bundle();
                this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.ResponsePlayAlbumTrack;
//                this.mMessage.obj = this.mRecordBitmap;
//                this.mMessage.setData(this.mBundle);
//                this.mHandlerMain.sendMessage(this.mMessage);
                HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                return;
            }
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.AlbumTrackPlayComplete;
//            this.mMessage.arg1 = this.mPlayPosition;
            this.HandlerMainSendMessage(Constants.AlbumTrackPlayComplete, this.mPlayPosition);
            this.HandlerMainSendEmptyMessage(-3);
        } catch (Exception e) {
            Log.e("MusicPlayer", "RequestPlayAlbumTrack " + this.mPlayMode);
        }
    }

    private void handleRequestGetTrackPlayProgress() {
        if (this.mMediaPlayer.isPlaying()) {
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetTrackPlayProgress;
//            this.mMessage.arg1 = this.mMediaPlayer.getCurrentPosition();
//            this.mMessage.arg2 = this.mMediaPlayer.getDuration();
//            this.mMessage.obj = this.mLrcLine;
            this.HandlerMainSendMessage(Constants.ResponseGetTrackPlayProgress, this.mMediaPlayer.getCurrentPosition(), this.mMediaPlayer.getDuration(), this.mLrcLine);
        }
    }

    private void handleRequestPlayLoop() {
        Log.e("MusicPlayer", "handleRequestPlayLoop PlayMode=" + this.mPlayMode);
        switch (this.mPlayMode) {
            case Constants.PlayMode_loopOff:
                this.mPlayMode = Constants.PlayMode_loopOver;
                break;
            case Constants.PlayMode_loopOver:
                this.mPlayMode = Constants.PlayMode_loopOverSingle;
                break;
            case Constants.PlayMode_loopOverSingle:
                this.mPlayMode = Constants.PlayMode_loopOff;
                break;
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponsePlayLoop;
//        this.mMessage.arg1 = this.mPlayMode;
        this.HandlerMainSendMessage(Constants.ResponsePlayLoop, this.mPlayMode);
    }

    private void handleRequestPlayPrevious(Message message) {
        switch (this.mPlayCommand) {
            case Constants.RequestPlayTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.TrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
                break;
            case Constants.RequestPlayArtistTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.ArtistTrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.ArtistTrackPlayComplete, this.mPlayPosition);
                break;
            case Constants.RequestPlayAlbumTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.AlbumTrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.AlbumTrackPlayComplete, this.mPlayPosition);
                break;
        }
        switch (this.mPlayMode) {
            case 0:
                this.mPlayPosition -= message.arg1;
                if (this.mPlayPosition < 0) {
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    break;
                } else {
                    try {
                        File file = new File(this.mListPlayTrack.get(this.mPlayPosition));
                        if (file.exists()) {
                            getLrc(file);
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                            this.mMediaPlayer.prepare();
                            MediaPlayerStart();
                            this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                            this.mCursor.moveToFirst();
                            this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                            while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                                this.mCursor.moveToNext();
                            }
                            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                            this.mRecordBitmap = null;
                            try {
                                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                            } catch (Exception e) {
                                Log.e("MusicPlayer", "RequestPlayPrevious " + e.getMessage());
                            }
                            this.mBundle = new Bundle();
                            this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                            this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                            this.mMessage = Message.obtain();
                            switch (this.mPlayCommand) {
                                case Constants.RequestPlayTrack:
//                                    this.mMessage.what = Constants.ResponsePlayTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayArtistTrack:
//                                    this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayAlbumTrack:
//                                    this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                    break;
                            }
//                            this.mMessage.obj = this.mRecordBitmap;
//                            this.mMessage.setData(this.mBundle);
//                            this.mHandlerMain.sendMessage(this.mMessage);
                            break;
                        } else {
                            this.mHandlerMediaManager.sendEmptyMessage(Constants.RequestPlayPrevious);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("MusicPlayer", "RequestPlayPrevious " + e.getMessage());
                        break;
                    }
                }
            case 1:
                this.mPlayPosition -= message.arg1;
                if (this.mPlayPosition < 0) {
                    this.mPlayPosition += this.mListPlayTrack.size();
                }
                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    break;
                } else {
                    try {
                        File file2 = new File(this.mListPlayTrack.get(this.mPlayPosition));
                        if (file2.exists()) {
                            getLrc(file2);
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                            this.mMediaPlayer.prepare();
                            MediaPlayerStart();
                            this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                            this.mCursor.moveToFirst();
                            this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                            while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                                this.mCursor.moveToNext();
                            }
                            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                            this.mRecordBitmap = null;
                            try {
                                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                            } catch (Exception e) {
                                Log.e("MusicPlayer", "RequestPlayPrevious " + e.getMessage());
                            }
                            this.mBundle = new Bundle();
                            this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                            this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                            this.mMessage = Message.obtain();
                            switch (this.mPlayCommand) {
                                case Constants.RequestPlayTrack:
//                                    this.mMessage.what = Constants.ResponsePlayTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayArtistTrack:
//                                    this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayAlbumTrack:
//                                    this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                    break;
                            }
//                            this.mMessage.obj = this.mRecordBitmap;
//                            this.mMessage.setData(this.mBundle);
//                            this.mHandlerMain.sendMessage(this.mMessage);
                            break;
                        } else {
                            this.mHandlerMediaManager.sendEmptyMessage(Constants.RequestPlayPrevious);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("MusicPlayer", "RequestPlayPrevious " + e.getMessage());
                        break;
                    }
                }
            case 2:
                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    break;
                } else {
                    try {
                        File file3 = new File(this.mListPlayTrack.get(this.mPlayPosition));
                        if (file3.exists()) {
                            getLrc(file3);
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                            this.mMediaPlayer.prepare();
                            MediaPlayerStart();
                            this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                            this.mCursor.moveToFirst();
                            this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                            while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                                this.mCursor.moveToNext();
                            }
                            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                            this.mRecordBitmap = null;
                            try {
                                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                            } catch (Exception e) {
                                Log.e("MusicPlayer", "RequestPlayPrevious " + e.getMessage());
                            }
                            this.mBundle = new Bundle();
                            this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                            this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                            this.mMessage = Message.obtain();
                            switch (this.mPlayCommand) {
                                case Constants.RequestPlayTrack:
//                                    this.mMessage.what = Constants.ResponsePlayTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayArtistTrack:
//                                    this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayAlbumTrack:
//                                    this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                    break;
                            }
//                            this.mMessage.obj = this.mRecordBitmap;
//                            this.mMessage.setData(this.mBundle);
//                            this.mHandlerMain.sendMessage(this.mMessage);
                            break;
                        } else {
                            this.mHandlerMediaManager.sendEmptyMessage(Constants.RequestPlayPrevious);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("MusicPlayer", "RequestPlayPrevious " + e.getMessage());
                        break;
                    }
                }
        }
        this.mHandlerMediaManager.removeMessages(Constants.RequestPlayPrevious);
    }

    private void handleRequestPlayPause() {
        if (this.mMediaPlayer.isPlaying()) {
            MediaPlayerPause();
        } else {
            MediaPlayerStart();
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetTrackPlayProgress;
//            this.mMessage.arg1 = this.mMediaPlayer.getCurrentPosition();
//            this.mMessage.arg2 = this.mMediaPlayer.getDuration();
            this.HandlerMainSendMessage(Constants.ResponseGetTrackPlayProgress, this.mMediaPlayer.getCurrentPosition(), this.mMediaPlayer.getDuration());
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponsePlayPause;
//        this.mMessage.arg1 = this.mMediaPlayer.isPlaying() ? 1 : 0;
        this.HandlerMainSendMessage(Constants.ResponsePlayPause, this.mMediaPlayer.isPlaying() ? 1 : 0);
    }

    private void handleRequestPlayNext(Message message) {
        switch (this.mPlayCommand) {
            case Constants.RequestPlayTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.TrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.TrackPlayComplete, this.mPlayPosition);
                break;
            case Constants.RequestPlayArtistTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.ArtistTrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.ArtistTrackPlayComplete, this.mPlayPosition);
                break;
            case Constants.RequestPlayAlbumTrack:
//                this.mMessage = Message.obtain();
//                this.mMessage.what = Constants.AlbumTrackPlayComplete;
//                this.mMessage.arg1 = this.mPlayPosition;
                this.HandlerMainSendMessage(Constants.AlbumTrackPlayComplete, this.mPlayPosition);
                break;
        }
        switch (this.mPlayMode) {
            case 0:
                this.mPlayPosition++;
                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    break;
                } else {
                    try {
                        File file = new File(this.mListPlayTrack.get(this.mPlayPosition));
                        if (file.exists()) {
                            getLrc(file);
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                            this.mMediaPlayer.prepare();
                            MediaPlayerStart();
                            this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                            this.mCursor.moveToFirst();
                            this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                            while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                                this.mCursor.moveToNext();
                            }
                            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                            this.mRecordBitmap = null;
                            try {
                                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                            } catch (Exception e) {
                                Log.e("RequestPlayNext", e.getMessage());
                            }
                            this.mBundle = new Bundle();
                            this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                            this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                            this.mMessage = Message.obtain();
                            switch (this.mPlayCommand) {
                                case Constants.RequestPlayTrack:
//                                    this.mMessage.what = Constants.ResponsePlayTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayArtistTrack:
//                                    this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayAlbumTrack:
//                                    this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                    break;
                            }
//                            this.mMessage.obj = this.mRecordBitmap;
//                            this.mMessage.setData(this.mBundle);
//                            this.mHandlerMain.sendMessage(this.mMessage);
                            break;
                        } else {
                            this.mHandlerMediaManager.sendEmptyMessage(Constants.RequestPlayNext);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("RequestPlayNext", e.getMessage());
                        break;
                    }
                }
            case 1:
                this.mPlayPosition = (this.mPlayPosition + 1) % this.mListPlayTrack.size();
                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    break;
                } else {
                    try {
                        File file2 = new File(this.mListPlayTrack.get(this.mPlayPosition));
                        if (file2.exists()) {
                            getLrc(file2);
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                            this.mMediaPlayer.prepare();
                            MediaPlayerStart();
                            this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                            this.mCursor.moveToFirst();
                            this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                            while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                                this.mCursor.moveToNext();
                            }
                            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                            this.mRecordBitmap = null;
                            try {
                                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                            } catch (Exception e) {
                                Log.e("RequestPlayNext", e.getMessage());
                            }
                            this.mBundle = new Bundle();
                            this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                            this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                            this.mMessage = Message.obtain();
                            switch (this.mPlayCommand) {
                                case Constants.RequestPlayTrack:
//                                    this.mMessage.what = Constants.ResponsePlayTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayArtistTrack:
//                                    this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayAlbumTrack:
//                                    this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                    break;
                            }
//                            this.mMessage.obj = this.mRecordBitmap;
//                            this.mMessage.setData(this.mBundle);
//                            this.mHandlerMain.sendMessage(this.mMessage);
                            break;
                        } else {
                            this.mHandlerMediaManager.sendEmptyMessage(Constants.RequestPlayNext);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("RequestPlayNext", e.getMessage());
                        break;
                    }
                }
            case 2:
                if (this.mPlayPosition >= this.mListPlayTrack.size()) {
                    MediaPlayerStop();
                    this.HandlerMainSendEmptyMessage(Constants.AllTrackComplete);
                    break;
                } else {
                    try {
                        File file3 = new File(this.mListPlayTrack.get(this.mPlayPosition));
                        if (file3.exists()) {
                            getLrc(file3);
                            this.mMediaPlayer.reset();
                            this.mMediaPlayer.setDataSource(this.mListPlayTrack.get(this.mPlayPosition));
                            this.mMediaPlayer.prepare();
                            MediaPlayerStart();
                            this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
                            this.mCursor.moveToFirst();
                            this.mRecordPath = this.mListPlayTrack.get(this.mPlayPosition);
                            while (!this.mRecordPath.equalsIgnoreCase(this.mCursor.getString(this.mCursor.getColumnIndex("_data")))) {
                                this.mCursor.moveToNext();
                            }
                            this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                            this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                            this.mRecordBitmap = null;
                            try {
                                this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                            } catch (Exception e) {
                                Log.e("RequestPlayNext", e.getMessage());
                            }
                            this.mBundle = new Bundle();
                            this.mBundle.putString("Track_Path", this.mListPlayTrack.get(this.mPlayPosition));
                            this.mBundle.putBoolean("LRC_Path_Valid", this.mLrcFlag);
//                            this.mMessage = Message.obtain();
                            switch (this.mPlayCommand) {
                                case Constants.RequestPlayTrack:
//                                    this.mMessage.what = Constants.ResponsePlayTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayArtistTrack:
//                                    this.mMessage.what = Constants.ResponsePlayArtistTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayArtistTrack, this.mRecordBitmap);
                                    break;
                                case Constants.RequestPlayAlbumTrack:
//                                    this.mMessage.what = Constants.ResponsePlayAlbumTrack;
                                    HandlerMainSendMessageWithData(Constants.ResponsePlayAlbumTrack, this.mRecordBitmap);
                                    break;
                            }
//                            this.mMessage.obj = this.mRecordBitmap;
//                            this.mMessage.setData(this.mBundle);
//                            this.mHandlerMain.sendMessage(this.mMessage);
                            break;
                        } else {
                            this.mHandlerMediaManager.sendEmptyMessage(Constants.RequestPlayNext);
                            return;
                        }
                    } catch (Exception e) {
                        Log.e("RequestPlayNext", e.getMessage());
                        break;
                    }
                }
        }
        this.mHandlerMediaManager.removeMessages(Constants.RequestPlayNext);
    }

    private void handleRequestChangeProgress(Message message) {
        decreaseVolume();
        this.mMediaPlayer.seekTo((this.mMediaPlayer.getDuration() * message.arg1) / 100);
        increaseVolume();
        this.HandlerMainSendEmptyMessage(Constants.ResponseChangeProgress);
    }

    private void handleRequestPlayShuffle() {
        if (this.mShuffleMode == Constants.ShuffleMode_Off) {
            if (this.mPlayPosition != 0) {
                Collections.shuffle(this.mListPlayTrack.subList(0, this.mPlayPosition - 1));
                Collections.shuffle(this.mListPlayTrack, new Random(new Date().getTime()));
            }
            if (this.mPlayPosition < this.mListPlayTrack.size() - 1) {
                Collections.shuffle(this.mListPlayTrack.subList(this.mPlayPosition + 1, this.mListPlayTrack.size()));
            }
            this.mShuffleMode = Constants.ShuffleMode_On;
        } else {
            this.mShuffleMode = Constants.ShuffleMode_Off;
        }
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponsePlayShuffle;
//        this.mMessage.arg1 = this.mShuffleMode;
        this.HandlerMainSendMessage(Constants.ResponsePlayShuffle, this.mShuffleMode);
    }

    private void handleRequestPauseTrack() {
        if (this.mMediaPlayer.isPlaying()) {
            MediaPlayerPause();
            this.HandlerMainSendEmptyMessage(Constants.ResponsePauseTrack);
        }
    }

    private void MediaPlayerPause() {
        decreaseVolume();
        this.mMediaPlayer.pause();
        increaseVolume();
    }

    private void handleRequestResumeTrack() {
        if (!this.mMediaPlayer.isPlaying()) {
            MediaPlayerStart();
//            this.mMessage = Message.obtain();
//            this.mMessage.what = Constants.ResponseGetTrackPlayProgress;
//            this.mMessage.arg1 = this.mMediaPlayer.getCurrentPosition();
//            this.mMessage.arg2 = this.mMediaPlayer.getDuration();
            this.HandlerMainSendMessage(Constants.ResponseGetTrackPlayProgress, this.mMediaPlayer.getCurrentPosition(), this.mMediaPlayer.getDuration());
            this.HandlerMainSendEmptyMessage(Constants.ResponseResumeTrack);
        }
    }

    private void handleRequestCloseDatabase() {
        this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, this.mVolume, 0);
        Log.d("zys", "volume = " + this.mVolume);
        this.mMediaPlayer.release();
        if (this.mCursor != null) {
            this.mCursor.close();
        }
        Looper.myLooper().quit();
        this.HandlerMainSendEmptyMessage(Constants.ResponseCloseDatabase);
    }

    private void handleRequestRestart() {
        this.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, this.mVolume, 0);
        this.mMediaPlayer.release();
        if (this.mCursor != null) {
            this.mCursor.close();
        }
        Looper.myLooper().quit();
        this.HandlerMainSendEmptyMessage(Constants.ResponseRestart);
    }

    private void handleRequestRetrieveTrackDatabase() {
        this.mTrackList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.trackPrejection, this.trackSelection, this.trackSelectionArgs, "title_key");
        if (this.mCursor.getCount() > 0) {
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast()) {
                this.mRecordMap = new HashMap();
                this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_tracklist_item));
                this.mRecordMap.put("ListItemName", this.mCursor.getString(this.mCursor.getColumnIndex("_display_name")));
                this.mRecordMap.put("ListItemType", this.mCursor.getString(this.mCursor.getColumnIndex("mime_type")));
                this.mRecordMap.put("ListItemSize", Long.valueOf(this.mCursor.getLong(this.mCursor.getColumnIndex("_size"))));
                this.mRecordMap.put("ListItemTime", Long.valueOf(this.mCursor.getLong(this.mCursor.getColumnIndex("date_modified"))));
                this.mRecordMap.put("ListItemState", 0);
                this.mRecordMap.put("ListItemPath", this.mCursor.getString(this.mCursor.getColumnIndex("_data")));
                this.mRecordMap.put("Artist_Name", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
                this.mTrackList.add(this.mRecordMap);
                this.mCursor.moveToNext();
            }

            if (this.mShuffleMode == Constants.ShuffleMode_On) {
                Collections.shuffle(this.mTrackList, new Random(new Date().getTime()));
            }
        }
        this.mCursor.close();
        this.HandlerMainSendEmptyMessage(Constants.ResponseRetrieveTrackDatabase);
    }

    private void handleRequestRetrieveArtistDatabase() {
        this.mArtistList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, this.artistPrejection, null, null, "artist_key");
        if (this.mCursor.getCount() > 0) {
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast()) {
                this.mRecordMap = new HashMap();
                this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_artistlist_item));
                this.mRecordMap.put("ListItemName", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
                this.mRecordMap.put("ListItemState", 0);
                this.mArtistList.add(this.mRecordMap);
                this.mCursor.moveToNext();
            }
        }
        this.mCursor.close();
        this.HandlerMainSendEmptyMessage(Constants.ResponseRetrieveArtistDatabase);
    }

    private void handleRequestRetrieveAlbumDatabase() {
        this.mAlbumList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, this.albumPrejection, null, null, "album_key");
        if (this.mCursor.getCount() > 0) {
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast()) {
                this.mRecordMap = new HashMap();
                this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.style_albumlist_item));
                this.mRecordMap.put("ListItemName", this.mCursor.getString(this.mCursor.getColumnIndex("album")));
                this.mRecordMap.put("ListItemState", 0);
                this.mAlbumList.add(this.mRecordMap);
                this.mCursor.moveToNext();
            }
        }
        this.mCursor.close();
        this.HandlerMainSendEmptyMessage(Constants.ResponseRetrieveAlbumDatabase);
    }

    private void handleRequestRetrieveArtistTrackDatabase(Message message) {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, new String[]{"_id"}, null, null, "artist_key");
        this.mCursor.moveToPosition(message.arg1);
        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("_id"));
        this.mCursor.close();
        this.mArtistTrackList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.trackPrejection, "artist_id=" + this.mRecordID, null, "title_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast()) {
                if (this.mCursor.getLong(this.mCursor.getColumnIndex("duration")) > this.trackLengthLimitation) {
                    this.mRecordMap = new HashMap();
                    this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.list_icon_track));
                    this.mRecordMap.put("ListItemName", this.mCursor.getString(this.mCursor.getColumnIndex("_display_name")));
                    this.mRecordMap.put("ListItemType", this.mCursor.getString(this.mCursor.getColumnIndex("mime_type")));
                    this.mRecordMap.put("ListItemSize", Long.valueOf(this.mCursor.getLong(this.mCursor.getColumnIndex("_size"))));
                    this.mRecordMap.put("ListItemTime", Long.valueOf(this.mCursor.getLong(this.mCursor.getColumnIndex("date_modified"))));
                    this.mRecordMap.put("ListItemState", 0);
                    this.mRecordMap.put("ListItemPath", this.mCursor.getString(this.mCursor.getColumnIndex("_data")));
                    this.mRecordMap.put("Artist_Name", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
                    this.mArtistTrackList.add(this.mRecordMap);
                }
                this.mCursor.moveToNext();
            }
        }
        this.mCursor.close();
        this.HandlerMainSendEmptyMessage(Constants.ResponseRetrieveArtistTrackDatabase);
    }

    private void handleRequestRetrieveAlbumTrackDatabase(Message message) {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, new String[]{"_id"}, null, null, "album_key");
        this.mCursor.moveToPosition(message.arg1);
        this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("_id"));
        this.mCursor.close();
        this.mAlbumTrackList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.trackPrejection, "album_id=" + this.mRecordID, null, "title_key");
        this.mRecordCount = this.mCursor.getCount();
        if (this.mRecordCount > 0) {
            this.mCursor.moveToFirst();
            while (!this.mCursor.isAfterLast()) {
                if (this.mCursor.getLong(this.mCursor.getColumnIndex("duration")) > this.trackLengthLimitation) {
                    this.mRecordMap = new HashMap();
                    this.mRecordMap.put("ListItemImage", Integer.valueOf(R.drawable.list_icon_track));
                    this.mRecordMap.put("ListItemName", this.mCursor.getString(this.mCursor.getColumnIndex("_display_name")));
                    this.mRecordMap.put("ListItemType", this.mCursor.getString(this.mCursor.getColumnIndex("mime_type")));
                    this.mRecordMap.put("ListItemSize", Long.valueOf(this.mCursor.getLong(this.mCursor.getColumnIndex("_size"))));
                    this.mRecordMap.put("ListItemTime", Long.valueOf(this.mCursor.getLong(this.mCursor.getColumnIndex("date_modified"))));
                    this.mRecordMap.put("ListItemState", 0);
                    this.mRecordMap.put("ListItemPath", this.mCursor.getString(this.mCursor.getColumnIndex("_data")));
                    this.mRecordMap.put("Artist_Name", this.mCursor.getString(this.mCursor.getColumnIndex("artist")));
                    this.mAlbumTrackList.add(this.mRecordMap);
                }
                this.mCursor.moveToNext();
            }
        }
        this.mCursor.close();
        this.HandlerMainSendEmptyMessage(Constants.ResponseRetrieveAlbumTrackDatabase);
    }

    private void handleRequestUpdateTrackBitmap(Message message) {
        this.mTrackBitmapList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, "title_key");
        for (int index = message.arg1; index <= message.arg2; index++) {
            if (index < this.mCursor.getCount()) {
                this.mCursor.moveToPosition(index);
                this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("album_id"));
                this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                this.mRecordBitmap = null;
                try {
                    this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "RequestUpdateTrackBmp " + e.getMessage());
                }
                this.mTrackBitmapList.add(this.mRecordBitmap);
            }
        }
        this.mCursor.close();
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseUpdateTrackBitmap;
//        this.mMessage.arg1 = message.arg1;
//        this.mMessage.arg2 = message.arg2;
        this.HandlerMainSendMessage(Constants.ResponseUpdateTrackBitmap, message.arg1, message.arg2);
    }

    private void handleRequestUpdateAlbumBitmap(Message message) {
        this.mAlbumBitmapList.clear();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, "album_key");
        for (int index = message.arg1; index <= message.arg2; index++) {
            if (index < this.mCursor.getCount()) {
                this.mCursor.moveToPosition(index);
                this.mRecordID = this.mCursor.getLong(this.mCursor.getColumnIndex("_id"));
                this.mRecordUri = ContentUris.withAppendedId(ALBUM_CONTENT_URI, this.mRecordID);
                this.mRecordBitmap = null;
                try {
                    this.mRecordBitmap = MediaStore.Images.Media.getBitmap(this.mContentResolverMain, this.mRecordUri);
                } catch (Exception e) {
                    Log.e("MusicPlayer", "RequestUpdateAlbumBmp " + e.getMessage());
                }
                this.mAlbumBitmapList.add(this.mRecordBitmap);
            }
        }
        this.mCursor.close();
//        this.mMessage = Message.obtain();
//        this.mMessage.what = Constants.ResponseUpdateAlbumBitmap;
//        this.mMessage.arg1 = message.arg1;
//        this.mMessage.arg2 = message.arg2;
        this.HandlerMainSendMessage(Constants.ResponseUpdateAlbumBitmap, message.arg1, message.arg2);
    }

    private void handleRequestUpdateCount() {
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this.trackPrejection, this.trackSelection, this.trackSelectionArgs, "title_key");
        this.mTrackCount = this.mCursor.getCount();
        this.mCursor.close();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI, this.artistPrejection, null, null, "artist_key");
        this.mArtistCount = this.mCursor.getCount();
        this.mCursor.close();
        this.mCursor = this.mContentResolverMain.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, this.albumPrejection, null, null, "album_key");
        this.mAlbumCount = this.mCursor.getCount();
        this.mCursor.close();
        this.HandlerMainSendEmptyMessage(Constants.ResponseUpdateCount);
    }
}
