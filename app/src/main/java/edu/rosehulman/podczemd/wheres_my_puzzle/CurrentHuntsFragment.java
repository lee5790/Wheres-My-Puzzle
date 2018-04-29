package edu.rosehulman.podczemd.wheres_my_puzzle;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Context.LOCATION_SERVICE;
import static edu.rosehulman.podczemd.wheres_my_puzzle.MainActivity.ARG_USER;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentHuntsFragment extends Fragment implements LocationObserver{

    private User user;
    private ViewChanger viewChanger;
    private LocationSource locationSource;

    private Button myHuntsButton;
    private TextView latTextView;
    private TextView longTextView;


    public CurrentHuntsFragment() {
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
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_USER);
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_hunts, container, false);

        myHuntsButton = view.findViewById(R.id.myHuntsButton);
        myHuntsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(MyHuntsFragment.newInstance(user), "My Hunts");
            }
        });

        latTextView = view.findViewById(R.id.latTextView);
        longTextView = view.findViewById(R.id.longTextView);
        return view;
    }

    @Override
    public void updateLocation(Location location) {
        latTextView.setText("Lat: " + location.getLatitude());
        longTextView.setText("Long: " + location.getLongitude());
    }
}
