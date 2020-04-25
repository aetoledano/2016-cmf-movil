package cu.uci.cmfmovil.controllers.problem.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/3/16.
 */
public class ProblemLstAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Activity context;
    Problem tmpProblem = null;
    private ArrayList<Problem> data;

    public ArrayList<Problem> getData() {
        return data;
    }

    public void setData(ArrayList<Problem> data) {
        this.data = data;
    }

    public ProblemLstAdapter(Activity context, ArrayList<Problem> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(R.layout.problem_problemlst_item, null);
            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.complexity = (TextView) vi.findViewById(R.id.problemlstitem_complexity);
            holder.title = (TextView) vi.findViewById(R.id.problemlstitem_title);
            holder.state = (ImageView) vi.findViewById(R.id.problemlstitem_state);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        if (data.size() <= 0) {
            holder.title.setText("No hay problemas disponibles");
            holder.complexity.setText("");
            holder.state.setVisibility(View.INVISIBLE);
        } else {
            holder.state.setVisibility(View.VISIBLE);
            tmpProblem = null;
            // tmpProblem = (ProblemEntity) data.get(position);
            tmpProblem = data.get(position);
            /************  Set Model values in Holder elements ***********/
            holder.complexity.setText(CmfUtils.format(tmpProblem.score));
            holder.title.setText(tmpProblem.title);
            switch (tmpProblem.accepted) {
                case -1:
                    holder.state.setImageResource(R.drawable.ic_problem_state_failed);
                    break;
                case 0: //do nothing
                    holder.state.setImageResource(R.drawable.ic_problem_state_not_tried);
                    break;
                case 1:
                    holder.state.setImageResource(R.drawable.ic_problem_state_accepted);
                    break;
            }
        }
        return vi;
    }

    /*********
     * HOLDER
     *********/
    public static class ViewHolder {
        public TextView complexity, title;
        public ImageView state;
    }
}
