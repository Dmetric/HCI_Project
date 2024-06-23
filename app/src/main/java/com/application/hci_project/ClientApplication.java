package com.application.hci_project;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.application.hci_project.datatypes.Recipe;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class ClientApplication extends Application {
    private ArrayList<Recipe> recipes= new ArrayList<Recipe>();
    private Boolean ttsEnabled;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private int size;
    private static HashMap<String, String> measurements =
            new HashMap<String, String>(){{
                put("","");
                put("tsp","teaspoon");
                put("tbsp","tablespoon");
                put("cup","cup");
                put("mL","millilitres");
                put("L","litres");
                put("mg","milligrams");
                put("g","grams");
                put("kg","kilograms");
    }};
    private TextToSpeech tts;


    @Override
    public void onCreate() {
        super.onCreate();
        preferences =getSharedPreferences(this.getPackageName(),MODE_PRIVATE);
        editor= preferences.edit();
        if(preferences.contains("size"))Log.d("APP", "size found");
        if(preferences.contains("tts"))Log.d("APP", "tts found");

        size=preferences.getInt("size",1);
        ttsEnabled=preferences.getBoolean("tts",true);
//        this.initializeList();
//        clearPrivateStorage();
//        for(Recipe recipe: recipes){
//            saveToInternalStorage(recipe);
//        }
        listInitFromFile();
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    tts.setLanguage(Locale.ENGLISH);
                    tts.setVoice(new ArrayList<>(tts.getVoices()).get(7)); //F (2,9) M(7) Normal,6 Italian, 37 38 58 85 (105 greek)
                    Log.d("TTS", "Initialized");
                } else {
                    // Failed to initialize TTS engine.
                    Log.d("TTS", "Failed initialization");
                }
            }
        });
    }

//    public void destroy(){
//        tts.shutdown();
//    }

    public Boolean isTTSEnabled() {
        return ttsEnabled;
    }

    public void setTTS(Boolean ttsEnabled) {
        this.ttsEnabled = ttsEnabled;
        editor.putBoolean("tts",ttsEnabled);
        editor.apply();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        editor.putInt("size",size);
        editor.apply();
    }

    public ArrayList<Recipe> getRecipes() {
        return recipes;
    }

    public static HashMap<String, String> getMeasurements() {
        return measurements;
    }

    public TextToSpeech getTts() {
        return tts;
    }

    public Recipe getRecipe(int i){
        return recipes.get(i);
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipes = recipes;
    }

    public boolean alreadyExists(String name){
        for(Recipe recipe: recipes){
            if (recipe.getName().toLowerCase().equals(name.toLowerCase())) return true;
        }
        return false;
    }
    public void addRecipe(Recipe recipe){
        Boolean alreadyExists = false;
        for(int i =0;i<recipes.size();i++){
            if(recipes.get(i).getName().toLowerCase().equals(recipe.getName().toLowerCase())){
                recipes.set(i,recipe);
                alreadyExists=true;
                break;
            }
        }
        if (!alreadyExists) recipes.add(recipe);
        Gson gson = new Gson();
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(nameAsFile(recipe.getName()), Context.MODE_PRIVATE));
            outputStreamWriter.write(gson.toJson(recipe));
            outputStreamWriter.close();
            Log.d("FILEADDED","nameAsFile(recipe.getName())");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeRecipe(int position){
        File file = new File(getFilesDir(),nameAsFile(recipes.get(position).getName()));
        file.delete();
        recipes.remove(position);
    }
    public void setRecipe(int i,Recipe recipe) {
        recipes.set(i,recipe);
    }

    private void saveToInternalStorage(Recipe recipe){
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(nameAsFile(recipe.getName()), Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Log.d("JSON", json);
    }

    public static String nameAsFile(String name){
        return String.format("recipe_%s.json", name.replaceAll("\\s+", "_").toLowerCase());
    }
    public void updateRecipe(int position, String newName){
        Gson gson = new Gson();
        File file = new File(getFilesDir(),nameAsFile(recipes.get(position).getName()));
        file.delete();
        if(!recipes.get(position).getName().toLowerCase().equals(newName.toLowerCase())) {
            recipes.get(position).setName(newName);
            file.renameTo(new File(getFilesDir(),nameAsFile(newName)));
        }
        String json = gson.toJson(recipes.get(position));
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(nameAsFile(newName), Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.close();
            Log.d("FILEEDIT", "Success");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void clearPrivateStorage(){
        File[] files = getFilesDir().listFiles();
        for (File file : files){
            file.delete();
        }
    }

    public File getFile(int position){
        File file = new File(getFilesDir(),nameAsFile(recipes.get(position).getName()));
        return file;
    }
    private String readFromFile(String file) {
        String ret = "";
        try {
            Log.d("IMPORT", "3");
            FileInputStream inputStream = openFileInput(file);
            Log.d("IMPORT", "4");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("Read JSON", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("Read JSON", "Error reading file: " + e.toString());
        }
        return ret;
    }

    public void importFile(Context context, Uri uri){
        Gson gson = new Gson();
        StringBuilder content = new StringBuilder();
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Recipe recipe = gson.fromJson(content.toString(),Recipe.class);
        addRecipe(recipe);
    }


    private void listInitFromFile(){
        Gson gson = new Gson();
        File[] files = getFilesDir().listFiles();
        for (File file : files){
            if (file.getName().matches("^recipe_.*\\.json$")){
                Log.d("FILEFOUND", file.getName());
                recipes.add(gson.fromJson(readFromFile(file.getName()), Recipe.class));
            }
        }
    }

    private void initializeList(){
        Recipe pizza = new Recipe("Pizza with gots   and peppers");
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
