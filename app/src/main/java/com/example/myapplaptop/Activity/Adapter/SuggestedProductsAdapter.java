package com.example.myapplaptop.Activity.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplaptop.Activity.DetailActivity;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class SuggestedProductsAdapter extends RecyclerView.Adapter<SuggestedProductsAdapter.ViewHolder> {

    private List<Laptops> laptopsList;
    private Context context;

    public SuggestedProductsAdapter(List<Laptops> laptopsList) {
        this.laptopsList = laptopsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggested_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laptops laptop = laptopsList.get(position);

        holder.titleTxt.setText(laptop.getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceFormatted = format.format(laptop.getPrice());
        holder.priceTxt.setText(priceFormatted);

        Glide.with(context)
                .load(laptop.getImage())
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object", laptop);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return laptopsList.size();
    }

    public void updateProducts(List<Laptops> laptops) {
        this.laptopsList = laptops;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.priceTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
