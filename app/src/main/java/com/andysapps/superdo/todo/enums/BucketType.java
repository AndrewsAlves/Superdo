package com.andysapps.superdo.todo.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrews on 16,November,2019
 */
public enum BucketType {
    tasks(0),
    gym(1),
    personal(2),
    work(3),
    house(4),
    love(5),
    travel(6),
    party(7),
    wedding(8),
    money(9),
    shopping(10),
    music(11),
    sports(12),
    repair(13),
    pets(14);

    private int value;
    private static HashMap<Integer, BucketType> map = new HashMap<>();

    private BucketType(int value) {
        this.value = value;
    }

    static {
        for (BucketType pageType : BucketType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static BucketType valueOf(int pageType) {
        return (BucketType) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
