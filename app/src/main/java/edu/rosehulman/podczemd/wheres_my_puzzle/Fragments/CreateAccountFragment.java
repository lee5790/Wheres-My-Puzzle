package edu.rosehulman.podczemd.wheres_my_puzzle.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

import edu.rosehulman.podczemd.wheres_my_puzzle.Interfaces.ViewChanger;
import edu.rosehulman.podczemd.wheres_my_puzzle.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccountFragment extends Fragment {


    private EditText mPasswordView;
    private EditText mEmailView;
    private View mLoginForm;
    private View mProgressSpinner;
    private boolean mLoggingIn;
    private CreateAccountListener mListener;
    private ViewChanger viewChanger;
    private EditText mConfirmPassword;
    private EditText mUsernameView;

    public CreateAccountFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggingIn = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_account, container, false);
        mEmailView = (EditText) rootView.findViewById(R.id.email);
        mPasswordView = (EditText) rootView.findViewById(R.id.password);
        mConfirmPassword = rootView.findViewById(R.id.confirmPassword);
        mUsernameView = rootView.findViewById(R.id.username);
        mLoginForm = rootView.findViewById(R.id.login_form);
        mProgressSpinner = rootView.findViewById(R.id.login_progress);
        View createButton = rootView.findViewById(R.id.create_button);
        View cancelButton = rootView.findViewById(R.id.cancel_button);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewChanger.changeView(new LoginFragment(),"Login");
            }
        });
        return rootView;
    }


    public void createAccount() {
        if (mLoggingIn) {
            return;
        }

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPassword.getText().toString();
        String username = mUsernameView.getText().toString();
        boolean cancelLogin = false;
        View focusView = null;

        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.invalid_password));
            focusView = mPasswordView;
            cancelLogin = true;
        }
        else if(!password.equals(confirmPassword)){
            mPasswordView.setError(getString(R.string.password_do_not_match));
            focusView = mPasswordView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.field_required));
            focusView = mEmailView;
            cancelLogin = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.invalid_email));
            focusView = mEmailView;
            cancelLogin = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.field_required));
            focusView = mUsernameView;
            cancelLogin = true;
        }

        if (cancelLogin) {
            // error in login
            focusView.requestFocus();
        } else {
            // show progress spinner, and start background task to login
            showProgress(true);
            mLoggingIn = true;
            mListener.onAccountCreate(username,email, password);
            hideKeyboard();
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
    }

    public void onLoginError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.login_error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();

        showProgress(false);
        mLoggingIn = false;
    }

    private void showProgress(boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (CreateAccountListener) activity;
            viewChanger = (ViewChanger) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        viewChanger = null;
    }

    public interface CreateAccountListener {
        void onAccountCreate(String username,String email, String password);
    }

}
