package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplaptop.Activity.Domain.Cart;
import com.example.myapplaptop.Activity.Domain.DetailCart;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.Activity.Domain.Payment;
import com.example.myapplaptop.Activity.Helper.ManagmentCart;
import com.example.myapplaptop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private EditText nameEditText, phoneEditText, addressEditText;
    private Spinner paymentSpinner;
    private TextView totalPriceTxt;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private ManagmentCart managmentCart;
    private List<Payment> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_activity);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        ImageView backBtn = findViewById(R.id.backBtn);
        paymentSpinner = findViewById(R.id.paymentSpinner);
        totalPriceTxt = findViewById(R.id.totalPriceTxt);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        managmentCart = new ManagmentCart(this);

        fetchPaymentMethods();

        findViewById(R.id.confirmBtn).setOnClickListener(v -> placeOrder());

        backBtn.setOnClickListener(v -> finish());
    }

    private void fetchPaymentMethods() {
        DatabaseReference paymentRef = databaseReference.child("payment");
        paymentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                paymentList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Payment payment = dataSnapshot.getValue(Payment.class);
                    if (payment != null) {
                        paymentList.add(payment);
                    }
                }
                setupPaymentSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserInfoActivity.this, "Lỗi phương thức payment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPaymentSpinner() {
        ArrayAdapter<Payment> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(adapter);

        paymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                calculateTotalPrice(position);

                // Lấy selected payment từ spinner
                Payment selectedPayment = (Payment) parent.getItemAtPosition(position);

                // Kiểm tra nếu ID_Payment là 4 thì hiển thị văn bản là "Thanh toán bằng tiền mặt"
                if (selectedPayment.getID_Payment() == 4) {
                    ((TextView) view).setText("Thanh toán bằng tiền mặt");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Default behavior if nothing is selected
            }
        });
    }


    private void calculateTotalPrice(int position) {
        double deliveryFee = 100000; // Assume delivery fee is constant

        double total = managmentCart.getTotalFee() + deliveryFee;
        totalPriceTxt.setText("Tổng giá tiền: " + formatCurrency(total));
    }

    private void placeOrder() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(UserInfoActivity.this, "Bạn cần đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        String cartId = databaseReference.child("cart").push().getKey();
        String detailId;

        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Payment selectedPayment = (Payment) paymentSpinner.getSelectedItem();

        // Lấy ID_Payment dưới dạng int từ selectedPayment
        int idPayment = selectedPayment.getID_Payment();

        // Lấy ngày giờ hiện tại
        Date currentDate = new Date(); // Lấy ngày giờ hiện tại của thiết bị

        Cart cart = new Cart();
        cart.setID_User(userId);
        cart.setID_Cart(cartId);
        cart.setDate(currentDate); // Set ngày giờ hiện tại
        cart.setTotal(managmentCart.getTotalFee());
        cart.setName(name);
        cart.setSDT(phone);
        cart.setAddress(address);
        cart.setPaymentMethod(selectedPayment.getName()); // Set selected payment method
        cart.setID_Payment(idPayment); // Set ID_Payment

        databaseReference.child("cart").child(cartId).setValue(cart);

        ArrayList<Laptops> cartList = managmentCart.getListCart();
        for (Laptops laptop : cartList) {
            detailId = databaseReference.child("detail_cart").push().getKey();

            DetailCart detailCart = new DetailCart();
            detailCart.setID_Cart(cartId);
            detailCart.setID_Detail(detailId);
            detailCart.setID_Laptop(laptop.getID_Laptop());
            detailCart.setQuantity(laptop.getNumberInCart());
            detailCart.setPrice(laptop.getPrice());
            detailCart.setTotal(laptop.getPrice() * laptop.getNumberInCart());
            detailCart.setId_User(userId); // Đặt giá trị id_User vào detailCart

            databaseReference.child("detail_cart").child(detailId).setValue(detailCart);
        }

        managmentCart.clearCart();

        Toast.makeText(UserInfoActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
        finish();
    }

    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }
}
