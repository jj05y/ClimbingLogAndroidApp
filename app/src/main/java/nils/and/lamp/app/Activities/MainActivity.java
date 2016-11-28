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
import android.support.v4.app.Fragment;
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
import nils.and.lamp.app.Fragments.ClimbsOnAMap;
import nils.and.lamp.app.Fragments.PrefsFragment;
import nils.and.lamp.app.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClimbBrowser.OnFragmentInteractionListener, ClimbCreator.OnFragmentInteractionListener, ClimbsOnAMap.OnFragmentInteractionListener {

    public static final String ON_A_MAP = "onamap";
    public static final String NO_FRAG = "nofrag";
    public static final String CREATE = "create";
    public static final String BROWSE = "browse";
    public static final String SETTINGS = "settings";
    private ClimbDataBaseHandler dataBaseHandler;
    private Context context;
    private FloatingActionButton fab;
    private String currentFrag;
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Clog: The Climbing Log");
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
            case "Donkey":
                fab.setImageResource(R.drawable.donkey);
                break;
            case "Goat":
                fab.setImageResource(R.drawable.goat);
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
                    case "Donkey":
                        Snackbar.make(view, "DONKEY!!!", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                        break;
                    case "Goat":
                        Snackbar.make(view, "Hi There :)", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
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

                    //Dalkey
                    Bitmap dalkey1 = BitmapFactory.decodeResource(getResources(), R.drawable.dalkey1);
                    Bitmap dalkey2 = BitmapFactory.decodeResource(getResources(), R.drawable.dalkey2);
                    Bitmap dalkey3 = BitmapFactory.decodeResource(getResources(), R.drawable.dalkey3);
                    Bitmap dalkey4 = BitmapFactory.decodeResource(getResources(), R.drawable.dalkey4);
                    dataBaseHandler.addClimb("Street Fighter", "4c", "~10m","A dalkey classic, The last move has great protection", dalkey1, "53.2713,-6.1075");
                    dataBaseHandler.addClimb("Tower Ridge", "4a", "~20m","A really fun climb for sunny afternoons. Bring flapjacks for snacks on the scaldy ledge",dalkey2,"53.2713,-6.1072");
                    dataBaseHandler.addClimb("Graham Crackers", "5a", "~25m","Gets pretty dusty towards the top but the rewards are huge! 2 sets of nuts needed",dalkey3,"53.2714,-6.1074");
                    dataBaseHandler.addClimb("Paradise Lost", "6c", "~20m","Really scary but a some bomber gear for protection.",dalkey4,"53.2712,-6.1074");
                    //fairhead
                    Bitmap fairhead1 = BitmapFactory.decodeResource(getResources(), R.drawable.fairhead1);
                    Bitmap fairhead2 = BitmapFactory.decodeResource(getResources(), R.drawable.fairhead2);
                    dataBaseHandler.addClimb("Muskat", "6c", "~35m","Really reachy route with minimal gear. Make sure to bring cams",fairhead1,"55.225663,-6.145032");
                    dataBaseHandler.addClimb("Denver Dog", "6a", "~45m","Only entertain this if you've climbed shoeless before. It will literally knock your socks off",fairhead2,"55.226481,-6.149848");
                    //burren
                    Bitmap burren1 = BitmapFactory.decodeResource(getResources(), R.drawable.burren1);
                    Bitmap burren2 = BitmapFactory.decodeResource(getResources(), R.drawable.burren2);
                    dataBaseHandler.addClimb("Great Rock", "5b", "~20m","It's rocky's favourite climb apparently",burren1,"53.061225,-9.362760");
                    dataBaseHandler.addClimb("Mossy Ledge", "6b", "~15m","Moss gets slippy in winter, really good gear placement",burren2,"53.061325,-9.362755");
                    Log.d("DB", "added some climbs");
                    //Toast.makeText(context, "Finished loading climbs", Toast.LENGTH_SHORT).show();
                }
            }
        }).start();


        currentFrag = NO_FRAG;

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            if (!(currentFrag.equals(SETTINGS)) ) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_frag, new PrefsFragment())
                        .commit();
                setTitle("Settings");
                currentFrag = SETTINGS;
            }
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
            if (!(currentFrag.equals(BROWSE)) ) {
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_frag, new ClimbBrowser())
                        .commit();

                setTitle("Browse Climbs");
                currentFrag = BROWSE;
            }
        } else if (id == R.id.nav_create) {
            if (!(currentFrag.equals(CREATE)) ) {
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_frag, new ClimbCreator())
                        .commit();

                setTitle("Climb Creator");
                currentFrag = CREATE;
            }
        } else if (id == R.id.nav_climbs_on_map) {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_frag);
            if (!(currentFrag.equals(ON_A_MAP)) ) {
                ClimbsOnAMap coam = new ClimbsOnAMap();
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_frag, coam)
                        .commit();
                setTitle("Map View");
                currentFrag = ON_A_MAP;
            }
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

