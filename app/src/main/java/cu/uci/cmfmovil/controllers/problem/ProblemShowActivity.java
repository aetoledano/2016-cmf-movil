package cu.uci.cmfmovil.controllers.problem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.controllers.problem.adapters.ProblemCommentLstAdapter;
import cu.uci.cmfmovil.controllers.problem.adapters.ProblemSendingLstAdapter;
import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Sending;
import cu.uci.cmfmovil.modules.data.Vote;
import cu.uci.cmfmovil.modules.judge.JudgingMotor;
import cu.uci.cmfmovil.utils.CmfUtils;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.PieChartView;

/**
 * Created by tesis on 3/8/16.
 */
public class ProblemShowActivity extends Activity {

    private static String msgs[] = {
            "inténtalo de nuevo!",
            "hoy no es tu día de suerte!",
            "prueba otra vez!", "quizás para la próxima!",
            "una vez más, por favor!",
            "hazme saber cuando vayas en serio!",
            "vuelve a intentarlo!"};

    private Problem problem;

    //tabRead
    private TextView title, text, input, specification;
    private ImageView image;
    private ImageButton btnJudge;
    private EditText userInput;

    //tabStatistics
    private TextView intents, acpercent, myintents, score, problemState, votesState, comments;
    private Button votePlus, voteMinus;
    private ImageView problemStateIndicator;
    private PieChartView pieChartView;
    private PieChartData pieChartData;
    //components here

    //tabComments
    private String whoAmI;
    private EditText comment_msg, comment_topic;
    private Button showCommentDialog, addComment;
    private LinearLayout commentDialog;
    private LinearLayout layoutShowCommentDialog;
    private ListView commentListView;
    private ArrayList<Comment> commentList;
    private ProblemCommentLstAdapter problemCommentLstAdapter;

    //tabSubmissions
    private ListView submissionsListView;
    private ArrayList<Sending> submissionList;
    private ProblemSendingLstAdapter problemSendingLstAdapter;

    //components here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_showproblem_tabhost);


        TabHost tabs = (TabHost) findViewById(R.id.problem_showproblemtabhost);
        tabs.setup();

        Resources resources = getResources();
        //tabProblemRead
        TabHost.TabSpec spec = tabs.newTabSpec("READ");
        spec.setContent(R.id.problem_tab_showproblem);
        spec.setIndicator("", resources.getDrawable(R.drawable.ic_action_read));
        tabs.addTab(spec);
        addEventsTabProblemRead();

        //tabProblemSending
        spec = tabs.newTabSpec("SUBMISSIONS");
        spec.setContent(R.id.problem_tab_sending);
        spec.setIndicator("", resources.getDrawable(R.drawable.ic_action_showsubmit));
        tabs.addTab(spec);
        addEventsTabSubmissions();

        //tabProblemStatistics
        spec = tabs.newTabSpec("STATISTICS");
        spec.setContent(R.id.problem_tab_statistics);
        spec.setIndicator("", resources.getDrawable(R.drawable.ic_action_statistics));
        tabs.addTab(spec);
        addEventsTabProblemStatistics();

        //tabProblemComments
        spec = tabs.newTabSpec("COMMENTS");
        spec.setContent(R.id.problem_showproblem_tab_comments);
        spec.setIndicator("", resources.getDrawable(R.drawable.ic_action_comment));
        tabs.addTab(spec);
        addEventTabProblemComments();

