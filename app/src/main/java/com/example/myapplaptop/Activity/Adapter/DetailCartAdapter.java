package com.example.myapplaptop.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Domain.DetailCart;
import com.example.myapplaptop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class DetailCartAdapter extends RecyclerView.Adapter<DetailCartAdapter.DetailCartViewHolder> {

    private Context context;
    private List<DetailCart> detailCartList;

    public DetailCartAdapter(Context context, List<DetailCart> detailCartList) {
        this.context = context;
        this.detailCartList = detailCartList;
    }

    @NonNull
    @Override
    public DetailCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_cart, parent, false);
        return new DetailCartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailCartViewHolder holder, int position) {
        DetailCart detailCart = detailCartList.get(position);
        holder.bind(detailCart);
    }

    @Override
    public int getItemCount() {
        return detailCartList.size();
    }

    public static class DetailCartViewHolder extends RecyclerView.ViewHolder {

        private TextView laptopNameTextView;
        private TextView quantityTextView;

        public DetailCartViewHolder(@NonNull View itemView) {
            super(itemView);
            laptopNameTextView = itemView.findViewById(R.id.laptopNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
        }

        public void bind(DetailCart detailCart) {
            // Fetch laptop name based on ID_Laptop
            fetchLaptopName(detailCart.getID_Laptop(), laptopName -> {
                laptopNameTextView.setText(laptopName);
                quantityTextView.setText(String.valueOf(detailCart.getQuantity()));
            });
        }

        private void fetchLaptopName(int laptopID, final LaptopNameCallback callback) {
            DatabaseReference laptopRef = FirebaseDatabase.getInstance().getReference("sanpham");
            laptopRef.child(String.valueOf(laptopID)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String laptopName = snapshot.child("Name").getValue(String.class);
                    callback.onCallback(laptopName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }

        private interface LaptopNameCallback {
            void onCallback(String laptopName);
        }
    }
}
