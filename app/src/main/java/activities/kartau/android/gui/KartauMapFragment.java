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
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

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
    HashMap<String,Marker> markerMap = new HashMap<String,Marker>();
    Marker home;
    boolean firstLoad = true;

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
        for(Map.Entry<String, Marker> entry : markerMap.entrySet()) {
            entry.getValue().setVisible(false);
        }
        LinkedList<Groups> list = User.getGroups();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(int i =0; i< list.size();i++){
            if(list.get(i).getToken()==id){
                for(int j =0; j< list.get(i).userList.size();j++){
                    double lat = list.get(i).userList.get(j).getLat();
                    double lon = list.get(i).userList.get(j).getLon();
                    LatLng location =  new LatLng(lat,lon);

                    home.remove();
                    if(markerMap.get(list.get(i).userList.get(j).getCryptID())==null) {
                        markerMap.put(list.get(i).userList.get(j).getCryptID(), map.addMarker(new MarkerOptions().snippet(list.get(i).userList.get(j).getCryptID()).position(location).title(list.get(i).userList.get(j).getUsername())));
                        markerMap.get(list.get(i).userList.get(j).getCryptID()).setVisible(false);
                    }


                    markerMap.get(list.get(i).userList.get(j).getCryptID()).setPosition(location);
                    if((!(Math.floor(lon)==0&&Math.floor(lat)==0))&&list.get(i).userList.get(j).getActive()==1){
                        if(list.get(i).userList.get(j).getCryptID()==User.getCryptId()){
                            //set home marker

                        }
                        markerMap.get(list.get(i).userList.get(j).getCryptID()).setVisible(true);
                        builder.include(markerMap.get(list.get(i).userList.get(j).getCryptID()).getPosition());
                        System.out.println("-----MARKER FOR " + list.get(i).userList.get(j).getUsername() + " AT " + location.latitude+","+location.longitude + " ADDED-----");
                    }





                }
            }
        }

        home.setPosition(new LatLng(TrackingInformation.getLat(), TrackingInformation.getLon()));
        builder.include(home.getPosition());
        LatLngBounds bounds = builder.build();
        int padding = 30; // offset from edges of the map in pixels


        if(firstLoad){
            CameraUpdate cameraUpdate;
            if(bounds.northeast.latitude-bounds.southwest.latitude<0.05) {
                cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(TrackingInformation.getLat(), TrackingInformation.getLon()), 15);
            }else {
                cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            }
            map.animateCamera(cameraUpdate);
            firstLoad = false;
        }


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
        //map.animateCamera(cameraUpdate);
        map.moveCamera(cameraUpdate);



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
    }

    @Override
    public void onStart() {
        super.onStart();
//        try {
//            mListener = (OnFragmentInteractionListener) getActivity();
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        //mapView.onDestroy();
        mListener = null;
        super.onDetach();

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
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onResume() {

        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        mapView.onDestroy();
        super.onDestroy();


    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();

    }

}
