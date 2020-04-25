package cu.uci.cmfmovil.modules.sync.action;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.modules.data.Profile;
import cu.uci.cmfmovil.modules.data.Sending;
import cu.uci.cmfmovil.modules.data.Vote;
import cu.uci.cmfmovil.modules.sync.CmfRequest;
import cu.uci.cmfmovil.modules.sync.CmfRequestException;
import cu.uci.cmfmovil.modules.sync.Utils;

/**
 * Created by tesis on 5/10/16.
 */
public class UpdateApkData extends CmfRequest {

    public UpdateApkData(Context context) {
        super(context);
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Espere mientras sus estadísticas son sincronizadas.");
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Utils.createDialog(UpdateApkData.this.context, "Información", "La sincronización ha sido cancelada.", -1);
                UpdateApkData.this.cancel(true);
            }
        });
    }

    @Override
    protected JSONObject buildRequestParams() throws CmfRequestException {
        String token = Login.getLoginService().getToken();
        try {
            JSONObject data = new JSONObject();
            data.put("token", token);
            data.put("sendings", getProblemSubmissions());
            data.put("comments", getCommentSubmissions());
            data.put("votes", getVoteSubmissions());
            return data;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    Profile profile;

    @Override
    protected void parseResponse(JSONObject data) {
        try {
            profile = new Profile();
            profile.ranking = data.getString("ranking");
            profile.score = data.getDouble("score");
            message = "Tus estadísticas han sido sincronizadas satisfactoriamente.";
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void updateTables() {
        try {
            ActiveAndroid.beginTransaction();
            Utils.saveProfile(profile);
            Vote.delete(Vote.class);
            ActiveAndroid.execSQL("UPDATE " + new Comment().getTableName() + " SET sync=1 WHERE sync=0");
            ActiveAndroid.execSQL("UPDATE " + new Sending().getTableName() + " SET sync=1 WHERE sync=0");
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    //methods to gather app data
    private JSONArray getProblemSubmissions() {
        JSONArray jsonArray = new JSONArray();
        ArrayList<Sending> arrayList = new Select().from(Sending.class).where("sync=0").execute();
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("problemId", arrayList.get(i).problemId);
                jsonObject.put("accepted", arrayList.get(i).accepted);
                jsonObject.put("date", arrayList.get(i).date.getTime());
                jsonObject.put("usrInput", arrayList.get(i).usrInput);
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println("ERROR making Sending table to JsonObject");
            }

            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private JSONArray getCommentSubmissions() {
        JSONArray jsonArray = new JSONArray();
        ArrayList<Comment> arrayList = new Select().from(Comment.class).where("sync=0").execute();
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("problemId", arrayList.get(i).problemId);
                jsonObject.put("topic", arrayList.get(i).topic);
                jsonObject.put("message", arrayList.get(i).message);
                jsonObject.put("date", arrayList.get(i).date.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println("ERROR making Sending table to JsonObject");
            }

            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private JSONArray getVoteSubmissions() {
        JSONArray jsonArray = new JSONArray();
        ArrayList<Vote> arrayList = Vote.all(Vote.class);
        for (int i = 0; i < arrayList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("problemId", arrayList.get(i).problemId);
                jsonObject.put("value", arrayList.get(i).value);
                jsonObject.put("date", arrayList.get(i).date.getTime());
            } catch (JSONException e) {
                e.printStackTrace();
                System.err.println("ERROR making Sending table to JsonObject");
            }
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

}
