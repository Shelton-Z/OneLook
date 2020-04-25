package com.shelton.onelook.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.shelton.onelook.R;


public class PreferenceHead extends Preference {
    private Context context;
    private View.OnClickListener onBackButtonClickListener;

    @SuppressWarnings("unused")
    public PreferenceHead(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private PreferenceHead(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_head, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        Button btBack = view.findViewById(R.id.config_back);
        btBack.setOnClickListener(v -> {
            if (onBackButtonClickListener != null) {
                onBackButtonClickListener.onClick(v);
            }
        });
        View settingBarTheme = view.findViewById(R.id.setting_bar_theme);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        settingBarTheme.setBackgroundColor(Color.parseColor(preferences.getString("theme_color", "#fb7299")));
    }

    public void setOnBackButtonClickListener(View.OnClickListener onClickListener) {
        this.onBackButtonClickListener = onClickListener;
    }
}
