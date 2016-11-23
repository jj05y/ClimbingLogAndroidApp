package nils.and.lamp.app.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nils.and.lamp.app.BuildConfig;
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
    private static final double NO_COORDS = 9999.9;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double latitude;
    private double longitude;
    private final int PERMISSION_REQUEST_CODE = 601;

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
    private final int LOAD_FROM_FILENAME = 803;
    private final int LOAD_FROM_URI = 807;
    private String tempFileName; //camera capture temp filename as string
    private View rootView;
    private Uri imageUri;
    private ClimbDataBaseHandler database;

    private static final String TAG = "CreateLog";
    private Bitmap imageBitmap;
    private File photoFile; // the file (hope it's neces)
    private EditText latitudeEdit;
    private EditText longitudeEdit;

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
        latitude = longitude = NO_COORDS;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_climb_creator, container, false);

        if (savedInstanceState != null) {
            // restore camera capture temp filename
            tempFileName = savedInstanceState.getString(getString(R.string.cameraCaptureTempFilename));
            new backgroundImageLoader(rootView).execute(LOAD_FROM_FILENAME);
        }
        database = new ClimbDataBaseHandler(getActivity());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                buildGoogleApiClient();
            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            }
        } else {
            buildGoogleApiClient();
        }
        //picky dialog
        imageContainer = (ImageView) rootView.findViewById(R.id.createlog_image);
        imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.pick_image)
                            .setItems(R.array.pick_image_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 1:
                                            dispatchTakePictureIntent();
                                            break;
                                        case 0:
                                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(pickPhoto, pickGallery);
                                    }
                                }
                            });
                    builder.create().show();
                } else {
                    Toast.makeText(getActivity(), "No permission", Toast.LENGTH_SHORT).show();
                }
            }
        });


        readonlyTitle = (TextView) rootView.findViewById(R.id.createlog_title_viewonly);
        editTitle = (AppCompatEditText) rootView.findViewById(R.id.createlog_title);
        editTitle.setText(prefs.getString("default_name", ""));


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
        editDesc.setText(prefs.getString("default_desc", ""));
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

        grade.setSelection(gradeAdapter.getPosition(prefs.getString("default_grade", "4a")));
        length.setSelection(lengthAdapter.getPosition(prefs.getString("default_length", "~10m")));

        latitudeEdit = (EditText) rootView.findViewById(R.id.createlog_latitude);
        longitudeEdit = (EditText) rootView.findViewById(R.id.createlog_longitude);

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
                latitudeEdit.setText("");
                longitudeEdit.setText("");
            }
        });

        // commit button
        commit = (Button) rootView.findViewById(R.id.createlog_commit_button);
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !editTitle.getText().toString().equals("") && database.isUnique(editTitle.getText().toString())) {
                    if (imageBitmap != null) {
                        String title = editTitle.getText().toString();
                        String desc = editDesc.getText().toString();
                        String g = grade.getSelectedItem().toString();
                        String l = length.getSelectedItem().toString();
                        String lat = (latitudeEdit.getText().toString().equals("")? NO_COORDS +"": latitudeEdit.getText().toString());
                        String lon = (longitudeEdit.getText().toString().equals("")? NO_COORDS +"": longitudeEdit.getText().toString());
                        String coords = (latitude != NO_COORDS && longitude != NO_COORDS) ? lat + "," + lon : null;
                        database.addClimb(title, g, l, desc, imageBitmap, coords);
                        Toast.makeText(getContext(), "climb log added to database", Toast.LENGTH_SHORT).show();
                        // this is to prevent double tap commit to overload DB
                        imageBitmap = null;
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                        getActivity().onBackPressed();

                    } else {
                        Toast.makeText(getActivity(), "Add an image first", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Title must be non-empty and unique",Toast.LENGTH_SHORT).show();
                }
            }
        });


        Button grabGPS = (Button) rootView.findViewById(R.id.createlog_gps_button);
        grabGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                latitudeEdit.setText(latitude+"");
                longitudeEdit.setText(longitude+"");
            }
        });

        return rootView;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = getAppName().replace(" ", "_") + "-" + timeStamp + "-";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        tempFileName = image.getAbsolutePath();
        return image;
    }

    private String getAppName() {
        return getString(getActivity().getApplicationInfo().labelRes);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
                tempFileName = null;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                Log.d(TAG, photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, pickCamera);
            }
        }
    }

    private class backgroundImageLoader extends AsyncTask<Integer, int[], Bitmap> {
        private final int width;
        private final int height;
        View view;

        backgroundImageLoader(View rootView) {
            this.view = rootView;
            ImageView img = (ImageView) view.findViewById(R.id.createlog_image);
            this.width = img.getWidth();
            this.height = img.getHeight();
            Log.d(TAG, "imgV size w:" + width + ", h:" + height);
        }

        @Override
        protected Bitmap doInBackground(Integer... args) {
            Bitmap ret;
            switch (args[0]) {
                case LOAD_FROM_FILENAME:
                    // Get the size of the image
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(tempFileName, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    // Figure out which way needs to be reduced less, chose max for very aggressive resize
                    int scaleFactor = 1;
                    if ((width > 0) || (height > 0)) {
                        scaleFactor = Math.max(photoW / width, photoH / height);
                    }

                    // Set bitmap options to scale the image decode target
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;

                    ret = BitmapFactory.decodeFile(tempFileName, bmOptions);
                    Log.d(TAG, "original bitmap size w:" + ret.getWidth() + ", h:" + ret.getHeight());
                    Log.d(TAG, "original bitmap byte count:" + ret.getByteCount());
                    if (ret.getByteCount() > 5 * 1024 * 1024) {
                        Bitmap instagramme = instagram(ret);
                        Log.d(TAG, "instagram bitmap size w:" + instagramme.getWidth() + ", h:" + instagramme.getHeight());
                        Log.d(TAG, "instagram bitmap byte count:" + instagramme.getByteCount());
                        return instagramme;
                    }
                    return ret;
                case LOAD_FROM_URI:
                    try {
                        ret = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                        Log.d(TAG, "original bitmap size w:" + ret.getWidth() + ", h:" + ret.getHeight());
                        Log.d(TAG, "original bitmap byte count:" + ret.getByteCount());
                        if (ret.getByteCount() > 5 * 1024 * 1024) {
                            Bitmap instagramme = instagram(ret);
                            Log.d(TAG, "instagram bitmap size w:" + instagramme.getWidth() + ", h:" + instagramme.getHeight());
                            Log.d(TAG, "instagram bitmap byte count:" + instagramme.getByteCount());
                            return instagramme;
                        }
                        return ret;
                    } catch (IOException e) {
                        Log.d(TAG, e.getMessage());
                        imageUri = null;
                        imageBitmap = null;
                    }
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView img = (ImageView) view.findViewById(R.id.createlog_image);
            img.setImageBitmap(bitmap);
            imageBitmap = bitmap;
        }
    }

    private static Bitmap instagram(Bitmap bm) {
        final int w = 1080, h = 1080;
        if (bm.getByteCount() >= 5 * 1024 * 1024) {
            Bitmap mask = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas board = new Canvas(mask);
            float scale = w / (float) bm.getWidth();
            float xT = 0.0f;
            float yT = (h - bm.getHeight() * scale) / 2.0f;
            Matrix m = new Matrix();
            m.postTranslate(xT, yT);
            m.preScale(scale, scale);
            Paint p = new Paint();
            p.setFilterBitmap(true);
            board.drawBitmap(bm, m, p);
            return mask;
        }
        return bm;
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
                        new backgroundImageLoader(rootView).execute(LOAD_FROM_FILENAME);
                    } else {
                        Bundle extras = imageReturnedIntent.getExtras();
                        if (extras != null) {
                            imageBitmap = (Bitmap) extras.get("data");
                            imageContainer.setImageBitmap(imageBitmap);

                        } else {
                            Toast.makeText(getActivity(), "Incompatible Camera, please select from gallery", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case pickGallery:
                if (resultCode == RESULT_OK) {
                    imageUri = imageReturnedIntent.getData();
                    Log.d(TAG, "Uri " + imageUri.getPath());
                    new backgroundImageLoader(rootView).execute(LOAD_FROM_URI);
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
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Thank you for permission", Toast.LENGTH_SHORT).show();
                    // permission was granted, yay!
                    buildGoogleApiClient();

                } else {
                    Toast.makeText(getActivity(), "You both permissions to use this app", Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                    // permission denied, boo!
                }
                break;
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
    }

    @Override
    public void onStart() {
        super.onStart();
        if (googleApiClient != null) googleApiClient.connect();
    }

    @Override
    public void onStop() {
        if (googleApiClient != null) googleApiClient.disconnect();
        super.onStop();
    }
}
