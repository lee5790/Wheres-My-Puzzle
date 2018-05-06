package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

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
public class PreviousHintsFragment extends Fragment implements OnMapReadyCallback {

    private User user;
    private ViewChanger viewChanger;

    private Button backButton;
    private MapView mapView;
    private GoogleMap map;
    private Spinner dropDownMenu;

    private boolean firstUpdate;
    private LatLng currentLatLing;
    private Hunt hunt;
    private TextView hintText;
    private TextView finishText;
    private int currentHintIndex;
    public PreviousHintsFragment() {
        // Required empty public constructor
    }

    public static PreviousHintsFragment newInstance(User user, Hunt hunt) {
        PreviousHintsFragment fragment = new PreviousHintsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARG_HUNT, hunt);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
            hunt = getArguments().getParcelable(ARG_HUNT);
        }
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
        currentHintIndex =0;
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

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_previous_hints, container, false);

        backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeViewAndBack(ActiveHuntFragment.newInstance(user,hunt));            }
        });
        dropDownMenu = view.findViewById(R.id.spinner);
        dropDownMenu.setVerticalScrollbarPosition(0);
        finishText = view.findViewById(R.id.finishMessageTextView);
        hintText = view.findViewById(R.id.hintTextView);
        hintText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(hunt.getHints().get(currentHintIndex).getHint());
                builder.create().show();
            }
        });
        finishText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(hunt.getHints().get(currentHintIndex).getFinishMessage());
                builder.create().show();
            }
        });
        ArrayList<String> hintStrings = new ArrayList<>();
        ArrayList<Hint> hints = hunt.getHints();
        for(int i =0;i<hunt.getCurrentHint();i++){
            String currentHint = hints.get(i).getHint();
            if(currentHint.length()>30){
               hintStrings.add(currentHint.substring(0,30)+"...");
            }
            else{
                hintStrings.add(currentHint);
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item,hintStrings);
        dropDownMenu.setAdapter(adapter);
        dropDownMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentHintIndex = position;
                updateHint();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mapView = view.findViewById(R.id.currentHintMapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        updateHint();
        return view;
    }

    private void updateHint() {
        hintText.setText(hunt.getHints().get(currentHintIndex).getHint());
        finishText.setText(hunt.getHints().get(currentHintIndex).getFinishMessage());
        if(map!=null){
            map.clear();
            Hint currentHint = hunt.getHints().get(currentHintIndex);
            LatLng hintPosition = new LatLng(currentHint.getLatitude(),currentHint.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(hintPosition,17));
            map.addMarker(new MarkerOptions().position(hintPosition));
            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return true;
                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);
        updateHint();
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
