package com.clicbrics.consumer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clicbrics.consumer.R;
import com.clicbrics.consumer.wrapper.PaymentPlanItem;

import java.util.List;

/**
 * Created by Paras on 06-11-2016.
 */
public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentPlanViewHolder> {

    private Context context;
    private List<PaymentPlanItem> items;
    private LayoutInflater vi;

    public PaymentAdapter(Context context, List<PaymentPlanItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public PaymentPlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.e_item_payment_plan, parent, false);
        return new PaymentPlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PaymentPlanViewHolder holder, int position) {
        if (!TextUtils.isEmpty(items.get(position).title)) {
            holder.title.setText(items.get(position).title);
        }
        if (!TextUtils.isEmpty(items.get(position).subtitle)) {
            holder.subtitle.setText(items.get(position).subtitle);
        }

        if(items.size()==1){
            holder.upper_line.setVisibility(View.INVISIBLE);
            holder.bottom_line.setVisibility(View.INVISIBLE);
            return;
        }
        if (position == 0) {
            holder.upper_line.setVisibility(View.INVISIBLE);
        }
        if (position == (items.size()-1)) {
            holder.bottom_line.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class PaymentPlanViewHolder extends RecyclerView.ViewHolder{
        TextView title, subtitle;
        View upper_line, bottom_line;
        public PaymentPlanViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subtitle = (TextView) itemView.findViewById(R.id.subtitle);
            upper_line = itemView.findViewById(R.id.upper_line);
            bottom_line = itemView.findViewById(R.id.bottom_line);
        }
    }
}