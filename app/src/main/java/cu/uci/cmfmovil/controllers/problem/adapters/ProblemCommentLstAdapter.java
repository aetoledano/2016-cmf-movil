package cu.uci.cmfmovil.controllers.problem.adapters;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.modules.data.Comment;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/3/16.
 */
public class ProblemCommentLstAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    Activity context;
    Comment tmpComment = null;
    private String whoAmI;
    private ArrayList<Comment> data;
    private boolean visible;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public ArrayList<Comment> getData() {
        return data;
    }

    public void setData(ArrayList<Comment> data) {
        this.data = data;
    }

    public ProblemCommentLstAdapter(Activity context, ArrayList<Comment> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        whoAmI = PreferenceManager.getDefaultSharedPreferences(CmfUtils.getContext()).getString("username", "");
    }

    @Override
    public int getCount() {
        if (data.size() <= 0)
            return 1;
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.problem_problemcommentlst_item, null);
            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.message = (TextView) vi.findViewById(R.id.problemcommentlstitem_message);
            holder.topic = (TextView) vi.findViewById(R.id.problemcommentlstitem_topic);
            holder.user = (TextView) vi.findViewById(R.id.problemcommentlstitem_person);
            holder.cloud = (ImageView) vi.findViewById(R.id.nubecita);
            holder.date = (TextView) vi.findViewById(R.id.problemcommentlstitem_date);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        if (data.size() <= 0 || !visible) {
            holder.message.setText("");
            if (!visible)
                holder.topic.setText("No puedes ver los comentarios a este problema hasta que no lo hayas aceptado.");
            else
                holder.topic.setText("No hay comentarios disponibles");
            holder.user.setText("");
            holder.cloud.setVisibility(View.INVISIBLE);
            holder.date.setVisibility(View.GONE);
        } else {
            holder.cloud.setVisibility(View.VISIBLE);
            holder.date.setVisibility(View.VISIBLE);
            tmpComment = null;
            // tmpComment = (CommentEntity) data.get(position);
            tmpComment = data.get(position);

            /************  Set Model values in Holder elements ***********/
            holder.message.setText(CmfUtils.decodeAES(tmpComment.message));
            holder.topic.setText(CmfUtils.decodeAES(tmpComment.topic));
            if (tmpComment.author.equals("") || tmpComment.author.equals(whoAmI))
                holder.user.setText("Yo dije:");
            else
                holder.user.setText(tmpComment.author + " dijo:");
            holder.date.setText(CmfUtils.getDate(tmpComment.date));
        }
        return vi;
    }

    /*********
     * Create a holder Class to contain inflated xml file elements
     *********/
    public static class ViewHolder {
        public TextView user, topic, message, date;
        public ImageView cloud;
    }


}
