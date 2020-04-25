package cu.uci.cmfmovil.controllers.problem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.ArrayList;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.controllers.problem.adapters.ProblemLstAdapter;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by Arlen Enrique Toledano Tamayo on 3/2/16.
 */
public class ProblemLstActivity extends ActionBarActivity {

    public static final int ALL = 0, ACCEPTED = 1, NOT_TRIED = 2, FAILS = 3, BY_SCORE = 4, LAST_DOWNLOADED = 5, BY_SEARCH = 6;
    private static final String tableName = new Problem().getTableName();

    private boolean isSearchVisible;
    private int currentFilter;
    private String searchText;
    private ListView listView;
    private ArrayList<Problem> listProblem;
    private ProblemLstAdapter problemLstAdapter;
    private int offset, totalProblems, currentPage, totalPages, limit = 10;
    private ImageButton init, end, next, previous;
    private MyEditText page;
    private MyEditText search;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.problemlst_actions, menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ActionBar ab = ProblemLstActivity.this.getSupportActionBar();
        switch (item.getItemId()) {
            case R.id.problemlst_action_search:
                if (isSearchVisible) {
                    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                    hideKeyBoard();
                } else {
                    ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                            | ActionBar.DISPLAY_SHOW_HOME);
                    search.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(search, 0);
                    isSearchVisible = true;
                }
                break;
            case R.id.problemlst_action_filter:
                hideKeyBoard();
                AlertDialog.Builder b = new AlertDialog.Builder(this);

