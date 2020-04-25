package cu.uci.cmfmovil;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.activeandroid.ActiveAndroid;

import cu.uci.cmfmovil.controllers.problem.ProblemLstActivity;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.judge.JudgingMotor;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testListProblems() {
        ActiveAndroid.initialize(getContext());
        ProblemLstActivity problemLstActivity = new ProblemLstActivity();
        problemLstActivity.prepareLimits(ProblemLstActivity.ALL);
        problemLstActivity.prepareData();
        assertTrue(
                "Lista de problemas vacía!, LIST SIZE: " + problemLstActivity.getListProblem().size(),
                problemLstActivity.getListProblem().size() != 0);
    }

    public void testEvaluateWrongAnswer() {
        ActiveAndroid.initialize(getContext());
        Problem testProblem = Problem.first(Problem.class);
        String testWrongAnswer = "wrongAnswer";
        boolean sentence = JudgingMotor.judge(getContext(), testProblem, testWrongAnswer);
        assertFalse("Sentencia del juez incorrecta!, SENTENCE: " + sentence, sentence);
    }

    public void testSearchProblem() {
        ActiveAndroid.initialize(getContext());
        ProblemLstActivity problemLstActivity = new ProblemLstActivity();
        String testSearchStr = "expl";
        problemLstActivity.setSearchText(testSearchStr);
        problemLstActivity.prepareLimits(ProblemLstActivity.BY_SEARCH);
        problemLstActivity.prepareData();
        assertFalse("Lista de problemas resultado de la búsqueda no contiene 9 problemas!, LIST SIZE: "
                        + problemLstActivity.getListProblem().size(),
                problemLstActivity.getListProblem().size() != 9);
    }

    public void testFilterProblem() {
        ActiveAndroid.initialize(getContext());
        ProblemLstActivity problemLstActivity = new ProblemLstActivity();
        problemLstActivity.prepareLimits(ProblemLstActivity.FAILS);
        problemLstActivity.prepareData();
        for (Problem p : problemLstActivity.getListProblem())
            assertFalse("Encontrado problema con accepted=" + p.accepted + " en lista de problemas fallados",
                    p.accepted != -1);
    }


}