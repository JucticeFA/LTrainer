package com.nikolai4.ltrainer_tabletest.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nikolai4.ltrainer_tabletest.WordRepository;
import com.nikolai4.ltrainer_tabletest.model.Example;
import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.List;

public class WordViewModel extends AndroidViewModel {

    private WordRepository wordRepository;
    private LiveData<List<Word>> liveDataWords;
    private LiveData<List<Example>> liveDataExamples;

    public WordViewModel(@NonNull Application application) {
        super(application);
        wordRepository = new WordRepository(application);
        liveDataWords = wordRepository.getLiveDataWords();
        liveDataExamples = wordRepository.getLiveDataExamples();
    }

    /*
     Methods for WORDS
     */
    public LiveData<List<Word>> getLiveDataWords() {
        return liveDataWords;
    }

    public List<Word> getWordList() {
        return wordRepository.getWordList();
    }

    public void insertWord(Word word) {
        wordRepository.insertWord(word);
    }

    public void deleteWord(Word word) {
        wordRepository.deleteWord(word);
    }

    public void updateWord(Word word) {
        wordRepository.updateWord(word);
    }

    public List<Word> getWordsByFirstChars(String startsWith, String category) {
        return wordRepository.getWordsByFirstChars(startsWith, category);
    }

    public List<Word> getWordsByCategory(String category) {
        return wordRepository.getWordsByCategory(category);
    }

    public List<Word> getEarliestWords(int number) {
        return wordRepository.getEarliestWords(number);
    }

    public List<Word> getLatestWords(int number) {
        return wordRepository.getLatestWords(number);
    }

    public Word getRandomWord() {
        return wordRepository.getRandomWord();
    }

    /*
     Methods for EXAMPLES
     */
    public LiveData<List<Example>> getLiveDataExamples() {
        return liveDataExamples;
    }

    public List<Example> getExamples() {
        return wordRepository.getExamples();
    }

    public void insertExample(Example example) {
        wordRepository.insertExample(example);
    }

    public void updateExample(Example example) {
        wordRepository.updateExample(example);
    }

    public void deleteExample(Example example) {
        wordRepository.deleteExample(example);
    }

    public void deleteAllExamples() {
        wordRepository.deleteAllExamples();
    }
    public List<Example> getExamplesByCategory(String word, String category) {
        return wordRepository.getExamplesByCategory(word, category);
    }
    public List<Example> getExamplesByWord(String word) {
        return wordRepository.getExamplesByWord(word);
    }

    /*
     Methods for EXAMPLES and Words
     */
    public void insertWordAndExample(Word word, Example example) {
        wordRepository.insertWordAndExamples(word, example);
    }

    public void updateWordAndExample(Word word, Example example) {
        wordRepository.updateWordAndExamples(word, example);
    }

    public void deleteWordAndExample(Word word, Example example) {
        wordRepository.deleteWordAndExamples(word, example);
    }
}
