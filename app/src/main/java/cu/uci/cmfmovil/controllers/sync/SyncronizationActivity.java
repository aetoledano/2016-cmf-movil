package cu.uci.cmfmovil.controllers.sync;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.modules.sync.action.GetApkProblems;
import cu.uci.cmfmovil.modules.sync.Connection;
import cu.uci.cmfmovil.modules.sync.action.UpdateApkData;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by Arlen Enrique Toledano Tamayo on 3/2/16.
 */
public class SyncronizationActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sinc_syncronization);
        init();
    }


    private void init() {
        String user = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("username", "").trim();
        if (!user.isEmpty()) {
            setTitle("Sincronízate con CMF " + user);
        } else {
            setTitle("Quién eres?");
        }
        Button btnSync = (Button) findViewById(R.id.sync_btnSync);
        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connection.isConnected()) {
                    new UpdateApkData(SyncronizationActivity.this).execute();
                } else {
                    createDialog(SyncronizationActivity.this, "Error", "La conexión de red no está disponible.", -1);
                    //Toast toast = Toast.makeText(SyncronizationActivity.this, "La conexión de red no esta disponible!", Toast.LENGTH_SHORT);
                    //toast.show();
                }
            }
        });
        Button btndownload = (Button) findViewById(R.id.sync_btndownload);
        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connection.isConnected()) {
                    new GetApkProblems(SyncronizationActivity.this).execute();
                } else {
                    createDialog(SyncronizationActivity.this, "Error", "La conexión de red no está disponible.", -1);
                    //Toast toast = Toast.makeText(SyncronizationActivity.this, "La conexión de red no esta disponible!", Toast.LENGTH_SHORT);
                    //toast.show();
                }
            }
        });
    }



    private void createDialog(Context context, String title, String message, int type) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setIcon(XXXXXXX)
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

}
