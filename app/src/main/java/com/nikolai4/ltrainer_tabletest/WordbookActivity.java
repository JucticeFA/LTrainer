package com.nikolai4.ltrainer_tabletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.nikolai4.ltrainer_tabletest.adapters.WordAdapter;
import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.utils.Constants;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WordbookActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText searchRow;
    private WordViewModel wordViewModel;
    private WordAdapter wordAdapter;

    private final List<Word> mWords = new ArrayList<>();

    private static final CharSequence[] itemsDefault = {Constants.A_Z_SORT,
                                                        Constants.NEW_OLD_SORT,
                                                        Constants.LOW_HIGH_SORT};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordbook);

        Toolbar toolbar = findViewById(R.id.wordbook_toolbar);
        setSupportActionBar(toolbar);

        searchRow = toolbar.findViewById(R.id.find_word);
        recyclerView = findViewById(R.id.vocabulary_recycler);
        wordAdapter = new WordAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(wordAdapter);

        hideKeyboard();

        Intent intent = getIntent();
//        String expression = intentFromInsert.getStringExtra("expression");
//        List<String> translates = Arrays.asList(intentFromInsert.getStringArrayExtra("translates"));

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        wordViewModel.getLiveDataWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
//                mWords.clear();
//                mWords.addAll(words);
//                wordAdapter.setWords(words);
                //  03.04.23
                mWords.clear();
                mWords.addAll(getWordsByCategory(intent.getStringExtra("menu_wordbook_activity_category")));
                wordAdapter.setWords(mWords);
            }
        });

        wordAdapter.setOnWordClickListener(new WordAdapter.OnWordClickListener() {
            @Override
            public void onWordClick(int position) {
                Intent insertWordIntent = new Intent(WordbookActivity.this, InsertWordActivity.class);
                insertWordIntent.putExtra("word_key", mWords.get(position));
                insertWordIntent.putExtra("menu_wordbook_activity_category",
                        intent.getStringExtra("menu_wordbook_activity_category"));
                startActivity(insertWordIntent);
//                wordAdapter.markItem(WordAdapter.WordViewHolder);
//                recyclerView.findViewHolderForLayoutPosition(position).itemView.findViewById(R.id.frame_word_item).setVisibility(View.VISIBLE);
                Toast.makeText(WordbookActivity.this, "item's position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        searchRow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String category = intent.getStringExtra("menu_wordbook_activity_category");
                setWordsByEdit(s.toString(), category);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setWordsByEdit(String start, String category) {
        mWords.clear();
        mWords.addAll(wordViewModel.getWordsByFirstChars(start, category));
        wordAdapter.setWords(mWords);
    }

    // 03.04.23
    private List<Word> getWordsByCategory(String category) {
        return wordViewModel.getWordsByCategory(category);
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void showSortingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(itemsDefault, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        if (itemsDefault[0].equals(Constants.A_Z_SORT)) {
                            Collections.sort(mWords, (w1, w2) -> w1.getExpression().compareTo(w2.getExpression()));
                            itemsDefault[0] = Constants.Z_A_SORT;
//                            itemsDefault[1] = Constants.NEW_OLD_SORT;
                        } else {
                            Collections.sort(mWords, (w1, w2) -> w2.getExpression().compareTo(w1.getExpression()));
                            itemsDefault[0] = Constants.A_Z_SORT;
                        }
                        wordAdapter.setWords(mWords);
                        break;
                    case 1:
                        if (itemsDefault[1].equals(Constants.NEW_OLD_SORT)) {
                            Collections.sort(mWords, (w1, w2) -> Long.compare(w2.getTimeStamp(), w1.getTimeStamp()));
//                            itemsDefault[0] = Constants.A_Z_SORT;
                            itemsDefault[1] = Constants.OLD_NEW_SORT;
                        } else {
                            Collections.sort(mWords, (w1, w2) -> w1.getExpression().compareTo(w2.getExpression()));
                            itemsDefault[1] = Constants.NEW_OLD_SORT;
                        }
                        wordAdapter.setWords(mWords);
                        break;
                    case 2:
                        if (itemsDefault[2].equals(Constants.LOW_HIGH_SORT)) {
                            Collections.sort(mWords, (w1, w2) -> Integer.compare(
                                    w1.getLearningPercentage(), w2.getLearningPercentage()));
                            itemsDefault[2] = Constants.HIGH_LOW_SORT;
                        } else {
                            Collections.sort(mWords, (w1, w2) -> Integer.compare(
                                    w2.getLearningPercentage(), w1.getLearningPercentage()));
                            itemsDefault[2] = Constants.LOW_HIGH_SORT;
                        }
                        wordAdapter.setWords(mWords);
                        break;
                }
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.wordbook_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sort_wordbook_menu:
                showSortingDialog();
                break;
            case R.id.select_word_wordbook_menu:
                // select
                break;
            case R.id.reset_progress_wordbook_menu:
                // reset
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_card_context:

                break;
            case R.id.learn_now_context:
                // learn now!
                break;
            case R.id.put_into_learning_list_context:
                // put_into_learning_list
                break;
            case R.id.reset_progress_context:
                // reset_progress
                break;
            case R.id.remove_context:
                // remove
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void passInfoIntent(Word word) {
        Intent intent = new Intent(WordbookActivity.this, InsertWordActivity.class);
        intent.putExtra("word_intent", word);
        startActivity(intent);
    }

    // is it a proper way???
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WordbookActivity.this, CategoriesActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}