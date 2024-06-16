package com.example.myapplaptop.Activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.Activity.Domain.Specifications;
import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivityDetailBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Laptops object;
    private int num = 1;
    private int quantity = 1;
    private TextView numTxt, totalAmountTxt, descriptionTxt, toggleButton, specsTxt;

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
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());

        Glide.with(DetailActivity.this)
                .load(object.getImage())
                .into(binding.pic);

        binding.priceText.setText(formatCurrency(object.getPrice()));
        binding.titleTxt.setText(object.getName());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText(formatCurrency(num * object.getPrice()));
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
}