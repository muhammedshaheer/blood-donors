package stepstkmce.blooddonors.searchResults;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import stepstkmce.blooddonors.Helper.FontHelper;
import stepstkmce.blooddonors.Item.Item;
import stepstkmce.blooddonors.R;
import stepstkmce.blooddonors.searchResults.adapters.ViewAdapter;
import stepstkmce.blooddonors.updateInfo.UpdateInfoActivity;

public class Activity_SearchList extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Item> itemList;
    TextView count_total;
    TextView count_non;
    public static ViewAdapter adapter;
    ViewAdapter.RecyclerViewClickListener listener;
    int STATE_SEARCH_ACTIVITY=0;
    int size;
    int non;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle=getIntent().getExtras();
        FontHelper helper=new FontHelper(getApplicationContext());
        itemList=(List<Item>) bundle.getSerializable("items");
        size=getIntent().getIntExtra("size",0);
        non=getIntent().getIntExtra("non",0);

        setContentView(R.layout.activity__search_list);
        recyclerView=findViewById(R.id.l_search_list);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        count_total=findViewById(R.id.l_search_count_total);
        count_non=findViewById(R.id.l_search_count_non);
        count_total.setText("TOTAL COUNT :"+Integer.toString(size));
        count_non.setText("CANNOT DONATE :"+Integer.toString(non));
        helper.setTypeFaceText(count_non,"PTreg");
        helper.setTypeFaceText(count_total,"PTreg");

        listener=new ViewAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (view.getId()==R.id.l_row_settings) {
                    Bundle bundle1=new Bundle();
                    bundle1.putSerializable("item",itemList.get(position));
                    Intent intent=new Intent(getApplicationContext(),UpdateInfoActivity.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
                else if (view.getId()==R.id.l_row_call){
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+itemList.get(position).getNumber()));
                    startActivity(intent);
                }else {
                    Bundle bundle1=new Bundle();
                    bundle1.putSerializable("item",itemList.get(position));
                    Intent intent=new Intent(getApplicationContext(),Activity_Search_Detailed.class);
                    intent.putExtras(bundle1);
                    startActivity(intent);
                }
            }
        };

        adapter=new ViewAdapter(getApplicationContext(),itemList,listener,STATE_SEARCH_ACTIVITY);
        recyclerView.setAdapter(adapter);
    }
}
