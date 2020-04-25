package cu.uci.cmfmovil.controllers.about;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import cu.uci.cmfmovil.R;

/**
 * Created by Arlen Enrique Toledano Tamayo on 3/2/16.
 */
public class AboutUsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setIcon(getResources().getDrawable(R.mipmap.ic_launcher));
        getSupportActionBar().setElevation(0);
        setTitle("Acerca de ...");
        setContentView(R.layout.about_about);
        //init();

    }

}
