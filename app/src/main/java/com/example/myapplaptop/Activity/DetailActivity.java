package com.example.myapplaptop.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplaptop.Activity.Adapter.SuggestedProductsAdapter;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.Activity.Domain.Specifications;
import com.example.myapplaptop.Activity.Helper.ManagmentCart;
import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivityDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Laptops object;
    private int num = 1;
    private int quantity = 1;
    private TextView numTxt, totalAmountTxt, descriptionTxt, toggleButton, specsTxt;
    private ManagmentCart managmentCart;
    private RecyclerView suggestedProductsRecyclerView;
    private SuggestedProductsAdapter suggestedProductsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail); // Set content view
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        // Initialize binding object after setContentView
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // Set binding root as content view

        getIntentExtra();
        setVariable();
        setRecommend();

        // Enable edge-to-edge layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        numTxt = findViewById(R.id.numTxt);
        totalAmountTxt = findViewById(R.id.totalTxt);
        descriptionTxt = findViewById(R.id.descriptionTxt);
        toggleButton = findViewById(R.id.toggleButton);
        specsTxt = findViewById(R.id.specsTxt);
        suggestedProductsRecyclerView = findViewById(R.id.viewrecommend);

        // Set initial quantity text
        numTxt.setText(String.valueOf(quantity));

        // Set initial total amount
        updateTotalAmount();

        // Set click listeners for buttons
        findViewById(R.id.minusBtn).setOnClickListener(v -> updateQuantity(false));
        findViewById(R.id.textView6).setOnClickListener(v -> updateQuantity(true));
        findViewById(R.id.addBtn).setOnClickListener(v -> addToCart());
        toggleButton.setOnClickListener(v -> toggleDescription());

        // Fetch and display technical specifications
        fetchSpecifications();

        // Initialize suggested products
        initSuggestedProducts();
        fetchSuggestedProducts();
    }

    private void setRecommend() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("sanpham");
        ArrayList<Laptops> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Laptops.class));
                    }
                    // Khởi tạo Adapter và set cho RecyclerView
                    binding.viewrecommend.setLayoutManager(new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL,false));
                    RecyclerView.Adapter adapter = new SuggestedProductsAdapter(list);
                    binding.viewrecommend.setAdapter(adapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Failed to load recommended products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVariable() {
        managmentCart= new ManagmentCart(this);
        binding.backBtn.setOnClickListener(v -> finish());

        Glide.with(DetailActivity.this)
                .load(object.getImage())
                .into(binding.pic);

        binding.priceText.setText(formatCurrency(object.getPrice()));
        binding.titleTxt.setText(object.getName());
        binding.descriptionTxt.setText(object.getDescription());
        binding.totalTxt.setText(formatCurrency(num * object.getPrice()));

        binding.textView6.setOnClickListener(v -> {
            num=num+1;
            binding.numTxt.setText(num+" ");
            binding.totalTxt.setText("$"+(num* object.getPrice()));
        });
        binding.minusBtn.setOnClickListener(v -> {
            if(num>1){
                num=num-1;
                binding.numTxt.setText(num+"");
                binding.totalTxt.setText("$"+(num*object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object.setNumberInCart(num);
                managmentCart.insertLaptop(object);
                Log.d("CartDebug", "Sản phẩm đã được thêm vào giỏ hàng: " + object.getName());
                Toast.makeText(DetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getIntentExtra() {
        object = (Laptops) getIntent().getSerializableExtra("object");
    }

    // Update product quantity
    private void updateQuantity(boolean isIncrement) {
        if (isIncrement) {
            quantity++;
        } else {
            if (quantity > 0) {
                quantity--;
            }
        }
        numTxt.setText(String.valueOf(quantity));
        updateTotalAmount();
    }

    // Update total amount
    private void updateTotalAmount() {
        double totalAmount = object.getPrice() * quantity;
        totalAmountTxt.setText(formatCurrency(totalAmount));
    }

    // Add product to cart
    private void addToCart() {
        object.setNumberInCart(quantity);
        managmentCart.insertLaptop(object);
        Log.d("CartDebug", "Sản phẩm đã được thêm vào giỏ hàng: " + object.getName());
        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
    }

    // Toggle description text
    private void toggleDescription() {
        if (descriptionTxt.getMaxLines() == 3) {
            descriptionTxt.setMaxLines(Integer.MAX_VALUE);
            toggleButton.setText("Ẩn bớt");
        } else {
            descriptionTxt.setMaxLines(3);
            toggleButton.setText("Xem thêm");
        }
    }

    // Format number as currency in VND
    private String formatCurrency(double amount) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount);
    }

    // Fetch specifications from Firebase and display them
    private void fetchSpecifications() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference specsRef = database.getReference("thongso").child(String.valueOf(object.getID_Laptop()));

        specsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Specifications specs = dataSnapshot.getValue(Specifications.class);
                if (specs != null) {
                    displaySpecifications(specs);
                } else {
                    specsTxt.setText("Không có thông số kỹ thuật.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load specifications.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Display specifications in the TextView
    private void displaySpecifications(Specifications specs) {
        String specsText = "CPU: " + specs.getCPU() + "\n" +
                "RAM: " + specs.getRAM() + "\n" +
                "Ổ cứng: " + specs.getSSD() + "\n" +
                "Card đồ họa: " + specs.getVGA() + "\n" +
                "Màn hình: " + specs.getLCD();
        specsTxt.setText(specsText);
    }

    // Initialize suggested products
    private void initSuggestedProducts() {
        suggestedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        suggestedProductsAdapter = new SuggestedProductsAdapter(new ArrayList<>());
        suggestedProductsRecyclerView.setAdapter(suggestedProductsAdapter);
    }

    private void fetchSuggestedProducts() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference productsRef = database.getReference("suggested_products");

        productsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Laptops> suggestedProducts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Laptops laptop = snapshot.getValue(Laptops.class);
                    if (laptop != null) {
                        suggestedProducts.add(laptop);
                    }
                }
                suggestedProductsAdapter.updateProducts(suggestedProducts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to load suggested products.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
