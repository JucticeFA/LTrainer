package com.nikolai4.ltrainer_tabletest.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sentence_table")
public class Sentence {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "repeatNumber")
    private int repeatNumber;

//    @ColumnInfo(name = "relatedRules")
//    @TypeConverters()   //point!)
//    private List<String> rules;
//
//    @ColumnInfo(name = "translates")
//    @TypeConverters()   //point!)
//    @NonNull
//    private List<String> translates;

    @ColumnInfo(name = "creationDate")
    private long timeStamp;

    // what else???

}
