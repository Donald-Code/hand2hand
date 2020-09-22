package tshabalala.bongani.courierservice.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import tshabalala.bongani.courierservice.R;
import tshabalala.bongani.courierservice.model.User;

/**
 * Created by Bongani on 2017/11/15.
 */


public class LogAllAdapter extends ArrayAdapter<User> {

    private Activity mContext = null;
    private LayoutInflater mInflater = null;

    private static class ViewHolder {

        private TextView tvStudent = null;
        private TextView tvName = null;
        private TextView tvSurname = null;
        private TextView tvVenue = null;
        private TextView tvSub = null;


    }

    public LogAllAdapter(Activity context, int textViewResourceId, List<User> objects){
        super(context, textViewResourceId, objects);
        this.mContext = context;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View contentView, final ViewGroup parent){
        final User item = getItem(position);
        final ViewHolder vh;
        if(contentView == null){
            vh = new ViewHolder();
            contentView = mInflater.inflate(R.layout.report_logs, parent, false);


            vh.tvStudent = (TextView) contentView.findViewById(R.id.textViewStudentNr);
            vh.tvName = (TextView) contentView.findViewById(R.id.textViewName);
            vh.tvSurname = (TextView) contentView.findViewById(R.id.textViewSurname);
            vh.tvVenue = (TextView) contentView.findViewById(R.id.textViewVenue);
            vh.tvSub = (TextView) contentView.findViewById(R.id.textViewSub);



            contentView.setTag(vh);
        } else {
            vh = (ViewHolder) contentView.getTag();
        }
        vh.tvStudent.setText(item.getName());
        vh.tvName.setText(item.getSurname());
        vh.tvSurname.setText(item.getPhone());
        vh.tvVenue.setText(item.getGender());
        vh.tvSub.setText(item.getAge());



        return (contentView);
    }
}