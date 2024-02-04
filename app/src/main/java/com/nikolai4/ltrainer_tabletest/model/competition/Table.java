package com.nikolai4.ltrainer_tabletest.model.competition;

import com.nikolai4.ltrainer_tabletest.model.Word;

public interface Table {
    void nextQuestion(Word nextWord);
    void nextTable();
}
