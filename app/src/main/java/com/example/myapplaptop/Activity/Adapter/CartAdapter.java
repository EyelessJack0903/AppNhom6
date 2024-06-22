package com.example.myapplaptop.Activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.myapplaptop.Activity.Domain.Laptops;
import com.example.myapplaptop.Activity.Helper.ChangeNumberItemsListener;
import com.example.myapplaptop.Activity.Helper.ManagmentCart;
import com.example.myapplaptop.R;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<Laptops> list;
    private ManagmentCart managmentCart;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private Context context;

    public CartAdapter(ArrayList<Laptops> list, Context context, ChangeNumberItemsListener changeNumberItemsListener) {
        this.list = list;
        this.context = context;
        this.changeNumberItemsListener = changeNumberItemsListener;
        managmentCart = new ManagmentCart(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Laptops laptop = list.get(position);

        holder.title.setText(laptop.getName());

        Glide.with(holder.itemView.getContext())
                .load(laptop.getImage())
                .transform(new CenterCrop(), new RoundedCorners(30))
                .into(holder.pic);

        holder.num.setText(String.valueOf(laptop.getNumberInCart()));

        double fee = laptop.getNumberInCart() * laptop.getPrice();
        holder.feeEachItem.setText(formatCurrency(fee));

        holder.totalEachItem.setText(String.format("%d * %s", laptop.getNumberInCart(), formatCurrency(fee)));

        holder.plusItem.setOnClickListener(v -> {
            managmentCart.plusNumberItem(list, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });

        holder.minusItem.setOnClickListener(v -> {
            managmentCart.minusNumberItem(list, position, () -> {
                notifyDataSetChanged();
                changeNumberItemsListener.change();
            });
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, feeEachItem, plusItem, minusItem, totalEachItem, num;
        ImageView pic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTxt);
            feeEachItem = itemView.findViewById(R.id.feeEachItem);
            plusItem = itemView.findViewById(R.id.plusCartBtn);
            minusItem = itemView.findViewById(R.id.minusCartBtn);
            totalEachItem = itemView.findViewById(R.id.totalEachItem);
            num = itemView.findViewById(R.id.numberItemTxt);
            pic = itemView.findViewById(R.id.pic);
        }
    }

    // Phương thức để định dạng số tiền thành chuỗi có dấu phân cách ngàn và ký hiệu tiền tệ
    private String formatCurrency(double amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        return currencyVN.format(amount);
    }
}
