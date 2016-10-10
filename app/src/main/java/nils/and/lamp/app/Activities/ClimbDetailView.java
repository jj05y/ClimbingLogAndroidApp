
package nils.and.lamp.app.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import nils.and.lamp.app.R;

public class ClimbDetailView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_climb_detail_view);

        Intent intent = getIntent();
        int index = intent.getIntExtra("ClimbIndex", 0);
        //Climb climb = ((IClimbingApp) intent.).getUserClimbs().get(index);
        Log.d("FRAG", "WOOOO" + index);

    }
}
