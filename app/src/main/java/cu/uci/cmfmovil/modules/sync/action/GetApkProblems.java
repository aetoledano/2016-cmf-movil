package cu.uci.cmfmovil.modules.sync.action;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.preference.PreferenceManager;

import com.activeandroid.ActiveAndroid;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Profile;
import cu.uci.cmfmovil.modules.sync.CmfRequest;
import cu.uci.cmfmovil.modules.sync.CmfRequestException;
import cu.uci.cmfmovil.modules.sync.Utils;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/12/16.
 */
public class GetApkProblems extends CmfRequest {

    public GetApkProblems(Context context) {
        super(context);
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Espere mientras los problemas son descargados.");
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utils.createDialog(GetApkProblems.this.context, "Información", "La descarga ha sido cancelada.", -1);
                GetApkProblems.this.cancel(true);
            }
        });
    }

    @Override
    protected JSONObject buildRequestParams() throws CmfRequestException {
        String token = Login.getLoginService().getToken();
        int max = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("problemsToDownload", "20"));
        JSONArray pIds = new JSONArray();
        Cursor cursor = ActiveAndroid.getDatabase().rawQuery("SELECT problemId as id FROM " + new Problem().getTableName(), new String[]{});
        if (cursor.getCount() > 0) {
            int index = cursor.getColumnIndex("id");
            cursor.moveToFirst();
            do {
                pIds.put(cursor.getInt(index));
            } while (cursor.moveToNext());
        }
        boolean acceptedProblems = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getBoolean("acceptedProblems", true);
        try {
            JSONObject data = new JSONObject();
            data.put("token", token);
            data.put("problemIds", pIds.toString());
            data.put("max", max);
            data.put("accepted", acceptedProblems);
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    Profile usrProfile;
    List<Problem> problemLst;

    @Override
    protected void parseResponse(JSONObject data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            usrProfile = mapper.readValue(
                    data.getJSONObject("profile").toString(),
                    Profile.class
            );
            problemLst = mapper.readValue(
                    data.getJSONObject("problems").toString(),
                    new TypeReference<List<Problem>>() {
                    }
            );
            int count = problemLst.size();
            if (count > 0)
                message = "Se han descargado " + count + " problemas satisfactoriamente.";
            else
                message = "No hay problemas disponibles para descargar.";
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateTables() {
        Utils.saveProfile(usrProfile);
        if (problemLst.size() > 0)
            try {
                ActiveAndroid.beginTransaction();
                for (Problem p : problemLst) {
                    Utils.saveProblem(p);
                }
                ActiveAndroid.setTransactionSuccessful();
            } finally {
                ActiveAndroid.endTransaction();
            }
    }

}
