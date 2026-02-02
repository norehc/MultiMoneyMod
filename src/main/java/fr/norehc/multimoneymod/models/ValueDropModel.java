package fr.norehc.multimoneymod.models;

import java.util.Map;

public class ValueDropModel {
    public int valueMin;
    public int valueMax;
    public int probability;

    public ValueDropModel(int valueMin, int valueMax, int probability) {
        this.valueMin = valueMin;
        this.valueMax = valueMax;
        this.probability = probability;
    }
}
