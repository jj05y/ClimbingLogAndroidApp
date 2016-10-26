package nils.and.lamp.app.Core;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.Vector;

import nils.and.lamp.app.Core.Climb;
import nils.and.lamp.app.R;

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
        ImageView imageView = (ImageView) myInflatedView.findViewById(R.id.imageView_climb);


        textName.setText(climbs.get(i).getName());
        textLength.setText(climbs.get(i).getLength());
        textGrade.setText(climbs.get(i).getGrade());
        textDescription.setText(climbs.get(i).getDescription());

        String imageKey = climbs.get(i).getPhoto();
        (new ImageLoader(imageView)).execute(imageKey);

        return myInflatedView;
    }

    public Vector<Climb> getClimbs() {
        return climbs;
    }

    private class ImageLoader extends AsyncTask<String,Void,Void> {

        private ImageView imageView;
        private Bitmap image;
        private String climbName;

        public ImageLoader(ImageView imageView) {
            this.imageView = imageView;
        }


        @Override
        protected Void doInBackground(String... strings) {
            climbName = strings[0];
            Log.d("thread", "Loading Image for " + strings[0]);
            for (String string : strings) image = (new ClimbDataBaseHandler(context)).getPicture(string);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (image!= null) imageView.setImageDrawable(new BitmapDrawable(context.getResources(), image));
            Log.d("thread", "Loaded Image for " + climbName);

        }
    }
}
