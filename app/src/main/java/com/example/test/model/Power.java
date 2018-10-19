package com.example.test.model;

public class Power {

    int power;
    int manual;

    public Power(int Power) {
        this.power = Power;
        this.manual = manual;
    }

    public int getPower() {
        return power;
    }

    public int getManual() {
        return manual;
    }

    @Override
    public String toString() {
        return "Power{" +
                "power=" + power +
                '}';
    }
}

