package com.nikolai4.ltrainer_tabletest.model.competition;

import android.util.Log;

import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.List;

public class FullWritingTable implements Table {
    private Competition competition;
    private String question;
    private String answer;
    private FragmentState.WritingState fragmentState;

    public FullWritingTable(Competition competition, Word currentWord) {
        this.competition = competition;
        this.question = currentWord.getExpression();     // For testing only & keep in mind a few meanings may be there. handle it!
        this.answer = currentWord.getExpression();
        this.fragmentState = new FragmentState.WritingState();
    }

    @Override
    public void nextQuestion(Word nextWord) {
        setQuestion(nextWord.getTranslates().get(0));
        setAnswer(nextWord.getExpression());
        competition.setTablesQuestion(nextWord.getTranslates().get(0));
        fragmentState = new FragmentState.WritingState();
    }

    // here the ResultTable must appear, so change the competition.quizOver
    @Override
    public void nextTable() {
        if (competition.getDoneWords().size() == competition.getWordsToLearn().size()) {
            competition.setQuizOver(true);
            competition.timer().stop();
            competition.setMainTable(this);
            // 28.01.24
            List<Word> wrongWords = competition.getWrongWords();
            List<Word> rightWords = competition.getRightWords();
            FragmentState.ResultState resultState = new FragmentState.ResultState(
                    rightWords.size(), wrongWords.size(), rightWords, wrongWords);
            resultState.setTime(competition.timer().totalTime());
            competition.setResultState(resultState);
            Log.d("fullWritingTable", "nextTable: competition is over");
        }
    }

    public void skipQuestion() {
        fragmentState.setAnswered(true);
        fragmentState.setUsersAnswer(answer);
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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public FragmentState.WritingState getFragmentState() {
        return fragmentState;
    }

    public void setFragmentState(FragmentState.WritingState fragmentState) {
        this.fragmentState = fragmentState;
    }
}