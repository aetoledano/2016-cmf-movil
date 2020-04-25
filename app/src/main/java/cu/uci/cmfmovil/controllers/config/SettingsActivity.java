package cu.uci.cmfmovil.controllers.config;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;

import java.util.List;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.utils.CmfUtils;


public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config_settings);
        init();
    }

    private void init() {
        CmfUtils.isEditablePasswordPreference = true;
        SharedPreferences secPref = getSharedPreferences("security", Context.MODE_PRIVATE);
        boolean value = secPref.getInt("alreadySync", -1) != 1;
        Preference userField = findPreference("username");
        Preference passField = findPreference("password");
        userField.setEnabled(value);
        passField.setEnabled(value);
    }
}