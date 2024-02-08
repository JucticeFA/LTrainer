package com.nikolai4.ltrainer_tabletest.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nikolai4.ltrainer_tabletest.apiwork.NetDataContainer;
import com.nikolai4.ltrainer_tabletest.apiwork.NetworkService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NetworkViewModel extends ViewModel {

    private MutableLiveData<NetDataContainer> data;

    public LiveData<NetDataContainer> getData(String word) {
        if (data == null) {
            data = new MutableLiveData<>();
            loadData(word);
        }
        return data;
    }

    public NetDataContainer loadDataTask(String word) {
        return NetworkService.startDataFetchingTask(word);
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
