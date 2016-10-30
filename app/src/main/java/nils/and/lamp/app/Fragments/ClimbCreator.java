package nils.and.lamp.app.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;

import nils.and.lamp.app.Core.ClimbDataBaseHandler;
import nils.and.lamp.app.R;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ClimbCreator.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ClimbCreator#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClimbCreator extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitude;
    private double longitude;
    private final int FINE_LOCATION_REQUEST_CODE = 601;

    private TextView readonlyTitle;
    private AppCompatEditText editTitle;
    private Button resetAll;
    private Button commit;
    private AppCompatEditText editDesc;
    private ImageView imageContainer;
    private Spinner grade;
    private Spinner length;
    private final int pickGallery = 401; // whatever code that is
    private final int pickCamera = 402; // whatever code that is
    private final int externalStoragePermissionRequestCode = 404;
    private String tempFileName; //camera capture temp filename as string
    private View rootView;
    private Uri imageUri;
    private ClimbDataBaseHandler database;

    private static final String TAG = "CreateLog";
    private Bitmap imageBitmap;

    public ClimbCreator() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClimbBrowser.
     */
    // TODO: Rename and change types and number of parameters
    public static ClimbCreator newInstance(String param1, String param2) {
        ClimbCreator fragment = new ClimbCreator();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_climb_creator, container, false);

        if (savedInstanceState != null) {
            // restore camera capture temp filename
            tempFileName = savedInstanceState.getString(getString(R.string.cameraCaptureTempFilename));
        }
        database= new ClimbDataBaseHandler(getActivity());

        // check permission
        int permissionCheck = ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d("permission", "not yet granted");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "it thinks i need to show rational...as if i care lmao");
            }
            // No explanation needed, we can request the permission.
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    externalStoragePermissionRequestCode);
        } else {
            Log.d("permission", "granted..?");
        }
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_REQUEST_CODE);
            }
        } else {
            buildGoogleApiClient();
        }
        //picky dialog
        imageContainer = (ImageView) rootView.findViewById(R.id.createlog_image);
        imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.pick_image)
                        .setItems(R.array.pick_image_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 1:
                                        File pants = getTempCaptureFile();
                                        if (pants == null) {
                                            Log.e(TAG, "sorry m8, no write permission, no camera");
                                            break;
                                        }
                                        tempFileName = pants.getAbsolutePath();
                                        Uri uri = Uri.fromFile(pants);
                                        Intent takePictureIndent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        if (takePictureIndent.resolveActivity(getActivity().getPackageManager())!=null){
                                            startActivityForResult(takePictureIndent,pickCamera);
                                        }
                                        break;
                                    case 0:
                                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(pickPhoto, pickGallery);
                                }
                            }
                        });
                builder.create().show();
            }
        });


        readonlyTitle = (TextView) rootView.findViewById(R.id.createlog_title_viewonly);
        editTitle = (AppCompatEditText) rootView.findViewById(R.id.createlog_title);

        // editTitle content will update the colorful readonly title
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.contains("\n")) {
                    str = str.replace("\n", " ");
                    editTitle.setText(str);
                }
                Selection.setSelection(editTitle.getText(), str.length());
                readonlyTitle.setText(str);
            }
        });

        // desc field
        editDesc = (AppCompatEditText) rootView.findViewById(R.id.createlog_desc);
        editDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String str = editable.toString();
                if (str.contains("\n")) {
                    str = str.replace("\n", " ");
                    editDesc.setText(str);
                }
                Selection.setSelection(editDesc.getText(), str.length());
            }
        });

        // grade spinner
        grade = (Spinner) rootView.findViewById(R.id.createlog_grade);
        final ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.grade_array, android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grade.setAdapter(gradeAdapter);
        // length spinner
        length = (Spinner) rootView.findViewById(R.id.createlog_length);
        final ArrayAdapter<CharSequence> lengthAdapter = ArrayAdapter.createFromResource(this.getContext(),
                R.array.length_array, android.R.layout.simple_spinner_item);
        lengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        length.setAdapter(lengthAdapter);

        // reset button
        resetAll = (Button) rootView.findViewById(R.id.createlog_reset_button);
        resetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTitle.setText("");
                editDesc.setText("");
                grade.setSelection(0);
                length.setSelection(0);
                imageContainer.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.placeholder));
            }
        });

        // commit button
        commit = (Button) rootView.findViewById(R.id.createlog_commit_button);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageBitmap!= null) {
                    String title = editTitle.getText().toString();
                    String desc = editDesc.getText().toString();
                    String g = grade.getSelectedItem().toString();
                    String l = length.getSelectedItem().toString();
                    database.addClimb(title, g, l, desc, imageBitmap, null);
                    Toast.makeText(getContext(), "climb log added to database", Toast.LENGTH_SHORT).show();
                    imageBitmap = null;
                } else {
                    Toast.makeText(getActivity(), "Add an image first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    File getTempCaptureFile() {
        File dir = new File(Environment.getExternalStorageDirectory(),
                getString(getActivity().getApplicationInfo().labelRes));
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.e(TAG, "wtf, I thought you got permission?");
                return null;
            }
        }
        String tempFilename = "capture"
                + ((Long) (System.currentTimeMillis() / 1000)).toString()
                + ".jpg";
        return new File(dir, tempFilename);
    }

    private class backgroundImageLoader extends AsyncTask<String, int[], Bitmap> {
        View view;

        backgroundImageLoader(View rootView) {
            this.view = rootView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(strings[0], options);
            options.inSampleSize = calculateInSampleSize(options, 400, 640);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(strings[0], options);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView img = (ImageView) view.findViewById(R.id.createlog_image);
            img.setImageBitmap(bitmap);
        }

        int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case pickCamera:
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent == null) {
                        if (tempFileName == null || tempFileName.equals("")) {
                            Log.e("Create:onActivityResult", "tempFilename is empty, this should not happen");
                        }
                        new backgroundImageLoader(rootView).execute(tempFileName);
                    } else {
                        Bundle extras = imageReturnedIntent.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        imageContainer.setImageBitmap(imageBitmap);
                    }
                }
                break;
            case pickGallery:
                if (resultCode == RESULT_OK) {
                    imageUri = imageReturnedIntent.getData();
                    imageContainer.setImageURI(imageUri);
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.cameraCaptureTempFilename), tempFileName);
        outState.putString(getString(R.string.createLogTitle), editTitle.getText().toString());
        outState.putString(getString(R.string.createLogDesc), editDesc.getText().toString());
        outState.putInt(getString(R.string.createLogGrade), grade.getSelectedItemPosition());
        outState.putInt(getString(R.string.createLogLength), length.getSelectedItemPosition());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case externalStoragePermissionRequestCode: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG, "permission really granted");
                } else {
                    Log.d(TAG, "permission really denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
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
    public void onResume() {
        super.onResume();
        getView().setBackgroundColor(Color.WHITE);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    protected GoogleApiClient googleApiClient;
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("loc", "Location Changed");
        System.out.println("lols");

        requestLastLocation();
        latitude = mLastLocation.getLatitude();
        longitude = mLastLocation.getLongitude();
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
}
