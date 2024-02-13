package com.nikolai4.ltrainer_tabletest.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.nikolai4.ltrainer_tabletest.R;
import com.nikolai4.ltrainer_tabletest.WordRepository;
import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.model.competition.Competition;
import com.nikolai4.ltrainer_tabletest.model.competition.EnRuTable;
import com.nikolai4.ltrainer_tabletest.model.competition.FragmentState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompetitionViewModel extends AndroidViewModel {
    private WordRepository wordRepository;
    private MutableLiveData<Competition> liveDataCompetition;
    private int wordsNumber;

    public CompetitionViewModel(Application application, int wordsNumber) {
        super(application);
        wordRepository = new WordRepository(application);
        this.wordsNumber = wordsNumber;
        getLiveDataCompetition();
    }

    public LiveData<Competition> getLiveDataCompetition() {
        if (liveDataCompetition == null) {
            liveDataCompetition = new MutableLiveData<>();
            createCompetition(wordsNumber);
        }
        return liveDataCompetition;
    }

    public void setLiveDataCompetition(Competition competition) {
        liveDataCompetition.setValue(competition);
    }

    private List<Word> createQuestionList(int wordsNumber) {
        List<Word> questions = new ArrayList<>();
        while (questions.size() < wordsNumber) {
            Word question = wordRepository.getRandomWord();  // for testing only
            Log.d("question", "+++++question: " + question.toString());
            if (!questions.contains(question)) {
                questions.add(question);
            }
        }
        return questions;
//        return wordRepository.getLatestWords(wordsNumber);
    }

    private void createCompetition(int wordsNumber) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                Competition competition = new Competition(createQuestionList(wordsNumber));
                competition.setTableChanged(true);
                competition.timer().start();
                EnRuTable enRuTable = competition.getEnRuTable();

                Log.d("questioncompare", "2) question: " + enRuTable.getQuestion());
                if (enRuTable.getAnswers().isEmpty()) {
                    enRuTable.inventAnswersForQuestion(wordRepository, competition.getCurrentWord());
                    Log.d("answersforquestion", "3) table.getAnswers(): " + enRuTable.getAnswers());
                }
                liveDataCompetition.postValue(competition);
            }
        });
    }
}
