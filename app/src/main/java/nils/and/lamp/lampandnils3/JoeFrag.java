package nils.and.lamp.lampandnils3;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Vector;


public class JoeFrag extends Fragment {


    private OnFragmentInteractionListener mListener;

    public JoeFrag() {
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


        Drawable climb1 = getActivity().getDrawable(R.drawable.climb1);
        Drawable climb2 = getActivity().getDrawable(R.drawable.climb2);
        Drawable climb3 = getActivity().getDrawable(R.drawable.climb3);
        Vector<Climb> climbs = new Vector<>();
        climbs.add(new Climb(climb1, "climb1", "4a", "40","fab"));
        climbs.add(new Climb(climb2, "climb2", "5a", "40" ,"suhper fab"));
        climbs.add(new Climb(climb3, "climb3", "4a", "40","fab"));
        climbs.add(new Climb(climb2, "climb2", "5a", "40" ,"suhper fab"));
        climbs.add(new Climb(climb1, "climb1", "4a", "40","fab"));
        climbs.add(new Climb(climb3, "climb3", "5a", "40" ,"suhper fab"));
        climbs.add(new Climb(climb1, "climb1", "4a", "40","fab"));
        climbs.add(new Climb(climb2, "climb2", "5a", "40" ,"suhper fab"));


        Log.d("FRAG", climbsList +"");
        climbsList.setAdapter(new ClimbListAdapter(climbs, getActivity()));

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
