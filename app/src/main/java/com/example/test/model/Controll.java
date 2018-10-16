package com.example.test.model;


import java.util.HashMap;
import java.util.Map;

public class Controll {

    private String statePower;

    Controll(){

    }

    Controll(String statePower){
        this.statePower = statePower;
    }

    public String getStatePower() {
        return statePower;
    }

    public void setStatePower(String statePower) {
        this.statePower = statePower;
    }

    @Override
    public String toString() {
        return "Controll{" +
                "statePower='" + statePower + '\'' +
                '}';
    }

}
