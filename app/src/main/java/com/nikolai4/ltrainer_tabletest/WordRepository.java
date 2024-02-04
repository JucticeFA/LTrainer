package com.nikolai4.ltrainer_tabletest;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.nikolai4.ltrainer_tabletest.db.dao.WordDao;
import com.nikolai4.ltrainer_tabletest.db.database.WordDatabase;
import com.nikolai4.ltrainer_tabletest.model.Example;
import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class WordRepository {

    private WordDao mWordDao;
    private LiveData<List<Word>> mLiveDataWords;
    private LiveData<List<Example>> mLiveDataExamples;

    public WordRepository(Application application) {
        WordDatabase wordDatabase = WordDatabase.getWordDatabase(application.getApplicationContext());
        mWordDao = wordDatabase.wordDao();
        mLiveDataWords = mWordDao.getLiveDataWords();
        mLiveDataExamples = mWordDao.getLiveDataExamples();
    }

    /**
     Methods for WORDS
     */
    public LiveData<List<Word>> getLiveDataWords() {
        return mLiveDataWords;
    }

    public void insertWord(Word word) {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.insertWord(word);
            }
        });
    }

    public void deleteWord(Word word) {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.deleteWord(word);
            }
        });
    }

    public void updateWord(Word word) {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.updateWord(word);
            }
        });
    }

    public List<Word> getWordsByFirstChars(String startsWith, String category) {
        List<Word> words = null;
        Future<List<Word>> future =  WordDatabase.databaseWriteExecutor.submit(new Callable<List<Word>>() {
            @Override
            public List<Word> call() throws Exception {
                return mWordDao.getWordsByFirstChars(startsWith, category);
            }
        });
        try {
            words = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return words;
    }

    public List<Word> getWordsByCategory(String category) {
        List<Word> words = null;
        Future<List<Word>> future =  WordDatabase.databaseWriteExecutor.submit(new Callable<List<Word>>() {
            @Override
            public List<Word> call() throws Exception {
                return mWordDao.getWordsByCategory(category);
            }
        });
        try {
            words = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return words;
    }

    public Word getRandomWord() {
        Word word = null;
        List<Word> words = new ArrayList<>();
        Future<Word> future = WordDatabase.databaseWriteExecutor.submit(new Callable<Word>() {
            @Override
            public Word call() throws Exception {
                words.addAll(mWordDao.getWordsList());
                Random random = new Random();
                int randomPosition = random.nextInt(words.size());
                return words.get(randomPosition);
            }
        });
        try {
            word = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return word;
    }

    public List<Word> getEarliestWords(int number) {
        List<Word> words = null;
        List<Word> allWords = new ArrayList<>();
        Future<List<Word>> future =  WordDatabase.databaseWriteExecutor.submit(new Callable<List<Word>>() {
            @Override
            public List<Word> call() throws Exception {
                allWords.addAll(mWordDao.getWordsList());
                allWords.sort(new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return Long.compare(o1.getTimeStamp(), o2.getTimeStamp());
                    }
                });
                List<Word> temp = new ArrayList<>();
                for (int i = 0; i < number; i++) {
                    temp.add(allWords.get(allWords.size()-1-i));
                }
                return temp;
            }
        });
        try {
            words = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return words;
    }

    public List<Word> getLatestWords(int number) {
        List<Word> words = null;
        List<Word> allWords = new ArrayList<>();
        Future<List<Word>> future =  WordDatabase.databaseWriteExecutor.submit(new Callable<List<Word>>() {
            @Override
            public List<Word> call() throws Exception {
                allWords.addAll(mWordDao.getWordsList());
                allWords.sort(new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return Long.compare(o1.getTimeStamp(), o2.getTimeStamp());
                    }
                });
                List<Word> temp = new ArrayList<>();
                for (int i = 0; i < number; i++) {
                    temp.add(allWords.get(i));
                }
                return temp;
            }
        });
        try {
            words = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return words;
    }

    public List<Word> getWordList() {
        List<Word> words = null;
        Future<List<Word>> future =  WordDatabase.databaseWriteExecutor.submit(new Callable<List<Word>>() {
            @Override
            public List<Word> call() throws Exception {
                return mWordDao.getWordsList();
            }
        });
        try {
            words = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return words;
    }

    /**
     Methods for EXAMPLES
     */
    public LiveData<List<Example>> getLiveDataExamples() {
        return mLiveDataExamples;
    }
    public void insertExample(Example example) {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.insertExample(example);
            }
        });
    }

    public List<Example> getExamples() {
        List<Example> examples = null;
        Future<List<Example>> future =  WordDatabase.databaseWriteExecutor.submit(new Callable<List<Example>>() {
            @Override
            public List<Example> call() throws Exception {
                return mWordDao.getExamples();
            }
        });
        try {
            examples = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return examples;
    }

    public void deleteExample(Example example) {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.deleteExample(example);
            }
        });
    }

    public void deleteAllExamples() {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.deleteAllExamples();
            }
        });
    }

    public void updateExample(Example example) {
        WordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mWordDao.updateExample(example);
            }
        });
    }

    public List<Example> getExamplesByCategory(String word, String category) {
        List<Example> examples = null;
        Future<List<Example>> future = WordDatabase.databaseWriteExecutor.submit(new Callable<List<Example>>() {
            @Override
            public List<Example> call() throws Exception {
                return mWordDao.getExamplesByCategory(word, category);
            }
        });
        try {
            examples = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return examples;
    }

    public List<Example> getExamplesByWord(String word) {
        List<Example> examples = null;
        Future<List<Example>> future = WordDatabase.databaseWriteExecutor.submit(new Callable<List<Example>>() {
            @Override
            public List<Example> call() throws Exception {
                return mWordDao.getExamplesByWord(word);
            }
        });
        try {
            examples = future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return examples;
    }

    /**
     Transaction methods for BOTH (word & example)
     */
    public void insertWordAndExamples(Word word, Example example) {
        mWordDao.insert(word, example);
    }

    public void deleteWordAndExamples(Word word, Example example) {
        mWordDao.delete(word, example);
    }

    public void updateWordAndExamples(Word word, Example example) {
        mWordDao.update(word, example);
    }
}
