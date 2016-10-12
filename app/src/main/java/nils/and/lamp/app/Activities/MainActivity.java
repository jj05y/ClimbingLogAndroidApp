package nils.and.lamp.app.Activities;

import android.content.ContentResolver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import java.io.File;

import nils.and.lamp.app.Core.ClimbDataBaseHandler;
import nils.and.lamp.app.Core.IClimbingApp;
import nils.and.lamp.app.Fragments.ClimbBrowser;
import nils.and.lamp.app.Fragments.ClimbCreator;
import nils.and.lamp.app.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, ClimbBrowser.OnFragmentInteractionListener, ClimbCreator.OnFragmentInteractionListener, IClimbingApp {

    private ClimbDataBaseHandler dataBaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (dataBaseHandler == null) {
            dataBaseHandler = new ClimbDataBaseHandler(this);
        }

        if (dataBaseHandler.isEmpty()) {

            String climb1 = null;
            String climb2 = null;
            String climb3 = null;

            dataBaseHandler.addClimb("Street Fighter", "4c", "~10m","fab", climb1);
            dataBaseHandler.addClimb("Tower Ridge Direct", "5c", "~20m","super fab",climb2);
            dataBaseHandler.addClimb("Graham Crackers", "5a", "~25m","super fab",climb3);
            dataBaseHandler.addClimb("Paradise Lost", "4a", "~20m","super super fab",climb2);
            dataBaseHandler.addClimb("Stereo-Tentacles", "5a", "~15m","super super super fab",climb3);
            Log.d("DB", "added some climbs");


        }


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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_browse) {
            // Handle the joe action
            // Create a new fragment and specify the planet to show based on position
            Fragment fragment = new ClimbBrowser();


            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, fragment)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
          //  mDrawerList.setItemChecked(position, true);
         //   setTitle(mPlanetTitles[position]);
         //   mDrawerLayout.closeDrawer(mDrawerList);
        } else if (id == R.id.nav_create) {
            // Handle the joe action
            // Create a new fragment and specify the planet to show based on position
            Fragment fragment = new ClimbCreator();


            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, fragment)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            //  mDrawerList.setItemChecked(position, true);
            //   setTitle(mPlanetTitles[position]);
            //   mDrawerLayout.closeDrawer(mDrawerList);

     /*   } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {*/

        } else if (id == R.id.nav_search) {
            Toast.makeText(this, "Retrieving Online Database", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Na, Just Kidding", Toast.LENGTH_SHORT).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Log.d("FRAG", "boop");
    }

    @Override
    public ClimbDataBaseHandler getDatabase() {
        return dataBaseHandler;
    }
}

