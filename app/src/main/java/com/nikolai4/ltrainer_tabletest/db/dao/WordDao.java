package com.nikolai4.ltrainer_tabletest.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.nikolai4.ltrainer_tabletest.model.Example;
import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.List;

@Dao
public abstract class WordDao {

    /**
     * word_table handling
     */
    @Query("SELECT * FROM word_table")
    public abstract LiveData<List<Word>> getLiveDataWords();

    @Query("SELECT * FROM word_table")
    public abstract List<Word> getWordsList();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    public abstract void insertWord(Word word);

    @Update
    public abstract void updateWord(Word word);

    @Delete
    public abstract void deleteWord(Word word);

    @Query("DELETE FROM word_table")
    public abstract void deleteAllWords();

//    @Query("SELECT * FROM word_table where expression like :startsWith||'%'")
//    public abstract List<Word> getWordsByFirstChars(String startsWith);

    @Query("SELECT * FROM word_table where expression like :startsWith || '%' AND groupCategory like '%' || :category || '%'")
    public abstract List<Word> getWordsByFirstChars(String startsWith, String category);

    @Query("SELECT * FROM word_table where groupCategory like '%' || :category || '%' order by 'asc'")
    public abstract List<Word> getWordsByCategory(String category);

//    @Query("SELECT * FROM word_table ")
//    public abstract List<Word> getLearningWords(int count);

    /**
     * example_table handling
     */
    @Query("SELECT * FROM example_table")
    public abstract LiveData<List<Example>> getLiveDataExamples();

    @Query("SELECT * FROM example_table")
    public abstract List<Example> getExamples();

    @Insert(onConflict = OnConflictStrategy.FAIL)
    public abstract void insertExample(Example example);

    @Update
    public abstract void updateExample(Example example);

    @Delete
    public abstract void deleteExample(Example example);

    @Query("DELETE FROM example_table")
    public abstract void deleteAllExamples();

    @Query("SELECT * FROM example_table where word_example = :word AND category_example = :category")
    public abstract List<Example> getExamplesByCategory(String word, String category);

    @Query("SELECT * FROM example_table where word_example = :word")
    public abstract List<Example> getExamplesByWord(String word);

    /**
     * common handling
     */
    @Transaction
    public void insert(Word word, Example example) {
        insertWord(word);
        insertExample(example);
    }

    @Transaction
    public void delete(Word word, Example example) {
        deleteWord(word);
        deleteExample(example);
    }

    @Transaction
    public void update(Word word, Example example) {
        updateWord(word);
        updateExample(example);
    }
}
