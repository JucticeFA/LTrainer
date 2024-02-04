package com.nikolai4.ltrainer_tabletest.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nikolai4.ltrainer_tabletest.utils.Constants;
import com.nikolai4.ltrainer_tabletest.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.WordbookViewHolder> {

    private List<String> categories;
    private LayoutInflater inflater;
    private OnCategoryClickListener onCategoryClickListener;

    public CategoriesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(int index);
    }

    public void setOnCategoryClickListener(OnCategoryClickListener onCategoryClickListener) {
        this.onCategoryClickListener = onCategoryClickListener;
    }

    @NonNull
    @Override
    public WordbookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.wordbook_category, parent, false);
        return new WordbookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordbookViewHolder holder, int position) {
        int holderId = holder.getAdapterPosition();
        String category = categories.get(holderId);
        holder.category.setText(category);

        holder.category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCategoryClickListener != null) {
                    onCategoryClickListener.onCategoryClick(holderId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categories != null) {
            return categories.size();
        } else {
            return 0;
        }
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    public String getCategoryByPosition(int id) {
        return categories.get(id);
    }

    public List<String> getCategories() {
        return categories;
    }

//    public int getPositionByCategory(String category) {
//        if (categories.contains(category)) {
//            return categories.indexOf(category);
//        } else {
//            return 0;
//        }
//    }
//

//    public void removeCategory(int position) {
//        categories.remove(position);
//        notifyDataSetChanged();
//    }

    public static class WordbookViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private CardView cardView;
        private TextView category;

        public WordbookViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.category_card);
            category = itemView.findViewById(R.id.category_item_tv);
            category.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            String currentCategory = category.getText().toString();
            if (!currentCategory.equals(Constants.WORDS_CATEGORY) &&
                    !currentCategory.equals(Constants.EXPRESSIONS_CATEGORY) &&
                    !currentCategory.equals(Constants.SENTENCES_CATEGORY)) {
                menu.add(getAdapterPosition(), 101, 0, "Add new expression");
                menu.add(getAdapterPosition(), 102, 1, "Rename");
                menu.add(getAdapterPosition(), 103, 2, "Delete");
            } else {
                menu.add(getAdapterPosition(), 101, 0, "Add new expression");
            }
//            MenuInflater inflater = new MenuInflater(v.getContext());
//            inflater.inflate(R.menu.category_item_context_menu, menu);
        }
    }
}
