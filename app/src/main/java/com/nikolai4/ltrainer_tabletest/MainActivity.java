package com.nikolai4.ltrainer_tabletest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button trainingButton;
    private Button addExpressionButton;
    private Button vocabularyButton;
    private Button rulesButton;
    private Button optionsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        trainingButton = findViewById(R.id.train_button);
        addExpressionButton = findViewById(R.id.add_button);
        vocabularyButton = findViewById(R.id.vocabulary_button);
        rulesButton = findViewById(R.id.rules_button);
        optionsButton = findViewById(R.id.options_button);

        trainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ChooseTrainActivity.class);
                startActivity(intent);
            }
        });

        addExpressionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InsertWordActivity.class);
                startActivity(intent);
            }
        });

        vocabularyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });
    }
}