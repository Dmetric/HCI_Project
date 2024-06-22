package com.application.hci_project.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.application.hci_project.ClientApplication;
import com.application.hci_project.R;
import com.application.hci_project.adapters.EditorIngredientAdapter;
import com.application.hci_project.adapters.EditorInstructionAdapter;
import com.application.hci_project.databinding.AddIngredientDialogBinding;
import com.application.hci_project.databinding.AddInstructionDialogBinding;
import com.application.hci_project.databinding.AddRecipeDialogBinding;
import com.application.hci_project.databinding.RecipeEditorBinding;
import com.application.hci_project.databinding.ViewerOptionsBinding;
import com.application.hci_project.datatypes.Ingredient;
import com.application.hci_project.datatypes.Instruction;
import com.application.hci_project.datatypes.Recipe;
import com.application.hci_project.decorations.Divider;
import com.application.hci_project.widgets.RecyclerReorderCallback;

import java.util.ArrayList;

public class RecipeEditor extends AppCompatActivity {
    private Recipe recipe;
    private int position=-1;
    private ArrayList<Instruction> instructions;
    private ArrayList<Ingredient> ingredients;

    private String recipeName;
    private RecipeEditorBinding binding;
    private EditorInstructionAdapter instructionAdapter;
    private EditorIngredientAdapter ingredientAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RecipeEditorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        if (intent.getSerializableExtra("type").toString().equals("Edit")) {
            position = (int) intent.getSerializableExtra("recipe");
            recipe = ((ClientApplication) getActivity().getApplication()).getRecipe(position);
            instructions = new ArrayList<Instruction>(recipe.getInstructions());
            ingredients = new ArrayList<Ingredient>(recipe.getIngredients());
            recipeName = recipe.getName();
            binding.recipeNameTXT.setText(recipeName);
        } else {
            recipeName = (String) intent.getSerializableExtra("name");
            recipe = new Recipe(recipeName);
            binding.recipeNameTXT.setText(recipe.getName());
            instructions = new ArrayList<Instruction>();
            ingredients = new ArrayList<Ingredient>();
        }

        //Setup recycler for instructions
        binding.instructionRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.instructionRecycler.addItemDecoration(new Divider(10, 10));
        instructionAdapter = new EditorInstructionAdapter(this, instructions);
        ItemTouchHelper.Callback callbackInstructions = new RecyclerReorderCallback(instructionAdapter);
        ItemTouchHelper touchHelperInstructions = new ItemTouchHelper(callbackInstructions);
        touchHelperInstructions.attachToRecyclerView(binding.instructionRecycler);
        binding.instructionRecycler.setAdapter(instructionAdapter);

        //Setup recycler for ingredients
        binding.ingredientRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.ingredientRecycler.addItemDecoration(new Divider(10, 10));
        ingredientAdapter = new EditorIngredientAdapter(this, ingredients);
        ItemTouchHelper.Callback callbackIngredients = new RecyclerReorderCallback(ingredientAdapter);
        ItemTouchHelper touchHelperIngredients = new ItemTouchHelper(callbackIngredients);
        touchHelperIngredients.attachToRecyclerView(binding.ingredientRecycler);
        binding.ingredientRecycler.setAdapter(ingredientAdapter);

