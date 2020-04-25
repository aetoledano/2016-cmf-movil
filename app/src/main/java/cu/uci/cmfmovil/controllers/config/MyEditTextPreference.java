package cu.uci.cmfmovil.controllers.config;

import android.content.Context;
import android.preference.EditTextPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/10/16.
 */
public class MyEditTextPreference extends EditTextPreference {

    public MyEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public CharSequence getTitle() {
        String value = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("username", "Usuario");
        if (value.trim().equals(""))
            return super.getTitle();
        return value;
    }

}
