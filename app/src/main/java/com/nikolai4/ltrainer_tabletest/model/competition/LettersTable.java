package com.nikolai4.ltrainer_tabletest.model.competition;

import android.util.Log;

import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LettersTable implements Table {
    private Competition competition;
    private List<Character> letters;
    private String question;
    private String answer;
    private FragmentState.LettersState fragmentState;

    public LettersTable(Competition competition, Word currentWord) {
        this.competition = competition;
        this.question = currentWord.getTranslates().get(0); // keep in mind a few meanings may be there. handle it!
        this.answer = currentWord.getExpression();
        this.letters = new ArrayList<>(getShuffledLetters(answer.toUpperCase()));
        this.fragmentState = new FragmentState.LettersState();
        this.fragmentState.setAbandonedLetters(letters);
    }

    @Override
    public void nextQuestion(Word nextWord) {
        setQuestion(nextWord.getTranslates().get(0));    // keep in mind a few meanings may be there. handle it!
        competition.setTablesQuestion(nextWord.getTranslates().get(0));
        setAnswer(nextWord.getExpression());
        setLetters(getShuffledLetters(answer));
        fragmentState = new FragmentState.LettersState();
        fragmentState.setAbandonedLetters(letters);
        Log.d("whatAnswer", "answer: " + answer);
    }

    @Override
    public void nextTable() {
        competition.setMainTable(competition.getFullWritingTable());
    }

    private List<Character> getShuffledLetters(String answer) {
        List<Character> letters = new ArrayList<>(answer.length());
        for (int i = 0; i < answer.length(); i++) {
            letters.add(answer.toUpperCase().charAt(i));
        }
        Collections.shuffle(letters);
        return letters;
    }

    public void skipQuestion() {
        fragmentState.setAnswered(true);
        List<Character> usedLetters = new ArrayList<>(answer.length());
        for (int i = 0; i < answer.length(); i++) {
            usedLetters.add(answer.toUpperCase().charAt(i));
        }
        fragmentState.setUsedLetters(usedLetters);
        fragmentState.setAbandonedLetters(new ArrayList<>());
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public List<Character> getLetters() {
        return letters;
    }

    public void setLetters(List<Character> letters) {
        this.letters = letters;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getRightAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public FragmentState.LettersState getFragmentState() {
        return fragmentState;
    }

    public void setFragmentState(FragmentState.LettersState fragmentState) {
        this.fragmentState = fragmentState;
    }
}