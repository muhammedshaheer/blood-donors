package stepstkmce.blooddonors.searchResults;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import stepstkmce.blooddonors.Helper.FontHelper;
import stepstkmce.blooddonors.Item.Item;
import stepstkmce.blooddonors.R;

public class Activity_Search_Detailed extends AppCompatActivity {
    TextView name;
    TextView number;
    TextView adm;
    TextView branch;
    TextView place;
    TextView year;
    TextView last;
    ImageButton dialer;
    TextView notAllowed;
    Item item;
    Boolean donated;
    int days;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Date cal =Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

        Bundle bundle=getIntent().getExtras();
        item= (Item) bundle.getSerializable("item");
        setContentView(R.layout.activity__search__detailed);

        name=findViewById(R.id.l_details_name);
        number=findViewById(R.id.l_details_number);
        adm=findViewById(R.id.l_details_adm);
        branch=findViewById(R.id.l_d_b);
        place=findViewById(R.id.l_d_p);
        year=findViewById(R.id.l_d_y);
        last=findViewById(R.id.l_d_l);
        notAllowed=findViewById(R.id.l_details_not);
        notAllowed.setVisibility(View.GONE);
        dialer=findViewById(R.id.l_details_dialer);
        addFont();

        donated=item.getDonated();

        if (donated) {
            String LastdateString = sdf.format(item.getLast());
            last.setText("Last Donated : "+LastdateString);

            DateTime lastDate=new DateTime(item.getLast());
            DateTime currentDate=new DateTime(cal);
            int days=Days.daysBetween(lastDate,currentDate).getDays();
            if (days<90) {
                int remaining=90-days;
                int remMonths=remaining/30;
                int remDays=remaining%30;
                notAllowed.setVisibility(View.VISIBLE);
                if (remMonths==0){
                    notAllowed.setText("Can Donate only after "+remDays+" days");
                }else {
                    notAllowed.setText("Can Donate only after "+remMonths+" months "+remDays+" days");
                }

            }
        }
        else {
            last.setText("not donated yet");
        }

        name.setText(item.getName());
        number.setText(item.getNumber());
        branch.setText("Branch :"+item.getBranch());
        adm.setText(item.getAdm());
        place.setText("Place :"+item.getPlace());
        year.setText("Year :"+item.getYear());

        dialer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+item.getNumber()));
                startActivity(intent);
            }
        });
    }

    public void addFont(){
        FontHelper fontHelper=new FontHelper(getApplicationContext());
        fontHelper.setTypeFaceText(name,"ptbold");
        fontHelper.setTypeFaceText(number,"ptbold");
        fontHelper.setTypeFaceText(adm,"ptbold");
        fontHelper.setTypeFaceText(branch,"ptbold");
        fontHelper.setTypeFaceText(place,"ptbold");
        fontHelper.setTypeFaceText(year,"ptbold");
        fontHelper.setTypeFaceText(last,"ptbold");
    }
}
