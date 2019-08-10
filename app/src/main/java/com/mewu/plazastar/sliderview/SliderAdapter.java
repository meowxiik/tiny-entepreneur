package com.mewu.plazastar.sliderview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mewu.plazastar.MainActivity;
import com.mewu.plazastar.GameState;
import com.mewu.plazastar.Library;
import com.mewu.plazastar.R;
import com.mewu.plazastar.utils.Numbers;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderView>  {

    MainActivity mParent;

    List<Integer> Content;

    public SliderAdapter(MainActivity parent) {
        this.mParent = parent;

    }

    public void SetContent(List<Integer> content){
        Content = content;
    }

    @NonNull
    @Override
    public SliderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item, parent, false);
        return new SliderView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderView holder, int position) {

        long price;

        int itemId = Content.get(position);

        if (mParent.shopMode == MainActivity.ShopMode.Buildings){
            price = Library.Prices.get(itemId);
            holder.price.setText(Numbers.HumanizeNumber(price) + " $");
            holder.image.setImageDrawable(Library.Textures.get(itemId).getConstantState().newDrawable());
            holder.name.setText(Library.Names.get(itemId));
            holder.desc.setText(Library.Descriptions.get(itemId));
        }
        else {
            price = Library.EmployeePrices.get(itemId);
            holder.price.setText(Numbers.HumanizeNumber(price) + " $");
            holder.image.setImageDrawable(Library.EmployeesTextures.get(itemId).getConstantState().newDrawable());
            holder.name.setText(Library.EmployeeNames.get(itemId));
            holder.desc.setText(Library.EmployeeDescriptions.get(itemId));
        }

        if (GameState.Instance.Money >= price) {
            holder.button.setEnabled(true);

            if (mParent.shopMode == MainActivity.ShopMode.Buildings){
                holder.button.setOnClickListener(view -> buy(itemId));
                holder.image.setOnClickListener(view -> buy(itemId));
            }else {
                holder.button.setOnClickListener(view -> buyEmployee(itemId));
                holder.image.setOnClickListener(view -> buyEmployee(itemId));
            }

        } else {
            holder.button.setEnabled(false);
        }

    }

    private void buy(int position){
        GameState.Instance.BuyShop(mParent.ShoppingLocation.X, mParent.ShoppingLocation.Y, position);
        notifyDataSetChanged();
        mParent.StopShopping();
    }

    private void buyEmployee(int position){
        GameState.Instance.BuyEmployee(position);
        notifyDataSetChanged();
        mParent.StopShopping();
    }

    @Override
    public int getItemCount()
    {
        return Content.size();
    }
}

class SliderView extends RecyclerView.ViewHolder {

    TextView price = itemView.findViewById(R.id.tv_price);
    Button button = itemView.findViewById(R.id.b_buy);
    ImageView image = itemView.findViewById(R.id.iv_thumbail);
    TextView name = itemView.findViewById(R.id.tv_name);
    TextView desc = itemView.findViewById(R.id.tv_desc);

    SliderView(@NonNull View itemView) {
        super(itemView);
    }
}