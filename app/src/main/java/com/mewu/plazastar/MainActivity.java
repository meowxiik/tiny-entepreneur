package com.mewu.plazastar;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mewu.plazastar.utils.LoadSave;
import com.mewu.plazastar.utils.Numbers;
import com.mewu.plazastar.utils.Pair;
import com.mewu.plazastar.utils.Point;
import com.mewu.plazastar.sliderview.SliderAdapter;
import com.mewu.plazastar.sliderview.SliderLayoutManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends Activity {


    public enum ShopMode {
        Buildings,
        People
    }

    public RenderView mRenderView;
    TextView mMoneyView;

    RecyclerView mShop;
    SliderAdapter mSliderAdapter;
    ConstraintLayout mShopLayout;

    Button mBuildButton;
    TextView mBuildInfo;

    Button mBuyPeople;

    public Point ShoppingLocation;
    public boolean BuildMode = false;
    public ShopMode shopMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Library.Prepare(this);
        LoadSave.Prepare(this);

        Story.Level = LoadSave.LoadStory();
        //Story.Level = 0;

        new GameState();

        setContentView(R.layout.activity_fullscreen);

        mRenderView = findViewById(R.id.rv_game);
        mRenderView.Parent = this;
        mMoneyView = findViewById(R.id.tv_money);
        mShop = findViewById(R.id.rv_shop);
        mShopLayout = findViewById(R.id.cl_shop);

        mShopLayout.setOnTouchListener((v, event) -> true);

        mBuildInfo = findViewById(R.id.tv_building);
        mBuildButton = findViewById(R.id.b_build);
        mBuildButton.setOnClickListener(view -> startBuilding());

        mBuyPeople = findViewById(R.id.b_hire);
        mBuyPeople.setOnClickListener(view -> OpenPeopleStore());

        setupPicker();

        startUpdateLoop();
    }

    private void startBuilding(){
        mBuildButton.setVisibility(View.GONE);
        mBuildInfo.setVisibility(View.VISIBLE);
        mBuyPeople.setVisibility(View.GONE);
        BuildMode = true;
    }

    public void StopShopping(){
        mBuildButton.setVisibility(View.VISIBLE);
        mBuildInfo.setVisibility(View.GONE);
        mShopLayout.setVisibility(View.GONE);
        mBuyPeople.setVisibility(View.VISIBLE);
        BuildMode = false;
    }

    private void startUpdateLoop(){
        android.os.Handler updateHandler = new android.os.Handler();
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                update();
                updateHandler.postDelayed(this, 100);
            }
        };

        updateHandler.post(updater);

        android.os.Handler incomeHandler = new android.os.Handler();
        Runnable updater2 = new Runnable() {
            @Override
            public void run() {
                slowUpdate();
                incomeHandler.postDelayed(this, 1000);
            }
        };

        incomeHandler.post(updater2);
    }

    private void update(){
        mRenderView.Update();
        mRenderView.invalidate();
    }

    private void slowUpdate(){
        float income = GameState.Instance.AutoTap(1f);

        if (income != 0f)
            mRenderView.createRandomMoney(income);

        mMoneyView.setText(Numbers.HumanizeNumber(GameState.Instance.Money) + " $");

        Story.Update(this);
    }


    private void setupPicker() {
        mShop.setLayoutManager(new SliderLayoutManager(this));
        mShop.setAdapter(new SliderAdapter(this));
        mSliderAdapter = (SliderAdapter) mShop.getAdapter();
    }

    public void OpenDepartmentStore(int x, int y) {
        shopMode = ShopMode.Buildings;
        ShoppingLocation = new Point(x, y);

        mSliderAdapter.SetContent(GameState.Instance.GetPossibleDepartments(x, y));
        mSliderAdapter.notifyDataSetChanged();

        mShopLayout.setVisibility(View.VISIBLE);
    }

    public void OpenPeopleStore(){

        mBuildButton.setVisibility(View.GONE);
        mBuyPeople.setVisibility(View.GONE);

        shopMode = ShopMode.People;

        mSliderAdapter.SetContent(Library.AllEmployees);
        mSliderAdapter.notifyDataSetChanged();

        mShopLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        mShopLayout.setVisibility(View.GONE);
        StopShopping();
    }

    boolean volumeDownPressed;
    boolean volumeUpPressed;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            volumeDownPressed = true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            volumeUpPressed = true;
        }

        if (volumeDownPressed && volumeUpPressed){

            PopupMenu popup = new PopupMenu(this, mMoneyView);

            popup.setOnMenuItemClickListener(menuItem -> {

                switch (menuItem.getItemId()){
                    case R.id.menu_add_money:
                        GameState.Instance.Money += 999999999L;
                        break;
                    case R.id.menu_reset_progress:
                        Story.Level = 0;
                        GameState.Instance.Money = 0;
                        GameState.Instance.Owned = new Pair<>(-2, 2);
                        GameState.Instance.HotelMap = new HashMap<>();
                        GameState.Instance.HotelMap.put(new Point(0, 0), 0);
                        GameState.Instance.Employees = new ArrayList<>();
                        GameState.Instance.Bump();
                        break;
                }

                return true;
            });

            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.cheat, popup.getMenu());
            popup.show();

        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            volumeDownPressed = false;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            volumeUpPressed = false;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        float delta = GameState.Instance.Load();

        if (delta > 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You earned " + Numbers.HumanizeNumber((long) delta) + "$ while away!");
            builder.setNegativeButton("Ok!", (dialogInterface, i) -> {});
            builder.create();
            builder.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameState.Instance.Bump();
    }
}