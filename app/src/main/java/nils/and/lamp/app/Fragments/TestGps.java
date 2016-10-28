package nils.and.lamp.app.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import nils.and.lamp.app.R;

public class TestGps extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 9998;
    private OnFragmentInteractionListener mListener;
    private double longitude;
    private double latitude;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest mLocationRequest;
    Location mLastLocation;

    private TextView text;
    private Button getGPSButton;
    private Button sendGPSButton;

    public TestGps() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_test_gps, container, false);

        text = (TextView) rootView.findViewById(R.id.gps_coords);
        getGPSButton = (Button) rootView.findViewById(R.id.button_getGPS);
        sendGPSButton = (Button) rootView.findViewById(R.id.button_sendToMaps);




        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
        } else {
            buildGoogleApiClient();
        }


        latitude = 0.0;
        longitude = 0.0;

        getGPSButton.setText("PUSH TO GRAB COORDS");
        text.setText("nothing");

        getGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastLocation != null) {
                    latitude = mLastLocation.getLatitude();
                    longitude = mLastLocation.getLongitude();
                    text.setText(latitude + "      " + longitude);
                } else {
                    text.setText("No Coords Yet");
                }
            }
        });

        sendGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latitude != 0.0 && longitude != 0.0) {
                    Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q="+latitude+","+longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Toast.makeText(getActivity(), "hi", Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Thank you for permission", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay!
                    buildGoogleApiClient();


                } else {
                    Toast.makeText(getActivity(), "You need permission", Toast.LENGTH_SHORT).show();
                    // permission denied, boo!
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLastLocation();
        createLocationRequest();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("loc", "Location Changed");
        System.out.println("lols");

        requestLastLocation();
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
        text.setText(latitude + "      " + longitude);
        Log.d("loc", "lat long " + latitude + "," + longitude);

    }

    void requestLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Failed at permission for getting location", Toast.LENGTH_SHORT).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //API23 requirement
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "Failed at permission for getting location", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
        Toast.makeText(getActivity(), "starting updates", Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
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
    public void onResume() {
        super.onResume();
        getView().setBackgroundColor(Color.WHITE);

    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


}
