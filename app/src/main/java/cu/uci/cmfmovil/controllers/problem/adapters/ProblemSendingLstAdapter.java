package cu.uci.cmfmovil.controllers.problem.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cu.uci.cmfmovil.R;
import cu.uci.cmfmovil.modules.data.Problem;
import cu.uci.cmfmovil.modules.data.Sending;
import cu.uci.cmfmovil.utils.CmfUtils;

/**
 * Created by tesis on 5/14/16.
 */
public class ProblemSendingLstAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Activity context;
    Sending sending = null;
    private ArrayList<Sending> data;

    public ArrayList<Sending> getData() {
        return data;
    }

    public void setData(ArrayList<Sending> data) {
        this.data = data;
    }

    public ProblemSendingLstAdapter(ArrayList<Sending> data, Activity context) {
        this.data = data;
        this.context = context;
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
            vi = inflater.inflate(R.layout.problem_problemsendinglst_item, null);
            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.input = (TextView) vi.findViewById(R.id.problem_sending_input);
            holder.date = (TextView) vi.findViewById(R.id.problem_sending_date);
            holder.state = (ImageView) vi.findViewById(R.id.problem_sending_state);
            /************  Set holder with LayoutInflater ************/
            vi.setTag(holder);
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        if (data.size() <= 0) {
            holder.input.setText("No hay envíos disponibles");
            holder.date.setText("");
            holder.state.setVisibility(View.INVISIBLE);
        } else {
            holder.state.setVisibility(View.VISIBLE);
            sending = null;
            sending = data.get(position);
            /************  Set Model values in Holder elements ***********/
            holder.input.setText(sending.usrInput);
            holder.date.setText(CmfUtils.getDate(sending.date));
            switch (sending.accepted) {
                case -1:
                    holder.state.setImageResource(R.drawable.ic_problem_state_failed);
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
        public TextView input, date;
        public ImageView state;
    }
}
