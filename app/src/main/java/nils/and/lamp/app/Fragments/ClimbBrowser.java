package nils.and.lamp.app.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Vector;

import nils.and.lamp.app.Activities.ClimbDetailView;
import nils.and.lamp.app.Core.ClimbListAdapter;
import nils.and.lamp.app.Core.Climb;
import nils.and.lamp.app.Core.IClimbingApp;
import nils.and.lamp.app.R;


public class ClimbBrowser extends Fragment {


    private OnFragmentInteractionListener mListener;

    public ClimbBrowser() {
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
        View rootView = inflater.inflate(R.layout.fragment_joe, container, false);
        Log.d("FRAG", rootView +"");
        ListView climbsList = (ListView) rootView.findViewById(R.id.listview_climbs);

        Vector<Climb> climbs = ((IClimbingApp) getActivity()).getDatabase().getClimbs();


        Log.d("FRAG", climbsList +"");
        climbsList.setAdapter(new ClimbListAdapter(climbs, getActivity()));
        climbsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Climb climb = ((ClimbListAdapter) adapterView.getAdapter()).getClimbs().get(i);
                Log.d("FRAG", climb.getName() );
                Intent intent = new Intent(getActivity(), ClimbDetailView.class);

                intent.putExtra("ClimbIndex", i);
                startActivity(intent);
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