        //Collapse Ingredients
        binding.collapseIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.ingredientRecycler.getVisibility() == View.GONE) {
                    binding.ingredientRecycler.setVisibility(View.VISIBLE);
                    binding.addIngredientButton.setVisibility(View.VISIBLE);
                    binding.ingredientCard.getLayoutParams().height = 0;
                    binding.collapseIngredients.setImageResource(R.drawable.arrow_up);
                } else {
                    binding.ingredientRecycler.setVisibility(View.GONE);
                    binding.addIngredientButton.setVisibility(View.GONE);
                    binding.ingredientCard.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.collapseIngredients.setImageResource(R.drawable.arrow_down);
                }
            }
        });
        //Collapse Instructions
        binding.collapseSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.instructionRecycler.getVisibility() == View.GONE) {
                    binding.instructionRecycler.setVisibility(View.VISIBLE);
                    binding.addInstructionButton.setVisibility(View.VISIBLE);
                    binding.instructionCard.getLayoutParams().height = 0;
                    binding.collapseSteps.setImageResource(R.drawable.arrow_up);
                } else {
                    binding.instructionRecycler.setVisibility(View.GONE);
                    binding.addInstructionButton.setVisibility(View.GONE);
                    binding.instructionCard.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.collapseSteps.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        //Edit Name
        binding.editRecipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(false);
                AddRecipeDialogBinding dialogBinding = AddRecipeDialogBinding.inflate(getLayoutInflater());
                dialog.setContentView(dialogBinding.getRoot());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialogBinding.recipeNameInput.setText(recipeName);
                dialogBinding.addRecipeButton.setText("SAVE");
                dialogBinding.addRecipeButton.setIcon(getDrawable(R.drawable.checkbox));
                dialog.show();
                dialogBinding.addRecipeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogBinding.recipeNameInput.getText().toString().trim().isEmpty()) {
                            dialogBinding.addRecipeButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                            dialogBinding.addRecipeButton.playSoundEffect(SoundEffectConstants.CLICK);
                            Toast.makeText(getActivity(), "The recipe name can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            recipeName = (dialogBinding.recipeNameInput.getText().toString());
                            binding.recipeNameTXT.setText(dialogBinding.recipeNameInput.getText().toString());
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

        //Add Ingredient Button Behavior
        binding.addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(RecipeEditor.this);
                dialog.setCancelable(false);
                AddIngredientDialogBinding ingredientDialogBinding = AddIngredientDialogBinding.inflate(getLayoutInflater());
                dialog.setContentView(ingredientDialogBinding.getRoot());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RecipeEditor.this,
                        android.R.layout.simple_spinner_item, // Layout for spinner item
                        new String[]{"", "tsp", "tbsp", "cup", "mL", "L", "mg", "g", "kg"});
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ingredientDialogBinding.measurementSpinner.setAdapter(adapter);
                dialog.show();
                ingredientDialogBinding.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = ingredientDialogBinding.ingredientNameInput.getText().toString();
                        String quantity = ingredientDialogBinding.quantityInput.getText().toString();
                        String measurement = ingredientDialogBinding.measurementSpinner.getSelectedItem().toString();
                        Log.d("EDITOR", "Selected " + measurement);
                        if (name.trim().isEmpty() || quantity.trim().isEmpty()) {
                            ingredientDialogBinding.addButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                            ingredientDialogBinding.addButton.playSoundEffect(SoundEffectConstants.CLICK);
                            Toast.makeText(RecipeEditor.this, "The name and quantity fields can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Float.parseFloat(quantity) == 0) {
                                Toast.makeText(RecipeEditor.this, "You can't set quantity to 0", Toast.LENGTH_SHORT).show();
                            } else {
                                ingredients.add(new Ingredient(name, Float.parseFloat(quantity), measurement));
                                ingredientAdapter.notifyItemInserted(ingredients.size() - 1);
                                dialog.dismiss();
                            }
                        }
                    }
                });
                ingredientDialogBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        //Add Instruction Button Behavior
        binding.addInstructionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(RecipeEditor.this);
                dialog.setCancelable(false);
                AddInstructionDialogBinding instructionDialogBinding = AddInstructionDialogBinding.inflate(getLayoutInflater());
                dialog.setContentView(instructionDialogBinding.getRoot());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                instructionDialogBinding.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int hours = getNumber(instructionDialogBinding.editHour.getText().toString());
                        int minutes = getNumber(instructionDialogBinding.editMin.getText().toString());
                        int seconds = getNumber(instructionDialogBinding.editSec.getText().toString());
                        String description = instructionDialogBinding.instructionInput.getText().toString();
                        boolean hasTimer = instructionDialogBinding.timeCheckbox.isChecked();

                        if (description.trim().isEmpty()) {
                            instructionDialogBinding.addButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                            instructionDialogBinding.addButton.playSoundEffect(SoundEffectConstants.CLICK);
                            Toast.makeText(RecipeEditor.this, "The description can't be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            if (hasTimer) {
                                if (hours == 0 && minutes == 0 && seconds == 0) {
                                    Toast.makeText(RecipeEditor.this, "You can't set a timer to 0", Toast.LENGTH_SHORT).show();
                                } else if (minutes > 59 || seconds > 59) {
                                    Toast.makeText(RecipeEditor.this, "Minutes and seconds can't have a value above 59", Toast.LENGTH_SHORT).show();
                                } else {
                                    instructions.add(new Instruction(description, toSeconds(hours, minutes, seconds)));
                                    instructionAdapter.notifyItemInserted(instructions.size() - 1);
                                    dialog.dismiss();
                                }
                            } else {
                                instructions.add(new Instruction(description));
                                instructionAdapter.notifyItemInserted(instructions.size() - 1);
                                dialog.dismiss();
                            }
                        }
                    }
                });
                instructionDialogBinding.cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

        //Options Button
        binding.optButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                ViewerOptionsBinding dialogBinding = ViewerOptionsBinding.inflate(getLayoutInflater());
                dialog.setContentView(dialogBinding.getRoot());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                //Repurpose edit button as save button
                dialogBinding.editButton.setText("SAVE");
                dialogBinding.editButton.setIcon(getDrawable(R.drawable.save));
                dialogBinding.editButton.setBackgroundColor(getColor(R.color.green));
                //Repurpose share button as delete button
                dialogBinding.shareButton.setText("DELETE");
                dialogBinding.shareButton.setIcon(getDrawable(R.drawable.delete));
                dialogBinding.shareButton.setBackgroundColor(getColor(R.color.red));
                dialog.show();
                dialogBinding.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recipe.setIngredients(ingredients);
                        recipe.setInstructions(instructions);
                        if (position == -1)
                            ((ClientApplication) getActivity().getApplication()).addRecipe(recipe);
                        else
                            ((ClientApplication)getActivity().getApplication()).updateRecipe(position,recipeName);
                        setResult(1);
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
                dialogBinding.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position == -1) {
                            getActivity().finish();
                        } else {
                            setResult(2);
                            ((ClientApplication) getActivity().getApplication()).removeRecipe(position);
                            dialog.dismiss();
                            getActivity().finish();
                        }
                    }
                });
            }
        });

    }

    private Activity getActivity() {
        return this;
    }

    public static int getNumber(String number){
        if(number.trim().isEmpty()) return 0;
        return Integer.parseInt(number);
    }
    public static int toSeconds(int h, int m, int s){
        return h*3600+m*60+s;
    }
}

