package com.nikolai4.ltrainer_tabletest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikolai4.ltrainer_tabletest.adapters.CategoriesAdapter;
import com.nikolai4.ltrainer_tabletest.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

//    private EditText findWordEdit;
    private FloatingActionButton addWordFb;
    private RecyclerView recyclerView;
    private Button addCategoryButton;

    private CategoriesAdapter adapter;

    private final List<String> categories = new ArrayList<>();

    private SharedPreferences preferences;
    public static final String sharedPrefFile = "com.nikolai4.ltrainer";
    public static final String categoriesSet_key_property = "categories_set_preferences";
    private String categoryKey = "category key-";
    private final String categoriesSizeKey = "category size key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

//        Toolbar toolbar = findViewById(R.id.menu_wordbook_toolbar);
//        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.recycler_menu_wordbook);
//        findWordEdit = toolbar.findViewById(R.id.find_word);
        addWordFb = findViewById(R.id.add_new_word_fb_mw);
        addCategoryButton = findViewById(R.id.add_new_category_button);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        adapter = new CategoriesAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        preferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);

        screenCategories();

        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategoryIntent();
            }
        });

        addWordFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newWordIntent();
            }
        });

//        findWordEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(CategoriesActivity.this, WordbookActivity.class);
//                startActivity(intent);
//            }
//        });

        adapter.setOnCategoryClickListener(new CategoriesAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(int index) {
                Toast.makeText(CategoriesActivity.this, "" + index, Toast.LENGTH_SHORT).show();
                String category = adapter.getCategoryByPosition(index);
                Intent intent;
                if (category.equals(Constants.SENTENCES_CATEGORY)) {
                    intent = new Intent(CategoriesActivity.this, SentencebookActivity.class);
                } else {
                    intent = new Intent(CategoriesActivity.this, WordbookActivity.class);
                    intent.putExtra("menu_wordbook_activity_category", category);
                }
                startActivity(intent);
            }
        });
    }

    private void screenCategories() {
        int categorySize = preferences.getInt(categoriesSizeKey, 0);
        if (categorySize < 4) {
            categories.addAll(Arrays.asList(
                    Constants.WORDS_CATEGORY,
                    Constants.EXPRESSIONS_CATEGORY,
                    Constants.SENTENCES_CATEGORY));
        } else {
            for (int i = 0; i < categorySize; i++) {
                String category = preferences.getString(categoryKey + i, "non value");
                categories.add(category);
            }
        }

        Intent intent = getIntent();
        if (intent.hasExtra("new_category_list")) {
            categories.clear();
            categories.addAll(intent.getStringArrayListExtra("new_category_list"));
        }
        adapter.setCategories(categories);
    }

    private void newWordIntent() {
        Intent newWordIntent = new Intent(this, InsertWordActivity.class);
        startActivity(newWordIntent);
    }

    private void insertWordIntent() {
        Intent intent = new Intent(this, InsertWordActivity.class);
        startActivity(intent);
    }

    private void addCategoryIntent() {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        intent.putStringArrayListExtra("categories", (ArrayList<String>) categories);
        startActivity(intent);
    }

    private void renameCategoryIntent(String currentCategory) {
        Intent intent = new Intent(this, AddCategoryActivity.class);
        intent.putExtra("current_category", currentCategory);
        intent.putStringArrayListExtra("categories", (ArrayList<String>) categories);
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 101:
                insertWordIntent();
                break;
            case 102:
                renameCategoryIntent(categories.get(item.getGroupId()));
                break;
            case 103:
                Toast.makeText(this,
                        "category \"" + categories.get(item.getGroupId()) + "\" is deleted",
                        Toast.LENGTH_SHORT).show();
                categories.remove(item.getGroupId());
                adapter.setCategories(categories);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences.Editor editor = preferences.edit();

        for (int i = 0; i < categories.size(); i++) {
            editor.putString(categoryKey + i, adapter.getCategoryByPosition(i));
        }
        editor.putStringSet(categoriesSet_key_property, new HashSet<>(adapter.getCategories()));
        editor.putInt(categoriesSizeKey, categories.size());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }


//    private void setItemTouchMovements() {
//        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
//                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
//                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView,
//                                  @NonNull RecyclerView.ViewHolder viewHolder,
//                                  @NonNull RecyclerView.ViewHolder target) {
//
//                int from = viewHolder.getAdapterPosition();
//                int to = target.getAdapterPosition();
//                Collections.swap(categories, from, to);
//                adapter.notifyItemMoved(from, to);
//                return true;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int categoryPosition = viewHolder.getAdapterPosition();
//                String currentCategory = adapter.getCategoryByPosition(categoryPosition);
//
//                if (currentCategory.equals(Constants.WORDS_CATEGORY) ||
//                        currentCategory.equals(Constants.EXPRESSIONS_CATEGORY) ||
//                        currentCategory.equals(Constants.SENTENCES_CATEGORY)) {
//                    return;
//                }
//
//                categories.remove(viewHolder.getAdapterPosition());
//                adapter.setCategories(categories);
//            }
//        });
//        touchHelper.attachToRecyclerView(recyclerView);
//    }
}