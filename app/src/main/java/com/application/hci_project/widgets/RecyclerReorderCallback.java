package com.application.hci_project.widgets;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.application.hci_project.adapters.CustomViewHolder;

public class RecyclerReorderCallback extends ItemTouchHelper.Callback {
    private RecyclerReorderTouchHelper touchHelper;
    public RecyclerReorderCallback(RecyclerReorderTouchHelper touchHelper) {
        this.touchHelper=touchHelper;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }



    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlag,0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        this.touchHelper.onRowMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState!=ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder instanceof CustomViewHolder) {
                CustomViewHolder editorViewHolder = (CustomViewHolder) viewHolder;
                touchHelper.onRowSelected(editorViewHolder);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if(viewHolder instanceof CustomViewHolder) {
            CustomViewHolder editorViewHolder = (CustomViewHolder) viewHolder;
            touchHelper.onRowClear(editorViewHolder);
        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    public interface RecyclerReorderTouchHelper{
        void onRowMoved(int from, int to);
        void onRowSelected(CustomViewHolder viewHolder);
        void onRowClear(CustomViewHolder viewHolder);
    }
}
