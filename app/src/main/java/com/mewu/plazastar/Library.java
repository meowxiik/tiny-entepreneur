package com.mewu.plazastar;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.mewu.plazastar.utils.Point;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

enum Environment {
    Day, Night
}

public class Library {

    public static SparseArray<Drawable> Textures = new SparseArray<>();
    public static SparseArray<String> Names = new SparseArray<>();
    public static SparseArray<String> Descriptions = new SparseArray<>();
    public static SparseArray< Long> Prices = new SparseArray<>();
    public static SparseIntArray Incomes = new SparseIntArray();
    public static SparseArray<Float> Bonuses = new SparseArray<>();

    public static SparseArray<Drawable[]> People = new SparseArray<>();
    public static SparseIntArray PeopleSizes = new SparseIntArray();

    public static Point BackgroundSize = new Point(11, 17);
    public static HashMap<Environment, Drawable> Backgrounds = new HashMap<>();
    public static HashMap<Environment, Drawable> Grounds = new HashMap<>();

    public static Drawable Deconstruction;
    public static Drawable Gradient;
    public static Drawable Money;
    public static Drawable Sign;

    public static Drawable Roof;
    public static Drawable Wall;
    public static Drawable Overlay;
    public static Drawable OverlayLeftOpen;
    public static Drawable OverlayRightOpen;
    public static Drawable OverlayBothOpen;

    public static final int IDConstruction = -1;

    public static final int IDLemonadeStand = 0;
    public static final int IDFastFood      = 1;
    public static final int IDClothesShop   = 2;
    public static final int IDPharmacy      = 3;
    public static final int IDResidentRoom  = 4;
    public static final int IDFlowerShop    = 5;
    public static final int IDFurnitureStore= 6;
    public static final int IDPetStore      = 7;
    public static final int IDTechStore     = 8;
    public static final int IDJewelry       = 9;
    public static final int IDMagicStore    = 10;
    public static final int IDElevator    = 11;

    public static final int IDMAX = 10;

    public static final SparseArray<Drawable> EmployeesTextures = new SparseArray<>();
    public static final SparseIntArray EmployeePrices = new SparseIntArray();
    public static SparseArray<String> EmployeeNames = new SparseArray<>();
    public static SparseArray<String> EmployeeDescriptions = new SparseArray<>();
    public static SparseArray<Float> EmployeeTaps = new SparseArray<>();

    public static List<Integer> AllShops = Arrays.asList(0, 1, 2, 11, 3, 4, 5, 6, 7, 8, 9, 10);
    public static List<Integer> AllEmployees = Arrays.asList(0, 1);

