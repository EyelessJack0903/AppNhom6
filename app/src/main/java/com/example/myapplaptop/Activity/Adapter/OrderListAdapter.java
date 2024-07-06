package com.example.myapplaptop.Activity.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplaptop.Activity.Domain.DetailCart;
import com.example.myapplaptop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import android.widget.TextView;
import android.widget.ImageView;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderViewHolder> {

    private Context mContext;
    private List<DetailCart> mDetailCartList;
    private DatabaseReference detailCartRef; // Reference to Firebase 'detail_cart' node

    public OrderListAdapter(Context context, List<DetailCart> detailCartList) {
        mContext = context;
        mDetailCartList = detailCartList;
        detailCartRef = FirebaseDatabase.getInstance().getReference().child("detail_cart"); // Initialize Firebase reference
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order_list, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, int position) {
        final DetailCart detailCart = mDetailCartList.get(position);

        // Display ID_Detail
        holder.orderIdTextView.setText("ID: " + detailCart.getID_Detail());

        // Retrieve data from 'sanpham' based on ID_Laptop
        DatabaseReference laptopRef = FirebaseDatabase.getInstance().getReference("sanpham")
                .child(String.valueOf(detailCart.getID_Laptop()));
        laptopRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String laptopName = snapshot.child("Name").getValue(String.class);
                    Double laptopPrice = snapshot.child("Price").getValue(Double.class);
                    String laptopImage = snapshot.child("Image").getValue(String.class);

                    holder.productNameTextView.setText(laptopName != null ? laptopName : "Unknown");
                    holder.productQuantityTextView.setText("Số lượng: " + detailCart.getQuantity());

                    if (laptopPrice != null) {
                        double totalPrice = laptopPrice * detailCart.getQuantity();
                        holder.totalPriceTextView.setText("Tổng giá: " + convertToVND(totalPrice));
                    }

                    Glide.with(mContext).load(laptopImage).into(holder.productImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Không thể lấy dữ liệu laptop từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "View More" click event
        holder.viewMoreTextView.setTag(position); // Set item position as tag
        holder.viewMoreTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onViewMoreClicked(v, detailCart.getID_Cart());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailCartList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, productNameTextView, productQuantityTextView, totalPriceTextView;
        ImageView productImageView;
        View viewMoreTextView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderHeader);
            productNameTextView = itemView.findViewById(R.id.productName);
            productQuantityTextView = itemView.findViewById(R.id.productQuantity);
            totalPriceTextView = itemView.findViewById(R.id.totalPrice);
            productImageView = itemView.findViewById(R.id.productImage);
            viewMoreTextView = itemView.findViewById(R.id.viewMoreText);
        }
    }

    // Method to convert price to VND format
    private String convertToVND(double price) {
        return String.format("%,.0f VNĐ", price);
    }

    private void onViewMoreClicked(final View view, String idCart) {
        // Retrieve data from Firebase 'cart' based on id_Cart
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart").child(idCart);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String address = snapshot.child("address").getValue(String.class);
                    String sdt = snapshot.child("sdt").getValue(String.class);
                    Integer idPayment = snapshot.child("id_Payment").getValue(Integer.class);

                    // Convert idPayment to String
                    String idPaymentString = String.valueOf(idPayment);

                    // Show popup dialog
                    showPopupDialog(name, address, sdt, idPaymentString);
                } else {
                    Toast.makeText(mContext, "Không tìm thấy thông tin cart cho đơn hàng này", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(mContext, "Lỗi khi truy xuất dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPopupDialog(String name, String address, String sdt, String idPayment) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.custom_popup_layout, null);
        dialogBuilder.setView(dialogView);

        TextView txvName = dialogView.findViewById(R.id.txvName);
        TextView txvAddress = dialogView.findViewById(R.id.txvAddress);
        TextView txvSDT = dialogView.findViewById(R.id.txvSDT);
        TextView txvIDPayment = dialogView.findViewById(R.id.txvIDPayment);

        txvName.setText("Khách hàng: " + name);
        txvAddress.setText("Địa chỉ: " + address);
        txvSDT.setText("Số điện thoại: " + sdt);

        // Kiểm tra nếu idPayment là 4 thì hiển thị "Thanh toán bằng tiền mặt"
        if (idPayment.equals("4")) {
            txvIDPayment.setText("Phương thức thanh toán: Thanh toán bằng tiền mặt");
        } else {
            // Nếu không phải, thực hiện lấy từ Firebase
            DatabaseReference paymentRef = FirebaseDatabase.getInstance().getReference("payment").child(idPayment);
            paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String paymentName = snapshot.child("Name").getValue(String.class);
                        if (paymentName != null) {
                            txvIDPayment.setText("Phương thức thanh toán: " + paymentName);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(mContext, "Lỗi khi truy xuất dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        dialogBuilder.setPositiveButton("Đóng", null); // OK button to close dialog

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

}
