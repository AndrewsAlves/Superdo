package com.andysapps.superdo.todo.enums;

/**
 * Created by Admin on 13,November,2019
 */
public enum  BucketColors {

    Red ("#F64F59"),
    Yellow ("#F0F64F"),
    Green("#4FF660"),
    Sky_blue("#4FD4F6"),
    Ink_blue("#4F55F6"),
    Rosa("#F64FF6"),
    Orange("#FF8B57");

    private final String name;

    BucketColors(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
