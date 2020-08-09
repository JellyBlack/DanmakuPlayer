package com.github.jellyblack.danmakuplayer;

public class DanmakuRule {

    public static final int TYPE_TEXT = 0;
    public static final int TYPE_REGEXP = 1;
    public static final int TYPE_USER = 2;

    private boolean enabled;
    private int type;
    private String content;

    public DanmakuRule() {
    }

    public DanmakuRule(boolean enabled, int type, String content) {
        this.enabled = enabled;
        this.type = type;
        this.content = content;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
