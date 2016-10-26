package nils.and.lamp.app.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;

import nils.and.lamp.app.R;

public class PrefsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);


    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setBackgroundColor(Color.WHITE);

    }
}
