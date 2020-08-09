package com.github.jellyblack.danmakuplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.DecimalFormat;

import master.flame.danmaku.danmaku.model.IDisplayer;

public class SettingsActivity extends Activity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "SettingsActivity";
    private Config config;

    // 控件
    private EditText text_internal;
    private EditText text_external;
    private EditText text_otg;
    private EditText text_custom;
    private CheckBox checkBox_DanmakuEnabled;
    private CheckBox checkBox_DanmakuRulesEnabled;
    private TextView text_DanmakuRulesPath;
    private RadioButton radioButton_mode0;
    private RadioButton radioButton_mode1;
    private RadioButton radioButton_mode2;
    private TextView textView_DanmakuTransparency;
    private LinearLayout layout_DanmakuTransparency;
    private SeekBar seekBar_DanmakuTransparency;
    private TextView textView_TextScale;
    private LinearLayout layout_TextScale;
    private SeekBar seekBar_TextScale;
    private TextView textView_RollSpeed;
    private LinearLayout layout_RollSpeed;
    private SeekBar seekBar_RollSpeed;
    private CheckBox checkBox_DISPLAY_TOP;
    private CheckBox checkBox_DISPLAY_BOTTOM;
    private CheckBox checkBox_DISPLAY_ROLL;
    private CheckBox checkBox_DISPLAY_SPECIAL;
    private CheckBox checkBox_DISPLAY_COLOR;
    private CheckBox checkBox_DISPLAY_DUPLICATE;
    private TextView textView_size;
    private RadioButton radioButton_auto;
    private RadioButton radioButton_none;
    private RadioButton radioButton_custom;
    private LinearLayout layout_max;
    private SeekBar seekBar_max;
    private RadioButton radioButton_Default;
    private RadioButton radioButton_Shadow;
    private RadioButton radioButton_Stroken;
    private RadioButton radioButton_Projection;
    private TextView textView_Shadow;
    private LinearLayout layout_Shadow;
    private SeekBar seekBar_Shadow;
    private TextView textView_Stroken;
    private LinearLayout layout_Stroken;
    private SeekBar seekBar_Stroken;
    private TextView textView_Projection_OffsetX;
    private LinearLayout layout_Projection_OffsetX;
    private SeekBar seekBar_Projection_OffsetX;
    private TextView textView_Projection_OffsetY;
    private LinearLayout layout_Projection_OffsetY;
    private SeekBar seekBar_Projection_OffsetY;
    private TextView textView_Projection_Alpha;
    private LinearLayout layout_Projection_Alpha;
    private SeekBar seekBar_Projection_Alpha;
    private TextView textView_MaximumLines;
    private LinearLayout layout_MaximumLines;
    private SeekBar seekBar_MaximumLines;
    private CheckBox checkBox_DanmakuBold;
    private CheckBox checkBox_Overlapping;
    private CheckBox checkBox_ShowFps;
    private CheckBox checkBox_fix;
    private Button button_license;
    private Button button_github;
    private Button button_finish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCrashHandler handler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        setContentView(R.layout.activity_settings);
        config = new Config(this);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bilibili)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bilibili));
        }
        // 获取控件
        text_internal = findViewById(R.id.text_internal);
        text_external = findViewById(R.id.text_external);
        text_otg = findViewById(R.id.text_otg);
        text_custom = findViewById(R.id.text_custom);
        checkBox_DanmakuEnabled = findViewById(R.id.checkBox_DanmakuEnabled);
        checkBox_DanmakuRulesEnabled = findViewById(R.id.checkBox_DanmakuRulesEnabled);
        text_DanmakuRulesPath = findViewById(R.id.text_DanmakuRulesPath);
        radioButton_mode0 = findViewById(R.id.radioButton_mode0);
        radioButton_mode1 = findViewById(R.id.radioButton_mode1);
        radioButton_mode2 = findViewById(R.id.radioButton_mode2);
        textView_DanmakuTransparency = findViewById(R.id.textView_DanmakuTransparency);
        layout_DanmakuTransparency = findViewById(R.id.layout_DanmakuTransparency);
        seekBar_DanmakuTransparency = findViewById(R.id.seekBar_DanmakuTransparency);
        textView_TextScale = findViewById(R.id.textView_TextScale);
        layout_TextScale = findViewById(R.id.layout_TextScale);
        seekBar_TextScale = findViewById(R.id.seekBar_TextScale);
        textView_RollSpeed = findViewById(R.id.textView_RollSpeed);
        layout_RollSpeed = findViewById(R.id.layout_RollSpeed);
        seekBar_RollSpeed = findViewById(R.id.seekBar_RollSpeed);
        checkBox_DISPLAY_TOP = findViewById(R.id.checkBox_DISPLAY_TOP);
        checkBox_DISPLAY_BOTTOM = findViewById(R.id.checkBox_DISPLAY_BOTTOM);
        checkBox_DISPLAY_ROLL = findViewById(R.id.checkBox_DISPLAY_ROLL);
        checkBox_DISPLAY_SPECIAL = findViewById(R.id.checkBox_DISPLAY_SPECIAL);
        checkBox_DISPLAY_COLOR = findViewById(R.id.checkBox_DISPLAY_COLOR);
        checkBox_DISPLAY_DUPLICATE = findViewById(R.id.checkBox_DISPLAY_DUPLICATE);
        textView_size = findViewById(R.id.textView_size);
        radioButton_auto = findViewById(R.id.radioButton_auto);
        radioButton_none = findViewById(R.id.radioButton_none);
        radioButton_custom = findViewById(R.id.radioButton_custom);
        layout_max = findViewById(R.id.layout_max);
        seekBar_max = findViewById(R.id.seekBar_max);
        radioButton_Default = findViewById(R.id.radioButton_Default);
        radioButton_Shadow = findViewById(R.id.radioButton_Shadow);
        radioButton_Stroken = findViewById(R.id.radioButton_Stroken);
        radioButton_Projection = findViewById(R.id.radioButton_Projection);
        textView_Shadow = findViewById(R.id.textView_Shadow);
        layout_Shadow = findViewById(R.id.layout_Shadow);
        seekBar_Shadow = findViewById(R.id.seekBar_Shadow);
        textView_Stroken = findViewById(R.id.textView_Stroken);
        layout_Stroken = findViewById(R.id.layout_Stroken);
        seekBar_Stroken = findViewById(R.id.seekBar_Stroken);
        textView_Projection_OffsetX = findViewById(R.id.textView_Projection_OffsetX);
        layout_Projection_OffsetX = findViewById(R.id.layout_Projection_OffsetX);
        seekBar_Projection_OffsetX = findViewById(R.id.seekBar_Projection_OffsetX);
        textView_Projection_OffsetY = findViewById(R.id.textView_Projection_OffsetY);
        layout_Projection_OffsetY = findViewById(R.id.layout_Projection_OffsetY);
        seekBar_Projection_OffsetY = findViewById(R.id.seekBar_Projection_OffsetY);
        textView_Projection_Alpha = findViewById(R.id.textView_Projection_Alpha);
        layout_Projection_Alpha = findViewById(R.id.layout_Projection_Alpha);
        seekBar_Projection_Alpha = findViewById(R.id.seekBar_Projection_Alpha);
        textView_MaximumLines = findViewById(R.id.textView_MaximumLines);
        layout_MaximumLines = findViewById(R.id.layout_MaximumLines);
        seekBar_MaximumLines = findViewById(R.id.seekBar_MaximumLines);
        checkBox_DanmakuBold = findViewById(R.id.checkBox_DanmakuBold);
        checkBox_Overlapping = findViewById(R.id.checkBox_Overlapping);
        checkBox_ShowFps = findViewById(R.id.checkBox_ShowFps);
        checkBox_fix = findViewById(R.id.checkBox_fix);
        button_license = findViewById(R.id.button_license);
        button_github = findViewById(R.id.button_github);
        button_finish = findViewById(R.id.button_finish);

        try {
            if (!config.getInternalPath().equals("unknown")) {
                text_internal.setText(config.getInternalPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!config.getExternalPath().equals("unknown")) {
                text_external.setText(config.getExternalPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!config.getOtgPath().equals("unknown")) {
                text_otg.setText(config.getOtgPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!config.getCustomPath().equals("unknown")) {
                text_custom.setText(config.getCustomPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        text_internal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = text_internal.getText().toString();
                if (!text.isEmpty()) {
                    config.setInternalPath(text);
                } else {
                    config.setInternalPath("unknown");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        text_external.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = text_internal.getText().toString();
                if (!text.isEmpty()) {
                    config.setExternalPath(text);
                } else {
                    config.setExternalPath("unknown");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        text_otg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = text_otg.getText().toString();
                if (!text.isEmpty()) {
                    config.setOtgPath(text);
                } else {
                    config.setOtgPath("unknown");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        text_custom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = text_custom.getText().toString();
                if (!text.isEmpty()) {
                    config.setCustomPath(text);
                } else {
                    config.setCustomPath("unknown");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        checkBox_DanmakuEnabled.setChecked(config.getDanmakuEnabled());
        checkBox_DanmakuEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                config.setDanmakuEnabled(b);
            }
        });
        checkBox_DanmakuRulesEnabled.setChecked(config.getDanmakuRulesEnabled());
        checkBox_DanmakuRulesEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                config.setDanmakuRulesEnabled(b);
                if (b) {
                    text_DanmakuRulesPath.setVisibility(View.VISIBLE);
                } else {
                    text_DanmakuRulesPath.setVisibility(View.GONE);
                }
            }
        });
        if (!config.getDanmakuRulesEnabled()) {
            text_DanmakuRulesPath.setVisibility(View.GONE);
        }
        text_DanmakuRulesPath.setText(config.getDanmakuRulesPath());
        text_DanmakuRulesPath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String text = text_DanmakuRulesPath.getText().toString();
                if (!text.isEmpty()) {
                    config.setDanmakuRulesPath(text);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        int mode = config.getDanmakuDisplayMode();
        switch (mode) {
            case DanmakuRulesHelper.MODE_NORMAL:
                radioButton_mode0.setChecked(true);
                break;
            case DanmakuRulesHelper.MODE_RED:
                radioButton_mode1.setChecked(true);
                break;
            case DanmakuRulesHelper.MODE_ONLY:
                radioButton_mode2.setChecked(true);
                break;
        }
        radioButton_mode0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    config.setDanmakuDisplayMode(DanmakuRulesHelper.MODE_NORMAL);
                }
            }
        });
        radioButton_mode1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    config.setDanmakuDisplayMode(DanmakuRulesHelper.MODE_RED);
                }
            }
        });
        radioButton_mode2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    config.setDanmakuDisplayMode(DanmakuRulesHelper.MODE_ONLY);
                }
            }
        });
        // 范围： 0.00 - 1.00 ， 步进：0.01
        final DecimalFormat decimalFormat1 = new DecimalFormat("0.00");
        final float trans = config.getDanmakuTransparency();
        textView_DanmakuTransparency.setText("不透明度：" + decimalFormat1.format(trans));
        seekBar_DanmakuTransparency.setProgress((int) (trans * 100));
        seekBar_DanmakuTransparency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = ((float) seekBar_DanmakuTransparency.getProgress()) / 100;
                textView_DanmakuTransparency.setText("不透明度：" + decimalFormat1.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = ((float) seekBar_DanmakuTransparency.getProgress()) / 100;
                textView_DanmakuTransparency.setText("不透明度：" + decimalFormat1.format(f));
                config.setDanmakuTransparency(f);
            }
        });
        // 范围： 0.1 - 5.0 ， 步进：0.1
        final DecimalFormat decimalFormat2 = new DecimalFormat("0.0");
        final float scale = config.getTextScale();
        textView_TextScale.setText("文字缩放：" + decimalFormat2.format(scale));
        seekBar_TextScale.setProgress((int) (scale * 10));
        seekBar_TextScale.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = ((float) seekBar_TextScale.getProgress()) / 10;
                textView_TextScale.setText("文字缩放：" + decimalFormat2.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = ((float) seekBar_TextScale.getProgress()) / 10;
                textView_TextScale.setText("文字缩放：" + decimalFormat2.format(f));
                config.setTextScale(f);
            }
        });
        // 范围： 0.10 - 3.00 ， 步进：0.05
        final DecimalFormat decimalFormat3 = new DecimalFormat("0.00");
        final float speed = config.getRollSpeed();
        textView_RollSpeed.setText("滚动速度：" + decimalFormat3.format(speed));
        seekBar_RollSpeed.setProgress((int) ((speed - 0.05) * 20));
        seekBar_RollSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = (float) (((float) seekBar_RollSpeed.getProgress()) / 20 + 0.05);
                textView_RollSpeed.setText("滚动速度：" + decimalFormat3.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = (float) (((float) seekBar_RollSpeed.getProgress()) / 20 + 0.05);
                textView_RollSpeed.setText("滚动速度：" + decimalFormat3.format(f));
                config.setRollSpeed(f);
            }
        });
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
        checkBox_DISPLAY_DUPLICATE.setChecked(display_duplicate == 1);
        checkBox_DISPLAY_COLOR.setChecked(display_color == 1);
        checkBox_DISPLAY_SPECIAL.setChecked(display_special == 1);
        checkBox_DISPLAY_ROLL.setChecked(display_roll == 1);
        checkBox_DISPLAY_BOTTOM.setChecked(display_bottom == 1);
        checkBox_DISPLAY_TOP.setChecked(display_top == 1);
        checkBox_DISPLAY_DUPLICATE.setOnCheckedChangeListener(this);
        checkBox_DISPLAY_COLOR.setOnCheckedChangeListener(this);
        checkBox_DISPLAY_SPECIAL.setOnCheckedChangeListener(this);
        checkBox_DISPLAY_ROLL.setOnCheckedChangeListener(this);
        checkBox_DISPLAY_BOTTOM.setOnCheckedChangeListener(this);
        checkBox_DISPLAY_TOP.setOnCheckedChangeListener(this);
        // 范围： 1 - 200 ， 步进：1
        int size = config.getMaximumVisibleSizeInScreen();
        if (size == -1) {
            radioButton_auto.setChecked(true);
            layout_max.setVisibility(View.GONE);
        } else if (size == 0) {
            radioButton_none.setChecked(true);
            layout_max.setVisibility(View.GONE);
        } else {
            radioButton_custom.setChecked(true);
            textView_size.setText("同屏弹幕密度：" + size);
        }
        radioButton_none.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    config.setMaximumVisibleSizeInScreen(0);
                    textView_size.setText("同屏弹幕密度");
                    layout_max.setVisibility(View.GONE);
                }
            }
        });
        radioButton_auto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    config.setMaximumVisibleSizeInScreen(-1);
                    textView_size.setText("同屏弹幕密度");
                    layout_max.setVisibility(View.GONE);
                }
            }
        });
        radioButton_custom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    config.setMaximumVisibleSizeInScreen(30);
                    textView_size.setText("同屏弹幕密度：30");
                    seekBar_max.setProgress(29);
                    layout_max.setVisibility(View.VISIBLE);
                }
            }
        });
        // 范围：1 - 200 ， 步进：1
        seekBar_max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView_size.setText("同屏弹幕密度：" + (i + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView_size.setText("同屏弹幕密度：" + (seekBar_max.getProgress() + 1));
                config.setMaximumVisibleSizeInScreen(seekBar_max.getProgress() + 1);
            }
        });
        textView_Shadow.setVisibility(View.GONE);
        textView_Stroken.setVisibility(View.GONE);
        textView_Projection_OffsetX.setVisibility(View.GONE);
        textView_Projection_OffsetY.setVisibility(View.GONE);
        textView_Projection_Alpha.setVisibility(View.GONE);
        layout_Shadow.setVisibility(View.GONE);
        layout_Stroken.setVisibility(View.GONE);
        layout_Projection_OffsetX.setVisibility(View.GONE);
        layout_Projection_OffsetY.setVisibility(View.GONE);
        layout_Projection_Alpha.setVisibility(View.GONE);
        int style = config.getDanmakuStyle();
        if (style == IDisplayer.DANMAKU_STYLE_NONE) {
            radioButton_Default.setChecked(true);
        } else if (style == IDisplayer.DANMAKU_STYLE_SHADOW) {
            textView_Shadow.setVisibility(View.VISIBLE);
            layout_Shadow.setVisibility(View.VISIBLE);
            radioButton_Shadow.setChecked(true);
        } else if (style == IDisplayer.DANMAKU_STYLE_STROKEN) {
            textView_Stroken.setVisibility(View.VISIBLE);
            layout_Stroken.setVisibility(View.VISIBLE);
            radioButton_Stroken.setChecked(true);
        } else {
            textView_Projection_OffsetX.setVisibility(View.VISIBLE);
            textView_Projection_OffsetY.setVisibility(View.VISIBLE);
            textView_Projection_Alpha.setVisibility(View.VISIBLE);
            layout_Projection_OffsetX.setVisibility(View.VISIBLE);
            layout_Projection_OffsetY.setVisibility(View.VISIBLE);
            layout_Projection_Alpha.setVisibility(View.VISIBLE);
            radioButton_Projection.setChecked(true);
        }
        radioButton_Default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    textView_Shadow.setVisibility(View.GONE);
                    textView_Stroken.setVisibility(View.GONE);
                    textView_Projection_OffsetX.setVisibility(View.GONE);
                    textView_Projection_OffsetY.setVisibility(View.GONE);
                    textView_Projection_Alpha.setVisibility(View.GONE);
                    layout_Shadow.setVisibility(View.GONE);
                    layout_Stroken.setVisibility(View.GONE);
                    layout_Projection_OffsetX.setVisibility(View.GONE);
                    layout_Projection_OffsetY.setVisibility(View.GONE);
                    layout_Projection_Alpha.setVisibility(View.GONE);
                    config.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_DEFAULT);
                }
            }
        });
        radioButton_Shadow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    textView_Shadow.setVisibility(View.VISIBLE);
                    textView_Stroken.setVisibility(View.GONE);
                    textView_Projection_OffsetX.setVisibility(View.GONE);
                    textView_Projection_OffsetY.setVisibility(View.GONE);
                    textView_Projection_Alpha.setVisibility(View.GONE);
                    layout_Shadow.setVisibility(View.VISIBLE);
                    layout_Stroken.setVisibility(View.GONE);
                    layout_Projection_OffsetX.setVisibility(View.GONE);
                    layout_Projection_OffsetY.setVisibility(View.GONE);
                    layout_Projection_Alpha.setVisibility(View.GONE);
                    config.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_SHADOW);
                }
            }
        });
        radioButton_Stroken.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    textView_Shadow.setVisibility(View.GONE);
                    textView_Stroken.setVisibility(View.VISIBLE);
                    textView_Projection_OffsetX.setVisibility(View.GONE);
                    textView_Projection_OffsetY.setVisibility(View.GONE);
                    textView_Projection_Alpha.setVisibility(View.GONE);
                    layout_Shadow.setVisibility(View.GONE);
                    layout_Stroken.setVisibility(View.VISIBLE);
                    layout_Projection_OffsetX.setVisibility(View.GONE);
                    layout_Projection_OffsetY.setVisibility(View.GONE);
                    layout_Projection_Alpha.setVisibility(View.GONE);
                    config.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN);
                }
            }
        });
        radioButton_Projection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    textView_Shadow.setVisibility(View.GONE);
                    textView_Stroken.setVisibility(View.GONE);
                    textView_Projection_OffsetX.setVisibility(View.VISIBLE);
                    textView_Projection_OffsetY.setVisibility(View.VISIBLE);
                    textView_Projection_Alpha.setVisibility(View.VISIBLE);
                    layout_Shadow.setVisibility(View.GONE);
                    layout_Stroken.setVisibility(View.GONE);
                    layout_Projection_OffsetX.setVisibility(View.VISIBLE);
                    layout_Projection_OffsetY.setVisibility(View.VISIBLE);
                    layout_Projection_Alpha.setVisibility(View.VISIBLE);
                    config.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_PROJECTION);
                }
            }
        });
        // 范围：0.0 - 12.0 ， 步进：0.1
        textView_Shadow.setText("阴影半径：" + decimalFormat2.format(config.getShadowValue()));
        seekBar_Shadow.setProgress((int) (config.getShadowValue() * 10));
        seekBar_Shadow.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = ((float) seekBar_Shadow.getProgress()) / 10;
                textView_Shadow.setText("阴影半径：" + decimalFormat2.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = ((float) seekBar_Shadow.getProgress()) / 10;
                textView_Shadow.setText("阴影半径：" + decimalFormat2.format(f));
                config.setShadowValue(f);
            }
        });
        // 范围：0.0 - 12.0 ， 步进：0.1
        textView_Stroken.setText("描边宽度：" + decimalFormat2.format(config.getStrokenValue()));
        seekBar_Stroken.setProgress((int) (config.getStrokenValue() * 10));
        seekBar_Stroken.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = ((float) seekBar_Stroken.getProgress()) / 10;
                textView_Stroken.setText("描边宽度：" + decimalFormat2.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = ((float) seekBar_Stroken.getProgress()) / 10;
                textView_Stroken.setText("描边宽度：" + decimalFormat2.format(f));
                config.setStrokenValue(f);
            }
        });
        // 范围： 0.0 - 15.0 ， 步进：0.2
        textView_Projection_OffsetX.setText("X偏移量：" + decimalFormat2.format(config.getProjectionOffsetX()));
        seekBar_Projection_OffsetX.setProgress((int) (config.getProjectionOffsetX() * 5));
        seekBar_Projection_OffsetX.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = ((float) seekBar_Projection_OffsetX.getProgress()) / 5;
                textView_Projection_OffsetX.setText("X偏移量：" + decimalFormat2.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = ((float) seekBar_Projection_OffsetX.getProgress()) / 5;
                textView_Projection_OffsetX.setText("X偏移量：" + decimalFormat2.format(f));
                config.setProjectionOffsetX(f);
            }
        });
        // 范围： 0.0 - 15.0 ， 步进：0.2
        textView_Projection_OffsetY.setText("Y偏移量：" + decimalFormat2.format(config.getProjectionOffsetY()));
        seekBar_Projection_OffsetY.setProgress((int) (config.getProjectionOffsetY() * 5));
        seekBar_Projection_OffsetY.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float f = ((float) seekBar_Projection_OffsetY.getProgress()) / 5;
                textView_Projection_OffsetY.setText("Y偏移量：" + decimalFormat2.format(f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float f = ((float) seekBar_Projection_OffsetY.getProgress()) / 5;
                textView_Projection_OffsetY.setText("Y偏移量：" + decimalFormat2.format(f));
                config.setProjectionOffsetY(f);
            }
        });
        // 范围： 0 - 255 ， 步进：1
        final float alpha = config.getProjectionAlpha();
        textView_Projection_Alpha.setText("投影不透明度：" + (int) alpha);
        seekBar_Projection_Alpha.setProgress((int) alpha);
        seekBar_Projection_Alpha.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView_Projection_Alpha.setText("投影不透明度：" + seekBar_Projection_Alpha.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView_Projection_Alpha.setText("投影不透明度：" + seekBar_Projection_Alpha.getProgress());
                config.setProjectionAlpha(seekBar_Projection_Alpha.getProgress());
            }
        });
        int lines = config.getMaximumLines();
        if (lines == -1) {
            seekBar_MaximumLines.setProgress(51);
            textView_MaximumLines.setText("最大显示行数：不限制");
        } else {
            seekBar_MaximumLines.setProgress(lines);
            textView_MaximumLines.setText("最大显示行数：" + lines);
        }
        seekBar_MaximumLines.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (i == 51) {
                    textView_MaximumLines.setText("最大显示行数：不限制");
                } else {
                    textView_MaximumLines.setText("最大显示行数：" + i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar_MaximumLines.getProgress() == 51) {
                    textView_MaximumLines.setText("最大显示行数：不限制");
                    config.setMaximumLines(-1);
                } else {
                    textView_MaximumLines.setText("最大显示行数：" + seekBar_MaximumLines.getProgress());
                    config.setMaximumLines(seekBar_MaximumLines.getProgress());
                }
            }
        });
        checkBox_DanmakuBold.setChecked(config.getDanmakuBold());
        checkBox_DanmakuBold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                config.setDanmakuBold(b);
            }
        });
        checkBox_Overlapping.setChecked(config.getOverlapping());
        checkBox_Overlapping.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                config.setOverlapping(b);
            }
        });
        checkBox_ShowFps.setChecked(config.getShowFps());
        checkBox_ShowFps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                config.setShowFps(b);
            }
        });
        checkBox_fix.setChecked(config.getFix());
        checkBox_fix.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                config.setFix(b);
            }
        });
        button_license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
                dialog.setTitle("开放源代码许可");
                dialog.setIcon(R.mipmap.ic_launcher_round);
                dialog.setMessage("1. UniversalVideoView\n版本：v1.1.0\n协议：Apache-2.0 License\nhttps://github.com/linsea/UniversalVideoView\n\n2. DanmakuFlameMaster\n版本：v0.9.25\n协议：Apache-2.0 License\nhttps://github.com/bilibili/DanmakuFlameMaster\n\n3. ndkbitmap-armv7a\n版本：v0.9.21\nhttps://github.com/bilibili/NativeBitmapFactory");
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog.show();
            }
        });
        button_github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://github.com/JellyBlack/DanmakuPlayer");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int value = 0;
        if (checkBox_DISPLAY_DUPLICATE.isChecked()) {
            value += Config.DISPLAY_DUPLICATE;
        }
        if (checkBox_DISPLAY_COLOR.isChecked()) {
            value += Config.DISPLAY_COLOR;
        }
        if (checkBox_DISPLAY_SPECIAL.isChecked()) {
            value += Config.DISPLAY_SPECIAL;
        }
        if (checkBox_DISPLAY_ROLL.isChecked()) {
            value += Config.DISPLAY_ROLL;
        }
        if (checkBox_DISPLAY_BOTTOM.isChecked()) {
            value += Config.DISPLAY_BOTTOM;
        }
        if (checkBox_DISPLAY_TOP.isChecked()) {
            value += Config.DISPLAY_TOP;
        }
        config.setDanmakuFlavor(value);
    }

    @Override
    public void finish() {
        super.finish();
        String text = text_internal.getText().toString();
        if (!text.isEmpty()) {
            config.setInternalPath(text);
        } else {
            config.setInternalPath("unknown");
        }
        text = text_internal.getText().toString();
        if (!text.isEmpty()) {
            config.setExternalPath(text);
        } else {
            config.setExternalPath("unknown");
        }
        text = text_otg.getText().toString();
        if (!text.isEmpty()) {
            config.setOtgPath(text);
        } else {
            config.setOtgPath("unknown");
        }
        text = text_custom.getText().toString();
        if (!text.isEmpty()) {
            config.setCustomPath(text);
        } else {
            config.setCustomPath("unknown");
        }
        text = text_DanmakuRulesPath.getText().toString();
        if (!text.isEmpty()) {
            config.setDanmakuRulesPath(text);
        }
    }
}
