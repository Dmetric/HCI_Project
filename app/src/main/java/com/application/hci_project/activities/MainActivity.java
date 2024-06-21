package com.application.hci_project.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

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


    private Activity getActivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        recipes = ((ClientApplication) getActivity().getApplication()).getRecipes();
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
                dialogBinding.addRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dialogBinding.recipeNameInput.getText().toString().trim().isEmpty()){
                            dialogBinding.addRecipeButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                            dialogBinding.addRecipeButton.playSoundEffect(SoundEffectConstants.CLICK);
                            Toast.makeText(getActivity(),"The recipe name can't be empty",Toast.LENGTH_SHORT).show();
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
    }
}