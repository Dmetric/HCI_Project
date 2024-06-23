package com.application.hci_project.adapters;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.hci_project.ClientApplication;
import com.application.hci_project.R;
import com.application.hci_project.databinding.IngredientItemBinding;
import com.application.hci_project.datatypes.Ingredient;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> {
    private TextToSpeech tts;
    public IngredientListAdapter(@NonNull Context context, ArrayList<Ingredient> ingredientList, TextToSpeech tts) {
        super(context, R.layout.ingredient_item, ingredientList);
        this.tts=tts;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        IngredientItemBinding binding = IngredientItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        Ingredient ingredient = getItem(position);
        if (view == null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.ingredient_item,parent,false);
        }
        TextView name = view.findViewById(R.id.ingredientTxt);
        TextView quantity = view.findViewById(R.id.amountTxt);
        TextView measurement = view.findViewById(R.id.measurementTxt);
        name.setText(ingredient.getName());

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tts.speak(String.format("%s %s, %s", ingredient.quantityToString(), ClientApplication.getMeasurements().get(ingredient.getMeasurement()), ingredient.getName()),TextToSpeech.QUEUE_FLUSH,null,null);
                return true;
            }
        });

        quantity.setText(ingredient.quantityToString());
        measurement.setText(ingredient.getMeasurement());

        return view;
    }
}
