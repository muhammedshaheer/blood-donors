package stepstkmce.blooddonors.splashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import stepstkmce.blooddonors.Helper.FireBaseAuthHelper;
import stepstkmce.blooddonors.Helper.FireStoreHelper;
import stepstkmce.blooddonors.R;
import stepstkmce.blooddonors.authentication.GoogleLogin;
import stepstkmce.blooddonors.home.Activity_Home;

public class SplashScreen extends AppCompatActivity {
    List<String> placeList=new ArrayList<>();
    FireStoreHelper fireStoreHelper;
    FireBaseAuthHelper authHelper;
    ProgressBar progressBar;
    Intent intent;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        FirebaseApp.initializeApp(this);

        fireStoreHelper=new FireStoreHelper(getApplicationContext());
        authHelper=new FireBaseAuthHelper();
        progressBar=findViewById(R.id.pb);
        progressBar.setVisibility(View.VISIBLE);
        user=authHelper.getUser();
        if (user==null) {
            intent=new Intent(this, GoogleLogin.class);
        }
        else{

            intent=new Intent(this, Activity_Home.class);
        }
        progressBar.setVisibility(View.GONE);
        startActivity(intent);
        finish();
    }


}
