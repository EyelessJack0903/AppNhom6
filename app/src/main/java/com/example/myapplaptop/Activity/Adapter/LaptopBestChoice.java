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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplaptop.Activity.DetailActivity;
import com.example.myapplaptop.Activity.Domain.Category;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class LaptopBestChoice extends RecyclerView.Adapter<LaptopBestChoice.ViewHolder> {
    private ArrayList<Laptops> items;
    private Context context;
    private ArrayList<Category> categoryList; // Thêm danh sách thương hiệu

    // Sửa đổi constructor để nhận danh sách Category
    public LaptopBestChoice(ArrayList<Laptops> items, ArrayList<Category> categoryList) {
        this.items = items;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_best_deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laptops laptop = items.get(position);

        holder.titleTxt.setText(laptop.getName());
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String priceFormatted = format.format(laptop.getPrice());
        holder.priceTxt.setText(priceFormatted);

        // Lấy tên thương hiệu từ ID_TH
        String brandName = getBrandNameById(laptop.getID_TH());
        holder.brandTxt.setText("Brand: " + brandName);

        holder.starTxt.setText(String.valueOf(laptop.getStar()));

        Glide.with(context)
                .load(laptop.getImage())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("object",items.get(position));
            context.startActivity(intent);

        });
    }

    // Phương thức để lấy tên thương hiệu từ ID_TH
    private String getBrandNameById(int id) {
        for (Category category : categoryList) {
            if (category.getID_TH() == id) {
                return category.getName();
            }
        }
        return "";
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt, priceTxt, starTxt, brandTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.titleTxt);
            priceTxt = itemView.findViewById(R.id.s);
            starTxt = itemView.findViewById(R.id.starTxt);
            brandTxt = itemView.findViewById(R.id.BrandTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }
}
