package cu.uci.cmfmovil.modules.sync.action;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cu.uci.cmfmovil.modules.sync.CmfRequestException;
import cu.uci.cmfmovil.modules.sync.Connection;
import cu.uci.cmfmovil.modules.sync.status.StatusCode;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by raven on 6/9/16.
 */
public class Login {

    private final String ACTION = this.getClass().getSimpleName().toLowerCase();
    private final String NT = "NO_TOKEN";

    private static Login loginInstance;

    public static Login getLoginService() {
        if (loginInstance == null)
            return loginInstance = new Login();
        return loginInstance;
    }

    public String getToken() throws CmfRequestException {
        SharedPreferences prefs = CmfUtils.getContext().getSharedPreferences("security", Context.MODE_PRIVATE);
        String tokenStr = prefs.getString("token", NT);
        if (NT.equals(tokenStr)) {
            if ((tokenStr = login()) != null) {
                SharedPreferences.Editor ed = prefs.edit();
                ed.putString("token", tokenStr);
                ed.commit();
            }
        }
        return tokenStr;
    }

    public void unsetToken() throws CmfRequestException {
        SharedPreferences prefs = CmfUtils.getContext().getSharedPreferences("security", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("token", NT);
        ed.commit();
    }

    private String login() throws CmfRequestException {

        try {
            String user = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("username", "").trim();
            String pass = CmfUtils.getContext().getSharedPreferences("security", Context.MODE_PRIVATE).getString("encodedPass", "").trim();

            if (user.equals("") || pass.equals(""))
                throw new CmfRequestException("No has introducido tus datos de la cuenta CMF.", StatusCode.STATUS_CODE_NOT_USER_OR_PASSWORD);

            JSONObject credentials = new JSONObject();
            credentials.put("user", CmfUtils.encodeAES(user));
            credentials.put("pass", pass);

            String rawResponse = Connection.sendRequest(ACTION, credentials,15000);

            JSONObject ans = new JSONObject(rawResponse);
            switch (ans.getInt("statuscode")) {
                case StatusCode.STATUS_CODE_BAD_REQUEST:
                    throw new CmfRequestException("Datos corruptos.", StatusCode.STATUS_CODE_BAD_REQUEST);

                case StatusCode.STATUS_CODE_UNAUTHORIZED:
                    throw new CmfRequestException("Usuario o contraseña incorrectos.", StatusCode.STATUS_CODE_UNAUTHORIZED);

                case StatusCode.STATUS_CODE_CREATED:
                    return ans.getString("token");
            }
            return null;
        } catch (JSONException | IOException e) {

        }
        return null;
    }

}
