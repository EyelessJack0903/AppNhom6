package com.example.myapplaptop.Activity.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Domain.Cart;
import com.example.myapplaptop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class OrderCartAdapter extends RecyclerView.Adapter<OrderCartAdapter.OrderViewHolder> {

    private Context context;
    private List<Cart> cartList;

    public OrderCartAdapter(Context context, List<Cart> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Cart cart = cartList.get(position);

        holder.textViewName.setText("Khách hàng: " + cart.getName());
        holder.textViewAddress.setText("Địa chỉ: " + cart.getAddress());
        holder.textViewPhone.setText("Số điện thoại: " + cart.getSDT());
        holder.textViewOrderId.setText("ID Đơn hàng: " + cart.getID_Cart());

        double totalPrice = cart.getTotal();
        String formattedTotal = String.format("%,.0f VNĐ", totalPrice);
        holder.textViewTotalPrice.setText("Tổng tiền: " + formattedTotal);

        // Lấy ID_Payment từ đối tượng Cart
        int idPayment = cart.getID_Payment();

        DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment").child(String.valueOf(idPayment));
        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String paymentName = snapshot.child("Name").getValue(String.class);
                    holder.textViewPaymentId.setText("Phương thức thanh toán: " + paymentName);
                } else {
                    holder.textViewPaymentId.setText("Phương thức thanh toán: Thanh toán bằng tiền mặt");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.textViewPaymentId.setText("Lỗi khi tải phương thức thanh toán");
            }
        });

        holder.viewMoreButton.setOnClickListener(v -> showPopupDialog(cart.getID_Cart(), holder.itemView.getContext()));
    }
    private void showPopupDialog(String idCart, Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_order_details, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        LinearLayout productsLayout = dialogView.findViewById(R.id.productsLayout);
        Button buttonClose = dialogView.findViewById(R.id.buttonClose);

        DatabaseReference detailCartRef = FirebaseDatabase.getInstance().getReference("detail_cart");
        detailCartRef.orderByChild("id_Cart").equalTo(idCart).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int idLaptop = dataSnapshot.child("id_Laptop").getValue(Integer.class);
                    int quantity = dataSnapshot.child("quantity").getValue(Integer.class);

                    DatabaseReference laptopRef = FirebaseDatabase.getInstance().getReference("sanpham").child(String.valueOf(idLaptop));
                    laptopRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot laptopSnapshot) {
                            if (laptopSnapshot.exists()) {
                                String laptopName = laptopSnapshot.child("Name").getValue(String.class);
                                // Create a TextView dynamically for each product
                                TextView productTextView = new TextView(context);
                                productTextView.setLayoutParams(new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                productTextView.setText(String.format("%s        x%d", laptopName, quantity));
                                productTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                productTextView.setTextColor(ContextCompat.getColor(context, R.color.black));

                                // Add the TextView to the productsLayout
                                productsLayout.addView(productTextView);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });

        buttonClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewAddress, textViewPhone, textViewPaymentId, textViewOrderId, textViewTotalPrice;
        TextView viewMoreButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewPhone = itemView.findViewById(R.id.textViewPhone);
            textViewPaymentId = itemView.findViewById(R.id.textViewPaymentId);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewTotalPrice = itemView.findViewById(R.id.textViewTotalPrice);
            viewMoreButton = itemView.findViewById(R.id.ViewMoreBTN);
        }
    }
}

