package com.application.hci_project.datatypes;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Instruction> instructions;

    public Recipe( ) {
        this.ingredients = new ArrayList<Ingredient>();
        this.instructions = new ArrayList<Instruction>();
    }
    public Recipe(String name) {
        this.name = name;
        this.ingredients = new ArrayList<Ingredient>();
        this.instructions = new ArrayList<Instruction>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void addIngredient(String name, Float quantity, String measurement){
        ingredients.add(new Ingredient(name,quantity,measurement));
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(ArrayList<Instruction> instructions) {
        this.instructions = instructions;
    }

    public void removeIngredient(int index){
        ingredients.remove(index);
    }

    public void updateIngredient(int index, String name,Float quantity, String measurement){
        ingredients.set(index, new Ingredient(name, quantity, measurement));
    }

    public void addInstruction(String description){
        instructions.add(new Instruction(description));
    }

    public void addInstruction(String description, Integer timer){
        instructions.add(new Instruction(description,timer));
    }

    public void removeInstruction(int index){
        instructions.remove(index);
    }

    public void updateInstruction(int index, String description){
        instructions.set(index, new Instruction(description));
    }

    public void updateInstruction(int index, String description, int timer){
        instructions.set(index, new Instruction(description,timer));
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", instructions=" + instructions +
                '}';
    }
}
