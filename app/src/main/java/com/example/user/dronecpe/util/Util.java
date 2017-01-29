package com.example.user.dronecpe.util;

import java.util.List;
import java.util.Random;

/**
 * Created by DEV on 29/1/2560.
 */

public class Util {
    private static Util instance;
    private Random random;

    public static Util getInstance() {
        if (instance == null) {
            instance = new Util();
        }
        return instance;
    }

    private Util() {
        random = new Random();
    }

    public <T> T randomObject(List<T> listObject) {
        int min = 0;
        int max = listObject.size() - 1;
        int i1 = random.nextInt(max - min + 1) + min;
        return listObject.get(i1);
    }

    public int randomNum(int max){
        int min = 1;
        return random.nextInt(max - min + 1) + min;
    }

}
