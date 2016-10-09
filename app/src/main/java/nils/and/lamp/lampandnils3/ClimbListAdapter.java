package nils.and.lamp.lampandnils3;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Vector;

/**
 * Created by joe on 09/10/16.
 */

public class ClimbListAdapter extends BaseAdapter {

    private Vector<Climb> climbs;

    public ClimbListAdapter(Vector<Climb> climbs) {
        this.climbs = climbs;
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
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
