package com.toddway.sandbox;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;


public class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter<T>.ViewHolder> {
    private List<T> items = Collections.emptyList();
    private SparseBooleanArray selectedItems;
    private OnItemClickListener<T> onItemClickListener;


    @Override
    public BaseRecyclerAdapter<T>.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter<T>.ViewHolder holder, int position) {
        holder.populate(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateList(List<T> thingList) {
        this.items = thingList;
        notifyDataSetChanged();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public static interface OnItemClickListener<T> {
        public void onItemClick(View view, T item, boolean isLongClick);
    }

    public void setOnItemClickListener(final OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void populate(T item) {

        }

        @Override
        public void onClick(View v) {
            handleClick(v, false);
        }

        @Override
        public boolean onLongClick(View v) {
            return handleClick(v, true);
        }

        private boolean handleClick(View v, boolean isLongClick) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, items.get(getAdapterPosition()), isLongClick);
                return true;
            }
            return false;
        }
    }
}
