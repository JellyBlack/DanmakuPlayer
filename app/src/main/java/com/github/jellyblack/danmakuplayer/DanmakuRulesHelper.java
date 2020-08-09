package com.github.jellyblack.danmakuplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DanmakuRulesHelper {

    public static final int MODE_NORMAL = 0;// 正常模式
    public static final int MODE_RED = 1;// 被屏蔽的弹幕显示为红色，其他显示为白色
    public static final int MODE_ONLY = 2;// 只显示被“屏蔽”的弹幕

    private File source;
    private DanmakuRule[] rules;
    private int mode = 0;

    public DanmakuRulesHelper(File source, DanmakuRule[] rules, int mode) {
        this.source = source;
        this.rules = rules;
        this.mode = mode;
    }

    public static String readFile(File file) throws IOException {
        InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
        String s;
        StringBuilder sb = new StringBuilder();
        BufferedReader buf = new BufferedReader(read);
        while ((s = buf.readLine()) != null) {
            sb.append(s);
        }
        read.close();
        buf.close();
        return sb.toString();
    }

    public static void writeFile(String xml, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        //fos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(xml);
        bw.close();
    }

    public String convert() throws IOException, InterruptedException {
        String xml = readFile(source);
        return convert(xml);
    }

    private String convert(String xml) throws UnsupportedEncodingException, InterruptedException {
        xml = xml.replaceAll("<!--.*?-->", "");// 去掉注释
        Pattern pattern1 = Pattern.compile("<.*?/source>");// 头部
        Matcher matcher1 = pattern1.matcher(xml);
        String head = "";
        int red = 0xFF0000;
        int white = 0xFFFFFF;
        if (matcher1.find()) {
            head = matcher1.group(0);
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        Pattern pattern2 = Pattern.compile("<d\\s+p.*?</d>");// 弹幕数据
        Matcher matcher2 = pattern2.matcher(xml);
        ArrayList<String> arr = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        while (matcher2.find()) {
            arr.add(matcher2.group(0));
        }
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
        tick:
        for (int i = 0; i < arr.size(); i++) {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
            String str = arr.get(i);
            String part = "";
            if (mode == MODE_RED) {
                Pattern pattern3 = Pattern.compile("<d\\s+p=\".*?,.*?,.*?,.*?,");
                Matcher matcher3 = pattern3.matcher(str);
                if (matcher3.find()) {
                    part = matcher1.group(0);
                }
            }
            String user = str.replaceFirst("^<d\\s+p=\".*?,.*?,.*?,.*?,.*?,.*?,", "").replaceAll(",.*$", "");
            str = str.replaceAll("<.*?>", "");
            // 替换实体
            str = str.replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&amp;", "&")
                    .replace("&apos;", "'")
                    .replace("&quot;", "\"");
            if (mode == MODE_NORMAL) {
                for (int j = 0; j < rules.length; j++) {
                    DanmakuRule rule = rules[j];
                    if (rule.isEnabled()) {
                        if (rule.getType() == DanmakuRule.TYPE_TEXT) {
                            if (str.contains(rule.getContent())) {
                                continue tick;
                            }
                        } else if (rule.getType() == DanmakuRule.TYPE_REGEXP) {
                            Pattern pattern4 = Pattern.compile(rule.getContent());
                            Matcher matcher4 = pattern4.matcher(str);
                            if (matcher4.find()) {
                                continue tick;
                            }
                        } else {
                            if (user.toLowerCase().equals(rule.getContent().toLowerCase())) {
                                continue tick;
                            }
                        }
                    }
                }
                result.add(arr.get(i));
            } else if (mode == MODE_RED) {
                for (int j = 0; j < rules.length; j++) {
                    DanmakuRule rule = rules[j];
                    if (rule.isEnabled()) {
                        if (rule.getType() == DanmakuRule.TYPE_TEXT) {
                            if (str.contains(rule.getContent())) {
                                String convert = part.replaceFirst(",.*?,$", "," + red + ",");
                                result.add(arr.get(i).replaceFirst("<d\\s+p=\".*?,.*?,.*?,.*?,", convert));
                                continue tick;
                            }
                        } else if (rule.getType() == DanmakuRule.TYPE_REGEXP) {
                            Pattern pattern3 = Pattern.compile(rule.getContent());
                            Matcher matcher3 = pattern3.matcher(str);
                            if (matcher3.find()) {
                                String convert = part.replaceFirst(",.*?,$", "," + red + ",");
                                result.add(arr.get(i).replaceFirst("<d\\s+p=\".*?,.*?,.*?,.*?,", convert));
                                continue tick;
                            }
                        } else {
                            if (user.toLowerCase().equals(rule.getContent().toLowerCase())) {
                                String convert = part.replaceFirst(",.*?,$", "," + red + ",");
                                result.add(arr.get(i).replaceFirst("<d\\s+p=\".*?,.*?,.*?,.*?,", convert));
                                continue tick;
                            }
                        }
                    }
                }
                String convert = part.replaceFirst(",.*?,$", "," + white + ",");
                result.add(arr.get(i).replaceFirst("<d\\s+p=\".*?,.*?,.*?,.*?,", convert));
            } else {
                for (int j = 0; j < rules.length; j++) {
                    DanmakuRule rule = rules[j];
                    if (rule.isEnabled()) {
                        if (rule.getType() == DanmakuRule.TYPE_TEXT) {
                            if (str.contains(rule.getContent())) {
                                result.add(arr.get(i));
                                continue tick;
                            }
                        } else if (rule.getType() == DanmakuRule.TYPE_REGEXP) {
                            Pattern pattern4 = Pattern.compile(rule.getContent());
                            Matcher matcher4 = pattern4.matcher(str);
                            if (matcher4.find()) {
                                result.add(arr.get(i));
                                continue tick;
                            }
                        } else {
                            if (user.toLowerCase().equals(rule.getContent().toLowerCase())) {
                                result.add(arr.get(i));
                                continue tick;
                            }
                        }
                    }
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(head);
        for (int i = 0; i < result.size(); i++) {
            sb.append(result.get(i));
        }
        sb.append("</i>");
        String bom = new String(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF}, "UTF-8");
        return bom + sb.toString();
    }
}
