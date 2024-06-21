package com.application.hci_project;

import android.app.Application;

import com.application.hci_project.datatypes.Recipe;

import java.util.ArrayList;

public class ClientApplication extends Application {
    private ArrayList<Recipe> recipes= new ArrayList<Recipe>();

    @Override
    public void onCreate() {
        super.onCreate();
        this.initializeList();
    }



    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public Recipe getRecipe(int i){
        return recipes.get(i);
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void addRecipe(Recipe recipe){
        recipes.add(recipe);
    }

    public void removeRecipe(int position){
        recipes.remove(position);
    }
    public void setRecipe(int i,Recipe recipe) {
        recipes.set(i,recipe);
    }

    private void initializeList(){
        Recipe pizza = new Recipe("Pizza");
        Recipe burger = new Recipe("Burger");

        pizza.addIngredient("Flour", (float)100.55555555, "g");
        pizza.addIngredient("Water", (float)100, "mL");
        pizza.addIngredient("Soda", (float)100, "mL");
        pizza.addIngredient("Juice", (float)100, "mL");
        pizza.addIngredient("Cola", (float)100, "mL");
        pizza.addIngredient("Milk", (float)100, "mL");
        pizza.addIngredient("Canned Tomatoes", (float)100, "mL");
        pizza.addInstruction("Make dough");
        pizza.addInstruction("Bake Pizza");

        burger.addIngredient("Beef", (float)200, "g");
        burger.addIngredient("Buns", (float)2, "");
        burger.addInstruction("Cook beef", 15);
        burger.addInstruction("Assemble burger");
        recipes.add(pizza);
        recipes.add(burger);
    }
}
