package com.github.jellyblack.danmakuplayer;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class DanmakuRulesHandler extends DefaultHandler {

    private List<DanmakuRule> rules = null;
    private DanmakuRule currentRule;
    private String tagName = null;//当前解析的元素标签

    @Override
    public void startDocument() {
        rules = new ArrayList<>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (localName.equals("item")) {
            currentRule = new DanmakuRule();
            String flag = attributes.getValue("enabled");
            if (flag.equals("true")) {
                currentRule.setEnabled(true);
            } else {
                currentRule.setEnabled(false);
            }
        }
        this.tagName = localName;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (localName.equals("item")) {
            rules.add(currentRule);
            currentRule = null;
        }
        this.tagName = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (tagName != null) {
            String data = new String(ch, start, length);
            if (tagName.equals("item")) {
                if (data.charAt(0) == 't') {
                    currentRule.setType(DanmakuRule.TYPE_TEXT);
                    currentRule.setContent(data.replaceFirst("^t=", ""));
                } else if (data.charAt(0) == 'r') {
                    currentRule.setType(DanmakuRule.TYPE_REGEXP);
                    currentRule.setContent(data.replaceFirst("^r=", ""));
                } else if (data.charAt(0) == 'u') {
                    currentRule.setType(DanmakuRule.TYPE_USER);
                    currentRule.setContent(data.replaceFirst("^u=", ""));
                }
            }
        }
    }

    public List<DanmakuRule> getRules() {
        return rules;
    }
}
