package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by tesis on 5/7/16.
 */
@Table(name = "Comment")
public class Comment extends Model {
    @Column(name = "problemId")
    public int problemId;
    @Column(name = "author")
    public String author;
    @Column(name = "topic")
    public String topic;
    @Column(name = "message")
    public String message;
    @Column(name = "date")
    public Date date;
    @Column(name = "sync")
    public int sync;

    public Comment() {
        super();
    }
}
