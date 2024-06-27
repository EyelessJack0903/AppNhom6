package com.example.myapplaptop.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Domain.Cart;
import com.example.myapplaptop.Activity.Domain.DetailCart;
import com.example.myapplaptop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private Context context;
    private List<Cart> cartList;

    public OrderListAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Cart cart = cartList.get(position);
        holder.bind(cart);
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        private TextView orderHeader;
        private TextView customerName;
        private TextView customerPhone;
        private TextView customerAddress;
        private RecyclerView detailRecyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderHeader = itemView.findViewById(R.id.orderHeader);
            customerName = itemView.findViewById(R.id.customerName);
            customerPhone = itemView.findViewById(R.id.customerPhone);
            customerAddress = itemView.findViewById(R.id.customerAddress);
            detailRecyclerView = itemView.findViewById(R.id.detailRecyclerView);
        }

        public void bind(Cart cart) {
            orderHeader.setText(String.format("Order ID: %s", cart.getID_Cart()));
            customerName.setText(String.format("Họ tên: %s", cart.getName()));
            customerPhone.setText(String.format("SDT: %s", cart.getSDT()));
            customerAddress.setText(String.format("Địa chỉ: %s", cart.getAddress()));

            // Fetch and bind detail cart list
            DatabaseReference detailCartRef = FirebaseDatabase.getInstance().getReference("detail_cart");
            detailCartRef.orderByChild("id_Cart").equalTo(cart.getID_Cart())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<DetailCart> detailCartList = new ArrayList<>();
                            for (DataSnapshot detailSnapshot : snapshot.getChildren()) {
                                DetailCart detailCart = detailSnapshot.getValue(DetailCart.class);
                                detailCartList.add(detailCart);
                            }
                            setupDetailRecyclerView(detailCartList);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle database error
                        }
                    });
        }

        private void setupDetailRecyclerView(List<DetailCart> detailCartList) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            detailRecyclerView.setLayoutManager(layoutManager);
            DetailCartAdapter adapter = new DetailCartAdapter(context, detailCartList);
            detailRecyclerView.setAdapter(adapter);
        }
    }
}
