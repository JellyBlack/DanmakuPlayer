package com.github.jellyblack.danmakuplayer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FileExplorerActivity extends Activity {

    // 配置
    private static final String TAG = "FileExplorerActivity";
    private final String[] SORT = {"名称 A→Z", "名称 Z→A", "修改日期 新→旧", "修改日期 旧→新", "大小 小→大", "大小 大→小"};
    private Config config;
    private boolean prepared = false;
    // 控件
    private Spinner pathSpinner;
    private Spinner sortSpinner;
    private ListView listView;
    private TextView dir;
    private Button button;
    private String[] paths;
    private ArrayAdapter<String> adapter_path;
    // 存储位置
    private String internal = "";
    private String external = "";
    private String otg = "";
    private String custom = "";
    private String root = "";
    private int root_index = 0;
    private int sort_type = 0;
    private String currentPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCrashHandler handler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
        setContentView(R.layout.file_explorer);
        config = new Config(this);
        getActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bilibili)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.bilibili));
        }
        // 获取控件
        pathSpinner = findViewById(R.id.path);
        sortSpinner = findViewById(R.id.sort);
        listView = findViewById(R.id.list_view);
        dir = findViewById(R.id.dir);
        button = findViewById(R.id.button);
        // 配置位置选择
        try {
            internal = config.getInternalPath();
        } catch (Exception e) {
            e.printStackTrace();
            internal = "unknown";
        }
        try {
            external = config.getExternalPath();
        } catch (Exception e) {
            e.printStackTrace();
            external = "unknown";
        }
        try {
            otg = config.getOtgPath();
        } catch (Exception e) {
            e.printStackTrace();
            otg = "unknown";
        }
        try {
            custom = config.getCustomPath();
        } catch (Exception e) {
            e.printStackTrace();
            custom = "unknown";
        }
        // 设置存储位置
        String defaultLocation = config.getDefaultLocation();
        root_index = config.getDefaultLocationIndex();
        if (defaultLocation.equals("unknown")) {
            root = internal;
            config.setDefaultLocation(root);
            config.setDefaultLocationIndex(0);
        } else {
            root = defaultLocation;
        }
        final String current = config.getCurrentPath();
        if (current.equals("unknown")) {
            currentPath = root;
        } else {
            currentPath = current;
        }
        sort_type = config.getSortType();
        dir.setText("  当前目录：" + currentPath);
        Log.d(TAG, "internal: " + internal);
        Log.d(TAG, "external: " + external);
        Log.d(TAG, "otg: " + otg);
        Log.d(TAG, "custom: " + custom);
        Log.d(TAG, "root: " + root);
        // 配置spinner
        paths = new String[]{
                "外部存储： " + internal,
                "SD卡： " + external,
                "OTG： " + otg,
                "自定义： " + custom
        };
        adapter_path = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paths);
        pathSpinner.setAdapter(adapter_path);
        pathSpinner.setSelection(root_index);
        pathSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "pathSpinner: selected");
                if (i == 0) {
                    root = internal;
                } else if (i == 1) {
                    root = external;
                } else if (i == 2) {
                    root = otg;
                } else {
                    root = custom;
                }
                if (!prepared) {
                    prepared = true;
                } else {
                    currentPath = root;
                }
                dir.setText("  当前目录：" + currentPath);
                root_index = i;
                config.setDefaultLocationIndex(root_index);
                config.setCurrentPath(currentPath);
                updateListView(currentPath, sort_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "Nothing selected.");
            }
        });
        // 配置排序方式选择的spinner
        sortSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SORT));
        sortSpinner.setSelection(sort_type);
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "sortSpinner: selected");
                sort_type = i;
                config.setSortType(sort_type);
                updateListView(currentPath, sort_type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "Nothing selected.");
            }
        });
        // 配置ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    if (i == 0) {
                        if (currentPath.equals("/")) {
                            return;
                        }
                        currentPath = new File(currentPath).getParent();
                        config.setCurrentPath(currentPath);
                        dir.setText("  当前目录：" + currentPath);
                        updateListView(currentPath, sort_type);
                    } else {
                        try {
                            File file = new File(currentPath + File.separator + listView.getAdapter().getItem(i));
                            if (file.isDirectory()) {
                                currentPath = file.getPath();
                                config.setCurrentPath(currentPath);
                                updateListView(currentPath, sort_type);
                            } else {
                                Intent intent = new Intent(FileExplorerActivity.this, MainActivity.class);
                                intent.setData(Uri.fromFile(file));
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        // 配置设置按钮
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FileExplorerActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void updateListView(String path, int sortType) {
        if (!prepared) {
            return;
        }
        Log.d(TAG, "ListView updated.");
        ArrayList<File> list = null;
        try {
            File[] files = new File(path).listFiles();
            list = new ArrayList<>();
            for (int i = 0; i < files.length; i++) {
                list.add(files[i]);
            }
            //名称 A→Z
            if (sortType == 0) {
                Collections.sort(list);
            }
            //名称 Z→A
            else if (sortType == 1) {
                Collections.sort(list);
                ArrayList newList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    newList.add(list.get(list.size() - i - 1));
                }
                list = newList;
            }
            //修改日期 新→旧
            else if (sortType == 2) {
                Collections.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        Long flag = o2.lastModified() - o1.lastModified();
                        if (flag > 0) {
                            return 1;
                        } else if (flag < 0) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
            }
            //修改日期 旧→新
            else if (sortType == 3) {
                Collections.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        Long flag = o1.lastModified() - o2.lastModified();
                        if (flag > 0) {
                            return 1;
                        } else if (flag < 0) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
            }
            //大小 小→大
            else if (sortType == 4) {
                Collections.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        Long flag = o1.length() - o2.length();
                        if (flag > 0) {
                            return 1;
                        } else if (flag < 0) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
            }
            //大小 大→小
            else {
                Collections.sort(list, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        Long flag = o2.length() - o1.length();
                        if (flag > 0) {
                            return 1;
                        } else if (flag < 0) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] names;
        if (list == null || list.size() == 0) {
            names = new String[1];
            names[0] = "..";
        } else {
            names = new String[list.size() + 1];
            names[0] = "..";
            for (int i = 0; i < list.size(); i++) {
                names[i + 1] = list.get(i).getName();
            }
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, names);
        listView.setAdapter(arrayAdapter);
    }
}
