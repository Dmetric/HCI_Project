package com.application.hci_project.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.provider.AlarmClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.application.hci_project.R;
import com.application.hci_project.databinding.InstructionItemBinding;
import com.application.hci_project.datatypes.Ingredient;
import com.application.hci_project.datatypes.Instruction;
import com.application.hci_project.datatypes.Instruction;

import java.util.ArrayList;

public class InstructionListAdapter extends ArrayAdapter<Instruction> {
    public InstructionListAdapter(@NonNull Context context, ArrayList<Instruction> instructionList) {
        super(context, R.layout.instruction_item, instructionList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        InstructionItemBinding binding = InstructionItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
        Instruction instruction = getItem(position);
        if (view == null){
            view= LayoutInflater.from(getContext()).inflate(R.layout.instruction_item,parent,false);
        }
        ImageButton timer = view.findViewById(R.id.timerButton);
        TextView text = view.findViewById(R.id.instructionTxt);
        TextView step = view.findViewById(R.id.instructionNumber);
        if (instruction.getHasTimer()) timer.setVisibility(View.VISIBLE);
        else timer.setVisibility(View.GONE);
        text.setText(instruction.getDescription());
        step.setText(Integer.toString(position+1)+". ");

        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openTimerIntent = new Intent(AlarmClock.ACTION_SET_TIMER);
                openTimerIntent.putExtra(AlarmClock.EXTRA_LENGTH, instruction.getTimer()); // Set the timer length to 60 seconds
                openTimerIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true); // Skip the timer setup UI
                getContext().startActivity(openTimerIntent);
            }
        });

        return view;
    }
}
