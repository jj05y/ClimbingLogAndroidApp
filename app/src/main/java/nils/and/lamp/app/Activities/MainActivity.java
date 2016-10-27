package nils.and.lamp.app.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import nils.and.lamp.app.Core.ClimbDataBaseHandler;
import nils.and.lamp.app.Fragments.ClimbBrowser;
import nils.and.lamp.app.Fragments.ClimbCreator;
import nils.and.lamp.app.Fragments.PrefsFragment;
import nils.and.lamp.app.Fragments.TestGps;
import nils.and.lamp.app.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClimbBrowser.OnFragmentInteractionListener, ClimbCreator.OnFragmentInteractionListener, TestGps.OnFragmentInteractionListener {

    private ClimbDataBaseHandler dataBaseHandler;
    private Context context;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        fab = (FloatingActionButton) findViewById(R.id.fab);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String animal = prefs.getString("animal_preference", "Duck");
        Log.d("prefs","Chosen Animal: " + animal);

        switch (animal) {
            case "Pig":
                fab.setImageResource(R.drawable.pig);
                break;
            case "Duck":
                fab.setImageResource(R.drawable.duck);
                break;
            default:
                fab.setImageResource(R.drawable.duck);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String animal = prefs.getString("animal_preference", "Duck");
                switch (animal) {
                    case "Pig":
                        Snackbar.make(view, "Oink Oink", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        break;
                    case "Duck":
                        Snackbar.make(view, "Quack Quack", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        break;
                    default:
                        Snackbar.make(view, "Quack Quack", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                fab.setRippleColor(color);

            }
        });


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dataBaseHandler == null) {
                    dataBaseHandler = new ClimbDataBaseHandler(context);
                }
                if (dataBaseHandler.isEmpty()) {
                    //Toast.makeText(context, "Filling Database with pre-loaded climbs", Toast.LENGTH_SHORT).show();
                    Bitmap climb1 = BitmapFactory.decodeResource(getResources(), R.drawable.climb1);
                    Bitmap climb3 = BitmapFactory.decodeResource(getResources(), R.drawable.climb3);

                    dataBaseHandler.addClimb("Street Fighter", "4c", "~10m","fab", climb1, "53.2713,-6.1074");
                    dataBaseHandler.addClimb("Tower Ridge Direct", "5c", "~20m","super fab",climb3,"53.2713,-6.1074");
                    dataBaseHandler.addClimb("Graham Crackers", "5a", "~25m","super fab",climb3,"53.2713,-6.1074");
                    dataBaseHandler.addClimb("Paradise Lost", "4a", "~20m","super super fab",climb1,"53.2713,-6.1074");
                    dataBaseHandler.addClimb("Stereo-Tentacles", "5a", "~15m","super super super fab",climb3,"53.2713,-6.1074");
                    Log.d("DB", "added some climbs");
                    //Toast.makeText(context, "Finished loading climbs", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(Gravity.LEFT);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        Log.d("BACK", "Back pressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            //drawer.closeDrawer(GravityCompat.START);
            super.onBackPressed();
        } else {
            drawer.openDrawer(Gravity.LEFT);
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.frame_frag, new PrefsFragment())
                    .commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_browse) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, new ClimbBrowser())
                    .commit();

            setTitle("Browse Climbs");
        } else if (id == R.id.nav_create) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, new ClimbCreator())
                    .commit();

               setTitle("Climb Creator");
        } else if (id == R.id.nav_search) {
            Toast.makeText(this, "Retrieving Online Database", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Na, Just Kidding", Toast.LENGTH_SHORT).show();

            setTitle("Browse Online");
        } else if (id == R.id.nav_gps) {
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, new TestGps())
                    .commit();

            setTitle("PANTS");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("FRAG", "boop");
    }

}

