package com.example.myapplaptop.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class LaptopListAdapter extends RecyclerView.Adapter<LaptopListAdapter.ViewHolder> {
    private ArrayList<Laptops> items;
    private Context context;
    private Map<Integer, String> brandNameMap;

    public LaptopListAdapter(ArrayList<Laptops> items, Map<Integer, String> brandNameMap) {
        this.items = items;
        this.brandNameMap = brandNameMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_list_laptop, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laptops laptop = items.get(position);
        holder.titleTxt.setText(laptop.getName());
        holder.brandTxt.setText(brandNameMap.get(laptop.getID_TH()));

        // Format the price
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(laptop.getPrice());
        holder.priceTxt.setText(formattedPrice);

        holder.rateTxt.setText(String.valueOf(laptop.getStar()));

        Glide.with(context)
                .load(laptop.getImage())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, rateTxt, brandTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            rateTxt = itemView.findViewById(R.id.rateTxt);
            brandTxt = itemView.findViewById(R.id.brandTxt);
            pic = itemView.findViewById(R.id.img);
        }
    }
}