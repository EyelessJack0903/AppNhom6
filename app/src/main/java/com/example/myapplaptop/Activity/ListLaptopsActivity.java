package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Adapter.LaptopListAdapter;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.Activity.Domain.Model;
import com.example.myapplaptop.databinding.ActivityListLaptopsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListLaptopsActivity extends BaseActivity {
    private ActivityListLaptopsBinding binding;
    private RecyclerView.Adapter adapterListLaptop;
    private int ID_TH;
    private String Name;
    private String searchText;
    private boolean isSearch;
    private FirebaseDatabase database;
    private DatabaseReference brandRef;
    private DatabaseReference modelRef;
    private Map<Integer, String> brandNameMap = new HashMap<>();
    private Map<Integer, String> modelNameMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListLaptopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        getIntentExtra();
        initBrandNames();
    }

    private void initBrandNames() {
        brandRef = database.getReference("thuonghieu");
        brandRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int brandId = dataSnapshot.child("ID_TH").getValue(Integer.class);
                    String brandName = dataSnapshot.child("Name").getValue(String.class);
                    brandNameMap.put(brandId, brandName);
                }
                initModelNames();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void initModelNames() {
        modelRef = database.getReference("model");
        modelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int modelId = dataSnapshot.child("ID_MD").getValue(Integer.class);
                    String modelName = dataSnapshot.child("Name").getValue(String.class);
                    modelNameMap.put(modelId, modelName);
                }
                initList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void initList() {
        DatabaseReference laptopRef = database.getReference("sanpham");
        Query query;
        if (isSearch) {
            query = laptopRef.orderByChild("Name").startAt(searchText).endAt(searchText + "\uf8ff");
        } else {
            query = laptopRef.orderByChild("ID_TH").equalTo(ID_TH);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Laptops> laptopsList = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Laptops laptop = dataSnapshot.getValue(Laptops.class);
                        if (laptop != null) {
                            laptopsList.add(laptop);
                        }
                    }
                    if (!laptopsList.isEmpty()) {
                        showRecyclerView(laptopsList);
                    } else {
                        showNoProductMessage();
                    }
                } else {
                    showNoProductMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void showRecyclerView(ArrayList<Laptops> dataList) {
        binding.progressBar.setVisibility(View.GONE);
        binding.laptopViewList.setVisibility(View.VISIBLE);
        binding.noProductText.setVisibility(View.GONE);
        binding.laptopViewList.setLayoutManager(new GridLayoutManager(this, 2));
        adapterListLaptop = new LaptopListAdapter(dataList, brandNameMap, modelNameMap);
        binding.laptopViewList.setAdapter(adapterListLaptop);
    }

    private void showNoProductMessage() {
        binding.progressBar.setVisibility(View.GONE);
        binding.laptopViewList.setVisibility(View.GONE);
        binding.noProductText.setVisibility(View.VISIBLE);
    }

    private void getIntentExtra() {
        ID_TH = getIntent().getIntExtra("ID_TH", 0);
        Name = getIntent().getStringExtra("Name");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);

        binding.titleTxt.setText(Name);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}
