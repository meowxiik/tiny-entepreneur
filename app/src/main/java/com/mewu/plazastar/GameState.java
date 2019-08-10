package com.mewu.plazastar;

import com.mewu.plazastar.utils.LoadSave;
import com.mewu.plazastar.utils.Pair;
import com.mewu.plazastar.utils.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameState {

    public static GameState Instance;

    public long Money;

    private float MoneyCache;

    public HashMap<Point, Integer> HotelMap;
    public int MapSize = 0;
    public int Tallest = 0;
    public Pair<Integer, Integer> Owned;
    public List<Integer> Employees;

    public float PerTapIncome = 1;
    public float PerTapBonus = 1;
    public float AutoTapsPerSecond = 0;

    public GameState () {

        Instance = this;
    }

    public float Load(){
        Owned = LoadSave.LoadOwnedProperty();
        HotelMap = LoadSave.Load();
        Money = LoadSave.LoadMoney();
        Employees = LoadSave.LoadEmployees();

        if (HotelMap.size() == 0){
            HotelMap.put(new Point(0, 0), 0);
        }

        float timeDelta = System.currentTimeMillis() / 1000 - LoadSave.LoadTime();
        Bump();

        if (timeDelta > 0)
            return AutoTap(timeDelta);

        return 0;
    }

    public void Bump(){

        MapSize = HotelMap.size();

        LoadSave.Save(HotelMap);
        LoadSave.SaveMoney(Money);
        LoadSave.SaveOwnedProperty(Owned);
        LoadSave.SaveEmployees(Employees);
        LoadSave.SaveTime(System.currentTimeMillis() / 1000);

        PerTapBonus = 1;
        PerTapIncome = 0;
        for (Map.Entry<Point, Integer> entry: HotelMap.entrySet()) {
            PerTapBonus += Library.Bonuses.get(entry.getValue());
            PerTapIncome += Library.Incomes.get(entry.getValue());

            if (entry.getKey().Y > Tallest)
                Tallest = entry.getKey().Y;
        }

        AutoTapsPerSecond = 0;
        for (Integer employee : Employees){
            AutoTapsPerSecond += Library.EmployeeTaps.get(employee);
        }

    }

    public void BuyProperty(int x){
        Money -= GetPropertyExpansionPrice(x);
        if (x < Owned.P1)
            Owned = new Pair<>(x, Owned.P2);
        if (x > Owned.P2)
            Owned = new Pair<>(Owned.P1, x);
    }

    public void BuyShop(int x, int y, int id){
        Money -= Library.Prices.get(id);
        HotelMap.put(new Point(x, y), id);
        Bump();
    }

    public void BuyEmployee(int id){
        Money -= Library.EmployeePrices.get(id);
        Employees.add(id);
        Bump();
    }

    public void Earn(){
        Money += IncomePerTap();
    }

    public float AutoTap(float secondsDelta){
        float delta = secondsDelta * (float)IncomePerTap() * AutoTapsPerSecond;
        MoneyCache += delta;
        Money += MoneyCache;
        MoneyCache = MoneyCache - (int) MoneyCache;
        return delta;
    }

    public double IncomePerTap(){
        return PerTapIncome * PerTapBonus;
    }

    public List<Point> GetExpansions(){
        List<Point> buildable = new ArrayList<>();

        /*for(Map.Entry<Point, Integer> shop : HotelMap.entrySet()){
            Point location = shop.getKey();
            int id = shop.getValue();

            Point top = new Point(location.X, location.Y + 1);

            if (Library.HasElevator(id) && !HotelMap.containsKey(top) && !buildable.contains(top))
                buildable.add(top);

            Point left = new Point(location.X - 1, location.Y);
            Point right = new Point(location.X + 1, location.Y);

            if (!HotelMap.containsKey(left) && Library.IsBuilding(id) && !buildable.contains(left) && Owned.P1 <= left.X && )
                buildable.add(left);

            if (!HotelMap.containsKey(right) && Library.IsBuilding(id) && !buildable.contains(right) && Owned.P2 >= right.X)
                buildable.add(right);
        }

        for (int x = Owned.P1; x <= Owned.P2; x++){

            Point p = new Point(x, 0);

            if (buildable.contains(p))
                continue;

            if (HotelMap.containsKey(p) && HotelMap.get(p) != Library.IDConstruction)
                continue;

            buildable.add(p);
        }*/

        for (int x = Owned.P1; x <= Owned.P2; x++){

            boolean searching = true;
            int y = 0;

            while(searching){
                searching = false;

                Point p = new Point(x, y);

                if (HotelMap.containsKey(p)){
                    int id = HotelMap.get(p);

                    if (Library.HasElevator(id)){
                        searching = true;
                        y++;
                        continue;
                    }
                    else if (Library.IsBuilding(id)){

                        if (id == Library.IDConstruction){
                            buildable.add(p);
                        }

                        Point left = new Point(x + 1, y + 1);
                        Point right = new Point(x - 1, y + 1);

                        if (HotelMap.containsKey(left) || HotelMap.containsKey(right)){
                            searching = true;
                            y++;
                            continue;
                        }

                    }
                }
                else {
                    buildable.add(p);
                }
            }
        }

        return buildable;
    }

    public List<Integer> GetPossibleDepartments(int x, int y){
        List<Integer> departments = new ArrayList<>();

        for (int department : Library.AllShops) {

            double price = Library.Prices.get(department);

            if (Money * 2 < price)
                continue;

            if (y > 0){
                if (department == Library.IDElevator){
                    if (HotelMap.get(new Point(x, y - 1)) == Library.IDElevator){
                        departments.add(department);
                        continue;
                    }
                    else {
                        continue;
                    }
                }
                else {
                    if (HotelMap.get(new Point(x, y - 1)) == Library.IDElevator){
                        continue;
                    }
                    else {
                        departments.add(department);
                        continue;
                    }
                }
            }
            else{
                departments.add(department);
                continue;
            }
        }

        return departments;
    }

    public void Deconstruct(Point p){
        if (!Library.IsBuilding(HotelMap.get(p)))
            HotelMap.remove(p);
        else
            HotelMap.put(p, -1);
    }

    public long GetPropertyExpansionPrice(int x){
        return (long) Math.pow(10D, Math.abs(x));
    }
}
