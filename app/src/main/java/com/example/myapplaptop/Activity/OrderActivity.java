package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Adapter.OrderCartAdapter;
import com.example.myapplaptop.Activity.Domain.Cart;
import com.example.myapplaptop.R;
import com.google.common.hash.HashingInputStream;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderCartAdapter orderCartAdapter;
    private List<Cart> cartList;
    private ImageView backBtn;

    private Button historyBTN;
    private Button orderBTN;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartList = new ArrayList<>();
        orderCartAdapter = new OrderCartAdapter(this, cartList);
        recyclerView.setAdapter(orderCartAdapter);

        backBtn = findViewById(R.id.backBtn);
        historyBTN = findViewById(R.id.historyBTN);
        orderBTN = findViewById(R.id.orderBTN);

        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        historyBTN.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, OrderListActivity.class);
            startActivity(intent);
            finish();
        });

        orderBTN.setOnClickListener(v -> {
            Intent intent = new Intent(OrderActivity.this, OrderActivity.class);
            startActivity(intent);
            finish();
        });

        // Lấy người dùng hiện tại từ Firebase Authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Load dữ liệu đơn hàng từ Firebase
        loadOrdersFromFirebase();
    }

    private void loadOrdersFromFirebase() {
        if (currentUser == null) {
            // Nếu người dùng hiện tại là null, không thực hiện truy vấn
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("cart");

        // Tạo query để lấy dữ liệu từ cart dựa trên id_User
        Query query = cartRef.orderByChild("id_User").equalTo(currentUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                cartList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Cart cart = snapshot.getValue(Cart.class);
                    if (cart != null) {
                        cart.setID_Cart(snapshot.getKey()); // Set id_Cart
                        cartList.add(cart);
                    }
                }
                orderCartAdapter.notifyDataSetChanged();

                // Kiểm tra nếu cartList rỗng thì hiển thị textNoOrders
                if (cartList.isEmpty()) {
                    findViewById(R.id.textNoOrders).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.textNoOrders).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(OrderActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
