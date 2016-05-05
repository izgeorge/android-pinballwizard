package com.isabellegeorge.pinballwizard.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.isabellegeorge.pinballwizard.Constants;
import com.isabellegeorge.pinballwizard.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.registerTextView) TextView mRegister;
    @Bind(R.id.emailEditText) EditText mEmail;
    @Bind(R.id.passwordEditText) EditText mPassword;
    @Bind(R.id.passwordLoginButton) Button mLogin;
    private Firebase ref;
    private SharedPreferences mSharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);

        mSharedPref = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        ref = new Firebase(Constants.FIREBASE_URL);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case(R.id.passwordLoginButton):
                loginWithPassword();
            case(R.id.registerTextView):
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
                break;
        }
    }

    public void loginWithPassword(){
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if(email.equals("")){
            mEmail.setError("Please enter a valid email address");
        }

        if(password.equals("")){
            mPassword.setText("Password cannot be blank");
        }

        ref.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                if(authData != null){
                    String uid = authData.getUid();
                    mSharedPref.edit().putString(Constants.KEY_UID, uid).apply();
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                switch(firebaseError.getCode()){
                    case FirebaseError.INVALID_EMAIL:
                    case FirebaseError.USER_DOES_NOT_EXIST:
                        mEmail.setError("This email has not been registered");
                        break;
                    case FirebaseError.INVALID_PASSWORD:
                        mPassword.setError(firebaseError.getMessage());
                        break;
                    case FirebaseError.NETWORK_ERROR:
                        showErrorToast("There was a problem with the network connection");
                        break;
                    default:
                        showErrorToast(firebaseError.toString());

                }
            }
        });
    }

    private void showErrorToast(String message){
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
