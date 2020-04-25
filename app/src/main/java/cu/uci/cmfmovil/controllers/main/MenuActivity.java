package cu.uci.cmfmovil.controllers.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import com.activeandroid.ActiveAndroid;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.controllers.about.AboutUsActivity;
import cu.uci.cmfmovil.controllers.config.SettingsActivity;
import cu.uci.cmfmovil.controllers.problem.ProblemLstActivity;
import cu.uci.cmfmovil.controllers.profile.UserProfileActivity;
import cu.uci.cmfmovil.controllers.sync.SyncronizationActivity;
import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by Arlen Enrique Toledano Tamayo on 3/2/16.
 */
public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        CmfUtils.init(this);
        init();
    }

    private void init() {
        //gestionar los eventos
        int ids[] = {
                R.id.btn_problem_lst,
                R.id.btn_profile_show,
                R.id.btn_sinc_show,
                R.id.btn_config_show,
                R.id.btn_about_us
        };
        Class cls[] = {
                ProblemLstActivity.class,
                UserProfileActivity.class,
                SyncronizationActivity.class,
                SettingsActivity.class,
                AboutUsActivity.class
        };
        View btn;
        for (int i = 0; i < ids.length; i++) {
            final Class class_ = cls[i];
            btn = findViewById(ids[i]);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switchView(class_);
                }
            });
        }
        //
        ActiveAndroid.initialize(this);
        long count = DatabaseUtils.queryNumEntries(ActiveAndroid.getDatabase(), (new Problem()).getTableName());
        if (count == 0) {
            CmfUtils.setupDemoProblems();
            CmfUtils.setUpProfile();
        }
        CmfUtils.isEditablePasswordPreference = false;
        final SharedPreferences prefs = getSharedPreferences("security", Context.MODE_PRIVATE);
        if (prefs.getInt("alreadySync", -1) == -1) {
            SharedPreferences.Editor ed = prefs.edit();
            ed.putInt("alreadySync", 0);
            ed.commit();
        }
        if (prefs.getString("encodedPass", "_").equals("_")) {
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("encodedPass", "");
            ed.commit();
        }
        if (prefs.getString("token", "_").equals("_")) {
            SharedPreferences.Editor ed = prefs.edit();
            ed.putString("token", "NO_TOKEN");
            ed.commit();
        }
        PreferenceManager.setDefaultValues(this, R.xml.config_settings, false);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(CmfUtils.passListener = (new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (CmfUtils.isEditablePasswordPreference) {
                    if (key.equals("password")) {
                        String pass = sharedPreferences.getString(key, "");
                        if (pass.isEmpty()) {
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putString("encodedPass", "");
                            edit.commit();
                        } else {
                            System.out.println("mm");
                            SharedPreferences.Editor edit = prefs.edit();
                            edit.putString("encodedPass", CmfUtils.encodeAES(pass));
                            edit.commit();
                            CmfUtils.isEditablePasswordPreference = false;
                            SharedPreferences.Editor edit1 = sharedPreferences.edit();
                            edit1.putString(key, "");
                            edit1.apply();
                            edit1.commit();
                            CmfUtils.isEditablePasswordPreference = true;
                            //System.err.println("PASS: " + pass);
                        }
                    }
                }
            }
        }));
    }

    private void switchView(Class c) {
        Intent i = new Intent(this, c);
        startActivity(i);
    }




}
