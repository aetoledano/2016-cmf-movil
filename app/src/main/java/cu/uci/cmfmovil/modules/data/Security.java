package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by tesis on 5/8/16.
 */
@Table(name = "Security")
public class Security extends Model {
    @Column(name = "user")
    public String user;
    @Column(name = "pass")
    public String pass;

    public Security() {
        super();
    }
}
