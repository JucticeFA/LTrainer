package com.nikolai4.ltrainer_tabletest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddCategoryActivity extends AppCompatActivity {

    private EditText enterCategory;
    private Button saveCategoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        enterCategory = findViewById(R.id.enter_category_edit);
        saveCategoryButton = findViewById(R.id.save_category_button);

        Intent intent = getIntent();
        ArrayList<String> existedCategories = intent.getStringArrayListExtra("categories");

        String currentCategory = intent.getStringExtra("current_category");
        enterCategory.setText(currentCategory);

        saveCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCategory = enterCategory.getText().toString().trim();

                Intent intentMenuWordbook = new Intent(
                        AddCategoryActivity.this, CategoriesActivity.class);
                if (!TextUtils.isEmpty(newCategory)) {
                    if (!existedCategories.contains(newCategory)) {
                        if (currentCategory != null) {
                            existedCategories.set(
                                    existedCategories.indexOf(currentCategory), newCategory);
                        } else {
                            existedCategories.add(newCategory);
                        }
                        intentMenuWordbook.putStringArrayListExtra(
                                "new_category_list", existedCategories);
                        startActivity(intentMenuWordbook);

                        finish();
                    } else {
                        Toast.makeText(AddCategoryActivity.this,
                                "Such category already exists!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AddCategoryActivity.this,
                            "There must be 1 character at least!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}