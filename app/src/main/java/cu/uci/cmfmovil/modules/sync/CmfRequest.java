package cu.uci.cmfmovil.modules.sync;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;


import com.fasterxml.jackson.databind.util.JSONPObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/12/16.
 */
public abstract class CmfRequest extends AsyncTask<Void, Void, Void> {


    protected ProgressDialog dialog;

    public final String ACTION = this.getClass().getSimpleName().toLowerCase();

    protected boolean success;

    protected int error;

    protected String rawResponse, message;

    protected JSONObject requestParams, jsonData;

    public Context context;

    public CmfRequest(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            if (error == 0)
                rawResponse = Connection.sendRequest(ACTION, requestParams, 15000);
        } catch (CmfRequestException e) {
            message = e.getMessage();
            error = e.statusCode;
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException)
                message = "El servidor tardó demasiado en responder.";
            else
                message = "Imposible conectar con el servidor CMF.";
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
        error = 0;
        try {
            requestParams = buildRequestParams();
        } catch (CmfRequestException e) {
            error = e.statusCode;
            message = e.getMessage();
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        dialog.dismiss();
        if (!isCancelled()) {
            if (error == 0 && rawResponse != null) {
                getResponseStatus();
                if (success) {
                    parseResponse(jsonData);
                    Utils.createDialog(CmfRequest.this.context, "Información", message, -1);
                    SharedPreferences.Editor ed = CmfUtils.getContext().getSharedPreferences("security", Context.MODE_PRIVATE).edit();
                    ed.putInt("alreadySync", 1);
                    ed.commit();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            updateTables();
                        }
                    }).start();
                    return;
                } else {
                    switch (error) {
                        case 1:
                            message = "Usuario o contraseña incorrectos, ya tienes una cuenta en CMF?";
                            break;
                        case 2:
                            message = "No llegaron datos suficientes al servidor CMF.";
                            break;
                        case 3:
                            message = "Datos corruptos durante la transferencia.";
                            break;
                        default:
                            message = "Error decodificando respuesta del servidor.";
                            break;
                    }
                }
            }
            Utils.createDialog(CmfRequest.this.context, "Error", message, -1);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        dialog.dismiss();
    }

    //methods to be implemented by subclases
    protected abstract JSONObject buildRequestParams() throws CmfRequestException;

    protected abstract void parseResponse(JSONObject data);

    protected abstract void updateTables();

    protected void getResponseStatus() {
        try {
            JSONObject obj = new JSONObject(rawResponse);
            success = obj.getBoolean("success");
            error = obj.getInt("error");
            if (success)
                jsonData = obj.getJSONObject("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
