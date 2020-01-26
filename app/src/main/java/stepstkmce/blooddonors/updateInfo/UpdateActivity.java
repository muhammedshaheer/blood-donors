package stepstkmce.blooddonors.updateInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import stepstkmce.blooddonors.Item.Item;
import stepstkmce.blooddonors.R;
import stepstkmce.blooddonors.searchResults.adapters.ViewAdapter;

public class UpdateActivity extends AppCompatActivity implements View.OnKeyListener {
    EditText name;
    RecyclerView recyclerView;
    ViewAdapter adapter;
    ViewAdapter.RecyclerViewClickListener listener;
    List<Item> items=new ArrayList<>();
    FirebaseFirestore firestore;
    ProgressBar progressBar;
    int STATE_UPDATE_ACTIVITY=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        FirebaseApp.initializeApp(getApplicationContext());
        firestore=FirebaseFirestore.getInstance();
        name=findViewById(R.id.l_update_name);
        recyclerView=findViewById(R.id.l_update_rv);
        progressBar=findViewById(R.id.l_update_pb);
        progressBar.setVisibility(View.GONE);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        name.setOnKeyListener(this);
        listener=new ViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("item",items.get(position));
                Intent intent=new Intent(getApplicationContext(),UpdateInfoActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        };
        adapter=new ViewAdapter(getApplicationContext(),items,listener,STATE_UPDATE_ACTIVITY);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        items.clear();
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            progressBar.setVisibility(View.VISIBLE);
            name.setEnabled(false);
            try  {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }

            firestore.collection("Information").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot:task.getResult()) {
                                    String myName= (String) snapshot.get("name");
                                    if (myName.contains(name.getText().toString().toUpperCase())) {
                                        items.add(snapshot.toObject(Item.class));
                                    }
                                    adapter.notifyDataSetChanged();
                                    progressBar.setVisibility(View.GONE);
                                    name.setEnabled(true);
                                }
                            }
                            else {
                                progressBar.setVisibility(View.GONE);
                                name.setEnabled(true);
                                try {
                                    throw task.getException();
                                }catch (FirebaseNetworkException e ) {
                                    Toast.makeText(UpdateActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    Toast.makeText(UpdateActivity.this, "Cannot Complete Action", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });
            return true;
        }
        return false;
    }
}
