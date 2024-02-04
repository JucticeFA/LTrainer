package com.nikolai4.ltrainer_tabletest.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "example_table")
public class Example {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "word_example")
    @NonNull
    private String word;

    // 14.01.23
//    @ColumnInfo(name = "word_example")
//    @NonNull
//    private Word word;

    @ColumnInfo(name = "category_example")
    @NonNull
    private String category;

    @ColumnInfo(name = "sentence_example")
    @NonNull
    private String exampleSentence;

    @ColumnInfo(name = "translate_example")
    @NonNull
    private String exampleTranslate;

    @Ignore
    public Example(int id, @NonNull String word, @NonNull String category,
                   @NonNull String exampleSentence, @NonNull String exampleTranslate) {
        this.id = id;
        this.word = word;
        this.category = category;
        this.exampleSentence = exampleSentence;
        this.exampleTranslate = exampleTranslate;
    }

    public Example(@NonNull String word, @NonNull String category,
                   @NonNull String exampleSentence, @NonNull String exampleTranslate) {
        this.word = word;
        this.category = category;
        this.exampleSentence = exampleSentence;
        this.exampleTranslate = exampleTranslate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getWord() {
        return word;
    }

    public void setWord(@NonNull String word) {
        this.word = word;
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = category;
    }

    @NonNull
    public String getExampleSentence() {
        return exampleSentence;
    }

    public void setExampleSentence(@NonNull String exampleSentence) {
        this.exampleSentence = exampleSentence;
    }

    @NonNull
    public String getExampleTranslate() {
        return exampleTranslate;
    }

    public void setExampleTranslate(@NonNull String exampleTranslate) {
        this.exampleTranslate = exampleTranslate;
    }

//    @Override
//    public boolean equals(@Nullable Object obj) {
//        if (obj == this) return true;
//        if (obj == null) return false;
//        if (!(obj instanceof Example)) return false;
//        Example example = (Example) obj;
//        return this.exampleSentence.equals(example.getExampleSentence());
//    }
}
