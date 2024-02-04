package com.nikolai4.ltrainer_tabletest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseTrainActivity extends AppCompatActivity {

    private Button trainOfferedWords;
    private Button trainChosenWords;
    private Button trainOfferedSentences;
    private Button trainChosenSentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_train);

        trainOfferedWords = findViewById(R.id.train_offered_words_button);
        trainChosenWords = findViewById(R.id.train_chosen_words_button);
        trainOfferedSentences = findViewById(R.id.train_offered_sentences_button);
        trainChosenSentences = findViewById(R.id.train_chosen_sentences_button);

        trainOfferedWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTrainActivity.this, TrainingActivity.class);
                startActivity(intent);
            }
        });

        trainChosenWords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        trainOfferedSentences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        trainChosenSentences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void startLearning(Intent intent) {
        startActivity(intent);
    }
}