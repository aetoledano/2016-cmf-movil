package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by tesis on 5/8/16.
 */
@Table(name = "Sending")
public class Sending extends Model {
    @Column(name = "problemId")
    public int problemId;
    @Column(name = "accepted")
    public int accepted;
    @Column(name = "date")
    public Date date;
    @Column(name = "usrInput")
    public String usrInput;
    @Column(name = "sync")
    public int sync;

    public Sending() {
        super();
    }
}
