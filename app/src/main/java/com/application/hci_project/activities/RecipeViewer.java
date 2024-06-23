package com.application.hci_project.activities;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.application.hci_project.ClientApplication;
import com.application.hci_project.R;
import com.application.hci_project.adapters.IngredientListAdapter;
import com.application.hci_project.adapters.InstructionListAdapter;
import com.application.hci_project.databinding.RecipeEditorBinding;
import com.application.hci_project.databinding.RecipeViewerBinding;
import com.application.hci_project.databinding.ViewerOptionsBinding;
import com.application.hci_project.datatypes.Recipe;

public class RecipeViewer extends AppCompatActivity {
    private Recipe recipe;
    private RecipeViewerBinding binding;
    private int position;
    private IngredientListAdapter ingredientListAdapter;
    private InstructionListAdapter instructionListAdapter;
    
    private TextToSpeech tts;

    private ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult o) {
                            int result = o.getResultCode();
                            Intent data = o.getData();
                            if (result==2)
                                getActivity().finish();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tts=getApp().getTts();
    }

    private ClientApplication getApp() {
        return (ClientApplication)getActivity().getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();

        switch (getApp().getSize()){
            case 0:
                this.setTheme(R.style.FontSizeSmall);
                binding = RecipeViewerBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                binding.recipeNameTXT.setTextAppearance(R.style.FontSizeMedium);
                binding.ingredientsTxt.setTextAppearance(R.style.FontSizeMedium);
                binding.stepsTxt.setTextAppearance(R.style.FontSizeMedium);
                break;
            case 1:
                this.setTheme(R.style.FontSizeMedium);
                binding = RecipeViewerBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                binding.recipeNameTXT.setTextAppearance(R.style.FontSizeLarge);
                binding.ingredientsTxt.setTextAppearance(R.style.FontSizeLarge);
                binding.stepsTxt.setTextAppearance(R.style.FontSizeLarge);
                break;
            case 2:
                this.setTheme(R.style.FontSizeLarge);
                binding = RecipeViewerBinding.inflate(getLayoutInflater());
                setContentView(binding.getRoot());
                binding.recipeNameTXT.setTextAppearance(R.style.FontSizeLarge);
                binding.ingredientsTxt.setTextAppearance(R.style.FontSizeLarge);
                binding.stepsTxt.setTextAppearance(R.style.FontSizeLarge);
                break;
        }
        Intent intent = getIntent();
        position = (int) intent.getSerializableExtra("recipe");
        //Get Recipe from extras
        recipe = getApp().getRecipe(position);

        //Set Recipe name to TextView
        binding.recipeNameTXT.setText(recipe.getName());

        //Add Adapter to Ingredient List
        ingredientListAdapter=new IngredientListAdapter(getActivity(), recipe.getIngredients(),tts,getApp().isTTSEnabled());
        binding.ingredientListView.setAdapter(ingredientListAdapter);

        //Add Adapter to Instruction List
        instructionListAdapter=new InstructionListAdapter(getActivity(), recipe.getInstructions(), tts,getApp().isTTSEnabled());
        binding.instructionListView.setAdapter(instructionListAdapter);

        //Collapse for cards
        binding.collapseIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.ingredientListView.getVisibility()== View.GONE){
                    binding.ingredientListView.setVisibility(View.VISIBLE);
                    binding.ingredientCard.getLayoutParams().height= 0;
                    binding.collapseIngredients.setImageResource(R.drawable.arrow_up);
                }else{
                    binding.ingredientListView.setVisibility(View.GONE);
                    binding.ingredientCard.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.collapseIngredients.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        binding.collapseSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.instructionListView.getVisibility()== View.GONE){
                    binding.instructionListView.setVisibility(View.VISIBLE);
                    binding.instructionCard.getLayoutParams().height= 0;
                    binding.collapseSteps.setImageResource(R.drawable.arrow_up);
                }else{
                    binding.instructionListView.setVisibility(View.GONE);
                    binding.instructionCard.getLayoutParams().height= ViewGroup.LayoutParams.WRAP_CONTENT;
                    binding.collapseSteps.setImageResource(R.drawable.arrow_down);
                }
            }
        });

        //Text to speech
        binding.ingredientsTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(getApp().isTTSEnabled())      
                    tts.speak("Ingredients",TextToSpeech.QUEUE_FLUSH,null,null);
                return true;
            }
        });
        binding.stepsTxt.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(getApp().isTTSEnabled())      
                    tts.speak("Instructions",TextToSpeech.QUEUE_FLUSH,null,null);
                return true;
            }
        });

        binding.recipeNameTXT.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(getApp().isTTSEnabled())      
                    tts.speak(recipe.getName(),TextToSpeech.QUEUE_FLUSH,null,null);
                return true;
            }
        });


        //Options
        binding.optButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(getActivity());
                dialog.setCancelable(true);
                ViewerOptionsBinding dialogBinding = ViewerOptionsBinding.inflate(getLayoutInflater());
                dialog.setContentView(dialogBinding.getRoot());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.show();
                dialogBinding.editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), RecipeEditor.class);
                        //Adding recipe as extra for new activity
                        intent.putExtra("type", "Edit");
                        intent.putExtra("recipe", position);
                        //Starting Activity
                        activityResultLauncher.launch(intent);
                        dialog.dismiss();
                    }
                });
                dialogBinding.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Get the URI of your JSON file (replace 'fileItem' with your actual file)
                        Uri uri = FileProvider.getUriForFile(getActivity(), "com.application.hci_project.fileprovider", ((ClientApplication)getActivity().getApplication()).getFile(position));

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("application/json");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        // Display the Sharesheet
                        Intent chooserIntent = Intent.createChooser(shareIntent, "Share JSON file");
                        startActivity(chooserIntent);
                        dialog.dismiss();
                    }
                });

                //Text to speech
                dialogBinding.editButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(getApp().isTTSEnabled())      
                    tts.speak("Edit Recipe", TextToSpeech.QUEUE_FLUSH,null,null);
                        return true;
                    }
                });

                dialogBinding.shareButton.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(getApp().isTTSEnabled())      
                    tts.speak("Share Recipe", TextToSpeech.QUEUE_FLUSH,null,null);
                        return true;
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    //    @Override
//    protected void onPause() {
//        super.onPause();
//        getActivity().finish();
//    }

    private Activity getActivity() {
        return this;
    }
}
