package nils.and.lamp.lampandnilsproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button button = (Button) view;
                CharSequence text = button.getText();
                Toast.makeText(getApplicationContext(), button.getText(), Toast.LENGTH_SHORT).show();
                Log.d("text", text + "!");
            }
        };

        Button[] buttons = {
                (Button) findViewById(R.id.button_boop),
                (Button) findViewById(R.id.button_beep),
                (Button) findViewById(R.id.button_bop),
                (Button) findViewById(R.id.button_bosh),
        };
        for (Button button : buttons) {
            button.setOnClickListener(listener);
        }

    }


}
