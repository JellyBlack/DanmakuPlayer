package com.github.jellyblack.danmakuplayer;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.loader.ILoader;
import master.flame.danmaku.danmaku.loader.IllegalDataException;
import master.flame.danmaku.danmaku.loader.android.DanmakuLoaderFactory;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.parser.IDataSource;

public class MainActivity extends AppCompatActivity implements VideoView.VideoViewCallback, OnSeekListener {

    private static final String TAG = "MainActivity";
    private static final String SEEK_POSITION_KEY = "SEEK_POSITION_KEY";

    private Config config;
    private Thread danmakuThread;
    IDataSource<?> dataSource;

    // 控件
    private VideoView mVideoView;
    private MediaController mMediaController;
    private View mVideoLayout;
    private IDanmakuView mDanmakuView;
    private BaseDanmakuParser parser;
    private DanmakuContext mContext;
    private ProgressDialog progressDialog;
    private Button mDanmakuSwitch;

    // 播放器控制属性
    private String title;
    private boolean firstPlay = true;
    private boolean firstEnter = true;
    private boolean isPlayingBeforePause = true;
    private int mSeekPosition = 0;
    private boolean danmakuPrepareStarted = false;
    private boolean isFirstPrepare = true;
    private boolean doDanmakuSeek = false;

    // 消息
    private static final int UPDATE_PROGRESS = 0;
    private static final int DANMAKU_PREPARED = 1;
    private String progressBarMsg = "";
    private Handler msgHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == UPDATE_PROGRESS) {
                if (progressDialog != null) {
                    progressDialog.setMessage(progressBarMsg);
                }
            } else if (msg.what == DANMAKU_PREPARED) {
                mDanmakuSwitch.setTextColor(getResources().getColor(R.color.bilibili));
                progressDialog.cancel();
                mDanmakuView.start();
                firstEnter = false;
                mVideoView.start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCrashHandler handler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        config = new Config(this);
        // 设置窗口属性
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        hideBottomUI(getWindow().getDecorView());
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int arg0) {
                hideBottomUI(getWindow().getDecorView());
            }
        });
        setContentView(R.layout.activity_main);
        // 初始化控件
        mVideoLayout = findViewById(R.id.video_layout);
        mVideoView = findViewById(R.id.videoView);
        mDanmakuView = findViewById(R.id.danmakuView);
        mMediaController = findViewById(R.id.media_controller);
        mVideoView.setMediaController(mMediaController);
        mDanmakuSwitch = findViewById(R.id.danmaku_switch);
        // 初始化视频播放器
        Uri uri = getIntent().getData();
        title = getRealPath(this, uri);
        mMediaController.setTitle(title);
        initVideoView(uri);
        mVideoView.setVideoViewCallback(this);
        mMediaController.setOnSeekListener(this);
        mDanmakuSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "danmaku_switch clicked");
                if (mDanmakuView == null || !mDanmakuView.isPrepared()) {
                    Toast.makeText(MainActivity.this, "必须先加载弹幕", Toast.LENGTH_SHORT).show();
                } else {
                    if (mDanmakuView.isShown()) {
                        mDanmakuView.hide();
                        mDanmakuSwitch.setTextColor(Color.WHITE);
                    } else {
                        mDanmakuView.show();
                        mDanmakuSwitch.setTextColor(getResources().getColor(R.color.bilibili));
                    }
                }
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion");
                if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                    mDanmakuView.pause();
                }
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d(TAG, "onPrepared");
                // 加载弹幕
                if (isFirstPrepare && config.getDanmakuEnabled()) {
                    isFirstPrepare = false;
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setTitle("正在加载弹幕");
                    progressDialog.setMessage("加载中...\n点击取消按钮可停止加载。");
                    progressDialog.setIcon(R.mipmap.ic_launcher_round);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.setButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                            parser = null;
//                            mDanmakuView.release();// release要等到加载完毕才能返回，此处不能release
                            mDanmakuView = null;
                            if (firstEnter) {
                                firstEnter = false;
                                mVideoView.start();
                            }
                        }
                    });
                    progressDialog.show();
                    if (!danmakuPrepareStarted) {
                        danmakuPrepareStarted = true;
                        danmakuThread.start();
                    }
                }
                // 控制视频
                else if (firstPlay) {
                    firstPlay = false;
                    mVideoView.start();
                } else {
                    if (isPlayingBeforePause) {
                        mVideoView.start();
                    }
                    mVideoView.seekTo(mSeekPosition);
                }
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                Log.d(TAG, "onError");
                danmakuThread.interrupt();
                return false;
            }
        });
        mDanmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                Log.d(TAG, "danmaku prepared");
                if (firstEnter) {
                    Message message = new Message();
                    message.what = DANMAKU_PREPARED;
                    msgHandler.sendMessage(message);
                }
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {
            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {
//                Log.d(TAG, "danmakuShown: " + danmaku.text);
            }

            @Override
            public void drawingFinished() {
                Log.d(TAG, "danmaku drawingFinished");
            }
        });
        danmakuThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // 可能无法从Uri获取文件路径，需要捕获异常
                try {
                    File danmakuPath;
                    try {
                        danmakuPath = new File(title.replaceFirst("\\.[^\\.]*?$", ".xml"));
                        if (!danmakuPath.exists() || danmakuPath.isDirectory() || !danmakuPath.canRead()) {
                            progressDialog.cancel();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "无法加载弹幕", Toast.LENGTH_SHORT).show();
                                }
                            });
                            if (firstEnter) {
                                firstEnter = false;
                                mVideoView.start();
                            }
                            return;
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        throw new IOException(e.getMessage());
                    }
                    parser = createParser(danmakuPath);
                    Long time = System.currentTimeMillis();
                    if (config.getDanmakuRulesEnabled()) {
                        try {
                            ((BiliDanmakuParser) parser).loadRules();
                        } catch (ParserConfigurationException | SAXException | IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "无法加载屏蔽列表", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    // 过早地调用Dialog的setMessage方法会抛空指针异常，别问我怎么知道的
                    while (System.currentTimeMillis() - time < 100) {
                        Thread.sleep(5);
                    }
                    initDanmaku();
                    mDanmakuView.showFPS(config.getShowFps());
                    mDanmakuView.enableDanmakuDrawingCache(true);
                    mDanmakuView.prepare(parser, mContext);
                } catch (IOException e) {
                    Looper.prepare();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "无法加载弹幕", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Looper.loop();
                    progressDialog.cancel();
                    if (firstEnter) {
                        firstEnter = false;
                        mVideoView.start();
                    }
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    // 认为是用户取消加载弹幕
                    Log.d(TAG, "NullPointerException thrown");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void hideBottomUI(View view) {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;    //View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        view.setSystemUiVisibility(uiFlags);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ");
        if (mVideoView != null) {
            mSeekPosition = mVideoView.getCurrentPosition();
            Log.d(TAG, "onPause mSeekPosition=" + mSeekPosition);
            isPlayingBeforePause = mVideoView.isPlaying();
            mVideoView.stopPlayback();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isPlayingBeforePause) {
            mVideoView.resume();
            mVideoView.seekTo(mSeekPosition);
            if (mDanmakuView != null && mDanmakuView.isPrepared()) {
                mDanmakuView.resume();
            }
        }
    }

    private void initVideoView(final Uri uri) {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                mVideoView.setVideoURI(uri);
                mVideoView.requestFocus();
            }
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState Position=" + mVideoView.getCurrentPosition());
        outState.putInt(SEEK_POSITION_KEY, mSeekPosition);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle outState) {
        super.onRestoreInstanceState(outState);
        mSeekPosition = outState.getInt(SEEK_POSITION_KEY);
        Log.d(TAG, "onRestoreInstanceState Position=" + mSeekPosition);
    }


    @Override
    public void onScaleChange(boolean isFullscreen) {
        Log.d(TAG, "onScaleChange UniversalVideoView callback");
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onPause UniversalVideoView callback");
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            mDanmakuView.pause();
        }
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onStart UniversalVideoView callback");
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            if (doDanmakuSeek) {
                doDanmakuSeek = false;
                mDanmakuView.seekTo((long) mediaPlayer.getCurrentPosition());
            } else {
                mDanmakuView.resume();
            }
        }
    }


    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingStart UniversalVideoView callback");
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.d(TAG, "onBufferingEnd UniversalVideoView callback");
    }

    @Override
    public void onBackPressed() {
        release(this);
    }

    public VideoView getVideoView() {
        return mVideoView;
    }

    public IDanmakuView getDanmakuView() {
        return mDanmakuView;
    }


    public static void release(MainActivity activity) {
        VideoView videoView = activity.getVideoView();
        videoView.stopPlayback();
        videoView.suspend();
        IDanmakuView danmakuView = activity.getDanmakuView();
        if (danmakuView != null) {
            if (danmakuView.isPrepared()) {
                danmakuView.release();
            }
        }
        ((AudioManager) activity.getSystemService(Context.AUDIO_SERVICE)).abandonAudioFocus(null);
        activity.finish();
    }

    private SpannableStringBuilder createSpannable(Drawable drawable) {
        String text = "bitmap";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
        ImageSpan span = new ImageSpan(drawable);//ImageSpan.ALIGN_BOTTOM);
        spannableStringBuilder.setSpan(span, 0, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.append("图文混排");
        spannableStringBuilder.setSpan(new BackgroundColorSpan(Color.parseColor("#8A2233B1")), 0, spannableStringBuilder.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    private BaseDanmakuParser createParser(File danmaku) throws IOException {

        InputStream stream = new FileInputStream(danmaku);

        ILoader loader = DanmakuLoaderFactory.create(DanmakuLoaderFactory.TAG_BILI);

        try {
            loader.load(stream);
        } catch (IllegalDataException e) {
            e.printStackTrace();
        }
        BaseDanmakuParser parser = new BiliDanmakuParser(this, danmaku);
        dataSource = loader.getDataSource();
        parser.load(dataSource);
        return parser;
    }

    public static String getRealPath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Video.DEFAULT_SORT_ORDER}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Video.DEFAULT_SORT_ORDER);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void initDanmaku() {
        mContext = DanmakuContext.create();
        mContext.setDanmakuTransparency(config.getDanmakuTransparency())
                .setScaleTextSize(config.getTextScale())
                .setMaximumVisibleSizeInScreen(config.getMaximumVisibleSizeInScreen())
                .setDanmakuBold(config.getDanmakuBold())
                .setScrollSpeedFactor(config.getRollSpeed());
        int value = config.getDanmakuFlavor();
        int display_duplicate = value / Config.DISPLAY_DUPLICATE;
        int tmp1 = value % Config.DISPLAY_DUPLICATE;
        int display_color = tmp1 / Config.DISPLAY_COLOR;
        int tmp2 = tmp1 % Config.DISPLAY_COLOR;
        int display_special = tmp2 / Config.DISPLAY_SPECIAL;
        int tmp3 = tmp2 % Config.DISPLAY_SPECIAL;
        int display_roll = tmp3 / Config.DISPLAY_ROLL;
        int tmp4 = tmp3 % Config.DISPLAY_ROLL;
        int display_bottom = tmp4 / Config.DISPLAY_BOTTOM;
        int display_top = tmp4 % Config.DISPLAY_BOTTOM;
        mContext.setFTDanmakuVisibility(display_top == 1)
                .setFBDanmakuVisibility(display_bottom == 1)
                .setL2RDanmakuVisibility(display_roll == 1)
                .setR2LDanmakuVisibility(display_roll == 1)
                .setSpecialDanmakuVisibility(display_special == 1)
                .setDuplicateMergingEnabled(display_duplicate == 0);
        if (display_color == 1) {
            mContext.setColorValueWhiteList();
        } else {
            mContext.setColorValueWhiteList(-1);
        }
        int style = config.getDanmakuStyle();
        if (style == IDisplayer.DANMAKU_STYLE_NONE) {
            mContext.setDanmakuStyle(style);
        } else if (style == IDisplayer.DANMAKU_STYLE_SHADOW) {
            mContext.setDanmakuStyle(style, config.getShadowValue());
        } else if (style == IDisplayer.DANMAKU_STYLE_STROKEN) {
            mContext.setDanmakuStyle(style, config.getStrokenValue());
        } else if (style == IDisplayer.DANMAKU_STYLE_PROJECTION) {
            mContext.setDanmakuStyle(style, config.getProjectionOffsetX(), config.getProjectionOffsetY(), config.getProjectionAlpha());
        }
        HashMap<Integer, Integer> maxLines;
        if (config.getMaximumLines() != -1) {
            maxLines = new HashMap<>(BaseDanmaku.TYPE_SCROLL_RL, config.getMaximumLines());
        } else {
            maxLines = null;
        }
        mContext.setMaximumLines(maxLines);
        HashMap<Integer, Boolean> pairs;
        if (config.getOverlapping()) {
            pairs = new HashMap<>();
            pairs.put(BaseDanmaku.TYPE_SCROLL_RL, true);
            pairs.put(BaseDanmaku.TYPE_FIX_TOP, true);
        } else {
            pairs = null;
        }
        mContext.setOverlapping(pairs);
    }

    public void setProgressBarText(String text) {
        progressBarMsg = text;
        Message message = new Message();
        message.what = UPDATE_PROGRESS;
        msgHandler.sendMessage(message);
    }

    @Override
    public void onSeek(int seekPosition, boolean isPlaying) {
        Log.d(TAG, "onSeek");
        if (mDanmakuView != null && mDanmakuView.isPrepared()) {
            if (isPlaying) {
                mDanmakuView.seekTo((long) seekPosition);
            } else {
                doDanmakuSeek = true;
                mDanmakuView.clearDanmakusOnScreen();
            }
        }
    }
}
