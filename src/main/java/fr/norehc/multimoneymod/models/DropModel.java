package fr.norehc.multimoneymod.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class DropModel {
    public ArrayList<String> selectedPokemon;
    public ArrayList<String> selectedPalette;
    public Map<String, Map<Integer, ValueDropModel>> values;

    public DropModel(ArrayList<String> selectedPokemon, ArrayList<String> selectedPalette, Map<String, Map<Integer, ValueDropModel>> values) {
        this.selectedPokemon = selectedPokemon;
        this.selectedPalette = selectedPalette;
        this.values = values;
    }
}
