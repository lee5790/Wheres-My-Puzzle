package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationObserver;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationSource;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hint;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_HUNT;
import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActiveHuntFragment extends Fragment implements LocationObserver, OnMapReadyCallback {

    private static final double ACCEPTABLE_DISTANCE_FROM_GOAL = 0.04;// 40 meters
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
    private DatabaseReference userRef;

    private boolean firstUpdate;
    private LatLng currentLatLing;
    private Hunt hunt;


    public ActiveHuntFragment() {
        // Required empty public constructor
    }

    public static ActiveHuntFragment newInstance(User user, Hunt hunt) {
        ActiveHuntFragment fragment = new ActiveHuntFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARG_HUNT, hunt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstUpdate = true;
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
            hunt = getArguments().getParcelable(ARG_HUNT);
        }
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                user.setUid(dataSnapshot.getKey());
                user.updateHunt(hunt);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        View view = inflater.inflate(R.layout.fragment_active_hunt, container, false);

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(CurrentHuntsFragment.newInstance(user.getUid()));            }
        });
        checkLocationButton = view.findViewById(R.id.checkLocationButton);
        previousHintsButton = view.findViewById(R.id.previousHintsButton);
        previousHintsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hunt.getCurrentHint() != 0) {
                    viewChanger.changeView(PreviousHintsFragment.newInstance(user, hunt), "Previous Hints");
                }
            }
        });
        titleText = view.findViewById(R.id.activeHuntTitleTextView);
        titleText.setText(hunt.getTitle());
        hintText = view.findViewById(R.id.activeHuntHintTextView);
        hintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(hunt.getHints().get(hunt.getCurrentHint()).getHint());
                builder.create().show();
            }
        });
        mapView = view.findViewById(R.id.activeHuntMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        checkLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocation();
            }
        });
        updateHintText();
        return view;
    }

    private void checkLocation() {
        Hint currentHint = hunt.getHints().get(hunt.getCurrentHint());
        double distanceFromGoal =  CalculationByDistance(currentLatLing,new LatLng(currentHint.getLatitude(),currentHint.getLongitude()));
        if(distanceFromGoal < ACCEPTABLE_DISTANCE_FROM_GOAL){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(currentHint.getFinishMessage());
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    hunt.incCurrentHint();
                    user.updateHunt(hunt);
                    userRef.setValue(user);
                    updateHintText();
                }
            });
            builder.create().show();



        }
        else{
            Toast.makeText(getContext(),"Wrong Location",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateHintText() {
        if(hunt.getCurrentHint()>=hunt.getHints().size()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(hunt.getFinalMessage());
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    user.removeCurrentHunt(hunt);
                    userRef.setValue(user);
                    viewChanger.changeViewAndBack(CurrentHuntsFragment.newInstance(user.getUid()));
                }
            });
            builder.create().show();


        }
        else{
            Hint currentHint = hunt.getHints().get(hunt.getCurrentHint());
            hintText.setText(currentHint.getHint());
        }
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }

    @Override
    public void updateLocation(Location location) {
        currentLatLing = new LatLng(location.getLatitude(), location.getLongitude());
        map.clear();
        map.addCircle(new CircleOptions().center(currentLatLing).radius(3).fillColor(Color.BLUE).strokeColor(Color.WHITE).strokeWidth(4));
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
        locationSource.subscribe(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return true;
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
