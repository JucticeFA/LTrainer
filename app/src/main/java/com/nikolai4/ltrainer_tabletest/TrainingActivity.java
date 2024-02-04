package com.nikolai4.ltrainer_tabletest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.model.competition.Competition;
import com.nikolai4.ltrainer_tabletest.model.competition.Table;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModel;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModelFactory;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

import java.util.ArrayList;
import java.util.List;

public class TrainingActivity extends AppCompatActivity {

//    private ImageButton soundButton;

    private TextView expressionQuestion;
    private ProgressBar trainingProgress;
    private TextView exitQuizButton;
    private WordViewModel wordViewModel;
    private CompetitionViewModel competitionViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        setViews();

        Intent intent = getIntent();

        EnRuTableFragment enRuTableFragment = new EnRuTableFragment();
        RuEnTableFragment ruEnTableFragment = new RuEnTableFragment();
        LettersTableFragment lettersTableFragment = new LettersTableFragment();
        WritingTableFragment writingTableFragment = new WritingTableFragment();
        ResultScreenFragment resultScreenFragment = new ResultScreenFragment();

        wordViewModel = ViewModelProviders.of(this).get(WordViewModel.class);
//        competitionViewModel = ViewModelProviders.of(this).get(CompetitionViewModel.class);
        CompetitionViewModelFactory factory = new CompetitionViewModelFactory(getApplication(), 5);
        competitionViewModel = ViewModelProviders.of(this, factory).get(CompetitionViewModel.class);

        competitionViewModel.getLiveDataCompetition().observe(this, competition -> {
            Log.d("countofcreates", "1) trainingActivity: ");
            String question = competition.getTablesQuestion();
            Log.d("questioncompare", "1) question: " + question);
            Log.d("currentTable", "1) currentTable: " + competition.getCurrentTable().toString());

            Fragment currentFragment = enRuTableFragment;
            Table currentTable = competition.getCurrentTable();
            if (!competition.isQuizOver()) {
                if (competition.isTableChanged()) {
                    if (currentTable.equals(competition.getEnRuTable())) {
                        currentFragment = enRuTableFragment;
                        Log.d("currentTable", "2) currentTable: " + competition.getCurrentTable().toString());
                    }
                    else if (currentTable.equals(competition.getRuEnTable())) {
                        currentFragment = ruEnTableFragment;
                        Log.d("currentTable", "3) currentTable: " + competition.getCurrentTable().toString());
                    }
                    else if (currentTable.equals(competition.getLettersTable())) {
                        currentFragment = lettersTableFragment;
                        Log.d("currentTable", "4) currentTable: " + competition.getCurrentTable().toString());
                    }
                    else if (currentTable.equals(competition.getFullWritingTable())) {
                        currentFragment = writingTableFragment;
                        Log.d("currentTable", "5) currentTable: " + competition.getCurrentTable().toString());
                    }
                    competition.setTableChanged(false);
                    pickFragment(currentFragment);
                }
                expressionQuestion.setText(question);
            } else {
                currentFragment = resultScreenFragment;
                pickFragment(currentFragment);
                Log.d("currentTable", "6) currentTable: " + currentFragment);
                String congratulation = competition.getResultState().getCongratulation();
                expressionQuestion.setText(congratulation);
            }
            trainingProgress.setMax(competition.getWordsToLearn().size() * 4);
            trainingProgress.setProgress(competition.getTrainingProgress());
        });

        exitQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void pickFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_holder, fragment)
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void setViews() {
//        soundButton = findViewById(R.id.sound_button_training);
        expressionQuestion = findViewById(R.id.original_expression_training);
        trainingProgress = findViewById(R.id.training_progress);
        exitQuizButton = findViewById(R.id.exit_quiz_button);
    }
}