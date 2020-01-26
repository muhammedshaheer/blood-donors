package stepstkmce.blooddonors.home;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stepstkmce.blooddonors.Helper.FireStoreHelper;
import stepstkmce.blooddonors.Helper.FontHelper;
import stepstkmce.blooddonors.Item.Item;
import stepstkmce.blooddonors.R;
import stepstkmce.blooddonors.searchResults.Activity_SearchList;
import stepstkmce.blooddonors.updateInfo.UpdateActivity;

public class Activity_Home extends AppCompatActivity implements View.OnKeyListener {
    Spinner spinner;
    AutoCompleteTextView places;
    Button submit;
    FireStoreHelper fireStoreHelper;
    FirebaseFirestore firestore;
    ProgressBar progressBar;
    List<String> placeList= new ArrayList<>();
    Button update;

    int k;
    int m;
    List<Item> sorted=new ArrayList<>();
    private int v=0;
    private String TAG="ERROR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__home);
        placeList= Arrays.asList(getResources().getStringArray(R.array.place_list));
        spinner=findViewById(R.id.l_home_sp);
        places=findViewById(R.id.l_home_place);
        submit=findViewById(R.id.l_home_btnSubmit);
        progressBar=findViewById(R.id.l_home_progressBar);
        progressBar.setVisibility(View.GONE);
        update=findViewById(R.id.l_home_update);

        FirebaseApp.initializeApp(getApplicationContext());
        firestore= FirebaseFirestore.getInstance();

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.select_dialog_item,placeList);

        FontHelper fontHelper=new FontHelper(getApplicationContext());
        fireStoreHelper=new FireStoreHelper(getApplicationContext());
        fontHelper.setTypeFaceText(places,"PTreg");
        fontHelper.setTypeFaceText(submit,"PTbold");

        places.setThreshold(1);
        places.setAdapter(adapter);
        places.setOnKeyListener(this);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(), UpdateActivity.class);
                    startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
                else {
                    submit.setEnabled(false);
                    String bloodType=(String) spinner.getSelectedItem();
                    String placeType=places.getText().toString().toUpperCase();
                    try  {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                    searchList(bloodType,placeType);
                }

            }
        });
    }

    public void searchList(String blood,String place){
        progressBar.setVisibility(View.VISIBLE);
        k=0;
        m=0;
        sorted.clear();
        if (place.equals("")){
            firestore.collection("Information")
                    .whereEqualTo("group",blood.toUpperCase())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    Item item=documentSnapshot.toObject(Item.class);
                                    sorted.add(k,item);
                                    k++;
                                    if (item.getDonated()) {
                                        m++;
                                    }
                                }

                                progressBar.setVisibility(View.GONE);
                                updateUI(sorted.size());
                            }else {
                                progressBar.setVisibility(View.GONE);
                                try {
                                    throw task.getException();
                                }
                                catch (NetworkErrorException e) {
                                    Toast.makeText(getApplicationContext(),"No internet available",Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),"Cannot complete request at the time",Toast.LENGTH_SHORT).show();

                                }
                            }
                            submit.setEnabled(true);
                        }
                    });
        }
        else {
            firestore.collection("Information")
                    .whereEqualTo("place",place).whereEqualTo("group",blood.toUpperCase())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    Item item=documentSnapshot.toObject(Item.class);
                                    sorted.add(k,item);
                                    k++;
                                    if (item.getDonated()) {
                                        m++;
                                    }
                                }

                                progressBar.setVisibility(View.GONE);
                                updateUI(sorted.size());
                            }else {
                                progressBar.setVisibility(View.GONE);
                                try {
                                    throw task.getException();
                                }
                                catch (NetworkErrorException e) {
                                    Toast.makeText(getApplicationContext(),"No internet available",Toast.LENGTH_SHORT).show();
                                }
                                catch (Exception e) {
                                    Toast.makeText(getApplicationContext(),"Cannot complete request at the time",Toast.LENGTH_SHORT).show();

                                }
                            }
                        }
                    });
        }


    }

    private void updateUI(int size) {
        if (size==0) {
            Toast.makeText(getApplicationContext(),"No matches obtained",Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent=new Intent(getApplicationContext(), Activity_SearchList.class);
            Bundle bundle=new Bundle();
            bundle.putSerializable("items", (Serializable) sorted);
            intent.putExtras(bundle);
            intent.putExtra("size",sorted.size());
            intent.putExtra("non",m);

            startActivity(intent);
            submit.setEnabled(true);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
            String bloodType=(String) spinner.getSelectedItem();
            String placeType=places.getText().toString().toUpperCase();
                try  {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {

                }
                searchList(bloodType,placeType);

            return true;
        }
        return false;
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void deleteBase() {
        progressBar.setVisibility(View.VISIBLE);
        firestore.collection("Information")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot:task.getResult()) {
                                snapshot.getReference().delete();
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                        else {
                            try {
                                throw task.getException();
                            }catch (Exception e){
                                Log.e(TAG, "onComplete: ",e.getCause() );
                            }
                        }
                    }
                });
    }

    public class Datahandler extends AsyncTask<String,Void,Integer> {

        public List<Item> items=new ArrayList<>();
        int v=0;

        @Override
        protected Integer doInBackground(String... strings) {
            Integer integer=0;
            HttpURLConnection connection;
            URL url;
            try {
                url=new URL(strings[0]);
                connection=(HttpURLConnection) url.openConnection();
                int statusCode=connection.getResponseCode();
                if (statusCode==200) {
                    String line;
                    BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder=new StringBuilder();
                    while ((line=reader.readLine())!=null) {
                        stringBuilder.append(line).append('\n');
                    }
                    parseResult(stringBuilder.toString());
                    integer=1;
                }else {
                    integer=0;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return integer;
        }

        private void parseResult(String string) {
            try {
                JSONArray init_Array=new JSONArray(string);
                for (int i=0;i<init_Array.length();i++) {
                    JSONObject item_obj=init_Array.getJSONObject(i);
                    Item item=new Item();
                    if (item_obj.getString("PLACE").equals("") || item_obj.getString("BLOOD GROUP").equals("ROLL NO")|| item_obj.getString("BLOOD GROUP").equals("") || item_obj.getString("PHONE NUMBER").equals("")
                            || item_obj.getString("NAME").equals("")) {

                    }else {

                        item.setAdm(item_obj.getString("ROLL NO").toUpperCase());
                        item.setBranch(item_obj.getString("BRANCH").toUpperCase());
                        item.setGroup(item_obj.getString("BLOOD GROUP").toUpperCase());
                        item.setPlace(item_obj.getString("PLACE").toUpperCase());
                        item.setYear(item_obj.getString("YEAR"));
                        item.setNumber(item_obj.getString("PHONE NUMBER"));
                        item.setName(item_obj.getString("NAME").toUpperCase());
                        item.setDonated(false);
                        item.setLast(null);
                        items.add(item);
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if (integer==1) {
                Toast.makeText(getApplicationContext(), "h"+items.size(), Toast.LENGTH_SHORT).show();
                for (Item item: items) {
                    firestore.collection("Information").add(item)
                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (!task.isSuccessful()) {
                                        try {
                                            Toast.makeText(getApplicationContext(), "Cannot complete", Toast.LENGTH_SHORT).show();
                                            v++;
                                        }catch (Exception e) {

                                        }

                                    }
                                }
                            });

                }
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "failed to process :"+v, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Process Failed", Toast.LENGTH_SHORT).show();
            }
        }

    }


}
