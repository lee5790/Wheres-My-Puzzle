package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationObserver;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationSource;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hint;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;

import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_HINT;
import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_HUNT;
import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


public class CreateHintFragment extends Fragment implements LocationObserver, OnMapReadyCallback {

    private User user;
    private Hunt hunt;
    private Hint hint;
    private ViewChanger viewChanger;
    private LocationSource locationSource;
    private LatLng currentLatLng;

    private Button cancelButton;
    private Button deleteButton;
    private Button finishButton;
    EditText hintText;
    EditText latitudeText;
    EditText longitudeText;
    EditText finishText;

    private MapView mapView;
    private GoogleMap map;


    public CreateHintFragment() {
        // Required empty public constructor
    }

    public static CreateHintFragment newInstance(User user, Hunt hunt, Hint hint) {
        CreateHintFragment fragment = new CreateHintFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARG_HUNT, hunt);
        args.putParcelable(ARG_HINT,hint);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
            hunt = getArguments().getParcelable(ARG_HUNT);
            hint = getArguments().getParcelable(ARG_HINT);
        }
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_hint, container, false);
        EditText hintDescription = view.findViewById(R.id.hintDetailEditText);
        hintDescription.setText(user.getPassword());
        cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(CreateHuntFragment.newInstance(user,hunt));
            }
        });

        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hunt.removeHint(hint);
                viewChanger.changeViewAndBack(CreateHuntFragment.newInstance(user,hunt));
            }
        });

        finishButton = view.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finalizeHint();
            }
        });
        this.hintText = view.findViewById(R.id.hintDetailEditText);
        this.hintText.setText(hint.getHint());

        this.latitudeText = view.findViewById(R.id.latitudeEditText);
        this.latitudeText.setText("" + hint.getLatitude());

        this.longitudeText = view.findViewById(R.id.longitudeEditText);
        this.longitudeText.setText("" + hint.getLongitude());

        this.finishText = view.findViewById(R.id.hintFinishMessageEditText);
        this.finishText.setText(hint.getFinishMessage());
        return view;
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

    @Override
    public void updateLocation(Location location) {
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.clear();
        map.addMarker(new MarkerOptions().position(currentLatLng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 17));
        locationSource.unSubscribe(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                currentLatLng = latLng;
                map.clear();
                map.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }

    private void finalizeHint(){
        this.hint.setHint(String.valueOf(hintText.getText()));
        this.hint.setLatitude(currentLatLng.latitude);
        this.hint.setLongitude(currentLatLng.longitude);
        this.hint.setFinishMessage(String.valueOf(finishText.getText()));
        if (!hunt.getHints().contains(hint)) {
            hunt.addHint(hint);
        }
        viewChanger.changeViewAndBack(CreateHuntFragment.newInstance(user,hunt));
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
