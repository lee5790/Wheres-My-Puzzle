package edu.rosehulman.podczemd.wheres_my_puzzle;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.rosehulman.podczemd.wheres_my_puzzle.Fragments.CreateAccountFragment;
import edu.rosehulman.podczemd.wheres_my_puzzle.Fragments.CurrentHuntsFragment;
import edu.rosehulman.podczemd.wheres_my_puzzle.Fragments.LoginFragment;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationObserver;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.LocationSource;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.OnLogoutListener;
import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hint;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.Hunt;
import edu.rosehulman.podczemd.wheres_my_puzzle.Models.User;

public class MainActivity extends AppCompatActivity implements ViewChanger, LocationSource, LocationListener, LoginFragment.OnLoginListener, OnLogoutListener, CreateAccountFragment.CreateAccountListener{
    public static final String ARG_USER = "user";
    public static final String ARG_HUNT = "hunt";
    public static final String ARG_HINT = "hint";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int RC_SIGN_IN = 2;

    private LocationManager locationManager;
    private ArrayList<LocationObserver> observers;
    private Location lastKnownLocation;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private OnCompleteListener onCompleteListener;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        User user = new User("Creator", "12345");

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        registerLocationListener();

        observers = new ArrayList<LocationObserver>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("title");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                setTitle((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        auth = FirebaseAuth.getInstance();
        initializeListeners();
        initializeGoogle();
    }

    private void initializeListeners() {
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d("tag", "User: " + user);
                if (user != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                changeView(CurrentHuntsFragment.newInstance(user.getUid()), "Current Hunts");
                            } else {
                                newUserDialog(user);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    changeView(new LoginFragment(), "Login");
                }
            }
        };
        onCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    showLoginError("Login failed");
                }
            }
        };
    }

    private void newUserDialog(final FirebaseUser firebaseUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose a Username");
        View view = getLayoutInflater().inflate(R.layout.dialog_new_user, null, false);
        builder.setView(view);
        final EditText usernameEditText = view.findViewById(R.id.usernameEditText);
        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                User user = new User(usernameEditText.getText().toString(), firebaseUser.getUid());
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                userRef.setValue(user);
                changeView(CurrentHuntsFragment.newInstance(user.getUid()), "Current Hunts");
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void initializeGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_hunt, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode == PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                registerLocationListener();
            }
        }
    }

    private void registerLocationListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                1, this);
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void changeView(Fragment fragment, String transactionName) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, transactionName);
        //ft.addToBackStack(transactionName);
        ft.commit();
    }

    @Override
    public void changeViewAndBack(Fragment fragment) {
        //getSupportFragmentManager().popBackStackImmediate();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
        for(LocationObserver obs: observers) {
            obs.updateLocation(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void subscribe(LocationObserver obs) {
        observers.add(obs);
        obs.updateLocation(lastKnownLocation);
    }

    @Override
    public void unSubscribe(LocationObserver obs) {
        observers.remove(obs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onLogin(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(onCompleteListener);
    }

    @Override
    public void onGoogleLogin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onCreateAccount() {
        this.changeView(new CreateAccountFragment(), "Create Account");
    }

    @Override
    public void onLogout() {
        auth.signOut();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                showLoginError("Google sign in failed");
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("tag", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, onCompleteListener);
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }


    @Override
    public void onAccountCreate(final String username, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "createUserWithEmail:success");
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            User user = new User(username, firebaseUser.getUid());
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
                            userRef.setValue(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Error", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
