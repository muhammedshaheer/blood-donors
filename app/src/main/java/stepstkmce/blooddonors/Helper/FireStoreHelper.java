package stepstkmce.blooddonors.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class FireStoreHelper {
    FirebaseFirestore firestore;
    Context context;

    public FireStoreHelper(Context context) {
        FirebaseApp.initializeApp(context);
        firestore=FirebaseFirestore.getInstance();
        this.context=context;
    }

    public void checkUser(final FirebaseUser firebaseUser){
        final DocumentReference user_item=firestore.collection("Users").document(firebaseUser.getUid());
        user_item.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot=task.getResult();
                            if (!snapshot.exists()){
                                HashMap<String,Object> user=new HashMap<>();
                                user.put("name",firebaseUser.getDisplayName());
                                user.put("mail",firebaseUser.getEmail());
                                user_item.set(user);
                            }
                        }
                        else {
                            try {
                                task.getException();
                            }catch (Exception e){
                                Toast.makeText(context, "Cannot Complete Registration at the moment", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