    public static void Prepare(MainActivity activity){
        CreateDepartment(activity, 0,  "Lemonade stand", "+1$ / tap", 20, 1, R.drawable.shop_lemonade);
        CreateDepartment(activity, 1,  "WacDonalds", "+2$ / tap", 200, 2, R.drawable.shop_fast_food);
        CreateDepartment(activity, 2,  "Clothe's shop", "+5$ / tap", 500, 5, R.drawable.shop_clothes);
        CreateDepartment(activity, 3,  "Goldman's pharmacy", "+10$ / tap", 10000, 10, R.drawable.shop_pharmacy);
        CreateDepartment(activity, 4,  "Resident room", "+5% / tap", 10000, 0, 1.05f, R.drawable.misc_hotel_room);
        CreateDepartment(activity, 5,  "Flower shop", "+10$ / tap", 100000, 10, R.drawable.shop_flowers);
        CreateDepartment(activity, 6,  "El's furniture", "+50$ / tap", 1000000000, 50, R.drawable.shop_furniture);
        CreateDepartment(activity, 7,  "Petstore", "+20$ / tap", 2000000000, 20, R.drawable.shop_petstore);
        CreateDepartment(activity, 8,  "uStore", "+100$ / tap", 10000000000L, 100, R.drawable.shop_ustore);
        CreateDepartment(activity, 9,  "Thief's", "+1K$ / tap", 50000000000L, 1000, R.drawable.shop_jewelry);
        CreateDepartment(activity, 10,  "Mystery Store", "+2K$ / tap", 1000000000000L, 2000, R.drawable.shop_mystery);
        CreateDepartment(activity, 11,  "Elevator", "More floors!", 1000, 0, R.drawable.misc_elevator);

        CreateBackground(activity, Environment.Day, R.drawable.bg_day, R.drawable.misc_ground_day);
        CreateBackground(activity, Environment.Night, R.drawable.bg_night, R.drawable.misc_ground_day);

        CreatePerson(activity, -10, R.drawable.client_1a, R.drawable.client_1b, R.drawable.client_1c, R.drawable.client_1d);
        CreatePerson(activity, -20, R.drawable.client_2a, R.drawable.client_2b, R.drawable.client_2c, R.drawable.client_2d);
        CreatePerson(activity, 20, R.drawable.client_3a, R.drawable.client_3b, R.drawable.client_3c, R.drawable.client_3d);
        CreatePerson(activity, 10, R.drawable.client_4a, R.drawable.client_4b, R.drawable.client_4c, R.drawable.client_4d);
        CreatePerson(activity, -10, R.drawable.client_5a, R.drawable.client_5b, R.drawable.client_5c, R.drawable.client_5d);
        CreatePerson(activity, 5, R.drawable.client_6a, R.drawable.client_6b, R.drawable.client_6c, R.drawable.client_6d);

        Sign = Load(activity, R.drawable.misc_sign_png);
        Deconstruction = Load(activity, R.drawable.misc_deconstruction);
        Gradient = Load(activity, R.drawable.misc_gradient);
        Money = Load(activity, R.drawable.misc_money);

        Roof = Load(activity, R.drawable.strut_roof);
        Wall = Load(activity, R.drawable.strut_wall);
        Overlay = Load(activity, R.drawable.strut_overlay);
        OverlayLeftOpen = Load(activity, R.drawable.strut_overlay_left_open);
        OverlayRightOpen = Load(activity, R.drawable.strut_overlay_right_open);
        OverlayBothOpen = Load(activity, R.drawable.strut_overlay_both_open);

        Textures.put(-1, Load(activity, R.drawable.misc_construction));
        Incomes.put(-1, 0);
        Bonuses.put(-1, 0f);

        CreateEmployee(activity, 0, "Emma", "+0.1 taps / s", 1000, 0.1f, R.drawable.employee_cashier);
        CreateEmployee(activity, 1, "Josh", "+100 taps / s", 100000, 100f,  R.drawable.employee_bouncer);
    }

    private static void CreateDepartment(MainActivity activity, int id, String name, String description, long price, int income, float bonus, int drawableId){
        Drawable image = Load(activity, drawableId);
        Textures.put(id, image);
        Names.put(id, name);
        Descriptions.put(id, description);
        Prices.put(id, price);
        Incomes.put(id, income);
        Bonuses.put(id, bonus);
    }

    private static void CreateDepartment(MainActivity activity, int id, String name, String description, long price, int income, int drawableId){
        CreateDepartment(activity, id, name, description, price, income, 0f,  drawableId);
    }

    private static void CreateBackground(MainActivity activity, Environment type, int bgDrawableId, int groundDrawableId){
        Backgrounds.put(type, Load(activity, bgDrawableId));
        Grounds.put(type, Load(activity, groundDrawableId));
    }

    private static int nextPersonId = 0;
    private static void CreatePerson(MainActivity activity, int size, int textureId1, int textureId2, int textureId3, int textureId4){
        int id = nextPersonId;

        Drawable[] animation = new Drawable[4];
        animation[0] = Load(activity, textureId1);
        animation[1] = Load(activity, textureId2);
        animation[2] = Load(activity, textureId3);
        animation[3] = Load(activity, textureId4);

        People.put(id, animation);
        PeopleSizes.put(id, size);

        nextPersonId++;
    }

    private static Drawable Load(MainActivity activity, int drawableId){
        Drawable image;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image = activity.getDrawable(drawableId);
        } else {
            image = activity.getResources().getDrawable(drawableId);
        }
        return image;
    }

    public static boolean HasElevator(int roomId){
        switch (roomId){
            case IDElevator:
                return true;
            default:
                return false;
        }
    }

    public static boolean IsBuilding(int roomId){
        switch (roomId){
            case IDLemonadeStand:
                return false;
            default:
                return true;
        }
    }

    public static boolean IsConnectable(int roomId){
        switch (roomId){
            case IDLemonadeStand:
            case IDResidentRoom:
                return false;
            default:
                return true;
        }
    }

    public static float GetEntryPoint(int roomId){
        return 0.6f;
    }

    private static void CreateEmployee(MainActivity activity, int id, String name, String description, int price, float taps, int resourceId){
        EmployeePrices.put(id, price);
        EmployeesTextures.put(id, Load(activity, resourceId));
        EmployeeNames.put(id, name);
        EmployeeDescriptions.put(id, description);
        EmployeeTaps.put(id, taps);
    }
}
