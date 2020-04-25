package cu.uci.cmfmovil.modules.sync;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Date;

import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Profile;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by raven on 9/15/16.
 */
public class Utils {

    public static void saveProblem(Problem p) {
        p.date = new Date();
        if (p.image != null && !p.image.equals("null") && !p.image.equals("")) {
            p.hasImage = 1;
            byte[] bytes = CmfUtils.strBase64ToByteArray(p.image);
            CmfUtils.writeImage(p.problemId, bytes);
            p.image = "";
        }
        if (p.comments != null && p.comments.size() > 0) {
            for (Comment c : p.comments) {
                c.problemId = p.problemId;
                c.sync = 1;
                //aki falta actualizar la fecha de los comments
                c.save();
            }
            p.comments = null;
        }
        p.save();
    }

    public static void saveProfile(Profile newProfile) {
        Profile profile = Profile.first(Profile.class);
        profile.score = newProfile.score;
        profile.ranking = newProfile.ranking;
        profile.save();
    }

    public static void createDialog(Context context, String title, String message, int type) {
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        //alertDialog.setIcon();
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
