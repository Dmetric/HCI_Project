package com.application.hci_project.adapters;

import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.hci_project.R;
import com.application.hci_project.activities.RecipeViewer;
import com.application.hci_project.datatypes.Recipe;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
    private TextToSpeech tts;
    public RecipeListAdapter(@NonNull Context context, ArrayList<Recipe> recipeList, TextToSpeech tts) {
        super(context, R.layout.recipe_item, recipeList);
        this.tts = tts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        Recipe recipe = getItem(position);
        if (view == null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.recipe_item,parent,false);
        }
        Button button = view.findViewById(R.id.recipeNameTXT);
        button.setText(recipe.getName());
        button.setTag(position);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tts.speak(getItem(position).getName(),TextToSpeech.QUEUE_FLUSH,null,null);
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent for starting new activity
                Intent intent = new Intent(getContext(), RecipeViewer.class);
                //Adding recipe as extra for new activity
                Log.d("STATUS", "Intent Created");
                intent.putExtra("recipe", position);
                Log.d("STATUS", "Extras Added");
                //Starting Activity
                getContext().startActivity(intent);
                Log.d("STATUS", "Activity Started");
            }
        });





        return view;
    }


}
