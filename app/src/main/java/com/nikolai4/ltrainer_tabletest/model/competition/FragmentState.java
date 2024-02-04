package com.nikolai4.ltrainer_tabletest.model.competition;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.utils.Constants;
import com.nikolai4.ltrainer_tabletest.R;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FragmentState {
    private EnRuState enRuState;
    private RuEnState ruEnState;
    private LettersState lettersState;
    private WritingState writingState;
    private ResultState resultState;

    public FragmentState(EnRuState enRuState) {
        this.enRuState = enRuState;
    }

    public FragmentState(RuEnState ruEnState) {
        this.ruEnState = ruEnState;
    }

    public FragmentState(LettersState lettersState) {
        this.lettersState = lettersState;
    }

    public FragmentState(WritingState writingState) {
        this.writingState = writingState;
    }

    public FragmentState(ResultState resultState) {
        this.resultState = resultState;
    }


    public EnRuState getEnRuState() {
        return enRuState;
    }

    public void setEnRuState(EnRuState enRuState) {
        this.enRuState = enRuState;
    }

    public RuEnState getRuEnState() {
        return ruEnState;
    }

    public void setRuEnState(RuEnState ruEnState) {
        this.ruEnState = ruEnState;
    }

    public LettersState getLettersState() {
        return lettersState;
    }

    public void setLettersState(LettersState lettersState) {
        this.lettersState = lettersState;
    }

    public WritingState getWritingState() {
        return writingState;
    }

    public void setWritingState(WritingState writingState) {
        this.writingState = writingState;
    }

    public ResultState getResultState() {
        return resultState;
    }

    public void setResultState(ResultState resultState) {
        this.resultState = resultState;
    }

    public static class EnRuState {
        private Boolean isAnswered;
        private List<Integer> buttonColors = new ArrayList<>(4);

        public EnRuState() {
            this.isAnswered = false;
            resetButtonColors();
        }

        public EnRuState(Boolean isAnswered, List<Integer> buttonColors) {
            this.isAnswered = isAnswered;
            this.buttonColors = buttonColors;
        }

        public Boolean isAnswered() {
            return isAnswered;
        }

        public void setIsAnswered(Boolean answered) {
            isAnswered = answered;
        }

        public List<Integer> getButtonColors() {
            return buttonColors;
        }

        public void setButtonColors(List<Integer> buttonColors) {
            this.buttonColors = buttonColors;
        }

        public void resetButtonColors() {
            buttonColors.clear();
            for (int i = 0; i < 4; i++) {
                buttonColors.add(R.drawable.button_standard);
            }
        }
    }

    public static class RuEnState {
        private Boolean isAnswered;
        private List<Integer> buttonColors = new ArrayList<>(4);

        public RuEnState() {
            this.isAnswered = false;
            resetButtonColors();
        }

        public RuEnState(Boolean isAnswered, List<Integer> buttonColors) {
            this.isAnswered = isAnswered;
            this.buttonColors = buttonColors;
        }

        public Boolean isAnswered() {
            return isAnswered;
        }

        public void setIsAnswered(Boolean answered) {
            isAnswered = answered;
        }

        public List<Integer> getButtonColors() {
            return buttonColors;
        }

        public void setButtonColors(List<Integer> buttonColors) {
            this.buttonColors = buttonColors;
        }

        public void resetButtonColors() {
            buttonColors.clear();
            for (int i = 0; i < 4; i++) {
                buttonColors.add(R.drawable.button_standard);
            }
        }
    }

    public static class LettersState {
        private List<Character> usedLetters = new ArrayList<>();
        private List<Character> abandonedLetters = new ArrayList<>();
        private Integer rightLetterId = 0;
        private boolean mistake;
        private boolean isAnswered;

        public LettersState() {
            resetState();
        }
        public LettersState(List<Character> usedLetters,
                            List<Character> abandonedLetters,
                            Integer rightLetterId) {
            this.usedLetters = usedLetters;
            this.abandonedLetters = abandonedLetters;
            this.rightLetterId = rightLetterId;
        }

        public List<Character> getUsedLetters() {
            return usedLetters;
        }
        public List<Character> getAbandonedLetters() {
            return abandonedLetters;
        }
        public Integer getRightLetterId() {
            return rightLetterId;
        }

        public void setRightLetterId(Integer rightLetterId) {
            this.rightLetterId = rightLetterId;
        }

        public void setUsedLetters(List<Character> usedLetters) {
            this.usedLetters = usedLetters;
        }

        public void setAbandonedLetters(List<Character> abandonedLetters) {
            this.abandonedLetters = abandonedLetters;
        }

        public boolean isThereMistake() {
            return mistake;
        }

        public void setMistake(boolean mistake) {
            this.mistake = mistake;
        }

        public boolean isAnswered() {
            return isAnswered;
        }

        public void setAnswered(boolean answered) {
            isAnswered = answered;
        }

        /**
         * Returns true if usedLetters is empty + right letterId = 0 + mistake = false
         */
        public boolean isStateFresh() {
            return usedLetters.isEmpty() && rightLetterId == 0 && !mistake;
        }

        public void resetState() {
            usedLetters = new ArrayList<>();
            abandonedLetters = new ArrayList<>();
            rightLetterId = 0;
            mistake = false;
            isAnswered = false;
        }
    }

    public static class WritingState {
        private String usersAnswer = "";
        private boolean isAnswered;
        public WritingState() {
            resetState();
        }
        public WritingState(String usersAnswer) {
            this.usersAnswer = usersAnswer;
        }
        public String getUsersAnswer() {
            return usersAnswer;
        }
        public void setUsersAnswer(String usersAnswer) {
            this.usersAnswer = usersAnswer;
        }
        public boolean isAnswered() {
            return isAnswered;
        }
        public void setAnswered(boolean answered) {
            isAnswered = answered;
        }

        public void resetState() {
            usersAnswer = "";
            isAnswered = false;
        }
    }

    public static class ResultState {
        private String congratulation;
        private String time;
        private int rightsNumber;
        private int wrongsNumber;
        private List<Word> rightWords;
        private List<Word> wrongWords;

        public ResultState() {
        }

        public ResultState(int rightsNumber, int wrongsNumber, List<Word> rightWords, List<Word> wrongWords) {
            this.rightsNumber = rightsNumber;
            this.wrongsNumber = wrongsNumber;
            this.rightWords = rightWords;
            this.wrongWords = wrongWords;
            congratulation = generateCongratulation(rightsNumber);
        }

        public String getCongratulation() {
            return congratulation;
        }

        public void setCongratulation(String congratulations) {
            this.congratulation = congratulations;
        }

        public String generateCongratulation(int score) {
            String congratulation = "";
            int learningWordsSize = rightsNumber + wrongsNumber;
            float percentage =  (float) score / learningWordsSize * 100;

            if (percentage == 100f) {
                List<String> grades = new ArrayList<>(Arrays.asList(
                        Constants.PERFECT, Constants.AMAZING,Constants.GRATE_JOB,
                        Constants.EXCELLENT, Constants.YOU_ARE_THE_BEST));
                congratulation = grades.get((int) (Math.random() * grades.size()));
            }
            if (percentage >= 90f && percentage < 100f) {
                List<String> grades = new ArrayList<>(Arrays.asList(
                        Constants.GOOD_JOB, Constants.WELL_DONE,Constants.NICE, Constants.SO_CLOSE));
                congratulation = grades.get((int) (Math.random() * grades.size()));
            }
            if (percentage >= 50f && percentage < 90f) {
                List<String> grades = new ArrayList<>(Arrays.asList(
                        Constants.NOT_BAD, Constants.WILL_DO,Constants.MIGHT_BE_BETTER,
                        Constants.ROOM_FOR_GROWTH));
                congratulation = grades.get((int) (Math.random() * grades.size()));
            }
            if (percentage >= 10f && percentage < 50f) {
                List<String> grades = new ArrayList<>(Arrays.asList(
                        Constants.SIT_DOWN_TWO, Constants.REALLY_BAD,Constants.NOT_YOUR_DAY,
                        Constants.BUT_YOU_BEAUTIFUL));
                congratulation = grades.get((int) (Math.random() * grades.size()));
            }
            if (percentage < 10f) {
                List<String> grades = new ArrayList<>(Arrays.asList(
                        Constants.DRUNK, Constants.WHAT_WAS_THAT,Constants.DONT_WORRY,
                        Constants.CHANGE_LANGUAGE, Constants.REPEAT_AND_REPEAT));
                congratulation = grades.get((int) (Math.random() * grades.size()));
            }

            return congratulation;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public int getRightsNumber() {
            return rightsNumber;
        }

        public void setRightsNumber(int rightsNumber) {
            this.rightsNumber = rightsNumber;
        }

        public int getWrongsNumber() {
            return wrongsNumber;
        }

        public void setWrongsNumber(int wrongsNumber) {
            this.wrongsNumber = wrongsNumber;
        }

        public List<Word> getRightWords() {
            return rightWords;
        }

        public void setRightWords(List<Word> rightWords) {
            this.rightWords = rightWords;
        }

        public List<Word> getWrongWords() {
            return wrongWords;
        }

        public void setWrongWords(List<Word> wrongWords) {
            this.wrongWords = wrongWords;
        }
    }

    public void resetState() {
        setEnRuState(new EnRuState());
        setRuEnState(new RuEnState());
        setLettersState(new LettersState(new ArrayList<>(), new ArrayList<>(), 0));
        setWritingState(new WritingState(""));
    }
}
