package com.nikolai4.ltrainer_tabletest.model.competition;

import android.util.Log;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.utils.Timer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Competition {
    private EnRuTable enRuTable;
    private RuEnTable ruEnTable;
    private LettersTable lettersTable;
    private FullWritingTable fullWritingTable;
    private Table mainTable;
    private Table currentTable;

    private List<Word> wordsToLearn;
    private List<Word> doneWords;
    private List<Mistake> mistakes;
    private Word currentWord;
    private int questionPosition;

    private String tablesQuestion;
    private int trainingProgress = 0;
    private boolean quizOver = false;
    private boolean repeatMode = false;
    private boolean tableChanged = false;
    private final Timer timer;
    private FragmentState.ResultState resultState;


    public Competition(List<Word> wordsToLearn) {
        this.wordsToLearn = wordsToLearn;
        this.doneWords = new ArrayList<>();
        this.mistakes = new ArrayList<>();
        this.questionPosition = 0;
        this.currentWord = this.wordsToLearn.get(questionPosition);
        this.tablesQuestion = currentWord.getExpression();

        this.enRuTable = new EnRuTable(this, currentWord);
        this.ruEnTable = new RuEnTable(this, currentWord);
        this.lettersTable = new LettersTable(this, currentWord);
        this.fullWritingTable = new FullWritingTable(this, currentWord);
        this.mainTable = enRuTable;   // since it's the 1st table, assign EnRuTable by default
        this.currentTable = enRuTable;

        this.resultState = new FragmentState.ResultState();
        this.timer = new Timer();
    }

    public void checkAnswer(Word word, Table table) {
        boolean isTableFinal = table.equals(fullWritingTable);
        boolean rightAnswer = false;
        if (!mistakeExists(word)) {
            rightAnswer = true;
        } else {
            try {
                if (mistakeExists(word) && mistakeByWord(word).isFixed()) {
                    rightAnswer = true;
                }
            } catch (NoExistingMistakeException e) {
                e.printStackTrace();
            }
        }
        Log.d("checkAnswer", "mainTable.equals(fullWritingTable): " + currentTable.equals(fullWritingTable)
                + ", rightAnswer: " + rightAnswer);
        if (rightAnswer) {
            ++trainingProgress;
            Log.d("isenreased", "progress: " + trainingProgress);
            if (isTableFinal) {
                doneWords.add(new Word(word));
            }
        }
        for (int i = 0; i < doneWords.size(); i++) {
            Log.d("doneWords", "doneWords: " + doneWords.get(i).getExpression());
        }
    }

    public void nextQuestion() {
        ++questionPosition;
        repeatMode = false;

        if (questionPosition >= wordsToLearn.size() || wordsToLearn.size() == doneWords.size()) {
            questionPosition = 0;
            Log.d("allMistakesUnfixed", "allMistakesUnfixed: " + allMistakesUnfixed());
            if (!allMistakesUnfixed()) {
                nextTable();
            }
        }

        currentWord = wordsToLearn.get(questionPosition);

        if (wordsToLearn.size() != doneWords.size()) {
            while (doneWords.contains(currentWord)) {
                Log.d("currentWord", "word: " + currentWord.getExpression());
                int position = (int) (Math.random() * wordsToLearn.size());
                currentWord = wordsToLearn.get(position);
            }

            Log.d("checkMistake", "mistakeExists(currentWord): " + mistakeExists(currentWord));
            Log.d("tablescompare", "1) word: " + tablesQuestion
                    + " currentTable: " + currentTable.getClass().getSimpleName()
                    + ", mainTable: " + mainTable.getClass().getSimpleName());
            if (mistakeExists(currentWord)) {
                Mistake mistake;
                try {
                    mistake = mistakeByWord(currentWord);
                    if (!mistake.isFixed()) {
                        repeatMode = true;
                        currentTable = mistake.getTable();
                        Log.d("tablescompare", "2.1) word: " + currentWord.getExpression()
                                + ", mistake: " + mistake.word.getExpression()
                                + ", currentTable: " + currentTable.getClass().getSimpleName()
                                + ", mainTable: " + mainTable.getClass().getSimpleName());
                    } else {
                        currentTable = mistake.getTable();
                        Log.d("tablescompare", "2.2) word: " + currentWord.getExpression()
                                + ", mistake: " + mistake.word.getExpression()
                                + " currentTable: " + currentTable.getClass().getSimpleName()
                                + ", mainTable: " + mainTable.getClass().getSimpleName());
                    }
                } catch (NoExistingMistakeException e) {
                    e.printStackTrace();
                }
            } else {
                currentTable = mainTable;
                Log.d("tablescompare", "3) word: " + currentWord.getExpression()
                        + " currentTable: " + currentTable.getClass().getSimpleName()
                        + ", mainTable: " + mainTable.getClass().getSimpleName());
            }
            currentTable.nextQuestion(currentWord);
        }
    }

    private void nextTable() {
        Collections.shuffle(wordsToLearn);
        mainTable.nextTable();
    }

    public int getTrainingProgress() {
        return trainingProgress;
    }

    public void setTrainingProgress(int trainingProgress) {
        this.trainingProgress = trainingProgress;
    }

    public  class Mistake {
        private Word word;
        private Table table;
        private int count = 1;
        private boolean fixed;

        public Mistake(Word word, Table table) {
            this.word = word;
            this.table = table;
        }

        public Word getWord() {
            return word;
        }

        public Table getTable() {
            return table;
        }

        public int getCount() {
            return count;
        }
        public boolean isFixed() {
            return fixed;
        }
        public void fix() {
            fixed = true;
            selectTable();
        }
        private void selectTable() {
            Log.d("mistakestable", "1) selectTable: " + table.toString());
            if (table.equals(enRuTable)) {
                table = ruEnTable;
                Log.d("mistakestable", "a): " + table.toString());
            } else if (table.equals(ruEnTable)) {
                table = lettersTable;
                Log.d("mistakestable", "b): " + table.toString());
            } else if (table.equals(lettersTable)) {
                table = fullWritingTable;
                Log.d("mistakestable", "c): " + table.toString());
            } else if (table.equals(fullWritingTable)) {
                table = fullWritingTable;
                Log.d("mistakestable", "d): " + table.toString());
            }
            Log.d("mistakestable", "2) selectTable: " + table.toString());
        }

        @Override
        public String toString() {
            return "Mistake{" +
                    "word=" + word.getExpression() +
                    ", table=" + table +
                    ", count=" + count +
                    ", fixed=" + fixed +
                    '}';
        }
    }

    public List<Mistake> getMistakes() {
        return mistakes;
    }

    public List<Word> getWrongWords() {
        List<Mistake> mistakes = getMistakes();
        List<Word> words = new ArrayList<>(mistakes.size());
        for (int i = 0; i < mistakes.size(); i++) {
            words.add(mistakes.get(i).getWord());
        }
        return words;
    }

    public List<Word> getRightWords() {
        List<Word> words = new ArrayList<>(wordsToLearn);
        words.removeAll(getWrongWords());
        return words;
    }

    public Mistake mistakeByWord(Word word) throws NoExistingMistakeException {
        Mistake mistake = null;
        for (int i = 0; i < mistakes.size(); i++) {
            if (mistakes.get(i).word.equals(word)) {
                mistake = mistakes.get(i);
                break;
            }
        }
        if (mistake == null) {
            throw new NoExistingMistakeException("There is no such a mistake");
        }
        return mistake;
    }

    public Mistake unfixedMistake(Word word) throws NoExistingMistakeException {
        Mistake mistake = null;
        for (int i = 0; i < mistakes.size(); i++) {
            Mistake m = mistakes.get(i);
            if (m.word.equals(word) && !m.fixed) {
                mistake = m;
            }
        }
        if (mistake == null) {
            throw new NoExistingMistakeException("There is no such a mistake");
        }
        return mistake;
    }

    /**
     * Returns true if a user gave the wrong answer to the question (Word) at least once
     * @param word
     * @return
     */
    public boolean mistakeExists(Word word) {
        if (!mistakes.isEmpty()) {
            for (int i = 0; i < mistakes.size(); i++) {
                Mistake mistake = mistakes.get(i);
                Log.d("equalwords", "mistake.word=" + mistake.word.getExpression() +
                        ", word=" + word.getExpression() + "\n" +
                        "mistake.word.equals(word): " + mistake.word.equals(word) +
                        ", mistake.word.id=" + mistake.word.getId() + ", word.id=" + word.getId());
                if (mistake.word.equals(word)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void addMistake(Word word, Table table) {
        Mistake mistake;
        if (mistakeExists(word)) {
            try {
                mistake = mistakeByWord(word);
                mistake.fixed = false;
                mistake.table = table;
                ++mistake.count;
            } catch (NoExistingMistakeException e) {
                e.printStackTrace();
            }
        } else {
            mistake = new Mistake(word, table);
            mistakes.add(mistake);
        }
    }

    public boolean isThereAnyMistake() {
        for (int i = 0; i < mistakes.size(); i++) {
            if (!mistakes.get(i).isFixed()) {
                return true;
            }
        }
        return false;
    }

    public boolean allMistakesUnfixed() {
        if (wordsToLearn.size() != mistakes.size()) return false;
        for (int i = 0; i < mistakes.size(); i++) {
            if (mistakes.get(i).isFixed()) {
                return false;
            }
        }
        return true;
    }

    public EnRuTable getEnRuTable() {
        return enRuTable;
    }

    public RuEnTable getRuEnTable() {
        return ruEnTable;
    }

    public LettersTable getLettersTable() {
        return lettersTable;
    }

    public FullWritingTable getFullWritingTable() {
        return fullWritingTable;
    }

    public Table getMainTable() {
        return mainTable;
    }

    public void setMainTable(Table mainTable) {
        this.mainTable = mainTable;
    }

    public List<Word> getWordsToLearn() {
        return wordsToLearn;
    }

    public void setWordsToLearn(List<Word> wordsToLearn) {
        this.wordsToLearn = wordsToLearn;
    }

    public Word getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(Word currentWord) {
        this.currentWord = currentWord;
    }

    public int getQuestionPosition() {
        return questionPosition;
    }

    public void setQuestionPosition(int questionPosition) {
        this.questionPosition = questionPosition;
    }

    public boolean isQuizOver() {
        return quizOver;
    }

    public void setQuizOver(boolean quizOver) {
        this.quizOver = quizOver;
    }

    public String getTablesQuestion() {
        return tablesQuestion;
    }

    public void setTablesQuestion(String tablesQuestion) {
        this.tablesQuestion = tablesQuestion;
    }

    public Timer timer() {
        return timer;
    }
    public Table getCurrentTable() {
        return currentTable;
    }

    public void setCurrentTable(Table currentTable) {
        this.currentTable = currentTable;
    }

    public boolean isRepeatMode() {
        return repeatMode;
    }


    public boolean isTableChanged() {
        return tableChanged;
    }

    public void setTableChanged(boolean tableChanged) {
        this.tableChanged = tableChanged;
    }

    public FragmentState.ResultState getResultState() {
        return resultState;
    }

    public void setResultState(FragmentState.ResultState resultState) {
        this.resultState = resultState;
    }

    public List<Word> getDoneWords() {
        return doneWords;
    }

    public static class NoExistingMistakeException extends Exception {
        public NoExistingMistakeException(String message) {
            super(message);
        }
    }
}
