package com.nikolai4.ltrainer_tabletest.db.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.nikolai4.ltrainer_tabletest.db.dao.WordDao;
import com.nikolai4.ltrainer_tabletest.model.Example;
import com.nikolai4.ltrainer_tabletest.model.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class, Example.class}, version = 13, exportSchema = false)
public abstract class WordDatabase extends RoomDatabase {

    public abstract WordDao wordDao();

    private static volatile WordDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 20;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final RoomDatabase.Callback callback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            prepopulateDb();
        }
    };

    public static WordDatabase getWordDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WordDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WordDatabase.class, "word_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(callback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void prepopulateDb() {
        List<Word> words = new ArrayList<>();

//        words.add(new Word("caveatttttttttttttttttttt", Collections.singletonList("предостережение1"), categories()));
//        words.add(new Word("embodyyyyyyyyyyyyyyyyyyyyy", Collections.singletonList("олицетворять1"), categories()));
//        words.add(new Word("sufficientttttttttttttttttt", Collections.singletonList("достаточный1"), categories()));
//        words.add(new Word("conscienceeeeeeeeeeeeeeeeeee", Collections.singletonList("совесть1"), categories()));
//        words.add(new Word("lingeringgggggggggggggggggggg", Collections.singletonList("длительный1"), categories()));
//        words.add(new Word("provokeeeeeeeeeeeeeeeeeeeee", Collections.singletonList("провоцировать1"), categories()));
//        words.add(new Word("considerablyyyyyyyyyyyyyyyyy", Collections.singletonList("существенно1"), categories()));
//        words.add(new Word("undergooooooooooooooooo", Collections.singletonList("подвергаться1"), categories()));
//        words.add(new Word("acquirrrrrrrrrrrrrrrrr", Collections.singletonList("приобрести1"), categories()));
//        words.add(new Word("caveatttttttttttttttttttt", Collections.singletonList("предостережение2"), categories()));
//        words.add(new Word("embod1111111111111111y", Collections.singletonList("олицетворять2"), categories()));
//        words.add(new Word("sufficienttttttttttttt", Collections.singletonList("достаточный2"), categories()));
//        words.add(new Word("conscienccccccccccccccce", Collections.singletonList("совесть2"), categories()));
//        words.add(new Word("lingerrrrrrrrrrrrrrrrrrrring", Collections.singletonList("длительный2"), categories()));
//        words.add(new Word("provooooooooooooooooke", Collections.singletonList("провоцировать2"), categories()));
//        words.add(new Word("considdddddddddddddddderably", Collections.singletonList("существенно2"), categories()));
//        words.add(new Word("underrrrrrrrrrrrrrrrrgo", Collections.singletonList("подвергаться2"), categories()));
//        words.add(new Word("acqqqqqqqqqqqqqqqqqquire", Collections.singletonList("приобрести2"), categories()));
//        words.add(new Word("cavvvvvvvvvvvvvvvvveat", Collections.singletonList("предостережение3"), categories()));
//        words.add(new Word("embooooooooooooooooooooody", Collections.singletonList("олицетворять3"), categories()));
//        words.add(new Word("sufffffffffffffffffffficient", Collections.singletonList("достаточный3"), categories()));
//        words.add(new Word("consssssssssssssssssscience", Collections.singletonList("совесть3"), categories()));
//        words.add(new Word("lingggggggggggggggggering", Collections.singletonList("длительный3"), categories()));
//        words.add(new Word("prooooooooooooooooooooooooovoke", Collections.singletonList("провоцировать3"), categories()));
//        words.add(new Word("conssssssssssssssssssssiderably", Collections.singletonList("существенно3"), categories()));
//        words.add(new Word("undddddddddddddddddddddergo", Collections.singletonList("подвергаться3"), categories()));
//        words.add(new Word("acquuuuuuuuuuuuuuuuuuuire", Collections.singletonList("приобрести3"), categories()));
//        words.add(new Word("caaaaaaaaaaaaaaaaaveat", Collections.singletonList("предостережение4"), categories()));
//        words.add(new Word("embooooooooooooooooooody", Collections.singletonList("олицетворять4"), categories()));
//        words.add(new Word("suffffffffffffffffffffficient", Collections.singletonList("достаточный4"), categories()));
//        words.add(new Word("consbbbbbbbbbbbbbbbbbbbcience", Collections.singletonList("совесть4"), categories()));
//        words.add(new Word("lingbbbbbbbbbbbbbbbbbbbering", Collections.singletonList("длительный4"), categories()));
//        words.add(new Word("provaaaaaaaaaaaaaaaaoke", Collections.singletonList("провоцировать4"), categories()));
//        words.add(new Word("consiaaaaaaaaaaaaaaaaaderably", Collections.singletonList("существенно4"), categories()));
//        words.add(new Word("undeaaaaaaaaaaaaaaaargo", Collections.singletonList("подвергаться4"), categories()));
//        words.add(new Word("acquaaaaaaaaaaaaaaaaaire", Collections.singletonList("приобрести4"), categories()));

        words.add(new Word("caveat", Collections.singletonList("предостережение"), categories()));
        words.add(new Word("embody", Collections.singletonList("олицетворять"), categories()));
        words.add(new Word("sufficient", Collections.singletonList("достаточный"), categories()));
        words.add(new Word("conscience", Collections.singletonList("совесть"), categories()));
        words.add(new Word("lingering", Collections.singletonList("длительный"), categories()));
        words.add(new Word("provoke", Collections.singletonList("провоцировать"), categories()));
        words.add(new Word("considerably", Collections.singletonList("существенно"), categories()));
        words.add(new Word("undergo", Collections.singletonList("подвергаться"), categories()));
        words.add(new Word("acquire", Collections.singletonList("приобрести"), categories()));

        words.add(new Word("accelerate", Collections.singletonList("ускоряться"), categories()));
        words.add(new Word("advise", Collections.singletonList("совет"), categories()));
        words.add(new Word("speak", Collections.singletonList("говорить"), categories()));
        words.add(new Word("unveil", Collections.singletonList("раскрыть"), categories()));
        words.add(new Word("zap", Collections.singletonList("бац"), categories()));
        words.add(new Word("widen", Collections.singletonList("расширять"), categories()));
        words.add(new Word("view", Collections.singletonList("вид"), categories()));
        words.add(new Word("visualize", Collections.singletonList("визуализировать"), categories()));
        words.add(new Word("withdraw", Collections.singletonList("снять"), categories()));
        words.add(new Word("witness", Collections.singletonList("свидетель"), categories()));
        words.add(new Word("zoom out", Collections.singletonList("уменьшать масштаб"), categories()));
        words.add(new Word("xerox", Collections.singletonList("ксерокопировать"), categories()));
        words.add(new Word("unify", Collections.singletonList("унифицировать"), categories()));
        words.add(new Word("validate", Collections.singletonList("подтвердить"), categories()));
        words.add(new Word("upgrade", Collections.singletonList("обновить"), categories()));
        words.add(new Word("uphold", Collections.singletonList("поддерживать"), categories()));
        words.add(new Word("utilize", Collections.singletonList("утилизировать"), categories()));
        words.add(new Word("verify", Collections.singletonList("проверить"), categories()));
        words.add(new Word("vitalize", Collections.singletonList("оживлять"), categories()));
        words.add(new Word("volunteer", Collections.singletonList("доброволец"), categories()));
        words.add(new Word("tend", Collections.singletonList("иметь тенденцию"), categories()));
        words.add(new Word("terminate", Collections.singletonList("прекратить"), categories()));
        words.add(new Word("theorize", Collections.singletonList("теоретизировать"), categories()));
        words.add(new Word("transmit", Collections.singletonList("передавать"), categories()));
        words.add(new Word("standardize", Collections.singletonList("стандартизировать"), categories()));
        words.add(new Word("stimulate", Collections.singletonList("стимулировать"), categories()));
        words.add(new Word("straighten", Collections.singletonList("выпрямлять"), categories()));
        words.add(new Word("represent", Collections.singletonList("представлять"), categories()));
        words.add(new Word("research", Collections.singletonList("исследование"), categories()));
        words.add(new Word("reserve", Collections.singletonList("резерв"), categories()));
        words.add(new Word("resolve", Collections.singletonList("разрешить"), categories()));
        words.add(new Word("respond", Collections.singletonList("откликнуться"), categories()));
        words.add(new Word("predict", Collections.singletonList("прогнозировать"), categories()));
        words.add(new Word("prepare", Collections.singletonList("подготовить"), categories()));
        words.add(new Word("preside", Collections.singletonList("председательствовать"), categories()));
        words.add(new Word("observe", Collections.singletonList("наблюдать"), categories()));
        words.add(new Word("obtain", Collections.singletonList("получать"), categories()));
        words.add(new Word("officiate", Collections.singletonList("исполнять обязанности"), categories()));
        words.add(new Word("master", Collections.singletonList("осваивать"), categories()));
        words.add(new Word("maximize", Collections.singletonList("максимизировать"), categories()));
        words.add(new Word("measure", Collections.singletonList("мера"), categories()));
        words.add(new Word("mechanize", Collections.singletonList("механизировать"), categories()));
        words.add(new Word("mediate", Collections.singletonList("посредничать"), categories()));
        words.add(new Word("narrate", Collections.singletonList("рассказывать"), categories()));
        words.add(new Word("navigate", Collections.singletonList("направлять"), categories()));
        words.add(new Word("negotiate", Collections.singletonList("вести переговоры"), categories()));
        words.add(new Word("notify", Collections.singletonList("уведомлять"), categories()));
        words.add(new Word("nurture", Collections.singletonList("воспитывать"), categories()));
        words.add(new Word("lighten", Collections.singletonList("осветить"), categories()));
        words.add(new Word("liquidate", Collections.singletonList("ликвидировать"), categories()));
        words.add(new Word("litigate", Collections.singletonList("подавать в суд"), categories()));
        words.add(new Word("lobby", Collections.singletonList("холл"), categories()));
        words.add(new Word("locate", Collections.singletonList("разместить"), categories()));
        words.add(new Word("join", Collections.singletonList("присоединиться"), categories()));
        words.add(new Word("judge", Collections.singletonList("судья"), categories()));
        words.add(new Word("justify", Collections.singletonList("оправдывать"), categories()));
        words.add(new Word("individualize", Collections.singletonList("индивидуализировать"), categories()));
        words.add(new Word("instruct", Collections.singletonList("инструктировать"), categories()));
        words.add(new Word("insure", Collections.singletonList("страховать"), categories()));
        words.add(new Word("integrate", Collections.singletonList("интегрировать"), categories()));
        words.add(new Word("interact", Collections.singletonList("взаимодействовать"), categories()));
        words.add(new Word("interface", Collections.singletonList("интерфейс"), categories()));
        words.add(new Word("interpret", Collections.singletonList("интерпретировать"), categories()));
        words.add(new Word("investigate", Collections.singletonList("исследовать"), categories()));
        words.add(new Word("incorporate", Collections.singletonList("объединять"), categories()));
        words.add(new Word("index", Collections.singletonList("индекс"), categories()));
        words.add(new Word("handle", Collections.singletonList("справляться"), categories()));
        words.add(new Word("head", Collections.singletonList("голова"), categories()));
        words.add(new Word("help", Collections.singletonList("помогать"), categories()));
        words.add(new Word("highlight", Collections.singletonList("выделить"), categories()));
        words.add(new Word("hire", Collections.singletonList("нанимать"), categories()));
        words.add(new Word("host", Collections.singletonList("хозяин"), categories()));
        words.add(new Word("gather", Collections.singletonList("собирать"), categories()));
        words.add(new Word("gauge", Collections.singletonList("калибр"), categories()));
        words.add(new Word("generate", Collections.singletonList("генерировать"), categories()));
        words.add(new Word("govern", Collections.singletonList("править"), categories()));
        words.add(new Word("grade", Collections.singletonList("оценка"), categories()));
        words.add(new Word("grant", Collections.singletonList("грант"), categories()));
        words.add(new Word("greet", Collections.singletonList("приветствовать"), categories()));
        words.add(new Word("foster", Collections.singletonList("способствовать"), categories()));
        words.add(new Word("found", Collections.singletonList("находить"), categories()));
        words.add(new Word("frame", Collections.singletonList("рамка"), categories()));
        words.add(new Word("fund", Collections.singletonList("фонд"), categories()));
        words.add(new Word("furnish", Collections.singletonList("предоставить"), categories()));
        words.add(new Word("further", Collections.singletonList("дальнейший"), categories()));
        words.add(new Word("establish", Collections.singletonList("установить"), categories()));
        words.add(new Word("estimate", Collections.singletonList("оценивать"), categories()));

        final WordDao wordDao = INSTANCE.wordDao();

        databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                wordDao.deleteAllWords();

                for (int i = 0; i < words.size(); i++) {
                    wordDao.insertWord(words.get(i));
                }
            }
        });
    }

    private static List<String> categories() {
        Random random = new Random();
        List<String> categories = new ArrayList<>();
        categories.add("Words");
        categories.add("Expressions");
        categories.add("Test");
        categories.add("Noun");
        categories.add("Verb");

        List<String> result = new ArrayList<>();
        result.add(categories.get(random.nextInt(5)));
        return result;
    }
}
