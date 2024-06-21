package com.application.hci_project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.hci_project.R;
import com.application.hci_project.databinding.IngredientItemBinding;
import com.application.hci_project.datatypes.Ingredient;

import java.util.ArrayList;

public class IngredientListAdapter extends ArrayAdapter<Ingredient> {
    public IngredientListAdapter(@NonNull Context context, ArrayList<Ingredient> ingredientList) {
        super(context, R.layout.ingredient_item, ingredientList);
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

        if(ingredient.getQuantity()%1==0) quantity.setText(String.format("%.0f",ingredient.getQuantity()));
        else quantity.setText(ingredient.getQuantity().toString());

        measurement.setText(ingredient.getMeasurement());

        return view;
    }
}
