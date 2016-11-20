package nils.and.lamp.app.Fragments;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Vector;

import nils.and.lamp.app.Activities.ClimbDetailView;
import nils.and.lamp.app.Core.Climb;
import nils.and.lamp.app.Core.ClimbDataBaseHandler;
import nils.and.lamp.app.Core.ClimbListAdapter;
import nils.and.lamp.app.R;


public class ClimbsOnAMap extends Fragment implements OnMapReadyCallback {

    private OnFragmentInteractionListener mListener;
    private GoogleMap map;
    private MapFragment mapFragment;
    private Vector<Climb> climbs;

    public ClimbsOnAMap() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_climbs_on_amap, container, false);
        if (mapFragment == null) {
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        (new ClimbLoader()).execute();
        Log.d("MAP", "Map Ready");
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onPause() {
        if (mapFragment != null)
            getActivity().getFragmentManager().beginTransaction().remove(mapFragment).commit();
        super.onPause();

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class ClimbLoader extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... v) {
            climbs = (new ClimbDataBaseHandler(getActivity())).getClimbs();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Vector<Marker> markers = new Vector<>();
            for (Climb climb : climbs) {
                if (!climb.getGpsCoords().equals("9999.9,9999.9")) {
                    String[] coords = climb.getGpsCoords().split(",");
                    try {
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1])))
                                .title(climb.getName()));
                        markers.add(marker);
                    } catch (NumberFormatException e) {
                        Log.d("GPS", "Invalid Coords");
                    }
                }
            }
            //Calculate the markers to get their position
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            for (Marker m : markers) {
                b.include(m.getPosition());
            }
            LatLngBounds bounds = b.build();
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,100);
            map.animateCamera(cu);
            super.onPostExecute(aVoid);
        }
    }
}
