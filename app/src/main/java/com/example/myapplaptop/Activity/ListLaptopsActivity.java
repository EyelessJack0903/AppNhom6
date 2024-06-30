package com.example.myapplaptop.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
    private boolean isFilter; // Thêm khai báo biến isFilter
    private int modelID; // Thêm khai báo biến modelID
    private boolean isFilterByType;
    private int typeID;
    private FirebaseDatabase database;
    private DatabaseReference brandRef;
    private DatabaseReference modelRef;
    private DatabaseReference typeRef;
    private Map<Integer, String> brandNameMap = new HashMap<>();
    private Map<Integer, String> modelNameMap = new HashMap<>();
    private Map<Integer, String> typeNameMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListLaptopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        getIntentExtra();
        initBrandNames();
        initTypeNames();
    }

    private void initList() {
        DatabaseReference laptopRef = database.getReference("sanpham");
        Query query;

        if (isSearch) {
            // Xử lý khi tìm kiếm
            query = laptopRef.orderByChild("Name");
        } else if (isFilter) {
            // Xử lý khi lọc theo Model_ID
            query = laptopRef.orderByChild("ID_MD").equalTo(modelID);
        } else if (isFilterByType) {
            // Xử lý khi lọc theo Type_ID
            query = laptopRef.orderByChild("ID_LM").equalTo(typeID);
        } else {
            // Xử lý khi hiển thị theo ID_TH
            query = laptopRef.orderByChild("ID_TH").equalTo(ID_TH);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Laptops> laptopsList = new ArrayList<>();
                if (snapshot.exists()) {
                    String searchTextLower = searchText != null ? searchText.toLowerCase() : "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Laptops laptop = dataSnapshot.getValue(Laptops.class);
                        if (laptop != null) {
                            if (searchTextLower.isEmpty() || laptop.getName().toLowerCase().contains(searchTextLower)) {
                                laptopsList.add(laptop);
                            }
                        }
                    }
                    if (!laptopsList.isEmpty()) {
                        showRecyclerView(laptopsList);
                        // Hiển thị tên khi lọc theo typeSp hoặc modelSp
                        if (isFilterByType) {
                            binding.titleTxt.setText("Loại máy: " + typeNameMap.get(typeID));
                        } else if (isFilter) {
                            binding.titleTxt.setText("Model: " +  modelNameMap.get(modelID));
                        }
                    } else {
                        showNoProductMessage();
                    }
                } else {
                    showNoProductMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi
                Toast.makeText(ListLaptopsActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
    private void initTypeNames() {
        typeRef = database.getReference("loaimay");
        typeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int typeID = dataSnapshot.child("ID_LM").getValue(Integer.class);
                    String typeName = dataSnapshot.child("Name").getValue(String.class);
                    typeNameMap.put(typeID, typeName);
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
                // Gọi hàm initList() ở đây để bắt đầu load dữ liệu ban đầu
                initList();
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
        // Lấy các dữ liệu từ Intent
        ID_TH = getIntent().getIntExtra("ID_TH", 0);
        Name = getIntent().getStringExtra("Name");
        searchText = getIntent().getStringExtra("text");
        isSearch = getIntent().getBooleanExtra("isSearch", false);
        isFilter = getIntent().getBooleanExtra("isFilter", false);
        isFilterByType = getIntent().getBooleanExtra("isFilterByType", false);
        modelID = getIntent().getIntExtra("Model_ID", 0);
        typeID = getIntent().getIntExtra("Type_ID", 0);

        // Đặt tên lọc lên titleTxt
        binding.titleTxt.setText(Name);
        binding.backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ListLaptopsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Gọi hàm initList() khi Intent được nhận và các dữ liệu đã được lấy
        initList();
    }
}