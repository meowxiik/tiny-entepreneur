package com.mewu.plazastar.sliderview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mewu.plazastar.FullscreenActivity;
import com.mewu.plazastar.GameState;
import com.mewu.plazastar.Library;
import com.mewu.plazastar.R;
import com.mewu.plazastar.utils.Numbers;

public class SliderAdapter extends RecyclerView.Adapter<SliderView>  {

    FullscreenActivity mParent;

    public SliderAdapter(FullscreenActivity parent) {
        this.mParent = parent;

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

        if (mParent.shopMode == FullscreenActivity.ShopMode.Buildings){
            price = Library.Prices.get(position);
            holder.price.setText(Numbers.HumanizeNumber(price) + " $");
            holder.image.setImageDrawable(Library.Textures.get(position).getConstantState().newDrawable());
            holder.name.setText(Library.Names.get(position));
            holder.desc.setText(Library.Descriptions.get(position));
        }
        else {
            price = Library.EmployeePrices.get(position);
            holder.price.setText(Numbers.HumanizeNumber(price) + " $");
            holder.image.setImageDrawable(Library.EmployeesTextures.get(position).getConstantState().newDrawable());
            holder.name.setText(Library.EmployeeNames.get(position));
            holder.desc.setText(Library.EmployeeDescriptions.get(position));
        }

        if (GameState.Instance.Money >= price) {
            holder.button.setEnabled(true);

            if (mParent.shopMode == FullscreenActivity.ShopMode.Buildings){
                holder.button.setOnClickListener(view -> buy(position));
                holder.image.setOnClickListener(view -> buy(position));
            }else {
                holder.button.setOnClickListener(view -> buyEmployee(position));
                holder.image.setOnClickListener(view -> buyEmployee(position));
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

        if (mParent.shopMode == FullscreenActivity.ShopMode.Buildings){
            return Library.IDMAX + 1;
        }
        else {
            return Library.EmployeesTextures.size();
        }
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