        /*
        adding events to tabHost, reacts when user change among tabs
         */
        tabs.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "READ":
                        updateViewTabProblemRead();
                        break;
                    case "SUBMISSIONS":
                        updateViewTabProblemSubmissions();
                        break;
                    case "STATISTICS":
                        updateViewTabProblemStatistics();
                        break;
                    case "COMMENTS":
                        updateViewTabProblemComments();
                        break;
                }
                hideKeyBoard();
            }
        });
        //
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    /*
    hides keyboard after whatever you want
     */
    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            userInput.clearFocus();
        }
    }

    /*
    notify sentence on ViewProblemRead
     */

    private void notifySentence(Context context, boolean accepted, boolean toastMsg) {
        Random rnd = new Random();
        String text = accepted ? "Felicidades lo has resuelto!" : "Respuesta incorrecta, " + msgs[rnd.nextInt(msgs.length)];
        if (toastMsg) {
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            AlertDialog alertDialog;
            alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle(accepted ? "Correcto" : "Incorrecto");
            alertDialog.setMessage(text);
            alertDialog.show();
        }
        if (accepted) {
            CmfUtils.play(1);
        } else {
            CmfUtils.play(3);
        }
    }

    /*
    adding events to TabRead
     */
    private void addEventsTabProblemRead() {
        title = (TextView) findViewById(R.id.problem_showproblem_title);
        text = (TextView) findViewById(R.id.problem_showproblem_text);
        image = (ImageView) findViewById(R.id.problem_showproblem_image);
        specification = (TextView) findViewById(R.id.problem_showproblem_input_specification);
        input = (TextView) findViewById(R.id.problem_showproblem_inputSample);
        userInput = (EditText) findViewById(R.id.problem_showproblem_user_input);
        int problemId = getIntent().getIntExtra("problemId", 0);
        problem = new Select().from(Problem.class).where("problemId=" + problemId).executeSingle();
        btnJudge = (ImageButton) findViewById(R.id.problem_showproblem_btn_judge);
        btnJudge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBoard();
                String str = userInput.getText().toString();
                str = str.trim();
                if (!"".equals(str)) {
                    boolean result = JudgingMotor.judge(getBaseContext(), problem, str);
                    notifySentence(ProblemShowActivity.this, result, true);
                    userInput.setText("");
                    if (result) {
                        updateViewTabProblemRead();
                    }
                }
            }
        });
        if (problem.hasImage == 1) {
            try {
                image.setImageBitmap(CmfUtils.readImage(problemId));
            } catch (Exception e) {
                System.err.println("Could not read image!");
            }
        }
        updateViewTabProblemRead();
    }

    /*
    updates TabViewRead
     */
    private void updateViewTabProblemRead() {
        title.setText(problem.title);
        text.setText(Html.fromHtml(problem.text));
        specification.setText(Html.fromHtml(problem.specification));
        input.setText(problem.inputSample);
        if (problem.accepted == 1) {
            userInput.setText(CmfUtils.decodeAES(problem.answer));
            userInput.setEnabled(false);
            userInput.setGravity(Gravity.CENTER);
            btnJudge.setVisibility(View.GONE);
        } else {
            userInput.setText("");
            userInput.setHint("Escribe tu respuesta");
        }
    }


    private void addEventsTabSubmissions() {
        submissionsListView = (ListView) findViewById(R.id.problem_sendinglst);
    }

    /*
    * updates tab submissions
    * */
    private void updateViewTabProblemSubmissions() {
        submissionList = (new Select()).from(Sending.class).where("problemId=" + problem.problemId).orderBy("date desc").execute();
        if (problemSendingLstAdapter == null) {
            problemSendingLstAdapter = new ProblemSendingLstAdapter(submissionList, this);
            submissionsListView.setAdapter(problemSendingLstAdapter);
        } else {
            problemSendingLstAdapter.setData(submissionList);
            problemSendingLstAdapter.notifyDataSetChanged();
        }
    }

    private void addEventsTabProblemStatistics() {
        intents = (TextView) findViewById(R.id.problem_label_intents);
        acpercent = (TextView) findViewById(R.id.problem_label_acpercent);
        myintents = (TextView) findViewById(R.id.problem_label_myintents);
        score = (TextView) findViewById(R.id.problem_label_score);
        comments = (TextView) findViewById(R.id.problem_label_comments);
        problemState = (TextView) findViewById(R.id.problem_label_state);
        votesState = (TextView) findViewById(R.id.problem_label_votes_state);
        votePlus = (Button) findViewById(R.id.problem_btn_plus);
        voteMinus = (Button) findViewById(R.id.problem_btn_minus);
        problemStateIndicator = (ImageView) findViewById(R.id.problem_label_inidicator_state);
        votePlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problem.myVote = 1;
                problem.votesPlus++;
                problem.save();
                Vote vote = new Vote();
                vote.problemId = problem.problemId;
                vote.value = problem.myVote;
                vote.date = new Date();
                vote.save();
                updateViewTabProblemStatistics();
                CmfUtils.play(0);
            }
        });
        voteMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problem.myVote = -1;
                problem.votesMinus++;
                problem.save();
                Vote vote = new Vote();
                vote.problemId = problem.problemId;
                vote.value = problem.myVote;
                vote.date = new Date();
                vote.save();
                updateViewTabProblemStatistics();
                CmfUtils.play(4);
            }
        });
    }

    private void updateViewTabProblemStatistics() {
        List<SliceValue> values = new ArrayList<SliceValue>();
        int color = ChartUtils.COLOR_BLUE;
        while (color == ChartUtils.COLOR_BLUE) color = ChartUtils.pickColor();
        SliceValue sliceValue = new SliceValue((float) problem.intents - problem.acCount, color);
        sliceValue.setLabel("Incorrectos: " + (problem.intents - problem.acCount));
        values.add(sliceValue);
        sliceValue = new SliceValue((float) problem.acCount, ChartUtils.COLOR_BLUE);
        sliceValue.setLabel("Aceptados: " + problem.acCount);
        values.add(sliceValue);
        if (pieChartView == null) {
            pieChartView = (PieChartView) findViewById(R.id.problem_chart);
            pieChartData = new PieChartData(values);
            pieChartData.setHasLabels(true);
            pieChartData.setHasLabelsOutside(false);
            pieChartData.setHasCenterCircle(false);
            pieChartData.setValueLabelBackgroundEnabled(true);
        } else {
            pieChartData.setValues(values);
        }
        pieChartView.setPieChartData(pieChartData);
        intents.setText("Total de intentos: " + problem.intents);
        acpercent.setText("Porciento: " + CmfUtils.format(problem.acPercent) + "%");
        myintents.setText("Mis intentos a este problema: " + problem.myIntents);
        score.setText("Complejidad: " + CmfUtils.format(problem.score));
        switch (problem.accepted) {
            case -1:
                problemStateIndicator.setImageResource(R.drawable.ic_problem_state_failed);
                problemState.setText("Has fallado en este problema");
                break;
            case 0: //do nothing
                problemStateIndicator.setImageResource(R.drawable.ic_problem_state_not_tried);
                problemState.setText("Aún no has intentado este problema");
                break;
            case 1:
                problemStateIndicator.setImageResource(R.drawable.ic_problem_state_accepted);
                problemState.setText("Ya has resuelto este problema");
                break;
        }
        voteMinus.setText("-" + problem.votesMinus);
        votePlus.setText("+" + problem.votesPlus);
        switch (problem.myVote) {
            case -1:
                votesState.setText("No te gusta este problema");
                voteMinus.setEnabled(false);
                votePlus.setEnabled(false);
                votePlus.setBackgroundResource(R.color.cmf_dark_gray);
                break;
            case 0: //do nothing
                votesState.setText("No has votado por este problema aún");
                break;
            case 1:
                votesState.setText("Te gusta este problema");
                voteMinus.setEnabled(false);
                votePlus.setEnabled(false);
                voteMinus.setBackgroundResource(R.color.cmf_dark_gray);
                break;
        }
        Cursor cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + (new Comment()).getTableName() + " where problemId=" + problem.problemId, new String[]{});
        cursor.moveToFirst();
        comments.setText("Comentarios: " + cursor.getLong(cursor.getColumnIndex("count")));
    }

    /*
    adding events to TabComments
     */
    private void addEventTabProblemComments() {
        whoAmI = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("username", "");
        //
        commentListView = (ListView) findViewById(R.id.problem_showproblem_comments_list);
        commentDialog = (LinearLayout) findViewById(R.id.problem_showproblem_comment_dialog);
        comment_msg = (EditText) findViewById(R.id.problem_showproblem_comment_message);
        comment_topic = (EditText) findViewById(R.id.problem_showproblem_comment_topic);
        showCommentDialog = (Button) findViewById(R.id.problem_showproblem_comment_btn_show_dialog);
        addComment = (Button) findViewById(R.id.problem_showproblem_comment_btn_addComment);
        layoutShowCommentDialog = (LinearLayout) findViewById(R.id.problem_showproblem_comment_layout_btn_show_dialog);
        //
        commentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                layoutShowCommentDialog.setVisibility(View.VISIBLE);
                commentDialog.setVisibility(View.GONE);
                comment_msg.clearFocus();
                comment_topic.clearFocus();
                hideKeyBoard();
            }
        });
        showCommentDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutShowCommentDialog.setVisibility(View.GONE);
                commentDialog.setVisibility(View.VISIBLE);
                comment_topic.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(comment_topic, 0);
            }
        });
        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment_topic.getText().toString().length() > 0 &&
                        comment_msg.getText().toString().length() > 0) {
                    Comment comment = new Comment();
                    comment.message = CmfUtils.encodeAES(comment_msg.getText().toString());
                    comment.topic = CmfUtils.encodeAES(comment_topic.getText().toString());
                    comment.date = new Date();
                    comment.problemId = problem.problemId;
                    comment.author = whoAmI.trim();
                    comment.sync = 0;
                    comment.save();
                    commentList.add(0, comment);
                    comment_topic.setText("");
                    comment_msg.setText("");
                    hideKeyBoard();
                    updateViewTabProblemComments();
                    Toast toast = Toast.makeText(ProblemShowActivity.this, "Tu comentario ha sido añadido!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(ProblemShowActivity.this, "Debes rellenar todos los campos!", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        commentList = null;
        commentsLoaded = false;
    }

    private boolean commentsLoaded;

    private void updateViewTabProblemComments() {
        if (commentList == null) {
            commentList = new ArrayList<>();
            problemCommentLstAdapter = new ProblemCommentLstAdapter(this, commentList);
            commentListView.setAdapter(problemCommentLstAdapter);
        }
        problemCommentLstAdapter.setVisible(problem.accepted == 1);
        if (problemCommentLstAdapter.isVisible()) {
            if (!commentsLoaded) {
                commentList = (new Select()).from(Comment.class).where("problemId=" + problem.problemId).orderBy("date desc").execute();
                problemCommentLstAdapter.setData(commentList);
                commentsLoaded = true;
            }
            problemCommentLstAdapter.notifyDataSetChanged();
            layoutShowCommentDialog.setVisibility(View.VISIBLE);
            commentDialog.setVisibility(View.GONE);
        } else {
            layoutShowCommentDialog.setVisibility(View.GONE);
            commentDialog.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (commentDialog.isShown()) {
            layoutShowCommentDialog.setVisibility(View.VISIBLE);
            commentDialog.setVisibility(View.GONE);
            comment_msg.clearFocus();
            comment_topic.clearFocus();
            hideKeyBoard();
            return;
        }
        super.onBackPressed();
    }
}
