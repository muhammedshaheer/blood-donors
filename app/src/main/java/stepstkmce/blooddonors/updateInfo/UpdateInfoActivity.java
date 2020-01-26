package stepstkmce.blooddonors.updateInfo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stepstkmce.blooddonors.Helper.FontHelper;
import stepstkmce.blooddonors.Item.Item;
import stepstkmce.blooddonors.R;
import stepstkmce.blooddonors.home.Activity_Home;

public class UpdateInfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Spinner spinner;
    TextView textView;
    Button datePick;
    Button submit;
    List<String> years=new ArrayList<>();
    FirebaseFirestore firestore;
    Item item;
    String year;
    Boolean donated;
    int position;
    int mYear,mMonth,mDay;
    Date lastDate;
    DatePickerDialog.OnDateSetListener listener;
    Calendar c;
    DocumentReference reference;
    ProgressBar progressBar;
    TextView year_title;
    TextView last_title;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        FirebaseApp.initializeApp(getApplicationContext());
        FontHelper helper=new FontHelper(getApplicationContext());
        firestore=FirebaseFirestore.getInstance();

        Bundle bundle=getIntent().getExtras();
        if (bundle!=null) {
            item= (Item) bundle.getSerializable("item");
        }


        donated=item.getDonated();
        year_title=findViewById(R.id.textView1);
        last_title=findViewById(R.id.textView2);
        spinner=findViewById(R.id.l_info_branch);
        textView =findViewById(R.id.l_info_view);
        datePick=findViewById(R.id.l_info_last);
        submit=findViewById(R.id.l_info_submit);
        progressBar=findViewById(R.id.l_info_pb);
        progressBar.setVisibility(View.GONE);
        years= Arrays.asList(getResources().getStringArray(R.array.year));
        year=item.getYear();
        position=years.indexOf(year);
        c=Calendar.getInstance();
        spinner.setSelection(position);
        helper.setTypeFaceBtn(submit,"PTbold");
        helper.setTypeFaceBtn(datePick,"PTbold");
        helper.setTypeFaceText(year_title,"PTbold");
        helper.setTypeFaceText(last_title,"PTbold");
        listener=this;

        if (donated) {
            lastDate=item.getLast();
            textView.setText(setDate(item.getLast()));
        }
        else {
            textView.setText("not donated");
        }

        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog=new DatePickerDialog(UpdateInfoActivity.this,listener,mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable(getApplicationContext())) {

                }else {
                    String branch= (String) spinner.getSelectedItem();
                    if (!years.contains(year)) {
                        Toast.makeText(UpdateInfoActivity.this, "Enter valid branch", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        submit.setEnabled(false);
                        item.setYear(year);
                        item.setLast(lastDate);
                        item.setDonated(true);

                        Query myRef=firestore.collection("Information").whereEqualTo("adm",item.getAdm());
                        myRef.get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            progressBar.setVisibility(View.VISIBLE);
                                            for (QueryDocumentSnapshot snapshot: task.getResult()) {
                                                reference=snapshot.getReference();
                                            }
                                            reference.set(item)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(UpdateInfoActivity.this, "Successfully updated", Toast.LENGTH_SHORT).show();
                                                                Intent intent=new Intent(getApplicationContext(), Activity_Home.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }else {
                                                                progressBar.setVisibility(View.GONE);
                                                                try {
                                                                    throw task.getException();
                                                                }catch (Exception e) {
                                                                    Toast.makeText(UpdateInfoActivity.this, "Cannot update at the moment", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    });
                                        }
                                        else {
                                            progressBar.setVisibility(View.GONE);
                                            try {
                                                task.getException();
                                            }
                                            catch (Exception e){
                                                Toast.makeText(UpdateInfoActivity.this, "Cannot update at the moment", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });

    }
    public  String setDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(item.getLast());
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        textView.setText((month+1) + "-" + dayOfMonth + "-" + year);
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth-1);
        c.set(Calendar.HOUR,12);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        lastDate=c.getTime();
    }

    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
