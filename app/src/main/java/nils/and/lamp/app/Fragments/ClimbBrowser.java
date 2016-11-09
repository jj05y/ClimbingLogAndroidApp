package nils.and.lamp.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.Vector;

import nils.and.lamp.app.Activities.ClimbDetailView;
import nils.and.lamp.app.Core.ClimbDataBaseHandler;
import nils.and.lamp.app.Core.ClimbListAdapter;
import nils.and.lamp.app.Core.Climb;
import nils.and.lamp.app.R;


public class ClimbBrowser extends Fragment {


    private OnFragmentInteractionListener mListener;
    private ListView climbsList;
    private ClimbDataBaseHandler database;
    private Vector<Climb> climbs;

    public ClimbBrowser() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_climb_browser, container, false);
        Log.d("FRAG", "create");
        climbsList = (ListView) rootView.findViewById(R.id.listview_climbs);

        climbs = null;
        database = new ClimbDataBaseHandler(getActivity());
 //       (new ClimbLoader()).execute();

        Button sortAZ = (Button) rootView.findViewById(R.id.sort_button_az);
        Button sortGrade = (Button) rootView.findViewById(R.id.sort_button_grade);
        Button sortLength = (Button) rootView.findViewById(R.id.sort_button_length);

        sortAZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (climbs != null) (new ClimbSorter()).execute("az");
            }
        });
        sortGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (climbs != null) (new ClimbSorter()).execute("grade");
            }
        });
        sortLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (climbs != null) (new ClimbSorter()).execute("length");
            }
        });




        return rootView;
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
        Log.d("FRAG", "AATTTAATTCCCHHH");
    }

    @Override
    public void onResume() {
        Log.d("FRAG", "RRREEESSSUUMMMEEE");
        (new ClimbLoader()).execute();
        super.onResume();
        getView().setBackgroundColor(Color.WHITE);

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
        void onFragmentInteraction(Uri uri);
    }

    private class ClimbLoader extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... v) {
            climbs = database.getClimbs();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("FRAG", climbsList +"");
            climbsList.setAdapter(new ClimbListAdapter(climbs, getActivity()));
            climbsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Climb climb = ((ClimbListAdapter) adapterView.getAdapter()).getClimbs().get(i);
                    Log.d("FRAG", climb.getName() );
                    Intent intent = new Intent(getActivity(), ClimbDetailView.class);
                    intent.putExtra("Climb", climb);
                    startActivity(intent);
                }
            });
            super.onPostExecute(aVoid);
        }
    }

    private class ClimbSorter extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... what) {
            Vector<Climb> sorted = new Vector<>();
            while (!climbs.isEmpty()) {
                int indexOfMin = 0;
                for (int i = 0; i < climbs.size(); i++) {
                    switch (what[0]) {
                        case "az":
                            if (climbs.get(indexOfMin).getName().compareTo(climbs.get(i).getName()) > 0)
                                indexOfMin = i;
                            break;
                        case "grade":
                            if (climbs.get(indexOfMin).getGrade().compareTo(climbs.get(i).getGrade()) > 0)
                                indexOfMin = i;
                            break;
                        case "length":
                            if (climbs.get(indexOfMin).getLength().compareTo(climbs.get(i).getLength()) > 0)
                                indexOfMin = i;
                            break;
                    }
                }
                sorted.add(climbs.remove(indexOfMin));
            }
            climbs = sorted;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("FRAG", climbsList +"");
            climbsList.setAdapter(new ClimbListAdapter(climbs, getActivity()));
            climbsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Climb climb = ((ClimbListAdapter) adapterView.getAdapter()).getClimbs().get(i);
                    Log.d("FRAG", climb.getName() );
                    Intent intent = new Intent(getActivity(), ClimbDetailView.class);
                    intent.putExtra("Climb", climb);
                    startActivity(intent);
                }
            });
            super.onPostExecute(aVoid);
        }
    }

}
