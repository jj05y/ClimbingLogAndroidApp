package nils.and.lamp.app.Fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import nils.and.lamp.app.R;

public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    private EditTextPreference nameEditTextpref;
    private EditTextPreference descEditTextpref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        nameEditTextpref = (EditTextPreference) findPreference("default_name");
        nameEditTextpref.setSummary(nameEditTextpref.getText());

        descEditTextpref = (EditTextPreference) findPreference("default_desc");
        descEditTextpref.setSummary(descEditTextpref.getText());

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
            case "Donkey":
                fab.setImageResource(R.drawable.donkey);
                break;
            case "Goat":
                fab.setImageResource(R.drawable.goat);
                break;
            default:
                fab.setImageResource(R.drawable.duck);
        }
        nameEditTextpref.setSummary(nameEditTextpref.getText());
        descEditTextpref.setSummary(nameEditTextpref.getText());


    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("", "PAUSE");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }



}
