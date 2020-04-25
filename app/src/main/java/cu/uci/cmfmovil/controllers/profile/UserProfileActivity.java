package cu.uci.cmfmovil.controllers.profile;

import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;

import java.util.ArrayList;
import java.util.List;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Profile;
import cu.uci.cmfmovil.modules.data.Security;
import cu.uci.cmfmovil.modules.data.Sending;
import cu.uci.cmfmovil.utils.CmfUtils;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by Arlen Enrique Toledano Tamayo on 3/2/16.
 */
public class UserProfileActivity extends ActionBarActivity {
    private PieChartView myintents_chart;
    private TextView ranking;
    private TextView score;
    private TextView total_myintents;
    //private TextView acpercent;
    private TextView total_intents;
    private PieChartView intents_chart;
    //private TextView attemps_percent;
    private TextView votes;
    private PieChartView votes_chart;
    private TextView label_problems;
    // private TextView label_tasaci;
    private PieChartView problems_chart;
    private String whoAmI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_userprofile);
        init();
    }

    //
    private final String PROBLEM = (new Problem()).getTableName();
    private final String COMMENT = (new Comment()).getTableName();
    private final String SENDING = (new Sending()).getTableName();
    //

    private void init() {

        whoAmI = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("username", "").trim();
        if (!whoAmI.isEmpty()) {
            setTitle("Tu desempeño " + whoAmI);
        } else {
            setTitle("Quién eres?");
        }

        PieChartData data;
        List<SliceValue> values;
        SliceValue sliceValue;
        Profile profile = Profile.first(Profile.class);
        //queries

        int attemptedProblems = queryAttemptedProblems();//cantidad de problemas que he intentado
        int totalIntents = queryTotalIntents();//cantidad  de intentos que hecho
        int totalProblemAccepted = queryTotalProblemAccepted();//cantidad de problemas aceptados
        int totalProblemFails = queryTotalProblemFails();//cantidad de problemas fallados
        int totalIntentFailed = totalIntents - totalProblemAccepted;
        //double percentAcceptedIntents = (double) (totalProblemAccepted * 100) / (double) totalIntents; //porciento de aceptados contra intentos
        int problemsCount = queryProblems();//cantidad de problemas
        //double attemptedProblemsPercent = (double) (attemptedProblems * 100) / (double) problemsCount;//porciento de intentados contra el total
        int likesCount = queryLikes();//cantidad de votos positivos
        int dislikesCount = queryDislikes();//cantidad de votos negativos
        //end queries

        //set up data
        //
        ranking = (TextView) findViewById(R.id.profile_label_ranking);
        ranking.setText("Ranking: " + profile.ranking);
        //
        score = (TextView) findViewById(R.id.profile_label_score);
        score.setText("Acumulado: " + CmfUtils.format(profile.score) + " pts");
        //
        label_problems = (TextView) findViewById(R.id.profile_label_problems);
        problems_chart = (PieChartView) findViewById(R.id.profile_problems_chart);
        // label_tasaci = (TextView) findViewById(R.id.profile_label_tasaci);
        if (attemptedProblems > 0) {
            label_problems.setText("Has intentado resolver " + attemptedProblems + " problemas");
            data = new PieChartData();
            data.setHasLabels(true);
            data.setHasLabelsOutside(false);
            data.setHasCenterCircle(false);
            data.setValueLabelBackgroundEnabled(true);
            values = new ArrayList<SliceValue>();
            sliceValue = new SliceValue((float) totalProblemAccepted, ChartUtils.COLOR_BLUE);
            sliceValue.setLabel("Aceptados: " + totalProblemAccepted);
            values.add(sliceValue);
            sliceValue = new SliceValue((float) totalProblemFails, ChartUtils.COLOR_RED);
            sliceValue.setLabel("Incorrectos: " + totalProblemFails);
            values.add(sliceValue);
            data.setValues(values);
            problems_chart.setPieChartData(data);
            //label_tasaci.setText("Tasa de Correctos/Incorrectos: " + CmfUtils.format((double) totalProblemAccepted / (double) totalProblemFails));
        } else {
            label_problems.setText("Aún no has intentado resolver ningún problema");
            problems_chart.setVisibility(View.GONE);
            // label_tasaci.setVisibility(View.GONE);
        }
        //
        total_myintents = (TextView) findViewById(R.id.profile_label_total_myintents);
        myintents_chart = (PieChartView) findViewById(R.id.profile_myintents_chart);
        //acpercent = (TextView) findViewById(R.id.profile_label_acpercent);
        if (totalIntents > 0) {
            total_myintents.setText("Has realizado " + totalIntents + " intentos");
            data = new PieChartData();
            data.setHasLabels(true);
            data.setHasLabelsOutside(false);
            data.setHasCenterCircle(false);
            data.setValueLabelBackgroundEnabled(true);
            values = new ArrayList<SliceValue>();
            sliceValue = new SliceValue((float) totalIntentFailed, ChartUtils.COLOR_RED);
            sliceValue.setLabel("Incorrectos: " + totalIntentFailed);
            values.add(sliceValue);
            sliceValue = new SliceValue((float) totalProblemAccepted, ChartUtils.COLOR_BLUE);
            sliceValue.setLabel("Aceptados: " + totalProblemAccepted);
            values.add(sliceValue);
            data.setValues(values);
            myintents_chart.setPieChartData(data);
            // acpercent.setText("para un " + CmfUtils.format(percentAcceptedIntents) + "% de intentos correctos");
        } else {
            total_myintents.setText("Qué esperas para hacer tu primer intento?");
            myintents_chart.setVisibility(View.GONE);
            // acpercent.setVisibility(View.GONE);
        }
        //
        total_intents = (TextView) findViewById(R.id.profile_label_total_intents);
        intents_chart = (PieChartView) findViewById(R.id.profile_intents_chart);
        // attemps_percent = (TextView) findViewById(R.id.profile_label_attemps_percent);
        if (attemptedProblems > 0) {
            total_intents.setText("Has intentado " + attemptedProblems + " problemas de " + problemsCount + " disponibles");
            data = new PieChartData();
            data.setHasLabels(true);
            data.setHasLabelsOutside(false);
            data.setHasCenterCircle(false);
            data.setValueLabelBackgroundEnabled(true);
            values = new ArrayList<SliceValue>();
            sliceValue = new SliceValue((float) attemptedProblems, ChartUtils.COLOR_BLUE);
            sliceValue.setLabel("Intentados: " + attemptedProblems);
            values.add(sliceValue);
            sliceValue = new SliceValue((float) (problemsCount - attemptedProblems), ChartUtils.COLOR_ORANGE);
            sliceValue.setLabel("Sin intentar: " + (problemsCount - attemptedProblems));
            values.add(sliceValue);
            data.setValues(values);
            intents_chart.setPieChartData(data);
            // attemps_percent.setText("para un " + CmfUtils.format(attemptedProblemsPercent) + "% de problemas intentados");
        } else {
            if (problemsCount > 0) {
                total_intents.setText("Tienes " + problemsCount + " problemas por probar. Anímate!");
            } else {
                total_intents.setText("Deberías descargar algunos problemas para comenzar");
            }
            intents_chart.setVisibility(View.GONE);
            //   attemps_percent.setVisibility(View.GONE);
        }
        //
        votes = (TextView) findViewById(R.id.profile_label_votes);
        votes_chart = (PieChartView) findViewById(R.id.profile_votes_chart);
        if ((likesCount + dislikesCount) > 0) {
            votes.setText("Has votado por " + (likesCount + dislikesCount) + " problemas");
            data = new PieChartData();
            data.setHasLabels(true);
            data.setHasLabelsOutside(false);
            data.setHasCenterCircle(false);
            data.setValueLabelBackgroundEnabled(true);
            values = new ArrayList<SliceValue>();
            sliceValue = new SliceValue((float) likesCount, ChartUtils.COLOR_BLUE);
            sliceValue.setLabel("Te gustan: " + likesCount);
            values.add(sliceValue);
            sliceValue = new SliceValue((float) dislikesCount, ChartUtils.COLOR_VIOLET);
            sliceValue.setLabel("No te gustan: " + dislikesCount);
            values.add(sliceValue);
            data.setValues(values);
            votes_chart.setPieChartData(data);
        } else {
            votes.setText("Aún no has votado");
            votes_chart.setVisibility(View.GONE);
        }

    }


    private int queryTotalIntents() {
        return queryDB("select sum(myIntents) as count from " + PROBLEM);
    }

    private int queryTotalProblemAccepted() {
        return queryDB("select count(*) as count from " + PROBLEM + " where accepted = 1");
    }

    private int queryTotalProblemFails() {
        return queryDB("select count(*) as count from " + PROBLEM + " where accepted = -1");
    }

    private int queryProblems() {
        return queryDB("select count(*) as count from " + PROBLEM);
    }

    private int queryDislikes() {
        return queryDB("select count(*) as count from " + PROBLEM + " where myVote=-1");
    }

    private int queryLikes() {
        return queryDB("select count(*) as count from " + PROBLEM + " where myVote=1");
    }

    private int queryAttemptedProblems() {
        return queryDB("select count(*) as count from " + PROBLEM + " where accepted <> 0");
    }

    private int queryDB(String query) {
        Cursor cursor = ActiveAndroid.getDatabase().rawQuery(query, new String[]{});
        cursor.moveToFirst();
        return cursor.getInt(cursor.getColumnIndex("count"));
    }


}
