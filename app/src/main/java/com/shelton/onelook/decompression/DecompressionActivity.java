package com.shelton.onelook.decompression;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shelton.onelook.R;
import com.shelton.onelook.adapter.CompressionListAdapter;
import com.shelton.onelook.util.FileUtil;
import com.shelton.onelook.widget.SwipeBackActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DecompressionActivity extends SwipeBackActivity implements DecompressionContract.View {
    @BindView(R.id.decompression_manager_bar_theme)
    View decompressionManagerBarTheme;
    @BindView(R.id.decompression_manager_back)
    Button decompressionManagerBack;
    @BindView(R.id.title_file)
    TextView title;
    @BindView(R.id.decompression_file_list)
    ListView decompressionFileList;
    @BindView(R.id.decompression_manager_unzip)
    TextView decompressionButton;

    private ProgressDialog mDialog;
    private CompressionListAdapter adapter;
    private String handleFilePath;
    private List<Object> data = new ArrayList<>();
    private String type;
    private DecompressionPresenter presenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decompression);
        Intent intent = getIntent();
        handleFilePath = intent.getStringExtra("file_path");
        ButterKnife.bind(this);
        presenter = new DecompressionPresenter(this);

        initData();
        initView();
    }


    private void initData() {
        mDialog = new ProgressDialog(this);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setTitle("正在解析压缩包");
        mDialog.setMessage("请稍等...");
        mDialog.show();
        adapter = new CompressionListAdapter(this, data);
        switch (FileUtil.getExtensionName(handleFilePath)) {
            case "zip":
                type = "zip";
                presenter.resolveZip(handleFilePath);
                break;
            case "rar":
                type = "rar";
                presenter.resolveRar(handleFilePath);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void initView() {
        title.setText(handleFilePath);
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        decompressionManagerBarTheme.setBackgroundColor(Color.parseColor(preferences.getString("theme_color",
                "#474747")));
        decompressionManagerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        decompressionFileList.setAdapter(adapter);
        decompressionFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                presenter.resolveFileContent(type, handleFilePath, data.get(position));
            }
        });

        decompressionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.decompression(type, handleFilePath, FileUtil.getDirFromPath(handleFilePath) + File.separator +
                                FileUtil.getFileNameNoEx(FileUtil.getFileNameFromPath(handleFilePath)),
                        false);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File[] files = new File(getExternalCacheDir() + "/compression_temp/").listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        presenter.unsubscribe();
    }


    @Override
    public void showResolvedCompressedFile(List<Object> dataFile) {
        mDialog.dismiss();
        data.addAll(dataFile);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showResolveFileContent(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void showToast(String msg) {
        mDialog.dismiss();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
