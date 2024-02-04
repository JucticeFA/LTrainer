package com.nikolai4.ltrainer_tabletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikolai4.ltrainer_tabletest.adapters.ExamplesAdapter;
import com.nikolai4.ltrainer_tabletest.apiwork.NetworkUtils;
import com.nikolai4.ltrainer_tabletest.db.WordConverter;
import com.nikolai4.ltrainer_tabletest.model.Example;
import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.utils.Constants;
import com.nikolai4.ltrainer_tabletest.viewmodels.NetworkViewModel;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InsertWordActivity extends AppCompatActivity {

    // main views
    private NestedScrollView nestedScrollView;
    private EditText editExpression;
    private EditText editTranslate;
    private EditText editTranscription;
    private TextView addExampleButton;
    private Spinner categoriesSpinner;
    private AppCompatImageButton pronunciationButton;
    private TextView transcriptionText;
    private TextView loadSynonyms;

    // statistics
    private TextView showStatistics;
    private LinearLayout statisticsLayout;
    private TextView categoriesText;
    private TextView dateOfAddition;
    private TextView progressState;
    private TextView learningPercentage;
    private TextView easyToLearn;
    private TextView nextRepeat;

    // examples
    private TextView showAllExamples;
    private RelativeLayout examplesLayout;
    private RecyclerView examplesRecycler;
    private FloatingActionButton saveFloatingButton;
    private LinearLayoutCompat floatingsLayout;

    // examples' editor
    private RelativeLayout examplesEditorLayout;
    private EditText editExample;
    private EditText editExampleTranslate;
    private Spinner spinnerExampleCategories;
    private ImageButton buttonSaveExample;
    private ImageButton buttonCloseExampleEdit;

    // oxford's info
    private TextView showOxfordsInfo;
    private LinearLayoutCompat oxfordsLayout;
//    private TextView oxfordsInfoText;
    private TextView oxfordsInfoWord;
    private TextView oxfordsInfoTranscription;
    private TextView oxfordsInfoLexicalCategoriesLabel;
    private TextView oxfordsInfoLexicalCategories;
    private TextView oxfordsInfoExamplesLabel;
    private TextView oxfordsInfoExamples;
    private TextView oxfordsInfoSynonymsLabel;
    private TextView oxfordsInfoSynonyms;


    // notes
    private TextView showNotes;
    private LinearLayoutCompat notesLayout;
    private EditText notesEditor;

    private Word mWord;
    private List<String> categories = new ArrayList<>();
    private List<Example> examples = new ArrayList<>();

    private String mAudioLink = "";

    private ExamplesAdapter examplesAdapter;
    private WordViewModel wordViewModel;
    private NetworkViewModel networkViewModel;
    private SharedPreferences preferences;

    private int mExamplePosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);

        Toolbar toolbar = findViewById(R.id.word_card_toolbar);
        setSupportActionBar(toolbar);

        setViews();

        preferences = getSharedPreferences(CategoriesActivity.sharedPrefFile, MODE_PRIVATE);
        categories = getCategories();
        setSpinnerCategories();

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);

        /*
         creating a Word
         */
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("word_key")) {
            hideKeyboard();
            mWord = (Word) intent.getSerializableExtra("word_key");
            fillWordCard();
        }

        addExampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExamplePosition = -1;
                showExampleEdit();
            }
        });

        saveFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWord();

                Intent intentToWordbook = new Intent(InsertWordActivity.this, CategoriesActivity.class);
                startActivity(intentToWordbook);
            }
        });

        // if I add new Example picking another category, the word must be added into this category automatically
        buttonSaveExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWord != null) {
                    if (!mWord.getExpression().trim().isEmpty() && !mWord.getTranslates().isEmpty()) {
                        if (!mWord.getGroupCategory().contains(buildExample().getCategory())) {
                            List<String> wordCategories = new ArrayList<>(mWord.getGroupCategory());
                            wordCategories.add(buildExample().getCategory());
                            mWord.setGroupCategory(wordCategories);
                        }
                        saveWord();
                        saveExample();
                    }
                } else {
                    Toast.makeText(InsertWordActivity.this, "Save the word at first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY + 12 && saveFloatingButton.isShown()) {
                    saveFloatingButton.hide();
                    addExampleButton.setVisibility(View.GONE);
                }
                if (scrollY < oldScrollY - 12 && !saveFloatingButton.isShown()) {
                    saveFloatingButton.show();
                    addExampleButton.setVisibility(View.VISIBLE);
                }
                if (scrollY == 0) {
                    saveFloatingButton.show();
                    addExampleButton.setVisibility(View.VISIBLE);
                }
            }
        });

        setExampleClickListener();
        setDeletingExample();

        showStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAndHideStatistics();
            }
        });

        showAllExamples.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllExamples(mWord.getExpression());
            }
        });

        pronunciationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound();
            }
        });

        showNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotes();
            }
        });

        showOxfordsInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOxfordsInfo();
            }
        });

        notesEditor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    saveNotes();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        transcriptionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTranscription(loadTranscription());
                setTranscription();
            }
        });

        transcriptionText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (editTranscription.getVisibility() == View.GONE) {
                    editTranscription.setVisibility(View.VISIBLE);
                    if (!transcriptionText.getText().toString().equals(
                            getResources().getString(R.string.transcription_tap_to_edit_button))) {
                        editTranscription.setText(transcriptionText.getText().toString());
                    }
                } else {
                    editTranscription.setVisibility(View.GONE);
                }
                return true;
            }
        });

        // 3 cases: entered+DONE; isn't entered+BACK
        editTranscription.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    saveTranscription(editTranscription.getText().toString());
                    setTranscription();
                    hideKeyboard();
                    editTranscription.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

        editExpression.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setAudioLink();
            }
        });
    }

    // ============================= WORD ==============================
    private void saveWord() {
        // check weather the word was changed. If so, update all the examples related to this word
        List<Example> allExamples = null;
        if (mWord != null) {
            if (!mWord.getExpression().equals(editExpression.getText().toString())) {
                allExamples = getAllExamplesByWord();
            }
        }

        buildWord();

        if (getIntent().hasExtra("word_key")) {
            updateWord();
            updateAllExamplesIfWordIsChanged(allExamples);
        } else {
            insertWord();
        }
    }

    // check if the word couldn't be added twice! It looks working..
    private void buildWord() {
        String expression = editExpression.getText().toString().trim();
        List<String> translates = new WordConverter().toTranslateList(editTranslate.getText().toString().trim());
        String category = (String) categoriesSpinner.getSelectedItem();
        List<String> categories = new ArrayList<>();
        categories.add(category);
        String notes = notesEditor.getText().toString().trim();
        String transcription = "";
        if (!transcriptionText.getText().toString().equals(
                getResources().getString(R.string.transcription_tap_to_edit_button))) {
            transcription = transcriptionText.getText().toString();
        }

        if (mWord == null) {
            mWord = new Word(expression, translates, categories);
            mWord.setTimeStamp(System.currentTimeMillis());
        } else {
            mWord.setExpression(expression);
            mWord.setTranslates(translates);
        }
        saveTranscription(transcription);
        mWord.setAudioLink(mAudioLink);
        mWord.setNotes(notes);
    }

    private void insertWord() {
        if (!mWord.getExpression().trim().isEmpty() && !mWord.getTranslates().isEmpty()) {
            wordViewModel.insertWord(mWord);
        } else {
            Toast.makeText(this, "All the fields must be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateWord() {
        if (!mWord.getExpression().isEmpty() && !mWord.getTranslates().isEmpty()) {
            wordViewModel.updateWord(mWord);
        } else {
            Toast.makeText(this, "All the fields must be filled", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetProgress() {
        if (mWord != null) {
            mWord.setDifficulty(Constants.NORMAL_TO_LEARN);
            mWord.setProgressState(Constants.DEFAULT_PROGRESS_STATE);
            mWord.setLearningPercentage(0);
            mWord.setNextRepeat(0);
            mWord.setRepeatNumber(0);
            wordViewModel.updateWord(mWord);

            setStatisticsInfo(mWord);
        }
    }

    private void deleteWordFromCategory() {
        String category = getCurrentCategory();
        List<String> wordsCategories = new ArrayList<>(mWord.getGroupCategory());
        if (category != null) {
            wordsCategories.remove(category);
            mWord.setGroupCategory(wordsCategories);

            if (examples != null && !examples.isEmpty()) {
                for (int i = 0; i < examples.size(); i++) {
                    wordViewModel.deleteExample(examples.get(i));
                }
            }
            wordViewModel.updateWord(mWord);

            Intent goToCategoriesActivity = new Intent(this, CategoriesActivity.class);
            startActivity(goToCategoriesActivity);
        }
    }

    private void deleteWordFromEverywhere() {
        List<Example> allExamples;
        if (mWord != null) {
            wordViewModel.deleteWord(mWord);
            allExamples = getAllExamplesByWord();
            if (allExamples != null && !allExamples.isEmpty()) {
                for (int i = 0; i < allExamples.size(); i++) {
                    wordViewModel.deleteExample(allExamples.get(i));
                }
            }
            Intent goToCategoriesActivity = new Intent(this, CategoriesActivity.class);
            startActivity(goToCategoriesActivity);
        }
    }

    private void playSound() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaPlayer player = new MediaPlayer();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    player.setDataSource(mAudioLink);
                    player.prepare();
                    player.start();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAudioLink() {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (mWord != null
                        && mWord.getExpression().equalsIgnoreCase(editExpression.getText().toString())
                        && !mWord.getAudioLink().trim().isEmpty()) {
                    mAudioLink = mWord.getAudioLink();
                } else {
                    mAudioLink = NetworkUtils.startAudioTask(editExpression.getText().toString());
                }
            }
        });
    }


    // ============================= EXAMPLE =============================

    private void showExampleEdit() {
        examplesEditorLayout.setVisibility(View.VISIBLE);
        spinnerExampleCategories.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories));
        spinnerExampleCategories.setSelection(categories.indexOf(
                categoriesSpinner.getSelectedItem().toString()));

        buttonCloseExampleEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                examplesEditorLayout.setVisibility(View.GONE);
            }
        });

        if (mExamplePosition == -1) {
            editExample.setText("");
            editExampleTranslate.setText("");
            spinnerExampleCategories.setSelection(categories.indexOf(
                    categoriesSpinner.getSelectedItem().toString()));
        } else {
            editExample.setText(examples.get(mExamplePosition).getExampleSentence());
            editExampleTranslate.setText(examples.get(mExamplePosition).getExampleTranslate());
            spinnerExampleCategories.setSelection(categories.indexOf(
                    examples.get(mExamplePosition).getExampleSentence()));
        }
    }

    private void saveExample() {
        Example example = buildExample();
        String exampleSentence = example.getExampleSentence().trim();

        // if an example is new, insert it into the DB. But if an example already exists, edit it.
        if (!exampleSentence.isEmpty()) {
            if (mExamplePosition == -1) {
                insertExample(example);
            } else {
                updateExample(example);
            }
            examplesEditorLayout.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "You should fill the example field", Toast.LENGTH_SHORT).show();
        }
    }

    private Example buildExample() {
        if (mExamplePosition == -1) {
            String word = editExpression.getText().toString();
            String exampleSentence = editExample.getText().toString();
            String translate = editExampleTranslate.getText().toString();
            String category = spinnerExampleCategories.getSelectedItem().toString();
            return new Example(word, category, exampleSentence, translate);
        } else {
            return examples.get(mExamplePosition);
        }
    }

    private void insertExample(Example example) {
        wordViewModel.insertExample(example);   // is it right?

        examples.add(example);
        examplesAdapter.setExamples(examples);
    }

    private void updateExample(Example example) {
        String word = mWord.getExpression();
        String exampleString = editExample.getText().toString().trim();
        String translateString = editExampleTranslate.getText().toString().trim();
        String categoryString = spinnerExampleCategories.getSelectedItem().toString();
        if (!exampleString.isEmpty()) {
            wordViewModel.updateExample(example);   // is it right?

            examples.get(mExamplePosition).setWord(word);
            examples.get(mExamplePosition).setExampleSentence(exampleString);
            examples.get(mExamplePosition).setExampleTranslate(translateString);
            examples.get(mExamplePosition).setCategory(categoryString);
            examplesAdapter.setExamples(examples);
        }
    }

    private void updateAllExamplesIfWordIsChanged(List<Example> allExamples) {
        if (allExamples != null && !allExamples.isEmpty()) {
            for (int i = 0; i < allExamples.size(); i++) {
                allExamples.get(i).setWord(mWord.getExpression());
                wordViewModel.updateExample(allExamples.get(i));
            }
        }
    }

    private List<Example> getAllExamplesByWord() {
        List<Example> result = null;
        try {
            result = wordViewModel.getExamplesByWord(mWord.getExpression());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void showAllExamples(String word) {
        if (showAllExamples.getText().equals("--show other examples--")) {
            List<Example> allExamples = new ArrayList<>(wordViewModel.getExamplesByWord(word));
            for (int i = 0; i < allExamples.size(); i++) {
                String category = allExamples.get(i).getCategory();
                if (!category.equals(getCurrentCategory())) {
                    examples.add(allExamples.get(i));
                }
            }
            examplesAdapter.setExamples(examples);
            showAllExamples.setText("--hide other examples--");
        } else {
            examples.clear();
            examples.addAll(wordViewModel.getExamplesByCategory(word, getCurrentCategory()));
            examplesAdapter.setExamples(examples);
            showAllExamples.setText("--show other examples--");
        }
    }

    private void setDeletingExample() {
        examplesAdapter.setOnDeleteClickListener(new ExamplesAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                wordViewModel.deleteExample(examples.get(position));
                examples.remove(position);
                examplesAdapter.setExamples(examples);
            }
        });
    }

    private void setExampleClickListener() {
        examplesAdapter.setOnItemClickListener(new ExamplesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mExamplePosition = position;
                showExampleEdit();
            }
        });
    }

    // ============================= CATEGORY =============================
    private void setExamplesForCategory() {
        examples = new ArrayList<>(wordViewModel.getExamplesByCategory(mWord.getExpression(), getCurrentCategory()));
        examplesAdapter.setExamples(examples);
    }

    private List<String> getCategories() {
        Set<String> categoriesDefault = new HashSet<>(Collections.singleton("Words"));
        Set<String> categories = preferences.getStringSet(
                CategoriesActivity.categoriesSet_key_property, categoriesDefault);
        return new ArrayList<>(categories);
    }

    private String getCurrentCategory() {
        return getIntent().getStringExtra("menu_wordbook_activity_category");
    }

    private void setSpinnerCategories() {
        categoriesSpinner.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories));
    }


    //============================= SCREEN =============================

    // common info, statistics, examples, +SOUND (make up how to do this)
    private void fillWordCard() {
        String expression = mWord.getExpression();
        List<String> translates = mWord.getTranslates();
        editExpression.setText(expression);
        String translateString = new WordConverter().fromTranslateList(translates);
        editTranslate.setText(translateString);

        examplesLayout.setVisibility(View.VISIBLE);

        categoriesSpinner.setSelection(categories.indexOf(getCurrentCategory()));

        // sound
        setAudioLink();

        // transcription
        setTranscription();

        // notes
        setNotes();

        // statistics
        setStatisticsInfo(mWord);

        // examples
        setExamplesForCategory();
    }

    // should I save oxford's info into DB???
    private void showOxfordsInfo() {
//        if (oxfordsLayout.getVisibility() == View.GONE) {
//            oxfordsLayout.setVisibility(View.VISIBLE);
//
//            if (networkViewModel == null) {
//                networkViewModel = ViewModelProviders.of(this).get(NetworkViewModel.class);
//            }
//
//            if (mWord != null) {
//                if (mWord.getOxfordsInfo().isEmpty() || mWord.getOxfordsInfo().get(0).trim().isEmpty()) {
//                    LiveData<List<String>> info = networkViewModel.getData(mWord.getExpression());
//                    info.observe(this, new Observer<List<String>>() {
//                        @Override
//                        public void onChanged(List<String> strings) {
//                            oxfordsInfoWord.setText(strings.get(0));
//                            oxfordsInfoTranscription.setText(strings.get(1));
//                            oxfordsInfoLexicalCategories.setText(strings.get(2));
//                            oxfordsInfoExamples.setText(strings.get(3));
//                            oxfordsInfoSynonyms.setText(strings.get(4));
//                            oxfordsInfoLexicalCategoriesLabel.setText("lexical categories:");
//                            oxfordsInfoExamplesLabel.setText("examples:");
//                            oxfordsInfoSynonymsLabel.setText("synonyms:");
//
//                            mWord.setOxfordsInfo(new ArrayList<>(strings));
//                        }
//                    });
//                } else {
//                    oxfordsInfoWord.setText(mWord.getOxfordsInfo().get(0));
//                    oxfordsInfoTranscription.setText(mWord.getOxfordsInfo().get(1));
//                    oxfordsInfoLexicalCategories.setText(mWord.getOxfordsInfo().get(2));
//                    oxfordsInfoExamples.setText(mWord.getOxfordsInfo().get(3));
//                    oxfordsInfoSynonyms.setText(mWord.getOxfordsInfo().get(4));
//                    oxfordsInfoLexicalCategoriesLabel.setText("lexical categories:");
//                    oxfordsInfoExamplesLabel.setText("examples:");
//                    oxfordsInfoSynonymsLabel.setText("synonyms:");
//                }
//                oxfordsLayout.requestFocus();
//            }
//        } else {
//            oxfordsLayout.setVisibility(View.GONE);
//        }
    }

    private void setTranscription() {
        if (mWord != null && !mWord.getTranscription().trim().isEmpty()) {
            transcriptionText.setText(mWord.getTranscription());
        } else if (!editTranscription.getText().toString().isEmpty()) {
            transcriptionText.setText(editTranscription.getText().toString());
        }
    }

    private void saveTranscription(String transcription) {
        if (mWord != null) {
            mWord.setTranscription(transcription);
        }
    }

    private String loadTranscription() {
        return NetworkUtils.startTranscriptionTask(editExpression.getText().toString());
    }

    // notes
    private void showNotes() {
        if (notesLayout.getVisibility() == View.GONE) {
            notesLayout.setVisibility(View.VISIBLE);
        } else {
            notesLayout.setVisibility(View.GONE);
        }
    }

    private void setNotes() {
        if (mWord != null && !mWord.getNotes().isEmpty()) {
            notesEditor.setText(mWord.getNotes());
        }
    }

    private void saveNotes() {
        if (mWord != null && !notesEditor.getText().toString().trim().isEmpty()) {
            mWord.setNotes(notesEditor.getText().toString());
        }
    }

    private void showAndHideStatistics() {
        if (statisticsLayout.getVisibility() == View.GONE) {
            statisticsLayout.setVisibility(View.VISIBLE);
            showStatistics.setText(R.string.hide_statistics);
            statisticsLayout.requestFocus();
        } else {
            statisticsLayout.setVisibility(View.GONE);
            showStatistics.setText(R.string.show_statistics);
        }
    }

    private void setStatisticsInfo(Word word) {
        String categories = getResources().getString(R.string.categories_statistics)
                + " " + new WordConverter().fromGroupCategoryList(word.getGroupCategory());

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(word.getTimeStamp());
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String date = getResources().getString(R.string.date_of_addition_statistics)
                + " " + format.format(calendar.getTime());
        String progress = getResources().getString(R.string.status_statistics)
                + " " + word.getProgressState();
        String difficulty = getResources().getString(R.string.learning_process_statistics)
                + " " + word.getDifficulty();
        String percentage = getResources().getString(R.string.learning_completed_statistics)
                + " " + word.getLearningPercentage();
        String next = getResources().getString(R.string.next_repeat_statistics)
                + " " + word.getNextRepeat();

        categoriesText.setText(categories);
        dateOfAddition.setText(date);
        progressState.setText(progress);
        easyToLearn.setText(difficulty);
        learningPercentage.setText(percentage);
        nextRepeat.setText(next);
    }


    private void setViews() {
        // common
        addExampleButton = findViewById(R.id.add_example);
        floatingsLayout = findViewById(R.id.floatings_layout_word_card);
        nestedScrollView = findViewById(R.id.nested_scroll_view_word_card);
        saveFloatingButton = findViewById(R.id.save_floating_button);

        // expression
        editExpression = findViewById(R.id.new_expression_word_card_edit);
        editTranslate = findViewById(R.id.new_translate_edit);
        editTranscription = findViewById(R.id.transcription_word_card_edit);
        transcriptionText = findViewById(R.id.transcription_word_card_text);
        pronunciationButton = findViewById(R.id.pronunciation_button);
        categoriesSpinner = findViewById(R.id.categories_spinner_word_card);

        // statistics
        showStatistics = findViewById(R.id.show_stat_button_word_card);
        statisticsLayout = findViewById(R.id.statistics_layout_word_card);
        categoriesText = findViewById(R.id.word_card_categories);
        dateOfAddition = findViewById(R.id.word_card_date_of_addition);
        progressState = findViewById(R.id.word_card_status);
        learningPercentage = findViewById(R.id.word_card_learning_percentage);
        easyToLearn = findViewById(R.id.word_card_learning_process);
        nextRepeat = findViewById(R.id.word_card_next_repeat);

        // examples
        examplesLayout = findViewById(R.id.show_examples_layout_word_card);
        showAllExamples = findViewById(R.id.label_show_examples_text);
        examplesEditorLayout = findViewById(R.id.example_editor_layout_word_card);
        editExample = findViewById(R.id.example_edit_word_card);
        editExampleTranslate = findViewById(R.id.example_translate_edit_word_card);
        spinnerExampleCategories = findViewById(R.id.categories_spinner_example_word_card);
        buttonSaveExample = findViewById(R.id.save_example_word_card);
        buttonCloseExampleEdit = findViewById(R.id.close_button_word_card);
        examplesRecycler = findViewById(R.id.recycler_example_insert);
        examplesAdapter = new ExamplesAdapter(this);
        examplesRecycler.setLayoutManager(new LinearLayoutManager(this));
        examplesRecycler.setAdapter(examplesAdapter);

        // notes
        showNotes = findViewById(R.id.show_notes_word_card);
        notesLayout = findViewById(R.id.notes_layout);
        notesEditor = findViewById(R.id.notes_editor_word_card);

        // oxford's info
        showOxfordsInfo = findViewById(R.id.load_oxford_info_text_word_card);
        oxfordsLayout = findViewById(R.id.oxfords_info_layout);
//        oxfordsInfoText = findViewById(R.id.oxfords_info_text_word_card);
        oxfordsInfoWord = findViewById(R.id.oxfords_info_word);
        oxfordsInfoTranscription = findViewById(R.id.oxfords_info_transcription);
        oxfordsInfoLexicalCategoriesLabel = findViewById(R.id.oxfords_info_lexical_categories_label);
        oxfordsInfoLexicalCategories = findViewById(R.id.oxfords_info_lexical_categories);
        oxfordsInfoExamplesLabel = findViewById(R.id.oxfords_info_examples_label);
        oxfordsInfoExamples = findViewById(R.id.oxfords_info_examples);
        oxfordsInfoSynonymsLabel = findViewById(R.id.oxfords_info_synonyms_label);
        oxfordsInfoSynonyms = findViewById(R.id.oxfords_info_synonyms);
//        registerForContextMenu(oxfordsInfoExamples);
//        registerForContextMenu(oxfordsInfoSynonyms);
    }

    private void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.word_card_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_to_other_category_card_menu:
                // Add into other category
                break;
            case R.id.add_to_learning_list_card_menu:
                // Add to learning list
                break;
            case R.id.learn_now_card_menu:
                // Learn now
                break;
            case R.id.reset_progress_card_menu:
                // Reset progress
                resetProgress();
                break;
            case R.id.delete_from_category_card_menu:
                // Delete from category
                deleteWordFromCategory();
                break;
            case R.id.full_delete_card_menu:
                // Delete from everywhere
                deleteWordFromEverywhere();
                break;
        }
        return true;
    }


    //    private void addEmptyExampleCard() {
