package nils.and.lamp.lampandnils3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, JoeFrag.OnFragmentInteractionListener, NilsFrag.OnFragmentInteractionListener, IClimbingApp {

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

            Drawable climb1 = getDrawable(R.drawable.climb1);
            Drawable climb2 = getDrawable(R.drawable.climb2);
            Drawable climb3 = getDrawable(R.drawable.climb3);
            dataBaseHandler.addClimb("Street Fighter", "4c", "12","fab", climb1);
            dataBaseHandler.addClimb("Tower Ridge Direct", "5c", "20","super fab",climb2);
            dataBaseHandler.addClimb("Graham Crackers", "5a", "22","super fab",climb3);
            dataBaseHandler.addClimb("Paradise Lost", "4a", "17","super super fab",climb3);
            dataBaseHandler.addClimb("Stereo-Tentacles", "5a", "14","super super super fab",climb3);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        if (id == R.id.nav_joe) {
            // Handle the joe action
            // Create a new fragment and specify the planet to show based on position
            Fragment fragment = new JoeFrag();


            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, fragment)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
          //  mDrawerList.setItemChecked(position, true);
         //   setTitle(mPlanetTitles[position]);
         //   mDrawerLayout.closeDrawer(mDrawerList);
        } else if (id == R.id.nav_nils) {
            // Handle the joe action
            // Create a new fragment and specify the planet to show based on position
            Fragment fragment = new NilsFrag();


            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_frag, fragment)
                    .commit();

            // Highlight the selected item, update the title, and close the drawer
            //  mDrawerList.setItemChecked(position, true);
            //   setTitle(mPlanetTitles[position]);
            //   mDrawerLayout.closeDrawer(mDrawerList);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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

