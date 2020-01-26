package stepstkmce.blooddonors.searchResults.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import stepstkmce.blooddonors.Helper.FontHelper;
import stepstkmce.blooddonors.Item.Item;
import stepstkmce.blooddonors.R;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.MyViewAdapter> {
    LayoutInflater inflater;
    Context context;
    List<Item> items;
    RecyclerViewClickListener mListener;
    int state;
    FontHelper helper;


    public ViewAdapter(Context context,List<Item> items,RecyclerViewClickListener listener,int state) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items=items;
        this.state=state;
        inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mListener=listener;
        helper=new FontHelper(context);
    }

    @NonNull
    @Override
    public MyViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View row;
        row=inflater.inflate(R.layout.search_row,parent,false);
        return new MyViewAdapter(row,mListener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewAdapter holder, final int position) {
           if (position%2==0) {
               holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.container_bg_light));
           }else{
               holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.container_bg_dark));
           }
           holder.name.setText(items.get(position).getName().toUpperCase());
           holder.branch.setText("Branch :"+items.get(position).getBranch().toUpperCase());
           holder.year.setText("Year :"+items.get(position).getYear());

           holder.canDonate.setVisibility(View.GONE);
           helper.setTypeFaceText(holder.name,"PTbold");
           helper.setTypeFaceText(holder.branch,"PTbold");
           helper.setTypeFaceText(holder.year,"PTbold");

           if (state==1) {
               holder.call.setVisibility(View.GONE);
               holder.edit.setVisibility(View.GONE);
           }

           if (items.get(position).getDonated() && state==0) {
               Date cal = Calendar.getInstance().getTime();
               SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");

               DateTime lastDate=new DateTime(items.get(position).getLast());
               DateTime currentDate=new DateTime(cal);
               int days= Days.daysBetween(lastDate,currentDate).getDays();

               if (days<90) {
                   int remaining=90-days;
                   int remMonths=remaining/30;
                   int remDays=remaining%30;
                   holder.canDonate.setVisibility(View.VISIBLE);
                   if (remMonths==0){
                       holder.canDonate.setText("Can Donate only after "+remDays+" days");
                   }else {
                       holder.canDonate.setText("Can Donate only after "+remMonths+" months "+remDays+" days");
                   }

               }
           }

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MyViewAdapter extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView name;
        TextView branch;
        TextView year;
        TextView canDonate;
        ImageButton call;
        ImageButton edit;
        RecyclerViewClickListener clickListener;

        public MyViewAdapter(View itemView,RecyclerViewClickListener listener) {
            super(itemView);
            clickListener=listener;
            cardView =(itemView.findViewById(R.id.l_row_container));
            name=(itemView.findViewById(R.id.l_row_name));
            branch=(itemView.findViewById(R.id.l_row_branch));
            year=(itemView.findViewById(R.id.l_row_year));
            canDonate=itemView.findViewById(R.id.l_row_dtext);
            call=itemView.findViewById(R.id.l_row_call);
            edit=itemView.findViewById(R.id.l_row_settings);
            call.setOnClickListener(this);
            itemView.setOnClickListener(this);
            edit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v==call) {
                clickListener.onClick(call,getAdapterPosition());
            }else if (v==edit) {
                clickListener.onClick(edit,getAdapterPosition());
            }
            else {
                clickListener.onClick(itemView,getAdapterPosition());
            }

        }
    }

    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}
