package com.example.myapplaptop.Activity;

import android.os.Bundle;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplaptop.Activity.Adapter.CartAdapter;
import com.example.myapplaptop.Activity.Helper.ChangeNumberItemsListener;
import com.example.myapplaptop.Activity.Helper.ManagmentCart;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.databinding.ActivityCartBinding;
import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends BaseActivity {

    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double deliveryFee = 100000; // Phí giao hàng là 100,000 VNĐ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        managmentCart = new ManagmentCart(this);
        setVariable();
        initList();
        calculateCart();
    }

    private void initList() {
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        binding.cartView.setLayoutManager(new LinearLayoutManager(this));
        binding.cartView.setAdapter(adapter);

        if (managmentCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollviewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollviewCart.setVisibility(View.VISIBLE);
        }
    }

    private void calculateCart() {
        double total = managmentCart.getTotalFee() + deliveryFee;
        binding.totalFeeTxt.setText(formatCurrency(managmentCart.getTotalFee()));
        binding.deliveryTxt.setText(formatCurrency(deliveryFee));
        binding.totalTxt.setText(formatCurrency(total));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish()); // Kết thúc CartActivity khi click vào backBtn
    }

    // Phương thức để định dạng số tiền thành chuỗi có dấu phân cách ngàn và ký hiệu tiền tệ
    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(amount);
    }
}
