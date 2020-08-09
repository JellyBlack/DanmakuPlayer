package com.github.jellyblack.danmakuplayer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import master.flame.danmaku.danmaku.model.IDisplayer;

public class Config {

    public static final int DISPLAY_TOP = 1;
    public static final int DISPLAY_BOTTOM = 2;
    public static final int DISPLAY_ROLL = 4;
    public static final int DISPLAY_SPECIAL = 8;
    public static final int DISPLAY_COLOR = 16;
    public static final int DISPLAY_DUPLICATE = 32;

    private static final String TAG = "Config";
    Class<?> mClass = null;// 用于反射调用
    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    public Config(Context context) {
        sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        try {
            mClass = Class.forName("android.os.Environment");
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "'android.os.Environment' not found!");
            e.printStackTrace();
        }
    }

    /**
     * 获取自带的外部存储路径，例如/storage/emulated/0
     *
     * @return 路径
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public String getInternalPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String defaultPath = sharedPreferences.getString("internal", "unknown");
        if (defaultPath.equals("unknown")) {
            Method method = mClass.getMethod("getInternalStoragePath", null);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            String path = "unknown";
            try {
                path = (String) method.invoke(null, null);
            } catch (ClassCastException e) {
                path = ((File) method.invoke(null, null)).getPath();
            }
            return path;
        } else {
            return defaultPath;
        }
    }

    public void setInternalPath(String path) {
        editor.putString("internal", path);
        editor.commit();
    }

    /**
     * 获取SD卡路径，例如/storage/1234-5678
     *
     * @return 路径
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public String getExternalPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String defaultPath = sharedPreferences.getString("external", "unknown");
        if (defaultPath.equals("unknown")) {
            Method method = mClass.getMethod("getExternalStorageDirectory", null);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            String path = "unknown";
            try {
                path = (String) method.invoke(null, null);
            } catch (ClassCastException e) {
                path = ((File) method.invoke(null, null)).getPath();
            }
            return path;
        } else {
            return defaultPath;
        }
    }

    public void setExternalPath(String path) {
        editor.putString("external", path);
        editor.commit();
    }

    /**
     * 获取OTG路径，兼容性非常差！
     *
     * @return 路径
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public String getOtgPath() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String defaultPath = sharedPreferences.getString("otg", "unknown");
        if (defaultPath.equals("unknown")) {
            return "/storage/usbdisk";
        } else {
            return defaultPath;
        }
    }

    public void setOtgPath(String path) {
        editor.putString("otg", path);
        editor.commit();
    }

    /**
     * 获取自定义的一个路径，例如/storage/sdcard1
     *
     * @return 路径
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public String getCustomPath() {
        String defaultPath = sharedPreferences.getString("custom", "unknown");
        return defaultPath;
    }

    public void setCustomPath(String path) {
        editor.putString("custom", path);
        editor.commit();
    }

    public String getDefaultLocation() {
        String defaultPath = sharedPreferences.getString("defaultPath", "unknown");
        return defaultPath;
    }

    public void setDefaultLocation(String location) {
        editor.putString("defaultPath", location);
        editor.commit();
    }

    public int getDefaultLocationIndex() {
        int defaultPathIndex = sharedPreferences.getInt("defaultPathIndex", 0);
        return defaultPathIndex;
    }

    public void setDefaultLocationIndex(int index) {
        editor.putInt("defaultPathIndex", index);
        editor.commit();
    }

    public int getSortType() {
        int type = sharedPreferences.getInt("sortType", 0);
        return type;
    }

    public void setSortType(int type) {
        editor.putInt("sortType", type);
        editor.commit();
    }

    public String getCurrentPath() {
        String defaultPath = sharedPreferences.getString("currentPath", "unknown");
        return defaultPath;
    }

    public void setCurrentPath(String path) {
        editor.putString("currentPath", path);
        editor.commit();
    }

    public boolean getDanmakuEnabled() {
        boolean enabled = sharedPreferences.getBoolean("danmakuEnabled", true);
        return enabled;
    }

    public void setDanmakuEnabled(boolean enabled) {
        editor.putBoolean("danmakuEnabled", enabled);
        editor.commit();
    }

    public boolean getDanmakuRulesEnabled() {
        boolean enabled = sharedPreferences.getBoolean("danmakuRulesEnabled", false);
        return enabled;
    }

    public void setDanmakuRulesEnabled(boolean enabled) {
        editor.putBoolean("danmakuRulesEnabled", enabled);
        editor.commit();
    }

    public String getDanmakuRulesPath() {
        String defaultPath = sharedPreferences.getString("danmakuRulesPath", "");
        return defaultPath;
    }

    public void setDanmakuRulesPath(String path) {
        editor.putString("danmakuRulesPath", path);
        editor.commit();
    }

    public int getDanmakuDisplayMode() {
        int mode = sharedPreferences.getInt("displayMode", DanmakuRulesHelper.MODE_NORMAL);
        return mode;
    }

    public void setDanmakuDisplayMode(int mode) {
        editor.putInt("displayMode", mode);
        editor.commit();
    }

    public float getDanmakuTransparency() {
        float transparency = sharedPreferences.getFloat("transparency", 1.0f);
        return transparency;
    }

    public void setDanmakuTransparency(float transparency) {
        editor.putFloat("transparency", transparency);
        editor.commit();
    }

    public float getTextScale() {
        float textScale = sharedPreferences.getFloat("textScale", 1.1f);
        return textScale;
    }

    public void setTextScale(float textScale) {
        editor.putFloat("textScale", textScale);
        editor.commit();
    }

    public int getDanmakuFlavor() {
        int flavor = sharedPreferences.getInt("danmakuFlavor", 63);
        return flavor;
    }

    public void setDanmakuFlavor(int flavor) {
        editor.putInt("danmakuFlavor", flavor);
        editor.commit();
    }

    public int getMaximumVisibleSizeInScreen() {
        int size = sharedPreferences.getInt("maximumVisibleSizeInScreen", 0);
        return size;
    }

    public void setMaximumVisibleSizeInScreen(int size) {
        editor.putInt("maximumVisibleSizeInScreen", size);
        editor.commit();
    }

    public int getDanmakuStyle() {
        int danmakuStyle = sharedPreferences.getInt("danmakuStyle", IDisplayer.DANMAKU_STYLE_STROKEN);
        return danmakuStyle;
    }

    public void setDanmakuStyle(int danmakuStyle) {
        editor.putInt("danmakuStyle", danmakuStyle);
        editor.commit();
    }

    public float getShadowValue() {
        float shadowValue = sharedPreferences.getFloat("shadowValue", 4.0f);
        return shadowValue;
    }

    public void setShadowValue(float shadowValue) {
        editor.putFloat("shadowValue", shadowValue);
        editor.commit();
    }

    public float getStrokenValue() {
        float strokenValue = sharedPreferences.getFloat("strokenValue", 4.0f);
        return strokenValue;
    }

    public void setStrokenValue(float strokenValue) {
        editor.putFloat("strokenValue", strokenValue);
        editor.commit();
    }

    public float getProjectionOffsetX() {
        float projectionOffsetX = sharedPreferences.getFloat("projectionOffsetX", 6.2f);
        return projectionOffsetX;
    }

    public void setProjectionOffsetX(float projectionOffsetX) {
        editor.putFloat("projectionOffsetX", projectionOffsetX);
        editor.commit();
    }

    public float getProjectionOffsetY() {
        float projectionOffsetY = sharedPreferences.getFloat("projectionOffsetY", 5.4f);
        return projectionOffsetY;
    }

    public void setProjectionOffsetY(float projectionOffsetY) {
        editor.putFloat("projectionOffsetY", projectionOffsetY);
        editor.commit();
    }

    public float getProjectionAlpha() {
        float projectionAlpha = sharedPreferences.getFloat("projectionAlpha", 100);
        return projectionAlpha;
    }

    public void setProjectionAlpha(float projectionAlpha) {
        editor.putFloat("projectionAlpha", projectionAlpha);
        editor.commit();
    }

    public boolean getDanmakuBold() {
        boolean danmakuBold = sharedPreferences.getBoolean("danmakuBold", true);
        return danmakuBold;
    }

    public void setDanmakuBold(boolean danmakuBold) {
        editor.putBoolean("danmakuBold", danmakuBold);
        editor.commit();
    }

    public float getRollSpeed() {
        float rollSpeed = sharedPreferences.getFloat("rollSpeed", 1.2f);
        return rollSpeed;
    }

    public void setRollSpeed(float rollSpeed) {
        editor.putFloat("rollSpeed", rollSpeed);
        editor.commit();
    }

    public int getMaximumLines() {
        int maximumLines = sharedPreferences.getInt("maximumLines", -1);// -1为不限制
        return maximumLines;
    }

    public void setMaximumLines(int maximumLines) {
        editor.putInt("maximumLines", maximumLines);
        editor.commit();
    }

    public boolean getOverlapping() {
        boolean overlapping = sharedPreferences.getBoolean("overlapping", false);
        return overlapping;
    }

    public void setOverlapping(boolean overlapping) {
        editor.putBoolean("overlapping", overlapping);
        editor.commit();
    }

    public boolean getFix() {
        boolean fix = sharedPreferences.getBoolean("fix", false);
        return fix;
    }

    public void setFix(boolean fix) {
        editor.putBoolean("fix", fix);
        editor.commit();
    }

    public boolean getShowFps() {
        boolean showFps = sharedPreferences.getBoolean("showFps", false);
        return showFps;
    }

    public void setShowFps(boolean showFps) {
        editor.putBoolean("showFps", showFps);
        editor.commit();
    }
}