                //ALL,ACCEPTED,NOT_TRIED,FAILS,BY_SCORE,LAST_DOWNLOADED,BY_SEARCH
                String[] types = {"Todos", "Aceptados", "Sin intentar", "Fallados", "Complejidad", "Últimos descargados"};
                b.setTitle("Filtrar por: ");
                b.setSingleChoiceItems(types, currentFilter, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //ALL,ACCEPTED,NOT_TRIED,FAILS,BY_SCORE,LAST_DOWNLOADED,BY_SEARCH
                        switch (which) {
                            case ALL:
                                triggerFilter(ALL);
                                break;
                            case ACCEPTED:
                                triggerFilter(ACCEPTED);
                                break;
                            case NOT_TRIED:
                                triggerFilter(NOT_TRIED);
                                break;
                            case FAILS:
                                triggerFilter(FAILS);
                                break;
                            case BY_SCORE:
                                triggerFilter(BY_SCORE);
                                break;
                            case LAST_DOWNLOADED:
                                triggerFilter(LAST_DOWNLOADED);
                                break;
                        }
                    }
                });
                b.setIcon(R.mipmap.ic_action_filter);
                b.show();
                break;
        }

        //return super.onOptionsItemSelected(item);
        return true;
    }

    private void triggerFilter(int FILTER) {
        prepareLimits(FILTER);
        prepareData();
        updateListView();
        listView.smoothScrollToPosition(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_problemlst);

        limit = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("problemsPerPage", "10"));

        listView = (ListView) findViewById(R.id.problemlistView);
        init = (ImageButton) findViewById(R.id.btn_problem_init);
        previous = (ImageButton) findViewById(R.id.btn_problem_previous);
        next = (ImageButton) findViewById(R.id.btn_problem_next);
        end = (ImageButton) findViewById(R.id.btn_problem_end);
        page = (MyEditText) findViewById(R.id.etext_problem_page);

        //add events
        addEvents();


        //init data
        problemLstAdapter = new ProblemLstAdapter(this, new ArrayList<Problem>());
        listView.setAdapter(problemLstAdapter);
        searchText = "";
        isSearchVisible = false;

        //
        triggerFilter(ALL);

    }

    public void prepareLimits(int FILTER) {
        currentFilter = FILTER;
        int byDate = 0;
        //limit = getSharedPreferences("cmfm", Context.MODE_PRIVATE).getInt("problemsPerPage", 10);
        offset = 0;
        currentPage = 1;
        Cursor cursor = null;
        switch (FILTER) {
            case ALL:
                setTitle("Lista de problemas");
                //totalProblems = (int) DatabaseUtils.queryNumEntries(ActiveAndroid.getDatabase(), (new Problem()).getTableName());
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName, new String[]{});
                break;
            case ACCEPTED:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName + " where accepted=1", new String[]{});
                break;
            case NOT_TRIED:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName + " where accepted=0", new String[]{});
                break;
            case FAILS:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName + " where accepted=-1", new String[]{});
                break;
            case BY_SCORE:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName, new String[]{});
                break;
            case BY_SEARCH:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName + " where title LIKE '%" + searchText + "%' OR text LIKE '%" + searchText + "%'", new String[]{});
                break;
            case LAST_DOWNLOADED:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName, new String[]{});
                break;
            default:
                cursor = ActiveAndroid.getDatabase().rawQuery("select count(*) as count from " + tableName, new String[]{});
                break;
        }
        cursor.moveToFirst();
        totalProblems = cursor.getInt(cursor.getColumnIndex("count"));
        totalPages = totalProblems / limit + ((totalProblems % limit != 0) ? 1 : 0);
        if (totalProblems == 0) currentPage = 0;
        if (FILTER == ALL || FILTER == BY_SCORE || FILTER == LAST_DOWNLOADED) {
            setTitle("Problemas: " + totalProblems);
        } else {
            setTitle("Resultados: " + totalProblems);
        }
    }

    public void prepareData() {
        switch (currentFilter) {
            case ALL:
                listProblem = new Select().from(Problem.class).orderBy("problemId asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            case ACCEPTED:
                listProblem = new Select().from(Problem.class).where("accepted=1").orderBy("problemId asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            case NOT_TRIED:
                listProblem = new Select().from(Problem.class).where("accepted=0").orderBy("problemId asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            case FAILS:
                listProblem = new Select().from(Problem.class).where("accepted=-1").orderBy("problemId asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            case BY_SCORE:
                listProblem = new Select().from(Problem.class).orderBy("score asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            case BY_SEARCH:
                listProblem = new Select().from(Problem.class).where("title LIKE '%" + searchText + "%' OR text LIKE '%" + searchText + "%'").orderBy("problemId asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            case LAST_DOWNLOADED:
                listProblem = new Select().from(Problem.class).orderBy("date desc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
            default:
                listProblem = new Select().from(Problem.class).orderBy("problemId asc").offset(Integer.toString(offset)).limit(Integer.toString(limit)).execute();
                break;
        }
    }

    private void updateListView() {
        isSearchVisible = false;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
        page.setText("");
        page.setHint("" + currentPage + " / " + totalPages);
        problemLstAdapter.setData(listProblem);
        problemLstAdapter.notifyDataSetChanged();
    }

    private void addEvents() {
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (ProblemLstActivity.this) {
                    if (currentPage != 1) {
                        hideKeyBoard();
                        currentPage = 1;
                        offset = 0;
                        prepareData();
                        updateListView();
                        listView.smoothScrollToPosition(0);//.setSelection(0);
                    }
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (ProblemLstActivity.this) {
                    if (currentPage > 1) {
                        hideKeyBoard();
                        offset -= limit;
                        currentPage--;
                        prepareData();
                        updateListView();
                        listView.smoothScrollToPosition(0);//.setSelection(0);
                    }
                }
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (ProblemLstActivity.this) {
                    if (currentPage < totalPages) {
                        hideKeyBoard();
                        offset += limit;
                        currentPage++;
                        prepareData();
                        updateListView();
                        listView.smoothScrollToPosition(0);//.setSelection(0);
                    }
                }

            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (ProblemLstActivity.this) {
                    if (currentPage < totalPages) {
                        hideKeyBoard();
                        currentPage = totalPages;
                        offset = ((totalProblems / limit) - 1) * limit;
                        prepareData();
                        updateListView();
                        listView.smoothScrollToPosition(0);//.setSelection(0);
                    }
                }
            }
        });


        page.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyBoard();
                    if (page.getText().toString().length() > 0) {
                        int value = Integer.parseInt(page.getText().toString());
                        if (value >= 1 && value <= totalPages && currentPage != value) {
                            currentPage = value;
                            offset = (currentPage - 1) * limit;

                            prepareData();
                            updateListView();
                        } else {
                            page.setText("");
                        }
                    }
                    return false;
                }
                return true;
            }
        });


        page.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isSearchVisible = false;
                    getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!listProblem.isEmpty()) {
                    hideKeyBoard();
                    Intent i = new Intent(ProblemLstActivity.this, ProblemShowActivity.class);
                    i.putExtra("problemId", listProblem.get(position).problemId);
                    ProblemLstActivity.this.startActivity(i);
                }
            }
        });

        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setCustomView(R.layout.searchfield);
        search = (MyEditText) actionBar.getCustomView().findViewById(R.id.searchfield);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getText().toString().trim().equals("")) {
                    hideKeyBoard();
                } else {
                    triggerSearch(v.getText().toString());
                }
                v.setText("");
                return false;
            }
        });

    }

    public void triggerSearch(String str) {
        searchText = str;
        hideKeyBoard();
        prepareLimits(BY_SEARCH);
        prepareData();
        updateListView();
        listView.smoothScrollToPosition(0);
    }

    private void hideKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        page.clearFocus();
        search.clearFocus();
        isSearchVisible = false;
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareData();
        updateListView();
    }

    /*
    * For testing purposes only
    * */
    public ArrayList<Problem> getListProblem() {
        return listProblem;
    }

    /*
    * For testing purposes only
    * */
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
}
