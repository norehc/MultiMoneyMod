package fr.norehc.multimoneymod.models;

import java.util.Map;

public class MoneyModel {
    public String name;
    public String symbol;
    public Map<String, DropModel> dropModel;

    public MoneyModel(String name, String symbol, Map<String, DropModel> dropModel) {
        this.name = name;
        this.symbol = symbol;
        this.dropModel = dropModel;
    }
}
