package com.example.myapplaptop.Activity;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplaptop.Activity.Adapter.LaptopListAdapter;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.R;
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
    private DatabaseReference myRef;
    private Map<Integer, String> brandNameMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityListLaptopsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();

        getIntentExtra();
        initBrandNames();
        setVariable();
    }

    private void setVariable() {

    }

    private void initBrandNames() {
        myRef = database.getReference("thuonghieu");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot issue : snapshot.getChildren()) {
                    int brandId = issue.child("ID_TH").getValue(Integer.class);
                    String brandName = issue.child("Name").getValue(String.class);
                    brandNameMap.put(brandId, brandName);
                }
                initList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initList() {
        myRef = database.getReference("sanpham");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Laptops> list = new ArrayList<>();

        Query query;
        if (isSearch) {
            // Handle search query
            ArrayList<Laptops> caseInsensitiveList = new ArrayList<>();
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Laptops laptop = issue.getValue(Laptops.class);
                            if (laptop != null && laptop.getName().toLowerCase().contains(searchText)) {
                                caseInsensitiveList.add(laptop);
                            }
                        }
                        if (!caseInsensitiveList.isEmpty()) {
                            showRecyclerView(caseInsensitiveList);
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
            return;
        } else {
            query = myRef.orderByChild("ID_TH").equalTo(ID_TH);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Laptops.class));
                    }
                    if (!list.isEmpty()) {
                        showRecyclerView(list);
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
        binding.laptopViewList.setVisibility(View.VISIBLE);
        binding.noProductText.setVisibility(View.GONE);
        binding.laptopViewList.setLayoutManager(new GridLayoutManager(ListLaptopsActivity.this, 2));
        adapterListLaptop = new LaptopListAdapter(dataList, brandNameMap);
        binding.laptopViewList.setAdapter(adapterListLaptop);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showNoProductMessage() {
        binding.laptopViewList.setVisibility(View.GONE);
        binding.noProductText.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
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
