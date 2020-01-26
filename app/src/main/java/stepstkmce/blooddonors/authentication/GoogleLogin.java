package stepstkmce.blooddonors.authentication;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import stepstkmce.blooddonors.Helper.FireBaseAuthHelper;
import stepstkmce.blooddonors.Helper.FireStoreHelper;
import stepstkmce.blooddonors.R;
import stepstkmce.blooddonors.home.Activity_Home;


public class GoogleLogin extends Activity implements View.OnClickListener{
    private static final int RC_SIGN_IN =333;
    private static final String TAG = "GoogleActivity";
    private GoogleSignInClient mGoogleSignInClient;
    Button button;
    Button submit;
    EditText field;
    ProgressBar progressBar;
    FirebaseFirestore firestore;
    String password;
    RelativeLayout container;
    int passwordReady=0;
    Snackbar network_error;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("passwordReady",passwordReady);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        passwordReady=savedInstanceState.getInt("passwordReady");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        container=findViewById(R.id.l_login_container);
        button=findViewById(R.id.l_login_gbutton);
        field=findViewById(R.id.l_login_key_field);
        submit=findViewById(R.id.l_login_submit);
        progressBar=findViewById(R.id.l_login_pb);
        progressBar.setVisibility(View.GONE);
        button.setEnabled(false);
        FirebaseApp.initializeApp(getApplicationContext());
        firestore=FirebaseFirestore.getInstance();
        if (savedInstanceState!=null) {
            onRestoreInstanceState(savedInstanceState);
        }else {
            progressBar.setVisibility(View.VISIBLE);
            getPassword(getApplicationContext(),0);
        }


        submit.setOnClickListener(this);

        network_error=Snackbar.make(container,"No Network",Snackbar.LENGTH_LONG)
                .setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getPassword(getApplicationContext(),0);
                    }
                });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    public void signIn() {
        progressBar.setVisibility(View.VISIBLE);
        button.setEnabled(false);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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
                progressBar.setVisibility(View.GONE);
                button.setEnabled(true);
                Toast.makeText(this, "Sign in failed,Try Again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        FireBaseAuthHelper authHelper=new FireBaseAuthHelper();
        final FirebaseAuth myAuth=authHelper.getAuth();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        myAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = myAuth.getCurrentUser();
                            FireStoreHelper fireStoreHelper=new FireStoreHelper(getApplicationContext());
                            fireStoreHelper.checkUser(user);
                            progressBar.setVisibility(View.GONE);
                            button.setEnabled(true);
                            updateUI();
                        } else {
                            try{
                                throw task.getException();}
                                catch (NetworkErrorException e){
                                    Toast.makeText(GoogleLogin.this, "No network at the moment", Toast.LENGTH_SHORT).show();
                                }
                            catch (Exception e) {
                                progressBar.setVisibility(View.GONE);
                                button.setEnabled(true);
                                Toast.makeText(GoogleLogin.this, "Login failed,try again later", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }
                });
    }

    public void updateUI() {
        Intent intent=new Intent(this, Activity_Home.class);
        startActivity(intent);
        finish();
    }

    public void getPassword(Context context, final int i) {
        if (isNetworkAvailable(context)) {


            firestore.collection("Password").document("myPassword")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot=task.getResult();
                                if (snapshot.exists()){
                                    password=snapshot.getString("value");
                                    passwordReady=1;
                                    progressBar.setVisibility(View.GONE);
                                    if (i==1) {
                                        submit.performClick();
                                    }
                                }
                            }else {
                                try {
                                    progressBar.setVisibility(View.GONE);
                                    throw task.getException();
                                }catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), "Failed to get key", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
        else {
           network_error.show();
        }
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onClick(View v) {
        if (field.getText().toString().equals("")) {
            Toast.makeText(this, "Input Valid Password", Toast.LENGTH_SHORT).show();
        }else {
            if (passwordReady==1) {
                if (field.getText().toString().equals(password)) {
                    button.setEnabled(true);
                    Toast.makeText(this, "Login Button Enabled", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                progressBar.setVisibility(View.VISIBLE);
                getPassword(getApplicationContext(),1);
            }
        }
    }
}