//        String word = editExpression.getText().toString();
//        String defaultCategory;
//        if (getIntent().hasExtra("menu_wordbook_activity_category")) {
//            defaultCategory = getIntent().getStringExtra("menu_wordbook_activity_category");
//        } else {
//            defaultCategory = "Words";
//        }
//
//        Example example = new Example(word, defaultCategory, "", "");
//        examples.add(example);
//        examplesAdapter.setExamples(examples);
//
//        setDeletingExample();
//
//        examplesAdapter.setInfoProvider(new ExamplesAdapter.InfoProvider() {
//            @Override
//            public void provideExampleText(int position, String example) {
//                if (!examples.get(position).getExampleSentence().equals(example)) {
//                    examples.get(position).setExampleSentence(example);
//                    examplesAdapter.setExamples(examples);
//                }
//
//                Toast.makeText(InsertWordActivity.this,
//                        "Position=" + position +
//                                " Example = " + examples.get(position).getExampleSentence(),
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void provideTranslateText(int position, String translate) {
//                if (!examples.get(position).getExampleTranslate().equals(translate)) {
//                    examples.get(position).setExampleTranslate(translate);
//                    examplesAdapter.setExamples(examples);
//                }
//            }
//
//            @Override
//            public void provideCategoryText(int position, String category) {
//                if (!examples.get(position).getCategory().equals(category)) {
//                    examples.get(position).setCategory(category);
//                    examplesAdapter.setExamples(examples);
//                }
//            }
//        });
//    }
}