package com.nikolai4.ltrainer_tabletest.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.nikolai4.ltrainer_tabletest.R;
import com.nikolai4.ltrainer_tabletest.model.Example;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ExamplesAdapter extends RecyclerView.Adapter<ExamplesAdapter.ExamplesViewHolder> {

    private List<Example> examples;
    private LayoutInflater inflater;
    private List<String> categories;
    private OnDeleteClickListener onDeleteClickListener;

    private OnItemClickListener onItemClickListener;
    private InfoProvider infoProvider;
    private PositionProvider positionProvider;

    private HolderProvider holderProvider;

    public void setHolderProvider(HolderProvider holderProvider) {
        this.holderProvider = holderProvider;
    }

    public void setPositionProvider(PositionProvider positionProvider) {
        this.positionProvider = positionProvider;
    }

    public void setInfoProvider(InfoProvider infoProvider) {
        this.infoProvider = infoProvider;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ExamplesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public interface PositionProvider {
        void providePosition(int position);
    }

    public interface HolderProvider {
        void provideHolder(int position, ExamplesViewHolder holder);
    }

    public interface InfoProvider {
        void provideExampleText(int position, String example);
        void provideTranslateText(int position, String translate);
        void provideCategoryText(int position, String category);
    }

    @NonNull
    @Override
    public ExamplesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.example_item_2, parent, false);
        return new ExamplesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamplesViewHolder holder, int position) {
//        int adapterPosition = holder.getAdapterPosition();
//        Example example = examples.get(adapterPosition);
//        EditText editExample = holder.example;
//        EditText editTranslate = holder.translate;
//        Spinner spinnerCategories = holder.categories;
//        TextView save = holder.save;
//        ImageView delete = holder.delete;
//
//        editExample.setText(example.getExampleSentence());
//        editTranslate.setText(example.getExampleTranslate());
//        spinnerCategories.setAdapter(new ArrayAdapter<>(
//                inflater.getContext(), android.R.layout.simple_spinner_item, categories));
//        spinnerCategories.setSelection(categories.indexOf(example.getCategory()));
//
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (onDeleteClickListener != null) {
//                    onDeleteClickListener.onDeleteClick(adapterPosition);
//                }
//            }
//        });
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (infoProvider != null) {
//                    infoProvider.provideExampleText(adapterPosition, editExample.getText().toString());
//                    infoProvider.provideTranslateText(adapterPosition, editTranslate.getText().toString());
//                    infoProvider.provideCategoryText(adapterPosition, spinnerCategories.getSelectedItem().toString());
//                }
//            }
//        });

        int adapterPosition = holder.getAdapterPosition();
        Example example = examples.get(adapterPosition);
        CardView itemCard = holder.cardView;
        TextView textExample = holder.example;
        TextView textTranslate = holder.translate;
        TextView category = holder.category;
        ImageView delete = holder.delete;

        textExample.setText(example.getExampleSentence());
        textTranslate.setText(example.getExampleTranslate());
        category.setText(examples.get(adapterPosition).getCategory());

        itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(adapterPosition);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(adapterPosition);
                }
            }
        });
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
        notifyDataSetChanged();
    }

    public List<Example> getExamples() {
        return examples;
    }

//    public void setCategoriesSpinner(Set<String> categories) {
//        this.categories = new ArrayList<>(categories);
//    }

    @Override
    public int getItemCount() {
        if (examples != null) {
            return examples.size();
        } else {
            return 0;
        }
    }

    public class ExamplesViewHolder extends RecyclerView.ViewHolder {
//        private CardView cardView;
//        private ImageButton delete;
//        private EditText example;
//        private EditText translate;
//        private Spinner categories;
//        private TextView save;

        private CardView cardView;
        private ImageButton delete;
        private TextView example;
        private TextView translate;
        private TextView category;
        public ExamplesViewHolder(@NonNull View itemView) {
            super(itemView);
//            cardView = itemView.findViewById(R.id.example_card_item);
//            delete = itemView.findViewById(R.id.delete_example_item);
//            example = itemView.findViewById(R.id.example_edit_item);
//            translate = itemView.findViewById(R.id.example_translate_edit_item);
//            categories = itemView.findViewById(R.id.categories_spinner_item);
//            save = itemView.findViewById(R.id.save_changes);

            cardView = itemView.findViewById(R.id.example_card_2_item);
            delete = itemView.findViewById(R.id.delete_example_2_item);
            example = itemView.findViewById(R.id.example_text_2_item);
            translate = itemView.findViewById(R.id.example_translate_text_2_item);
            category = itemView.findViewById(R.id.categories_text_2_item);
        }
    }
}
