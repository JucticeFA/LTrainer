package com.nikolai4.ltrainer_tabletest.viewmodels;

import android.app.Application;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CompetitionViewModelFactory implements ViewModelProvider.Factory {
    private Application application;
    private int wordsNumber;

    public CompetitionViewModelFactory(Application application, int wordsNumber) {
        this.application = application;
        this.wordsNumber = wordsNumber;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CompetitionViewModel(application, wordsNumber);
    }
}
