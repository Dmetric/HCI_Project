package com.application.hci_project.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.hci_project.ClientApplication;
import com.application.hci_project.R;
import com.application.hci_project.activities.RecipeEditor;
import com.application.hci_project.databinding.AddIngredientDialogBinding;
import com.application.hci_project.datatypes.Ingredient;
import com.application.hci_project.widgets.RecyclerReorderCallback;

import java.util.ArrayList;
import java.util.Collections;

public class EditorIngredientAdapter extends RecyclerView.Adapter<EditorIngredientAdapter.EditorViewHolder> implements RecyclerReorderCallback.RecyclerReorderTouchHelper{

    private LayoutInflater inflater;
    private Context context;

    private TextToSpeech tts;

    private ArrayList<Ingredient> ingredients;

    public EditorIngredientAdapter(Context context, ArrayList<Ingredient> ingredients, TextToSpeech tts) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.ingredients = ingredients;
        this.tts=tts;
    }

    @Override
    public EditorIngredientAdapter.EditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ingredient_item, parent, false);
        EditorIngredientAdapter.EditorViewHolder holder = new EditorIngredientAdapter.EditorViewHolder(view,parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(EditorIngredientAdapter.EditorViewHolder holder, int position) {

        holder.bind(ingredients.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (ingredients != null)
            return ingredients.size();
        else
            return 0;
    }



    //Interface implementation
    @Override
    public void onRowMoved(int from, int to) {
        if (from < to){
            for (int i =from; i<to;i++) Collections.swap(ingredients,i,i+1);
        }else{
            for (int i =from; i>to;i--) Collections.swap(ingredients,i,i-1);
        }
        notifyItemMoved(from,to);
    }

    @Override
    public void onRowSelected(CustomViewHolder viewHolder) {
        viewHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(context, R.color.orange_l2));
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onRowClear(CustomViewHolder viewHolder) {
        viewHolder.getCardView().setCardBackgroundColor(ContextCompat.getColor(context, R.color.orange_l));
    }

    class EditorViewHolder extends RecyclerView.ViewHolder implements CustomViewHolder{
        private CheckBox box;
        private CardView cardView;
        private TextView quantity;
        private TextView measurement;
        private TextView name;
        private ViewGroup parent;

        public EditorViewHolder(View itemView, ViewGroup parent) {
            super(itemView);
            this.parent=parent;
            cardView = (CardView) itemView.findViewById(R.id.card);
            box = (CheckBox) itemView.findViewById(R.id.checkBox2);
            quantity = (TextView) itemView.findViewById(R.id.amountTxt);
            measurement = (TextView) itemView.findViewById(R.id.measurementTxt);
            name = (TextView) itemView.findViewById(R.id.ingredientTxt);

        }

        
        @Override
        public CardView getCardView() {
            return cardView;
        }

        void bind(Ingredient ingredient, int position) {
            box.setVisibility(View.GONE);
            if(ingredient.getQuantity()%1==0) quantity.setText(String.format("%.0f",ingredient.getQuantity()));
            else quantity.setText(ingredient.getQuantity().toString());
            //quantity.setPadding(8,0,4,0);
            measurement.setText(ingredient.getMeasurement());
            name.setText(ingredient.getName());

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    tts.speak(String.format("%s %s, %s",ingredient.quantityToString(),ClientApplication.getMeasurements().get(ingredient.getMeasurement()),ingredient.getName()),TextToSpeech.QUEUE_FLUSH,null,null);
                    return true;
                }
            });

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    AddIngredientDialogBinding binding = AddIngredientDialogBinding.inflate(inflater,parent,false);
                    dialog.setContentView(binding.getRoot());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                            android.R.layout.simple_spinner_item, // Layout for spinner item
                            new String[]{"", "tsp", "tbsp", "cup", "mL", "L", "mg", "g", "kg"});
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.measurementSpinner.setAdapter(adapter);
                    binding.addButton.setText("SAVE");
                    binding.addButton.setIcon(ContextCompat.getDrawable(context,R.drawable.save));
                    binding.cancelButton.setText("DELETE");
                    binding.cancelButton.setIcon(ContextCompat.getDrawable(context,R.drawable.delete));
                    binding.ingredientNameInput.setText(ingredient.getName());
                    binding.quantityInput.setText(ingredient.getQuantity().toString());
                    binding.measurementSpinner.setSelection(adapter.getPosition(ingredient.getMeasurement()));
                    binding.addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String name = binding.ingredientNameInput.getText().toString();
                            String quantity = binding.quantityInput.getText().toString();
                            String measurement = binding.measurementSpinner.getSelectedItem().toString();
                            if (name.trim().isEmpty() || quantity.trim().isEmpty()) {
                                binding.addButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                                binding.addButton.playSoundEffect(SoundEffectConstants.CLICK);
                                Toast.makeText(context, "The name and quantity fields can't be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (Float.parseFloat(quantity) == 0) {
                                    Toast.makeText(context, "You can't set quantity to 0", Toast.LENGTH_SHORT).show();
                                } else {
                                    ingredient.setName(name);
                                    ingredient.setQuantity(Float.parseFloat(quantity));
                                    ingredient.setMeasurement(measurement);
                                    notifyItemChanged(position);
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                    binding.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ingredients.remove(position);
                            //notifyItemRemoved(position);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    binding.addButton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            tts.speak("Save changes to ingredient",TextToSpeech.QUEUE_FLUSH,null,null);
                            return true;
                        }
                    });

                    binding.cancelButton.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            tts.speak("Delete ingredient",TextToSpeech.QUEUE_FLUSH,null,null);
                            return true;
                        }
                    });

                    binding.ingredientNameInput.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            tts.speak( binding.ingredientNameInput.getText().toString(),TextToSpeech.QUEUE_FLUSH,null,null);
                            return true;
                        }
                    });

                    binding.quantityInput.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if(!binding.quantityInput.getText().toString().isEmpty())
                                tts.speak(Ingredient.floatToString(Float.parseFloat(binding.quantityInput.getText().toString())),TextToSpeech.QUEUE_FLUSH,null,null);
                            return true;
                        }
                    });

                    binding.measurementSpinner.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            tts.speak(ClientApplication.getMeasurements().get(binding.measurementSpinner.getSelectedItem().toString()),TextToSpeech.QUEUE_FLUSH,null,null);
                            return true;
                        }
                    });
                    dialog.show();
                };
            });
        }
    }
}
