package activities.kartau.android.gui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.LinkedList;

import activities.kartau.android.staticdata.Groups;
import activities.kartau.android.staticdata.TrackingInformation;
import activities.kartau.android.staticdata.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KartauMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KartauMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class KartauMapFragment extends Fragment {
    OnHeadlineSelectedListener mCallback;
    MapView mapView;
    GoogleMap map;
    LinkedList<Marker> markers = new LinkedList<Marker>();
    Marker home;

    // Container Activity must implement this interface
    public interface OnHeadlineSelectedListener {
        public void onArticleSelected(int position);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            updateStatus();
        }

    };



    public void updateStatus(){
        System.out.println("----------UPDATING MAP POITION-------------");
        for(int i =0;i<markers.size();i++){
            markers.get(i).setVisible(false);
        }
        LinkedList<Groups> list = User.getGroups();
        for(int i =0; i< list.size();i++){
            if(list.get(i).getToken()==id){
                for(int j =0; j< list.get(i).userList.size();j++){
                    double lat = list.get(i).userList.get(j).getLat();
                    double lon = list.get(i).userList.get(j).getLon();
                    LatLng location =  new LatLng(lat,lon);
                    boolean locFound = false;

                        for (int k = 0; k < markers.size(); k++) {
                            if (markers.get(k).getSnippet() == list.get(i).userList.get(j).getCryptID()) {
                                markers.get(k).setPosition(location);
                                if(!(lon==0&&lat==0)){
                                    markers.get(k).setVisible(true);
                                }
                                locFound = true;
                                break;
                            }
                        }

                    if(!locFound)
                        markers.add(map.addMarker(new MarkerOptions().snippet(list.get(i).userList.get(j).getCryptID()).position(location).title(list.get(i).userList.get(j).getUsername())));

                }
            }
        }
        home.setPosition(new LatLng(TrackingInformation.getLat(), TrackingInformation.getLon()));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(TrackingInformation.getLat(),TrackingInformation.getLon()), 10);
        map.animateCamera(cameraUpdate);

    }


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ID = "ID";

    // TODO: Rename and change types of parameters
    private String id;



    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param id Parameter 1.
     * @return A new instance of fragment KartauMapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static KartauMapFragment newInstance(String id) {
        KartauMapFragment fragment = new KartauMapFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    public KartauMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("update-maps");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                intentFilter);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, null, false);

        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        home = map.addMarker(new MarkerOptions().snippet("home").position(new LatLng(TrackingInformation.getLat(),TrackingInformation.getLon())).title("You"));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(TrackingInformation.getLat(),TrackingInformation.getLon()), 10);
        map.animateCamera(cameraUpdate);



        return v;
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
        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

}
