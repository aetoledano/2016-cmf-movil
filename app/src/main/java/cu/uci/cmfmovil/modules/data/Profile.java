package cu.uci.cmfmovil.modules.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by tesis on 5/8/16.
 */
@Table(name = "Profile")
public class Profile extends Model {
    @Column(name = "neurons")
    public int neurons;
    @Column(name = "ranking")
    public String ranking;
    @Column(name = "score")
    public double score;

    public Profile() {
        super();
    }
}
