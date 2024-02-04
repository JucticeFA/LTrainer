package com.nikolai4.ltrainer_tabletest.model.competition;

import android.util.Log;

import com.nikolai4.ltrainer_tabletest.R;
import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuEnTable implements Table {
    private Competition competition;
    private String question;
    private List<String> answers;
    private String rightAnswer;
    private FragmentState.RuEnState fragmentState;

    public RuEnTable(Competition competition, Word currentWord) {
        this.competition = competition;
        this.question = currentWord.getTranslates().get(0);    // keep in mind a few meanings may be there. handle it!
        this.answers = new ArrayList<>();
        this.rightAnswer = currentWord.getExpression();
        this.fragmentState = new FragmentState.RuEnState();
    }

    @Override
    public void nextQuestion(Word nextWord) {
        setQuestion(nextWord.getTranslates().get(0));    // keep in mind a few meanings may be there. handle it!
        competition.setTablesQuestion(nextWord.getTranslates().get(0));
        setRightAnswer(nextWord.getExpression());
        fragmentState = new FragmentState.RuEnState();
        answers.clear();
    }

    @Override
    public void nextTable() {
        competition.setMainTable(competition.getLettersTable());
    }

    public void inventAnswersForQuestion(WordViewModel model) {
        answers.clear();
        answers.add(rightAnswer);
        while (answers.size() < 4) {
            String randomAnswer = model.getRandomWord().getExpression();
            if (!answers.contains(randomAnswer)) {
                answers.add(randomAnswer);
                Log.d("randomAnswer", "randomAnswer: " + randomAnswer);
            }
        }
        Collections.shuffle(answers);
    }

    public void convertDataToState(String usersAnswer) {
        List<Integer> colors = new ArrayList<>(answers.size());
        int rightColor = R.drawable.button_right;
        int wrongColor = R.drawable.button_wrong;
        int standardColor = R.drawable.button_standard;

        for (int j = 0; j < answers.size(); j++) {
            colors.add(standardColor);
        }

        if (!usersAnswer.equalsIgnoreCase(rightAnswer)) {
            colors.set(answers.indexOf(usersAnswer), wrongColor);
        }
        colors.set(answers.indexOf(rightAnswer), rightColor);

        fragmentState.setIsAnswered(true);
        fragmentState.setButtonColors(colors);
    }

    public Competition getCompetition() {
        return competition;
    }

    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public FragmentState.RuEnState getFragmentState() {
        return fragmentState;
    }

    public void setFragmentState(FragmentState.RuEnState fragmentState) {
        this.fragmentState = fragmentState;
    }
}