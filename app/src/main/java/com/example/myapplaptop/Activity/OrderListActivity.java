package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Adapter.OrderListAdapter;
import com.example.myapplaptop.Activity.Domain.DetailCart;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderListAdapter orderListAdapter;
    private List<DetailCart> detailCartList;
    private ImageView backBtn;

    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailCartList = new ArrayList<>();
        orderListAdapter = new OrderListAdapter(this, detailCartList);
        recyclerView.setAdapter(orderListAdapter);

        backBtn = findViewById(R.id.backBtn);

        backBtn.setOnClickListener(v -> {
            // Quay về màn hình chính (HomeActivity)
            Intent intent = new Intent(OrderListActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Kết thúc OrderListActivity sau khi quay lại HomeActivity
        });

        // Lấy người dùng hiện tại từ Firebase Authentication
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // Load dữ liệu đơn hàng từ Firebase
        loadOrdersFromFirebase();

        // Lắng nghe sự kiện khi có dữ liệu mới được thêm vào detail_cart
        listenForNewOrders();
    }

    private void loadOrdersFromFirebase() {
        if (currentUser == null) {
            // Nếu người dùng hiện tại là null, không thực hiện truy vấn
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference detailCartRef = FirebaseDatabase.getInstance().getReference("detail_cart");

        // Tạo query để lấy dữ liệu từ detail_cart dựa trên id_User
        Query query = detailCartRef.orderByChild("id_User").equalTo(currentUser.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                detailCartList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DetailCart detailCart = snapshot.getValue(DetailCart.class);
                    if (detailCart != null) {
                        detailCart.setID_Detail(snapshot.getKey()); // Set ID_Detail
                        detailCartList.add(detailCart);
                        loadProductInfo(detailCart);
                    }
                }
                orderListAdapter.notifyDataSetChanged();

                // Kiểm tra nếu detailCartList rỗng thì hiển thị textNoOrders
                if (detailCartList.isEmpty()) {
                    findViewById(R.id.textNoOrders).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.textNoOrders).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(OrderListActivity.this, "Failed to load orders: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductInfo(final DetailCart detailCart) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("sanpham")
                .child(String.valueOf(detailCart.getID_Laptop() - 1)); // Giảm giá trị ID_Laptop đi 1

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Laptops laptop = snapshot.getValue(Laptops.class); // Sử dụng Laptops để lấy thông tin sản phẩm
                    if (laptop != null) {
                        detailCart.setLaptopName(laptop.getName());
                        detailCart.setPrice(laptop.getPrice());
                        detailCart.setTotal(detailCart.getPrice() * detailCart.getQuantity());
                    } else {
                        Toast.makeText(OrderListActivity.this, "Product does not exist in database.", Toast.LENGTH_SHORT).show();
                    }
                }
                // Notify adapter that data set changed
                orderListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OrderListActivity.this, "Failed to load product information: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listenForNewOrders() {
        DatabaseReference detailCartRef = FirebaseDatabase.getInstance().getReference("detail_cart");

        // Sử dụng ChildEventListener để lắng nghe sự kiện khi có dữ liệu mới được thêm vào detail_cart
        detailCartRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                DetailCart detailCart = snapshot.getValue(DetailCart.class);
                if (detailCart != null) {
                    detailCart.setID_Detail(snapshot.getKey()); // Set ID_Detail
                    detailCart.setID_Laptop(detailCart.getID_Laptop() - 1); // Giảm giá trị ID_Laptop đi 1

                    // Kiểm tra null cho id_User trước khi so sánh
                    if (detailCart.getId_User() != null && detailCart.getId_User().equals(currentUser.getUid())) {
                        if (!detailCartList.contains(detailCart)) {
                            detailCartList.add(detailCart);
                            loadProductInfo(detailCart);
                            orderListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Xử lý khi có sự thay đổi dữ liệu trong detail_cart
                // Đây có thể là nơi bạn cập nhật lại dữ liệu khi có sự thay đổi
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                // Xử lý khi có dữ liệu trong detail_cart bị xóa
                // Đây có thể là nơi bạn xóa dữ liệu khỏi danh sách khi bị xóa từ Firebase
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildName) {
                // Xử lý khi có sự di chuyển dữ liệu trong detail_cart
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle khi có lỗi xảy ra
                Toast.makeText(OrderListActivity.this, "Failed to listen for new orders: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
