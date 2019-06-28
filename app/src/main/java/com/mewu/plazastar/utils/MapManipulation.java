package com.mewu.plazastar.utils;

import com.mewu.plazastar.GameState;
import com.mewu.plazastar.Library;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MapManipulation {


    public static List<Point> GetSpawnableRooms(){

        List<Point> output = new ArrayList<>();

        for(Map.Entry<Point, Integer> shop : GameState.Instance.HotelMap.entrySet()){
            if (Library.HasElevator(shop.getValue()))
                output.add(shop.getKey());
        }

        return output;
    }

    public static Point GetRandomRoom(Random random){
        Object[] rooms = GameState.Instance.HotelMap.entrySet().toArray();
        Point room = ((Map.Entry<Point, Integer>)rooms[random.nextInt(rooms.length)]).getKey();
        return room;
    }
}
