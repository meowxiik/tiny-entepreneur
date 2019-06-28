package com.mewu.plazastar.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadSave {

    private static final String SAVE_NAME = "save";
    private static final String MONEY_NAME = "money";
    private static final String STORY_NAME = "story";
    private static final String OWNED_PROPERTY_NAME = "property";
    private static final String EMPLOYEES_NAME = "employees";
    private static final String TIME_NAME = "time";
    private static File Save;
    private static File MoneySave;
    private static File StorySave;
    private static File OwnedPropertySave;
    private static File EmployeesSave;
    private static File TimeSave;

    public static void Prepare(Context context){
        Save = new File(context.getFilesDir(), SAVE_NAME);
        MoneySave = new File(context.getFilesDir(), MONEY_NAME);
        StorySave = new File(context.getFilesDir(), STORY_NAME);
        OwnedPropertySave = new File(context.getFilesDir(), OWNED_PROPERTY_NAME);
        EmployeesSave = new File(context.getFilesDir(), EMPLOYEES_NAME);
        TimeSave = new File(context.getFilesDir(), TIME_NAME);
    }

    public static void Save(HashMap<Point, Integer> hotelMap){
        try {
            FileWriter writer = new FileWriter(Save, false);
            for (HashMap.Entry<Point, Integer> entry : hotelMap.entrySet()) {
                writer.append(String.valueOf(entry.getKey().X)).append(" ");
                writer.append(String.valueOf(entry.getKey().Y)).append(" ");
                writer.append(String.valueOf(entry.getValue())).append('\n');
            }
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static HashMap<Point, Integer> Load(){
        BufferedReader reader;

        HashMap<Point, Integer> output = new HashMap<>();

        try {
            reader = new BufferedReader(new FileReader(Save));
            String line = reader.readLine();

            while (line != null) {

                String[] pieces = line.split(" ");

                if (pieces.length == 3){
                    int x = Integer.parseInt(pieces[0]);
                    int y = Integer.parseInt(pieces[1]);
                    int type = Integer.parseInt(pieces[2]);

                    output.put(new Point(x, y), type);
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            return new HashMap<>();
        }

        return output;
    }

    public static void SaveMoney(long money){
        try {
            FileWriter writer = new FileWriter(MoneySave, false);
            writer.append(String.valueOf(money));
            writer.append("\n");
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static long LoadMoney(){
        BufferedReader reader;

        long money;

        try {
            reader = new BufferedReader(new FileReader(MoneySave));
            String line = reader.readLine();
            money = Long.parseLong(line);
            reader.close();
        } catch (Exception e) {
            return 0;
        }

        return money;
    }

    public static int LoadStory(){
        BufferedReader reader;

        int level;

        try {
            reader = new BufferedReader(new FileReader(StorySave));
            String line = reader.readLine();
            level = Integer.parseInt(line);
            reader.close();
        } catch (Exception e) {
            return 0;
        }

        return level;
    }

    public static void SaveStory(int level){
        try {
            FileWriter writer = new FileWriter(StorySave, false);
            writer.append(String.valueOf(level));
            writer.append("\n");
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static void SaveOwnedProperty(Pair<Integer, Integer> p){
        try {
            FileWriter writer = new FileWriter(OwnedPropertySave, false);
            writer.append(String.valueOf(p.P1));
            writer.append("\n");
            writer.append(String.valueOf(p.P2));
            writer.append("\n");
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static Pair<Integer, Integer> LoadOwnedProperty() {
        BufferedReader reader;

        int x_min;
        int x_max;

        try {
            reader = new BufferedReader(new FileReader(OwnedPropertySave));
            x_min = Integer.parseInt(reader.readLine());
            x_max = Integer.parseInt(reader.readLine());
            reader.close();
        } catch (Exception e) {
            return new Pair<>(-2, 2);
        }

        return new Pair<>(x_min, x_max);
    }

    public static List<Integer> LoadEmployees() {
        BufferedReader reader;

        List<Integer> employees = new ArrayList<>();

        try {
            reader = new BufferedReader(new FileReader(EmployeesSave));
            String line = reader.readLine();

            while (line != null) {
                employees.add(Integer.parseInt(line));
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            return employees;
        }

        return employees;
    }

    public static void SaveEmployees(List<Integer> employees) {
        try {
            FileWriter writer = new FileWriter(EmployeesSave, false);

            for (int employee: employees) {
                writer.append(String.valueOf(employee)).append("\n");
            }

            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static void SaveTime(long time){
        try {
            FileWriter writer = new FileWriter(TimeSave, false);
            writer.append(String.valueOf(time));
            writer.append("\n");
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static long LoadTime(){
        BufferedReader reader;

        long time;

        try {
            reader = new BufferedReader(new FileReader(TimeSave));
            String line = reader.readLine();
            time = Long.parseLong(line);
            reader.close();
        } catch (Exception e) {
            return 0;
        }

        return time;
    }

}
