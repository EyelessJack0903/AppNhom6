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
import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivityDetailBinding;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Laptops object;
    private int num = 1;

    private int quantity = 0;
    private TextView numTxt, totalAmountTxt, descriptionTxt, toggleButton;

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

        // Set click listeners for buttons
        findViewById(R.id.minusBtn).setOnClickListener(v -> updateQuantity(false));
        findViewById(R.id.textView6).setOnClickListener(v -> updateQuantity(true));
        findViewById(R.id.addBtn).setOnClickListener(v -> addToCart());
        toggleButton.setOnClickListener(v -> toggleDescription());
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
}
