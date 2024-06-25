package com.example.myapplaptop.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Adapter.CategoryBrand;
import com.example.myapplaptop.Activity.Adapter.LaptopBestChoice;
import com.example.myapplaptop.Activity.Domain.Category;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.Activity.Domain.Model;
import com.example.myapplaptop.Activity.Domain.Type;
import com.example.myapplaptop.R;
import com.example.myapplaptop.databinding.ActivityMainBinding;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Get the User UID from the Intent
        String userUid = getIntent().getStringExtra("USER_UID");
        if (userUid != null) {
            Toast.makeText(MainActivity.this, "User UID: " + userUid, Toast.LENGTH_SHORT).show();
        }

        binding.updatePasword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdatePasswordActivity.class));
            }
        });
        binding.orderItem.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, OrderListActivity.class)));

        initLaptops();
        initCategory();
        initBestLaptop();
        initBrand();
        setVariable();
        initModel();
    }


    private void initModel() {
        DatabaseReference myRef = database.getReference("model");
        ArrayList<Model> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Model.class));
                    }
                    // Sắp xếp list theo tên (tên của Model)
                    Collections.sort(list, new Comparator<Model>() {
                        @Override
                        public int compare(Model model1, Model model2) {
                            return model1.getName().compareTo(model2.getName());
                        }
                    });

                    ArrayAdapter<Model> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.modelSp.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }

    private void setVariable() {
        binding.logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, IntroActivity.class));
        });

        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdt.getText().toString().trim();
            if (!text.isEmpty()) {
                String searchText = text.toLowerCase();
                Intent intent = new Intent(MainActivity.this, ListLaptopsActivity.class);
                intent.putExtra("text", searchText);
                intent.putExtra("isSearch", true);
                startActivity(intent);
            }
        });
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));
    }

    private void initBestLaptop() {
        DatabaseReference myRef = database.getReference("sanpham");
        binding.progressBarBestChoice.setVisibility(View.VISIBLE);
        ArrayList<Laptops> list = new ArrayList<>();
        Query query = myRef.orderByChild("BestLaptop").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Laptops.class));
                    }
                    if (list.size() > 0){
                        // Lấy danh sách thương hiệu
                        DatabaseReference brandRef = database.getReference("thuonghieu");
                        brandRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ArrayList<Category> categoryList = new ArrayList<>();
                                if (snapshot.exists()){
                                    for (DataSnapshot issue: snapshot.getChildren()){
                                        categoryList.add(issue.getValue(Category.class));
                                    }
                                    // Khởi tạo Adapter và set cho RecyclerView
                                    binding.bestLaptopView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL,false));
                                    RecyclerView.Adapter adapter = new LaptopBestChoice(list, categoryList);
                                    binding.bestLaptopView.setAdapter(adapter);
                                }
                                binding.progressBarBestChoice.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        binding.progressBarBestChoice.setVisibility(View.GONE);
                    }
                } else {
                    binding.progressBarBestChoice.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initBrand(){
        DatabaseReference myRef = database.getReference("thuonghieu");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Category.class));
                    }
                    if (list.size() > 0){
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                        RecyclerView.Adapter adapter = new CategoryBrand(list);
                        binding.categoryView.setAdapter(adapter);
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initLaptops() {
        DatabaseReference myRef = database.getReference("loaimay");
        ArrayList<Type> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Type.class));
                    }
                    // Sắp xếp list theo tên (tên của Type)
                    Collections.sort(list, new Comparator<Type>() {
                        @Override
                        public int compare(Type type1, Type type2) {
                            return type1.getName().compareTo(type2.getName());
                        }
                    });

                    ArrayAdapter<Type> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.typeSp.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("thuonghieu");
        ArrayList<Category> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot issue: snapshot.getChildren()){
                        list.add(issue.getValue(Category.class));
                    }
                    // Sắp xếp list theo tên (tên của Category)
                    Collections.sort(list, new Comparator<Category>() {
                        @Override
                        public int compare(Category category1, Category category2) {
                            return category1.getName().compareTo(category2.getName());
                        }
                    });

                    ArrayAdapter<Category> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.brandSp.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
            }
        });
    }


}