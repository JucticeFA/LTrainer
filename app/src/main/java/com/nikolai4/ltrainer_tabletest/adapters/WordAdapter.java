package com.nikolai4.ltrainer_tabletest.adapters;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.nikolai4.ltrainer_tabletest.R;
import com.nikolai4.ltrainer_tabletest.db.WordConverter;
import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> words;
    private LayoutInflater inflater;
    private OnWordClickListener onWordClickListener;

    private int checkedPosition = 0;

    public WordAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public interface OnWordClickListener {
        void onWordClick(int position);
    }

    public void setOnWordClickListener(OnWordClickListener onWordClickListener) {
        this.onWordClickListener = onWordClickListener;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.word_item, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = words.get(holder.getAdapterPosition());
        String expression = word.getExpression();
        List<String> translates = word.getTranslates();
        holder.expression.setText(expression);
        holder.translates.setText(new WordConverter().fromTranslateList(translates));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkSign.setVisibility(View.VISIBLE);
                if (checkedPosition != holder.getAdapterPosition()) {
                    notifyItemChanged(holder.getAdapterPosition());
                    checkedPosition = holder.getAdapterPosition();
                }
                if (onWordClickListener != null) {
                    onWordClickListener.onWordClick(holder.getAdapterPosition());
//                    word.setSelected(!word.isSelected());
//                    if (word.isSelected()) {
//                        holder.checkSign.setVisibility(View.VISIBLE);
//                    } else {
//                        holder.checkSign.setVisibility(View.GONE);
//                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (words != null) {
            return words.size();
        } else {
            return 0;
        }
    }

    public void setWords(List<Word> wordList) {
        words = wordList;
        notifyDataSetChanged();
    }

    public List<Word> getWords() {
        return words;
    }

//    public void markItem(WordViewHolder holder) {
//        holder.frameLayout.setVisibility(View.VISIBLE);
//    }

    public class WordViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private CardView cardView;
        private TextView status;
        private TextView expression;
        private TextView translates;
        private TextView transcription;
        private ImageButton pronounceButton;

        private FrameLayout frameLayout;
        private ShapeableImageView checkSign;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.word_card_view);
            status = itemView.findViewById(R.id.word_item_status);
            expression = itemView.findViewById(R.id.word_item);
            translates = itemView.findViewById(R.id.word_item_translates);
            transcription = itemView.findViewById(R.id.word_item_transcription);
            pronounceButton = itemView.findViewById(R.id.pronunciation_button);

//            frameLayout = itemView.findViewById(R.id.frame_word_item);
            checkSign = itemView.findViewById(R.id.check_sign);
            cardView.setOnCreateContextMenuListener(this);

            if (checkedPosition == -1) {
                checkSign.setVisibility(View.GONE);
            } else {
                if (checkedPosition == getAdapterPosition()) {
                    checkSign.setVisibility(View.VISIBLE);
                } else {
                    checkSign.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            // open card, learn, add translate, add example, reset progress, remove
            MenuInflater inflater = new MenuInflater(v.getContext());
            inflater.inflate(R.menu.word_context_menu, menu);
        }

//        public void markItem(Word word) {
//            if (word.isSelected()) {
//                checkSign.setVisibility(View.VISIBLE);
//            } else {
//                checkSign.setVisibility(View.GONE);
//            }
//        }
    }
}
