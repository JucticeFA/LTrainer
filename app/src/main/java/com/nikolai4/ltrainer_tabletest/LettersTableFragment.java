package com.nikolai4.ltrainer_tabletest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.model.competition.Competition;
import com.nikolai4.ltrainer_tabletest.model.competition.FragmentState;
import com.nikolai4.ltrainer_tabletest.model.competition.LettersTable;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModel;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModelFactory;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LettersTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LettersTableFragment extends Fragment {
    private LinearLayoutCompat gapsLayout;
    private LinearLayoutCompat lettersLayout;
    private TextView examplesText;
    private TextView nextText;
    private final List<TextView> gapsList = new ArrayList<>();

    private WordViewModel wordViewModel;
    private CompetitionViewModel competitionViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LettersTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LettersTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LettersTableFragment newInstance(String param1, String param2) {
        LettersTableFragment fragment = new LettersTableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_letters_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViews(view);

        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
//        competitionViewModel = ViewModelProviders.of(requireActivity()).get(CompetitionViewModel.class);
        CompetitionViewModelFactory factory = new CompetitionViewModelFactory(requireActivity().getApplication(), 5);
        competitionViewModel = ViewModelProviders.of(requireActivity(), factory).get(CompetitionViewModel.class);

        competitionViewModel.getLiveDataCompetition().observe(getViewLifecycleOwner(), competition -> {
            if (competition.getCurrentTable() instanceof LettersTable) {
                LettersTable table = competition.getLettersTable();
                FragmentState.LettersState state = table.getFragmentState();
                // There is no need to update the vies each time the data change
                Log.d("emptyandfresh", "gapsList.isEmpty(): " + gapsList.isEmpty()
                        + ", table.getFragmentState().isStateFresh():" + table.getFragmentState().isStateFresh());
                if (gapsList.isEmpty() || state.isStateFresh()) {
                    tieDataToViews(competition, table);
                    Log.d("here", "i'am here");
                }
                setNextClickListener(nextText, competition);
            }
        });
    }

    public void setNextClickListener(TextView nextText, Competition competition) {
        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LettersTable table = competition.getLettersTable();
                FragmentState.LettersState state = table.getFragmentState();

                if (!state.isAnswered()) {
                    Word currentWord = competition.getCurrentWord();

                    table.skipQuestion();

                    lettersLayout.removeAllViews();
                    for (int i = 0; i < state.getUsedLetters().size(); i++) {
                        gapsList.get(i).setText(state.getUsedLetters().get(i).toString());
                    }
                    setNextTextLabel(state.isAnswered());

                    competition.addMistake(currentWord, table);
                    Log.d("getMistakes", "mistakes after rotation: " +
                            competitionViewModel.getLiveDataCompetition().getValue().getMistakes().toString());
                } else {
                    competition.checkAnswer(competition.getCurrentWord(), table);
                    competition.nextQuestion();

                    if (competition.getCurrentTable() instanceof LettersTable) {
                        competition.setTableChanged(false);
                    } else {
                        competition.setTableChanged(true);
                    }
                }
                competitionViewModel.setLiveDataCompetition(competition);
            }
        });
    }

    private void tieDataToViews(Competition competition, LettersTable table) {
        String answer = table.getRightAnswer();
        List<Character> letters = table.getLetters();
        FragmentState.LettersState state = table.getFragmentState();

        Log.d("tablegetLetters", "letters: " + letters);
        Log.d("tablegetAnswer", "table.getAnswer(): " + table.getRightAnswer());

        setGaps(letters.size(), table);
        setLetters(letters, competition);

        setNextTextLabel(state.isAnswered());
    }

    private void setNextTextLabel(boolean isAnswered) {
        if (isAnswered) {
            nextText.setText(R.string.next);
        } else {
            nextText.setText(R.string.show_the_answer);
        }
    }

    /**
     * In progress...
     * This method is supposed to reveal letter by letter of a TextView answer-field
     * @param view
     */
    private void setAnswerField(TextView view) {

    }

    private void setGaps(int lettersCount, LettersTable table) {
        // it helps to remove all inner views before a new question appears
        gapsLayout.removeAllViews();
        // refreshes the gapsList
        gapsList.clear();

        FragmentState.LettersState state = table.getFragmentState();
        List<Character> usedLetters = state.getUsedLetters();

        Log.d("usedletters", "usedletters: " + usedLetters);

        gapsLayout.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                LinearLayout gapsLine = new LinearLayout(requireContext());
                final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
                params.gravity = Gravity.CENTER_HORIZONTAL;

                List<LinearLayout> linesList = new ArrayList<>();

                gapsLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int gapWidth = dpToPx(34);    // counting margins (2+2)
                int mainContainerWidth = gapsLayout.getWidth();
                int gapsSumWidth = lettersCount * gapWidth;
                int gapsPerLine = mainContainerWidth / gapWidth;
                int linesCount = (int)(Math.ceil((double) gapsSumWidth / mainContainerWidth));

                for (int i = 0; i < linesCount; i++) {
                    linesList.add(new LinearLayout(requireContext()));
                    linesList.get(i).setLayoutParams(params);
                    linesList.get(i).setGravity(Gravity.CENTER_HORIZONTAL);
                    linesList.get(i).setOrientation(LinearLayout.HORIZONTAL);
                    gapsLayout.addView(linesList.get(i));
                }

                for (int i = 0; i < lettersCount; i++) {
                    TextView gapView = new TextView(requireContext());
                    LinearLayout.LayoutParams paramsGap = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsGap.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
                    paramsGap.gravity = Gravity.CENTER_HORIZONTAL;

                    gapView.setMinWidth(dpToPx(30));
                    gapView.setMaxWidth(dpToPx(30));
                    gapView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    // sets a proper letter(s) according to the model
                    if (i < usedLetters.size()) {
                        gapView.setText(usedLetters.get(i).toString());
                    } else {
                        gapView.setText("_");
                    }
                    gapView.setTextSize(30);
                    gapView.setBackground(AppCompatResources.getDrawable(requireContext(), R.drawable.gap_shape));
                    gapView.setLayoutParams(paramsGap);

                    // places gaps into a certain line
                    int lineId = i / gapsPerLine;
                    gapsList.add(gapView);
                    linesList.get(lineId).addView(gapView);
                }
            }
        });
    }

    private void setLetters(List<Character> letters, Competition competition) {
        // helps to remove all inner views before a new question appears
        lettersLayout.removeAllViews();
        int lettersCount = letters.size();

        final ArrayList<Character> abandonedLetters = new ArrayList<>();

        FragmentState.LettersState state = competition.getLettersTable().getFragmentState();

        if (!state.getUsedLetters().isEmpty()) {
            lettersCount = state.getAbandonedLetters().size();
            abandonedLetters.addAll(state.getAbandonedLetters());
        } else {
            abandonedLetters.addAll(letters);
        }
        Log.d("lettersinfo", "1) usedletters: " + state.getUsedLetters()
                + ", abandonedLetters: " + abandonedLetters
                + ", state.getAbandonedLetters():" + state.getAbandonedLetters()
                + ", letters: " + letters);

        final int finalLettersCount = lettersCount;

        lettersLayout.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                List<LinearLayout> linesList = new ArrayList<>();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
                params.gravity = Gravity.CENTER_HORIZONTAL;

                int mainContainerWidth;
                int letterWidth = dpToPx(34);    // counting margins (2+2)
                int lettersPerLine;

                lettersLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mainContainerWidth = lettersLayout.getWidth();

                // defining lettersCount in the lines that meant to the letters
                float linesCount = (float) ((dpToPx(34)) * finalLettersCount) / mainContainerWidth;
                if (linesCount % 2.0f != 0.0f || linesCount % 2.0f != 1.0f) {
                    linesCount += 1.0f;
                }

                Log.d("linesCount", "linesCount float: " + linesCount + ", linesCount int: " + (int)linesCount);
                // adds lines meant for letters
                for (int i = 0; i < (int) linesCount; i++) {
                    linesList.add(new LinearLayout(requireContext()));
                    linesList.get(i).setLayoutParams(params);
                    linesList.get(i).setGravity(Gravity.CENTER_HORIZONTAL);
                    linesList.get(i).setOrientation(LinearLayout.HORIZONTAL);

                    lettersLayout.addView(linesList.get(i));
                }
                Log.d("lettersLayout", "lettersLayout.getChildCount(): " + lettersLayout.getChildCount());

                lettersPerLine = mainContainerWidth / letterWidth;
                Log.d("lettersPerLine", "lettersPerLine: " + lettersPerLine);

                List<TextView> lettersViewList = new ArrayList<>();

                // puts the text on the letters
                for (int i = 0; i < finalLettersCount; i++) {
                    TextView letterView = new TextView(requireContext());
                    LinearLayout.LayoutParams paramsLetter = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsLetter.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
                    paramsLetter.gravity = Gravity.CENTER_HORIZONTAL;

                    letterView.setGravity(Gravity.CENTER_HORIZONTAL);
                    letterView.setLayoutParams(paramsLetter);
                    letterView.setMinWidth(dpToPx(30));
                    letterView.setMaxWidth(dpToPx(30));
                    letterView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    letterView.setText(abandonedLetters.get(i).toString());
                    letterView.setTextSize(30);
                    letterView.setBackground(AppCompatResources.getDrawable(requireContext(),
                            R.drawable.gap_shape));

                    lettersViewList.add(letterView);

                    // places letters into a certain line
                    int lineId = i / lettersPerLine;
                    linesList.get(lineId).addView(letterView);
                }

                // defines events for letters' clickListener
                setLettersClickListener(lettersViewList, competition);
            }
        });
    }

    private void setLettersClickListener(List<TextView> lettersViewList, Competition competition) {
        for (int i = 0; i < lettersViewList.size(); i++) {
            TextView currentLetter = lettersViewList.get(i);
            currentLetter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Word currentWord = competition.getCurrentWord();
                    FragmentState.LettersState state = competition.getLettersTable().getFragmentState();
                    int rightLetterId = state.getRightLetterId();
                    char rightLetter = competition.getLettersTable().getRightAnswer().charAt(rightLetterId);
                    Character usersLetter = currentLetter.getText().toString().charAt(0);
                    boolean userRight = (usersLetter.toString()).equalsIgnoreCase(Character.toString(rightLetter));
                    List<Character> abandonedLetters = new ArrayList<>(state.getAbandonedLetters());
                    List<Character> usedLetters = new ArrayList<>(state.getUsedLetters());
                    boolean isAnswered = state.isAnswered();

                    if (!isAnswered) {
                        if (userRight) {
                            gapsList.get(rightLetterId).setText(usersLetter.toString());
                            currentLetter.setVisibility(View.GONE);

                            abandonedLetters.remove(usersLetter);
                            usedLetters.add(usersLetter);

                            highlightLetter(gapsList.get(rightLetterId), true);

                            ++rightLetterId;

                            if (abandonedLetters.isEmpty() && !state.isThereMistake()) {
                                try {
                                    competition.mistakeByWord(currentWord).fix();
                                } catch (Competition.NoExistingMistakeException e) {
                                    e.printStackTrace();
                                }
                            }
                            state.setAbandonedLetters(abandonedLetters);
                            state.setUsedLetters(usedLetters);
                            state.setRightLetterId(rightLetterId);
                        } else {
                            highlightLetter(currentLetter, false);

                            currentWord.adjustRepeat(true);
                            wordViewModel.updateWord(currentWord);

                            // Prevents from interminable adding the same word
                            if (!state.isThereMistake()) {
                                competition.addMistake(currentWord, competition.getLettersTable());
                            }
                            Log.d("ismistake", "" + state.isThereMistake());
                            state.setMistake(true);
                            Log.d("ismistake", "" + state.isThereMistake());
                        }

                        if (abandonedLetters.isEmpty()) {
                            state.setAnswered(true);
                            setNextTextLabel(true);
                        }

                        competition.getLettersTable().setFragmentState(state);

                        Log.d("lettersinfo", "2) usedletters: " + state.getUsedLetters()
                                + ", abandonedLetters: " + abandonedLetters
                                + ", state.getAbandonedLetters():" + state.getAbandonedLetters());
                        competitionViewModel.setLiveDataCompetition(competition);
                    }
                }
            });
        }
    }

    private int dpToPx(int dp){
        return (int)(dp * requireContext().getResources().getDisplayMetrics().density);
    }

    /**
     * Highlights the letter with a color according to the correctness of the users answer
     * @param letter
     * @param right
     */
    private void highlightLetter(TextView letter, boolean right) {
        int color = R.drawable.gap_shape_green;
        if (!right) {
            color = R.drawable.gap_shape_red;
        }
        letter.setBackground(AppCompatResources
                .getDrawable(requireContext(), color));
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                letter.setBackground(AppCompatResources
                        .getDrawable(requireContext(), R.drawable.gap_shape));
            }
        });
    }

    private void setViews(View view) {
        gapsLayout = view.findViewById(R.id.gaps_layout);
        lettersLayout = view.findViewById(R.id.letters_layout);
        examplesText = view.findViewById(R.id.examples_letters_training);
        nextText = view.findViewById(R.id.next_letters_training);
    }
}