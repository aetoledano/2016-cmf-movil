package cu.uci.cmfmovil.modules.sync;


import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import cu.uci.cmfmovil.modules.sync.action.Login;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/2/16.
 */
public class Connection {

    public static final String URL_BASE = "http://10.42.0.182:8080" + "/cmf.apk-server/cmfm/";//"http://cmf.uci.cu" + "/cmf.apk-server/cmfm/";

    public static boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) CmfUtils.getContext().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String sendRequest(String ACTION, JSONObject params_as_json, int timeout) throws IOException, CmfRequestException, JSONException, SocketTimeoutException {

        byte params[] = params_as_json.toString().getBytes("UTF-8");
        URL url = new URL(URL_BASE + ACTION);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setReadTimeout(timeout);
        conn.setConnectTimeout(timeout);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestMethod("POST");
        conn.setFixedLengthStreamingMode(params.length);

        BufferedOutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(params);
        out.flush();
        out.close();
        conn.connect();

        //read the rawResponse data
        int responseCode = conn.getResponseCode();
        String response = "";
        switch (responseCode) {
            case HttpURLConnection.HTTP_OK:
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null)
                    response += line;
                br.close();
                break;
            default:
                throw new CmfRequestException("Error conectando con CMF. Código de error: "+responseCode, responseCode);
        }
        conn.disconnect();
        return response;
    }

}
