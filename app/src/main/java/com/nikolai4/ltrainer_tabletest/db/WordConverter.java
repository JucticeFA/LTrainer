package com.nikolai4.ltrainer_tabletest.db;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.List;

public class WordConverter {

    @TypeConverter
    public String fromTranslateList(List<String> translates) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < translates.size(); i++) {
            builder.append(translates.get(i));
            if (i < translates.size()-1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    @TypeConverter
    public List<String> toTranslateList(String translate) {
        return Arrays.asList(translate.split(", "));
    }

    public String fromGroupCategoryList(List<String> groupCategory) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < groupCategory.size(); i++) {
            builder.append(groupCategory.get(i));
            if (i < groupCategory.size()-1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public List<String> toGroupCategoryList(String groupCategory) {
        return Arrays.asList(groupCategory.split(", "));
    }

    public String fromExamplesList(List<String> examples) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < examples.size(); i++) {
            builder.append(examples.get(i));
            if (i < examples.size()-1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public List<String> toExamplesList(String examples) {
        return Arrays.asList(examples.split(", "));
    }


    public String fromOxfordsInfoList(List<String> info) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < info.size(); i++) {
            builder.append(info.get(i));
            if (i < info.size()-1) {
                builder.append("<>");
            }
        }
        return builder.toString();
    }

    public List<String> toOxfordsInfoList(String info) {
        return Arrays.asList(info.split("<>"));
    }
//    public long fromDate(Date creationDate) {
//        return creationDate.getTime();
//    }
//
//    public Date toDate(long timeStamp) {
//        return new Date(timeStamp);
//    }
//
//    public String fromLexicalList(List<String> lexicalCategory) {
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < lexicalCategory.size(); i++) {
//            builder.append(lexicalCategory.get(i));
//            if (i < lexicalCategory.size()-1) {
//                builder.append(",");
//            }
//        }
//        return builder.toString();
//    }

//    public List<String> toLexicalList(String lexicalCategory) {
//        return Arrays.asList(lexicalCategory.split(","));
//    }
//
//
//    public String fromSynonymsList(List<String> synonyms) {
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < synonyms.size(); i++) {
//            builder.append(synonyms.get(i));
//            if (i < synonyms.size()-1) {
//                builder.append(",");
//            }
//        }
//        return builder.toString();
//    }
//
//    public List<String> toSynonymsList(String synonyms) {
//        return Arrays.asList(synonyms.split(","));
//    }
}
