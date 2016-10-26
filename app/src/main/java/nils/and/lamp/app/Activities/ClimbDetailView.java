
package nils.and.lamp.app.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;

import nils.and.lamp.app.Core.Climb;
import nils.and.lamp.app.Core.ClimbDataBaseHandler;
import nils.and.lamp.app.R;

import static java.security.AccessController.getContext;

public class ClimbDetailView extends AppCompatActivity {


    private ArrayAdapter<CharSequence> lengthAdapter;
    private ArrayAdapter<CharSequence> gradeAdapter;
    private Spinner grade;
    private Spinner length;
    private TextView name;
    private EditText desc;
    private Context context;

    private Climb climb;
    private ClimbDataBaseHandler database;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climb_detail_view);

        context = this;

        Intent intent = getIntent();
        climb = (Climb) intent.getSerializableExtra("Climb");
        Log.d("FRAG", "WOOOO: " + climb);

        database = new ClimbDataBaseHandler(this);


        imageView = (ImageView) findViewById(R.id.detailview_image);

        (new ImageLoader()).execute(climb.getName());

        name = (TextView) findViewById(R.id.detailview_title);
        name.setText(climb.getName());
        desc = (EditText) findViewById(R.id.detailview_desc);
        desc.setText(climb.getDescription());

        grade = (Spinner) findViewById(R.id.detailview_grade);
        length = (Spinner) findViewById(R.id.detailview_length);

        gradeAdapter = ArrayAdapter.createFromResource(this,
                R.array.grade_array, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(gradeAdapter);
        lengthAdapter = ArrayAdapter.createFromResource(this,
                R.array.length_array, android.R.layout.simple_spinner_item);
        lengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        length.setAdapter(lengthAdapter);

        grade.setSelection(gradeAdapter.getPosition(climb.getGrade()), true);
        length.setSelection(gradeAdapter.getPosition(climb.getLength()), true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String units = prefs.getString("unit_preference", "Metric");
        Log.d("prefs","Chosen Units: " + units );

        Button commit = (Button) findViewById(R.id.detailview_commit_button);
        Button delete = (Button) findViewById(R.id.detailview_delete_button);
        Button showOnMap = (Button) findViewById(R.id.detailview_go_gps_button);

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String d = desc.getText().toString();
                String g = grade.getSelectedItem().toString();
                String l = length.getSelectedItem().toString();
                (new ClimbUpdater()).execute(n,d,g,l);
                climb.setName(name.getText().toString()); //need to update this just in-case deleted after changing
                ClimbDetailView.super.onBackPressed();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    (new ClimbDeleter()).execute(climb.getName());
                    ClimbDetailView.super.onBackPressed();
                }
            }
        );

        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (climb.getGpsCoords() != null) {
                    Uri gmmIntentUri = Uri.parse("geo:" + climb.getGpsCoords() + "?q=" + climb.getGpsCoords());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                } else {
                    Toast.makeText(context, "No Coordinatess For This Climb :(", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private class ImageLoader extends AsyncTask<String,Void,Void> {

        Bitmap image;

        @Override
        protected Void doInBackground(String... strings) {
            Log.d("thread", "Loading Image");
            for (String string : strings) image = database.getPicture(string);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (image != null) {
                imageView.setImageDrawable(new BitmapDrawable(getResources(), image));
            }
            Log.d("thread", "Image Loaded");

        }
    }
    private class ClimbUpdater extends AsyncTask<String,Void,Void> {

        Bitmap image;

        @Override
        protected Void doInBackground(String... strings) {
            Log.d("thread", "Updating Climb");
            String n = strings[0];
            String d = strings[1];
            String g = strings[2];
            String l = strings[3];
            database.updateClimb(n, g, l, d);


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "Climb Updated", Toast.LENGTH_SHORT).show();
            Log.d("thread", "Climb Updated");

        }
    }
    private class ClimbDeleter extends AsyncTask<String,Void,Void> {



        @Override
        protected Void doInBackground(String... strings) {
            Log.d("thread", "Deleting Climb" );
            for (String string : strings) database.deleteClimb(string);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(context, "Climb Deleted", Toast.LENGTH_SHORT).show();
            Log.d("thread", "Climb Deleted");

        }
    }
}
