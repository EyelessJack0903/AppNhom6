package com.example.myapplaptop.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Adapter.OrderListAdapter;
import com.example.myapplaptop.Activity.Domain.Cart;
import com.example.myapplaptop.Activity.Domain.DetailCart;
import com.example.myapplaptop.R;
import com.google.firebase.auth.FirebaseAuth;
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
    private OrderListAdapter adapter;
    private List<Cart> cartList;
    private TextView textView;

    private DatabaseReference cartRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        String userUid = mAuth.getCurrentUser().getUid();

        cartRef = FirebaseDatabase.getInstance().getReference().child("cart");
        Query query = cartRef.orderByChild("id_User").equalTo(userUid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        cartList.add(cart);
                    }
                    adapter = new OrderListAdapter(OrderListActivity.this, cartList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderListActivity", "Error fetching data", error.toException());
            }
        });
    }
}
