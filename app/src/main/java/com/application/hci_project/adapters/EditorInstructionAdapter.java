package com.application.hci_project.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.application.hci_project.R;
import com.application.hci_project.activities.RecipeEditor;
import com.application.hci_project.databinding.AddIngredientDialogBinding;
import com.application.hci_project.databinding.AddInstructionDialogBinding;
import com.application.hci_project.datatypes.Instruction;
import com.application.hci_project.widgets.RecyclerReorderCallback;

import java.util.ArrayList;
import java.util.Collections;


public class EditorInstructionAdapter extends RecyclerView.Adapter<EditorInstructionAdapter.EditorViewHolder> implements RecyclerReorderCallback.RecyclerReorderTouchHelper{

    private LayoutInflater inflater;
    private Context context;
    
    private ArrayList<Instruction> instructions;

    public EditorInstructionAdapter(Context context, ArrayList<Instruction> instructions) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.instructions=instructions;
    }

    @Override
    public EditorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.instruction_item, parent, false);
        EditorViewHolder holder = new EditorViewHolder(view,parent);
        return holder;
    }

    @Override
    public void onBindViewHolder(EditorViewHolder holder, int position) {
        holder.bind(instructions.get(position), position);
    }

    @Override
    public int getItemCount() {
        if (instructions != null)
            return instructions.size();
        else
            return 0;
    }

    //Interface implementation
    @Override
    public void onRowMoved(int from, int to) {
        if (from < to){
            for (int i =from; i<to;i++) {
                Collections.swap(instructions,i,i+1);
                notifyItemChanged(i+1);
            }
        }else{
            for (int i =from; i>to;i--){
                Collections.swap(instructions,i,i-1);
                notifyItemChanged(i-1);
            }
        }
        notifyItemMoved(from,to);
        notifyItemChanged(to);

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


    public class EditorViewHolder extends RecyclerView.ViewHolder implements CustomViewHolder
    {
        private TextView step;
        private TextView description;

        private ViewGroup parent;
        private CardView cardView;
        private ImageButton timerButton;

        public EditorViewHolder(View itemView, ViewGroup parent) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card);
            step = (TextView)itemView.findViewById(R.id.instructionNumber);
            description = (TextView)itemView.findViewById(R.id.instructionTxt);
            timerButton = (ImageButton)itemView.findViewById(R.id.timerButton);
        }
        @Override
        public CardView getCardView() {
            return cardView;
        }

        void bind(Instruction instruction, int position) {
            timerButton.setClickable(false);
            if(instruction.getHasTimer()) timerButton.setVisibility(View.VISIBLE);
            else timerButton.setVisibility(View.GONE);
            step.setText(Integer.toString(position+1)+". ");
            description.setText(instruction.getDescription());
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog(context);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    AddInstructionDialogBinding binding = AddInstructionDialogBinding.inflate(inflater,parent,false);
                    dialog.setContentView(binding.getRoot());
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    binding.addButton.setText("SAVE");
                    binding.addButton.setIcon(ContextCompat.getDrawable(context,R.drawable.save));
                    binding.cancelButton.setText("DELETE");
                    binding.cancelButton.setIcon(ContextCompat.getDrawable(context,R.drawable.delete));
                    binding.instructionInput.setText(instruction.getDescription());
                    if(instruction.getHasTimer()){
                        binding.timeCheckbox.setChecked(true);
                        binding.editHour.setText(Integer.toString(instruction.getTimer()/3600));
                        binding.editMin.setText(Integer.toString(instruction.getTimer()%3600/60));
                        binding.editSec.setText(Integer.toString(instruction.getTimer()%60));
                    }
                    binding.addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            int hours = RecipeEditor.getNumber(binding.editHour.getText().toString());
                            int minutes = RecipeEditor.getNumber(binding.editMin.getText().toString());
                            int seconds = RecipeEditor.getNumber(binding.editSec.getText().toString());
                            String description = binding.instructionInput.getText().toString();
                            boolean hasTimer = binding.timeCheckbox.isChecked();

                            if (description.trim().isEmpty()) {
                                binding.addButton.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
                                binding.addButton.playSoundEffect(SoundEffectConstants.CLICK);
                                Toast.makeText(context, "The description can't be empty", Toast.LENGTH_SHORT).show();
                            } else {
                                if (hasTimer) {
                                    if (hours == 0 && minutes == 0 && seconds == 0) {
                                        Toast.makeText(context, "You can't set a timer to 0", Toast.LENGTH_SHORT).show();
                                    } else if (minutes > 59 || seconds > 59) {
                                        Toast.makeText(context, "Minutes and seconds can't have a value above 59", Toast.LENGTH_SHORT).show();
                                    } else {
                                        instruction.setDescription(description);
                                        instruction.setTimer(RecipeEditor.toSeconds(hours, minutes, seconds));
                                        instruction.setHasTimer(true);
                                        notifyItemChanged(position);
                                        timerButton.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                } else {
                                    instruction.setHasTimer(false);
                                    instruction.setDescription(description);
                                    instruction.setTimer(-1);
                                    notifyItemChanged(position);
                                    timerButton.setVisibility(View.GONE);
                                    dialog.dismiss();
                                }
                            }
                        }
                    });
                    binding.cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            instructions.remove(position);
                            notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                };
            });
        }
    }
}
