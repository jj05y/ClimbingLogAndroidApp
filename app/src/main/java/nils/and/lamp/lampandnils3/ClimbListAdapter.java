package nils.and.lamp.lampandnils3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.Vector;

/**
 * Created by joe on 09/10/16.
 */

public class ClimbListAdapter extends BaseAdapter {

    private Vector<Climb> climbs;
    private Context context;

    public ClimbListAdapter(Vector<Climb> climbs, Context context) {
        this.climbs = climbs;
        this.context = context;
    }

    @Override
    public int getCount() {
        return climbs.size();
    }

    @Override
    public Object getItem(int i){
        if (climbs.size() < i) {
            return climbs.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View myInflatedView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myInflatedView = inflater.inflate(R.layout.list_item_climb, null);
        } else {
            myInflatedView = convertView;
        }

        TextView textName = (TextView) myInflatedView.findViewById(R.id.textView_name);
        TextView textLength = (TextView) myInflatedView.findViewById(R.id.textView_length);
        TextView textGrade = (TextView) myInflatedView.findViewById(R.id.textView_grade);
        TextView textDescription = (TextView) myInflatedView.findViewById(R.id.textView_description);



        return myInflatedView;
    }
}
