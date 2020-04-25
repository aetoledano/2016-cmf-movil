package cu.uci.cmfmovil.modules.judge;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import java.util.Date;

import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Profile;
import cu.uci.cmfmovil.modules.data.Sending;
import cu.uci.cmfmovil.utils.CmfUtils;
import cu.uci.cmfmovil.utils.Security;

/**
 * Created by tesis on 5/2/16.
 */
public class JudgingMotor {
    private String db_ans, usr_ans;

    public static boolean judge(Context c, Problem p, String userInput) {
        Profile profile = Profile.first(Profile.class);
        //
        p.intents++;
        p.myIntents++;
        //
        Sending sending = new Sending();
        sending.date = new Date();
        sending.problemId = p.problemId;
        sending.usrInput = userInput;
        sending.sync = 0;
        //
        userInput = processUserInput(userInput);
        boolean accepted = p.answer.equals(userInput);
        if (accepted) {
            sending.accepted = 1;
            p.accepted = 1;
            p.acCount++;
            profile.neurons += 5;
            profile.score++;
        } else {
            sending.accepted = -1;
            p.accepted = -1;
            //modificar el score del problema
        }
        p.acPercent = (double) (p.acCount * 100) / (double) (p.intents);
        ActiveAndroid.beginTransaction();
        profile.save();
        sending.save();
        p.save();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        return accepted;
    }

    static private String processUserInput(String input) {
        input = input.trim();
        return CmfUtils.encodeAES(input);
    }

}
