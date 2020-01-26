package stepstkmce.blooddonors.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FireBaseAuthHelper {
   FirebaseAuth mAuth;
   FirebaseUser mUser;

    public FireBaseAuthHelper() {
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
    }

    public Boolean checkUser(){
        return mUser != null;
    }

    public FirebaseUser getUser(){
        return mUser;
    }

    public FirebaseAuth getAuth(){
        return mAuth;
    }
}
