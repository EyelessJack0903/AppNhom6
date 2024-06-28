package com.example.myapplaptop.Activity.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.myapplaptop.Activity.Domain.Laptops;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ManagmentCart {
    private static final String SHARED_PREF_NAME = "cart_pref";
    private static final String CART_KEY = "cart_key";

    private Context context;

    public ManagmentCart(Context context) {
        this.context = context;
    }

    // Lấy danh sách giỏ hàng từ SharedPreferences
    public ArrayList<Laptops> getListCart() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(CART_KEY, null);
        if (json == null) {
            return new ArrayList<>(); // Trả về danh sách rỗng nếu chưa có dữ liệu trong SharedPreferences
        }
        Type type = new TypeToken<ArrayList<Laptops>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    // Lưu danh sách giỏ hàng vào SharedPreferences
    private void saveListCart(ArrayList<Laptops> list) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = new Gson().toJson(list);
        editor.putString(CART_KEY, json);
        editor.apply();
    }

    // Thêm sản phẩm vào giỏ hàng
    public void insertLaptop(Laptops laptop) {
        ArrayList<Laptops> cart = getListCart();
        if (cart == null) {
            cart = new ArrayList<>();
        }
        cart.add(laptop);
        saveListCart(cart); // Lưu danh sách giỏ hàng vào SharedPreferences
    }

    // Tăng số lượng sản phẩm trong giỏ hàng
    public void plusNumberItem(ArrayList<Laptops> cart, int position, ChangeNumberItemsListener listener) {
        Laptops laptop = cart.get(position);
        laptop.setNumberInCart(laptop.getNumberInCart() + 1);
        cart.set(position, laptop);
        saveListCart(cart);
        if (listener != null) {
            listener.change();
        }
    }

    // Giảm số lượng sản phẩm trong giỏ hàng
    public void minusNumberItem(ArrayList<Laptops> cart, int position, ChangeNumberItemsListener listener) {
        Laptops laptop = cart.get(position);
        int currentQuantity = laptop.getNumberInCart();
        if (currentQuantity > 1) {
            laptop.setNumberInCart(currentQuantity - 1);
            cart.set(position, laptop);
            saveListCart(cart);
            if (listener != null) {
                listener.change();
            }
        } else if (currentQuantity == 1) {
            cart.remove(position);
            saveListCart(cart);
            if (listener != null) {
                listener.change();
            }
        }
    }

    // Tính tổng chi phí của giỏ hàng
    public double getTotalFee() {
        double total = 0;
        ArrayList<Laptops> cart = getListCart();
        if (cart != null) {
            for (Laptops laptop : cart) {
                total += laptop.getPrice() * laptop.getNumberInCart();
            }
        }
        return total;
    }

    // Xóa giỏ hàng
    public void clearCart() {
        saveListCart(new ArrayList<>());
    }
}
