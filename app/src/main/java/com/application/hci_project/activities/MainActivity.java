package com.application.hci_project.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Toast;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.application.hci_project.ClientApplication;
import com.application.hci_project.databinding.ActivityMainBinding;
import com.application.hci_project.adapters.RecipeListAdapter;
import com.application.hci_project.databinding.AddRecipeDialogBinding;
import com.application.hci_project.datatypes.Recipe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private RecipeListAdapter recipeListAdapter;
    private ArrayList<Recipe> recipes;
    
    private ClientApplication getApp(){
        return (ClientApplication)this.getApplication();
    }
    private ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            int result = o.getResultCode();
                            Intent data = o.getData();
                            if (result==RESULT_OK){
                                Uri uri = data.getData();
                                Log.d("IMPORT", "1");
                                getApp().importFile(getActivity(), uri);
                                recipeListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
            );

    private Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    @Override
    protected void onResume() {
        super.onResume();
        recipes = getApp().getRecipes();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        recipeListAdapter =new RecipeListAdapter(getActivity(), recipes);
        binding.recipeListView.setAdapter(recipeListAdapter);
        Log.d("Status", "working");

        binding.addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(false);
                AddRecipeDialogBinding dialogBinding = AddRecipeDialogBinding.inflate(getLayoutInflater());
                dialog.setContentView(dialogBinding.getRoot());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                dialogBinding.recipeNameInput.setFilters(new InputFilter[] {
                        new InputFilter() {
                            public CharSequence filter(CharSequence src, int start, int end, Spanned dst, int dstart, int dend) {
                                if (src.equals("")) {
                                    // Allow backspace
                                    return src;
                                }
                                if (src.toString().matches("[a-zA-Z0-9]+")) {
                                    // Allow only letters and numbers
                                    return src;
                                }
                                return ""; // Reject other characters
                            }
                        }
                });
                dialogBinding.addRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dialogBinding.recipeNameInput.getText().toString().trim().isEmpty()){
                            dialogBinding.addRecipeButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                            dialogBinding.addRecipeButton.playSoundEffect(SoundEffectConstants.CLICK);
                            Toast.makeText(getActivity(),"The recipe name can't be empty",Toast.LENGTH_SHORT).show();
                        }else if(getApp().alreadyExists(dialogBinding.recipeNameInput.getText().toString())){
                            Toast.makeText(getActivity(),"This recipe already exists",Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent = new Intent(getActivity(), RecipeEditor.class);
                            //Adding recipe as extra for new activity
                            intent.putExtra("type", "Create");
                            intent.putExtra("name", dialogBinding.recipeNameInput.getText().toString());
                            //Starting Activity
                            getActivity().startActivity(intent);
                            dialog.dismiss();
                        }
                    }
                });
                dialogBinding.cancelRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });
        binding.importRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFileIntent.setType("application/json");
                chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFileIntent = Intent.createChooser(chooseFileIntent, "Choose a file");
                activityResultLauncher.launch(chooseFileIntent);
            }
        });
    }
}