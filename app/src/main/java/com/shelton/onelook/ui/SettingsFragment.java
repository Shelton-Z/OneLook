package com.shelton.onelook.ui;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shelton.onelook.R;
import com.shelton.onelook.task.ClearCacheTask;
import com.shelton.onelook.widget.PreferenceHead;

import androidx.annotation.Nullable;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        ((PreferenceHead) findPreference("configHead")).setOnBackButtonClickListener(v ->
                SettingsFragment.this.getActivity().finish());
        findPreference("clear_cache").setOnPreferenceChangeListener((preference, newValue) -> {
            new ClearCacheTask(getActivity()).execute(newValue.toString());
            return false;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        assert view != null;
        view.setBackgroundColor(Color.WHITE);
        /*
        去除preference两边的空白,可修改子preference分割线
         */
        ListView listView = view.findViewById(android.R.id.list);
        listView.setPadding(0, listView.getPaddingTop(), 0, listView.getPaddingBottom());
        return view;
    }


    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference != null) {
            if (preference.getKey().equals("restore_default")) {
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                editor.putString("text_size", "100");
                editor.putString("theme_color", "#fb7299");
                editor.putStringSet("clear_cache", null);
                editor.apply();
                getActivity().finish();
                return true;
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
