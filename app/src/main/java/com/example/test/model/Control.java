package com.example.test.model;

public class Control {

    int power;

    public Control(int control) {
        this.power = control;
    }

    public int getPower() {
        return power;
    }

    @Override
    public String toString() {
        return "Control{" +
                "power=" + power +
                '}';
    }
}
