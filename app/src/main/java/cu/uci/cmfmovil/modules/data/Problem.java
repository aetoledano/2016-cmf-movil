package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

/**
 * Created by tesis on 5/5/16.
 */

@Table(name = "Problem")
public class Problem extends Model {

    //persistent variables declaration
    @Column(name = "title")
    public String title;
    @Column(name = "text")
    public String text;
    @Column(name = "inputSample")
    public String inputSample;
    @Column(name = "answer")
    public String answer;
    @Column(name = "specification")
    public String specification;
    @Column(name = "acCount")
    public int acCount;
    @Column(name = "intents")
    public int intents;
    @Column(name = "myIntents")
    public int myIntents;
    @Column(name = "problemId")
    public int problemId;
    @Column(name = "acPercent")
    public double acPercent;
    @Column(name = "score")
    public double score;
    @Column(name = "accepted")
    public int accepted;//-1 tried but failed, 0 not tried, 1 accepted
    @Column(name = "votesPlus")
    public int votesPlus;
    @Column(name = "votesMinus")
    public int votesMinus;
    @Column(name = "myVote")
    public int myVote;
    @Column(name = "hasImage")
    public int hasImage;//1 true 0 false
    @Column(name = "date")
    public Date date;
    //end variables declaration

    //not mapped variables
    public String image;
    public List<Comment> comments;

    public Problem() {
        super();
    }

}