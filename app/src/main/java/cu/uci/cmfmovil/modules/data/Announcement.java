package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by tesis on 5/8/16.
 */
@Table(name = "Announcement")
public class Announcement extends Model{
    @Column(name = "title")
    public String title;
    @Column(name = "topic")
    public String topic;
    @Column(name = "date")
    public String date;
    @Column(name = "cantUsersAllowed")
    public int cantUsersAllowed;

    public Announcement() {
        super();
    }
}
