package nils.and.lamp.app.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import nils.and.lamp.app.R;

public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
        getView().setBackgroundColor(Color.WHITE);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        String animal = sharedPreferences.getString("animal_preference", "Duck");
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
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("", "PAUSE");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }



}
