
package nils.and.lamp.app.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
    private EditText name;
    private EditText desc;

    private Climb climb;
    private ClimbDataBaseHandler database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climb_detail_view);

        Intent intent = getIntent();
        climb = (Climb) intent.getSerializableExtra("Climb");
        Log.d("FRAG", "WOOOO: " + climb);

        database = new ClimbDataBaseHandler(this);

        ImageView imageView = (ImageView) findViewById(R.id.detailview_image);

        //TODO thread this
        Bitmap image = database.getPicture(climb.getName());
        imageView.setImageDrawable(new BitmapDrawable(getResources(), image));

        name = (EditText) findViewById(R.id.detailview_title);
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


        Button commit = (Button) findViewById(R.id.detailview_commit_button);
        Button delete = (Button) findViewById(R.id.detailview_delete_button);

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String n = name.getText().toString();
                String d = desc.getText().toString();
                String g = grade.getSelectedItem().toString();
                String l = length.getSelectedItem().toString();
                climb.setName(name.getText().toString()); //need to update this just in-case deleted after changing
                //TODO thread this
                database.updateClimb(n, g, l, d);
                Toast.makeText(getApplicationContext(), "climb updated", Toast.LENGTH_SHORT).show();
                ClimbDetailView.super.onBackPressed();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO thread this
                    database.deleteClimb(climb.getName());
                    Toast.makeText(getApplicationContext(), "climb deleted", Toast.LENGTH_SHORT).show();
                    ClimbDetailView.super.onBackPressed();
                }
            }
        );


    }
}
