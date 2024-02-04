package com.nikolai4.ltrainer_tabletest.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkViewModel extends ViewModel {

//    private MutableLiveData<List<String>> data;
    private MutableLiveData<String> data;

    public LiveData<String> getData(String word) {
        if (data == null) {
            data = new MutableLiveData<>();
        }
        loadData(word);
        return data;
    }

//    public List<String> loadDataTask(String word) {
//        return NetworkUtils.startExecutionTask(word);
//    }

    public String loadDataTask(String word) {
//        return NetworkService.startExecutionTask(word);
        return null;
    }

    public void loadData(String word) {
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                data.postValue(loadDataTask(word));
            }
        });
    }
}
