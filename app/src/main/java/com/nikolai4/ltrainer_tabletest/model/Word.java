package com.nikolai4.ltrainer_tabletest.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.nikolai4.ltrainer_tabletest.utils.Constants;
import com.nikolai4.ltrainer_tabletest.db.WordConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "word_table")
public class Word implements Serializable {

    // general info
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "expression")
    @NonNull
    private String expression;

    @ColumnInfo(name = "transcription")
    @NonNull
    private String transcription;

    @ColumnInfo(name = "translates")
    @TypeConverters(WordConverter.class)
    @NonNull
    private List<String> translates;

    // 12.01.24. Added due to some problems related to the List of translates may appear
    // The main idea is: one word - one meaning
//    @ColumnInfo(name = "meaning")
//    @NonNull
//    private String meaning;

    @ColumnInfo(name = "groupCategory")
    @TypeConverters(WordConverter.class)
    @NonNull
    private List<String> groupCategory;

    @ColumnInfo(name = "creationDate")
    private long timeStamp;

    @ColumnInfo(name = "audioLink")
    @NonNull
    private String audioLink;

    // oxford's info
    @ColumnInfo(name = "oxfordsInfo")
    @TypeConverters(WordConverter.class)
    @NonNull
    private List<String> oxfordsInfo;

//    @ColumnInfo(name = "definition")
//    @NonNull
//    private String definition;

//    @ColumnInfo(name = "lexicalCategory")
//    @NonNull
//    private List<String> lexicalCategory;

