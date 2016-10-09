package nils.and.lamp.lampandnils3;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NilsFrag.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NilsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NilsFrag extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private TextView readonlyTitle;
    private AppCompatEditText editTitle;
    private Button resetAll;
    private AppCompatEditText editDesc;
    private ImageView imageContainer;
    private final int pickGallery = 401; // whatever code that is
    private final int pickCamera = 402; // whatever code that is
    private final int externalStoragePermissionRequestCode = 404;
    private String tempFileName; //camera capture temp filename as string
    private View rootView;

    private static final String TAG = "CreateLog";

    public NilsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JoeFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static NilsFrag newInstance(String param1, String param2) {
        NilsFrag fragment = new NilsFrag();
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
        rootView = inflater.inflate(R.layout.fragment_nils, container, false);

        if (savedInstanceState != null) {
            // restore camera capture temp filename
            tempFileName = savedInstanceState.getString(getString(R.string.cameraCaptureTempFilename));
        }

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
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    externalStoragePermissionRequestCode);
        } else {
            Log.d("permission", "granted..?");
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
                                        tempFileName = pants.getAbsolutePath();
                                        Uri uri = Uri.fromFile(pants);
                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                                        startActivityForResult(intent, pickCamera);
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
                readonlyTitle.setText(editable.toString());
            }
        });

        // desc field
        editDesc = (AppCompatEditText) rootView.findViewById(R.id.createlog_desc);

        // reset button
        resetAll = (Button) rootView.findViewById(R.id.createlog_reset_button);
        resetAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTitle.setText("");
                editDesc.setText("");
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
                        imageContainer.setImageURI(imageReturnedIntent.getData());
                    }
                }
                break;
            case pickGallery:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    imageContainer.setImageURI(selectedImage);
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.cameraCaptureTempFilename), tempFileName);
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
}
