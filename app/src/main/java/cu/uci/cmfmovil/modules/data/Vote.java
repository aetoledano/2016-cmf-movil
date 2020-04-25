package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by tesis on 5/7/16.
 */
@Table(name = "Vote")
public class Vote extends Model {
    @Column(name = "problemId")
    public int problemId;
    @Column(name = "value")
    public int value;
    @Column(name = "date")
    public Date date;

    public Vote() {
        super();
    }
}