//    @ColumnInfo(name = "synonyms")
//    @NonNull
//    private List<String> synonyms;  // can it be used in DB and how it should be done?

    // notes
    @ColumnInfo(name = "notes")
    @NonNull
    private String notes;

    // statistics
    // depends on a user's preferences
    @ColumnInfo(name = "totalRepeats")
    private int totalRepeats;

    /**
     * Depends on the number of mistakes in a row (mistakes >= 1 per round = 1 per game)
     */
    @ColumnInfo(name = "mistakesFrequency")
    private int mistakesFrequency;

    @ColumnInfo(name = "repeatNumber")
    private int repeatNumber;

    @ColumnInfo(name = "progressState")
    @NonNull
    private String progressState;

    @ColumnInfo(name = "learningPercentage")
    private int learningPercentage;

    @ColumnInfo(name = "nextRepeat")
    private int nextRepeat;

    @ColumnInfo(name = "easyToLearn")
    @NonNull
    private String difficulty;

    @Ignore
    private boolean selected;

    public Word(@NonNull String expression, @NonNull List<String> translates,
                @NonNull List<String> groupCategory) {
        this.expression = expression;
        this.translates = translates;
        this.groupCategory = groupCategory;
        transcription = "";
        timeStamp = new Date().getTime();
        repeatNumber = 0;
        learningPercentage = 0;
        nextRepeat = 0;
        totalRepeats = 8;
        mistakesFrequency = 0;
        difficulty = Constants.NORMAL_TO_LEARN;
        progressState = Constants.DEFAULT_PROGRESS_STATE;
        audioLink = "";
        notes = "";
        oxfordsInfo = new ArrayList<>();
    }

    @Ignore
    public Word(int id, @NonNull String expression, @NonNull List<String> translates,
                @NonNull List<String> groupCategory) {
        this.id = id;
        this.expression = expression;
        this.translates = translates;
        this.groupCategory = groupCategory;
        transcription = "";
        timeStamp = new Date().getTime();
        repeatNumber = 0;
        learningPercentage = 0;
        nextRepeat = 0;
        totalRepeats = 8;
        mistakesFrequency = 0;
        difficulty = Constants.NORMAL_TO_LEARN;
        progressState = Constants.DEFAULT_PROGRESS_STATE;
        audioLink = "";
        notes = "";
        oxfordsInfo = new ArrayList<>();
    }

    @Ignore
    public Word(Word word) {
        this.id = word.id;
        this.expression = word.getExpression();
        this.translates = word.getTranslates();
        this.groupCategory = word.getGroupCategory();
        transcription = word.getTranscription();
        timeStamp = word.getTimeStamp();
        repeatNumber = word.getRepeatNumber();
        learningPercentage = word.getLearningPercentage();
        nextRepeat = word.getNextRepeat();
        totalRepeats = 8;
        mistakesFrequency = word.getMistakesFrequency();
        difficulty = word.getDifficulty();
        progressState = word.getProgressState();
        audioLink = word.getAudioLink();
        notes = word.getNotes();
        oxfordsInfo = word.getOxfordsInfo();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRepeatNumber() {
        return repeatNumber;
    }

    public void setRepeatNumber(int repeatNumber) {
        this.repeatNumber = repeatNumber;
    }

    @NonNull
    public String getExpression() {
        return expression;
    }

    public void setExpression(@NonNull String expression) {
        this.expression = expression;
    }

//    @NonNull
//    public String getMeaning() {
//        return meaning;
//    }
//
//    public void setMeaning(@NonNull String meaning) {
//        this.meaning = meaning;
//    }

    @NonNull
    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(@NonNull String transcription) {
        this.transcription = transcription;
    }

    @NonNull
    public List<String> getOxfordsInfo() {
        return oxfordsInfo;
    }

    public void setOxfordsInfo(@NonNull List<String> oxfordsInfo) {
        this.oxfordsInfo = oxfordsInfo;
    }

    @NonNull
    public List<String> getTranslates() {
        return translates;
    }

    public void setTranslates(@NonNull List<String> translates) {
        this.translates = translates;
    }

    @NonNull
    public String getNotes() {
        return notes;
    }

    public void setNotes(@NonNull String notes) {
        this.notes = notes;
    }

    @NonNull
    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(@NonNull String audioLink) {
        this.audioLink = audioLink;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @NonNull
    public String getProgressState() {
        return progressState;
    }

    public void setProgressState(@NonNull String progressState) {
        this.progressState = progressState;
    }

    public int getTotalRepeats() {
        return totalRepeats;
    }

    public void setTotalRepeats(int totalRepeats) {
        this.totalRepeats = totalRepeats;
    }

    public int getMistakesFrequency() {
        return mistakesFrequency;
    }

    public void setMistakesFrequency(int mistakesFrequency) {
        this.mistakesFrequency = mistakesFrequency;
    }

    @NonNull
    public List<String> getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(@NonNull List<String> groupCategory) {
        this.groupCategory = groupCategory;
    }

    @NonNull
    public String getDifficulty() {
        return difficulty;
    }

    @NonNull
    public void setDifficulty(@NonNull String difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getLearningPercentage() {
        return learningPercentage;
    }

    public void setLearningPercentage(int learningPercentage) {
        this.learningPercentage = learningPercentage;
    }

    public int getNextRepeat() {
        return nextRepeat;
    }

    public void setNextRepeat(int nextRepeat) {
        this.nextRepeat = nextRepeat;
    }

    /**
     * Should be called after the last train table done.
     * If a user was mistaken, the next repeat will be in 2 days.
     * @param isMistaken
     */
    public void adjustRepeat(boolean isMistaken) {
        if (!isMistaken) {
            switch (repeatNumber) {
                case 0: repeatNumber = 1; nextRepeat = 1; break;
                case 1: repeatNumber = 2; nextRepeat = 3; break;
                case 2: repeatNumber = 3; nextRepeat = 7; break;
                case 3: repeatNumber = 4; nextRepeat = 14; break;
                case 4: repeatNumber = 5; nextRepeat = 30; break;
                case 5: repeatNumber = 6; nextRepeat = 45; break;
                case 6: repeatNumber = 7; nextRepeat = 65; break;
                case 7: repeatNumber = 8; nextRepeat = 95; break;
            }
        } else {
            nextRepeat = 2;
        }
    }

    /**
     * Compiles the Word's statistics after checking a user's answer
     * on the last table (by default FullWritingTable).
     * Word's statistics consists of learning percentage, progress state and difficulty.
     */
    public void compileStatistics() {
        learningPercentage = repeatNumber / totalRepeats * 100;

        if (repeatNumber > 0 && repeatNumber < totalRepeats) {
            progressState = Constants.IN_PROCESS_PROGRESS_STATE;
        }
        if (repeatNumber == totalRepeats) {
            progressState = Constants.LEARNED_PROGRESS_STATE;
        }

        if (mistakesFrequency <= 1) {
            difficulty = Constants.EASY_TO_LEARN;
        }
        if (mistakesFrequency > 1 && mistakesFrequency < 4) {
            difficulty = Constants.NORMAL_TO_LEARN;
        }
        if (mistakesFrequency >= 4) {
            difficulty = Constants.HARD_TO_LEARN;
        }
    }

    /**
     * Increases the mistakes frequency regarding the correctness of the user's answer.
     * Necessary for the statistics.
     * @param isMistaken
     */
    public void increaseMistakesFrequency(boolean isMistaken) {
        if (isMistaken) {
            ++mistakesFrequency;
        }
    }

//    @NonNull
//    public String getDefinition() {
//        return definition;
//    }
//
//    public void setDefinition(@NonNull String definition) {
//        this.definition = definition;
//    }

//    @NonNull
//    public List<String> getLexicalCategory() {
//        return lexicalCategory;
//    }
//
//    public void setLexicalCategory(@NonNull List<String> lexicalCategory) {
//        this.lexicalCategory = lexicalCategory;
//    }
//

//
//    @NonNull
//    public List<String> getSynonyms() {
//        return synonyms;
//    }
//
//    public void setSynonyms(@NonNull List<String> synonyms) {
//        this.synonyms = synonyms;
//    }


//    @NonNull
//    public List<String> getExamples() {
//        return examples;
//    }
//
//    public void setExamples(@NonNull List<String> examples) {
//        this.examples = examples;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return id == word.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
