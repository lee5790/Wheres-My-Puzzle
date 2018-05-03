package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationObserver;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationSource;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveHuntFragment extends Fragment implements LocationObserver, OnMapReadyCallback {

    private User user;
    private ViewChanger viewChanger;
    private LocationSource locationSource;

    private Button backButton;
    private TextView titleText;
    private TextView hintText;
    private Button checkLocationButton;
    private Button previousHintsButton;
    private MapView mapView;
    private GoogleMap map;

    private boolean firstUpdate;


    public ActiveHuntFragment() {
        // Required empty public constructor
    }

    public static CurrentHuntsFragment newInstance(User user) {
        CurrentHuntsFragment fragment = new CurrentHuntsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstUpdate = true;
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
        }
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ViewChanger) {
            viewChanger = (ViewChanger) context;
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement ViewChanger");
        }
        if (context instanceof LocationSource) {
            locationSource = (LocationSource)context;
            locationSource.subscribe(this);
        }
        else {
            throw new RuntimeException(context.toString()
                    + " must implement ViewChanger");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationSource.unSubscribe(this);
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_hunts, container, false);

        backButton = view.findViewById(R.id.backButton);
        checkLocationButton = view.findViewById(R.id.checkLocationButton);
        previousHintsButton = view.findViewById(R.id.previousHintsButton);
        titleText = view.findViewById(R.id.activeHuntTitleTextView);
        hintText = view.findViewById(R.id.activeHuntHintTextView);
        mapView = view.findViewById(R.id.activeHuntMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void updateLocation(Location location) {
        LatLng currentLatLing = new LatLng(location.getLatitude(), location.getLongitude());
        map.addCircle(new CircleOptions().center(currentLatLing).radius(3));
        if(firstUpdate) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLing, 17));
            firstUpdate = false;
        } else {
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLatLing));
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
