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
import com.example.myapplaptop.Activity.Domain.Category;
import com.example.myapplaptop.Activity.ListLaptopsActivity;
import com.example.myapplaptop.R;

import java.util.ArrayList;

public class CategoryBrand extends RecyclerView.Adapter<CategoryBrand.ViewHolder> {
    private ArrayList<Category> items;
    private Context context;

    public CategoryBrand(ArrayList<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CategoryBrand.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View inflate = LayoutInflater.from(context).inflate(R.layout.viewholder_category, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = items.get(position);
        holder.titleTxt.setText(category.getName());

        // Xác định vị trí để đặt background cho hình ảnh
        switch (position % 8) { // Sử dụng % 8 để tránh ArrayIndexOutOfBoundsException
            case 0:
                holder.pic.setBackgroundResource(R.drawable.cat_1_background);
                break;
            case 1:
                holder.pic.setBackgroundResource(R.drawable.cat_2_background);
                break;
            case 2:
                holder.pic.setBackgroundResource(R.drawable.cat_3_background);
                break;
            case 3:
                holder.pic.setBackgroundResource(R.drawable.cat_4_background);
                break;
            case 4:
                holder.pic.setBackgroundResource(R.drawable.cat_5_background);
                break;
            case 5:
                holder.pic.setBackgroundResource(R.drawable.cat_6_background);
                break;
            case 6:
                holder.pic.setBackgroundResource(R.drawable.cat_7_background);
                break;
            case 7:
                holder.pic.setBackgroundResource(R.drawable.cat_8_background);
                break;
        }

        // Sử dụng Glide để tải ảnh từ URL trong cột Logo của bảng thuonghieu
        Glide.with(context)
                .load(category.getLogo()) // Sử dụng URL từ cột Logo
                .transform(new CenterCrop(), new RoundedCorners(15)) // Cắt và làm tròn góc
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ListLaptopsActivity.class);
            intent.putExtra("ID_TH", items.get(position).getID_TH());
            intent.putExtra("Name", items.get(position).getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTxt = itemView.findViewById(R.id.catNametxt);
            pic = itemView.findViewById(R.id.imaCat);
        }
    }
}
