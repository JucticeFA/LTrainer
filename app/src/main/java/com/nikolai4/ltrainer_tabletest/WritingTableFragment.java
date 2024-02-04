package com.nikolai4.ltrainer_tabletest;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.model.competition.Competition;
import com.nikolai4.ltrainer_tabletest.model.competition.FragmentState;
import com.nikolai4.ltrainer_tabletest.model.competition.FullWritingTable;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModel;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModelFactory;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WritingTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WritingTableFragment extends Fragment {
    private EditText fullAnswerEdit;
    private TextView usersAnswerText;
    private Button checkAnswerButton;
    private TextView rightAnswerText;
    private TextView examplesText;
    private TextView nextText;

    private CompetitionViewModel competitionViewModel;
    private WordViewModel wordViewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WritingTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WritingTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WritingTableFragment newInstance(String param1, String param2) {
        WritingTableFragment fragment = new WritingTableFragment();
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
        return inflater.inflate(R.layout.fragment_writing_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViews(view);

        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
//        competitionViewModel = ViewModelProviders.of(requireActivity()).get(CompetitionViewModel.class);
        CompetitionViewModelFactory factory = new CompetitionViewModelFactory(requireActivity().getApplication(), 5);
        competitionViewModel = ViewModelProviders.of(requireActivity(), factory).get(CompetitionViewModel.class);
        Log.d("countofcreates", "2) writingTableFragment: ");

        competitionViewModel.getLiveDataCompetition().observe(getViewLifecycleOwner(), competition -> {
            if (competition.getCurrentTable() instanceof FullWritingTable) {
                FullWritingTable table = competition.getFullWritingTable();
                tieDataToViews(table);
                setNextClickListener(nextText, competition);
                setCheckClickListener(checkAnswerButton, competition);
            } else {
                fullAnswerEdit.setText("");
            }
        });
    }

    public void setNextClickListener(TextView nextText, Competition competition) {
        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FullWritingTable table = competition.getFullWritingTable();
                FragmentState.WritingState state = table.getFragmentState();

                if (!state.isAnswered()) {
                    table.skipQuestion();
                    setNextTextLabel(state.isAnswered());

//                    competition.addMistake(currentWord, table);
                    Log.d("getMistakes", "mistakes after rotation: " +
                            competitionViewModel.getLiveDataCompetition().getValue().getMistakes().toString());
                } else {
                    competition.checkAnswer(competition.getCurrentWord(), table);
                    competition.nextQuestion();

                    if (competition.getCurrentTable() instanceof FullWritingTable) {
                        competition.setTableChanged(false);
                        fullAnswerEdit.setText("");
                    } else {
                        competition.setTableChanged(true);
                    }
                }
                competitionViewModel.setLiveDataCompetition(competition);
            }
        });
    }

    private void setCheckClickListener(Button checkAnswerButton, Competition competition) {
        checkAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMistaken = false;
                rightAnswerText.setVisibility(View.VISIBLE);
                Word currentWord = competition.getCurrentWord();
                FullWritingTable fullWritingTable = competition.getFullWritingTable();
                FragmentState.WritingState state = fullWritingTable.getFragmentState();
                String rightAnswer = fullWritingTable.getAnswer();
                String usersAnswer = fullAnswerEdit.getText().toString().toLowerCase().trim();
                String answerWithMarks = getUsersAnswerWithMarks(rightAnswer, usersAnswer);

                if (!rightAnswer.equalsIgnoreCase(usersAnswer)) {
                    competition.addMistake(currentWord, competition.getFullWritingTable());
                } else {
                    try {
                        competition.mistakeByWord(currentWord).fix();
                    } catch (Competition.NoExistingMistakeException e) {
                        e.printStackTrace();
                    }
                }

                // is it should be placed there?
                if (competition.mistakeExists(currentWord)) {
                    isMistaken = true;
                }
                currentWord.increaseMistakesFrequency(isMistaken);
                currentWord.adjustRepeat(isMistaken);
                currentWord.compileStatistics();
                wordViewModel.updateWord(currentWord);
                //---

                state.setUsersAnswer(answerWithMarks);
                state.setAnswered(true);
                competition.getFullWritingTable().setFragmentState(state);
                competitionViewModel.setLiveDataCompetition(competition);
            }
        });
    }

    private void setViews(View view) {
        fullAnswerEdit = view.findViewById(R.id.full_answer_edit_training);
        usersAnswerText = view.findViewById(R.id.users_answer_training);
        checkAnswerButton = view.findViewById(R.id.check_the_answer_training);
        rightAnswerText = view.findViewById(R.id.right_answer_training);
        examplesText = view.findViewById(R.id.examples_writing_training);
        nextText = view.findViewById(R.id.next_writing_training);
    }

    private void tieDataToViews(FullWritingTable table) {
        String rightAnswer = table.getAnswer();
        String answerWithMarks = table.getFragmentState().getUsersAnswer();
        FragmentState.WritingState state = table.getFragmentState();

        Log.d("answerWithMarks", "answerWithMarks: " + answerWithMarks);

        if (answerWithMarks.equals("")) {
            Log.d("tieDataToViews", "1) tieDataToViews: ");
            fullAnswerEdit.setEnabled(true);
            checkAnswerButton.setEnabled(true);
            rightAnswerText.setText(answerWithMarks);
        } else {
            Log.d("tieDataToViews", "2) tieDataToViews: ");
            fullAnswerEdit.setText(Html.fromHtml(answerWithMarks));
            fullAnswerEdit.setEnabled(false);
            checkAnswerButton.setEnabled(false);
            rightAnswerText.setText(rightAnswer);
        }
        setNextTextLabel(state.isAnswered());
    }

    private void setNextTextLabel(boolean isAnswered) {
        if (isAnswered) {
            nextText.setText(R.string.next);
        } else {
            nextText.setText(R.string.show_the_answer);
        }
    }

    private String getUsersAnswerWithMarks(String rightAnswer, String usersAnswer) {
        String rightAnswerLower = rightAnswer.toLowerCase();
        StringBuilder usersAnswerMarked = new StringBuilder();
        char usersLetter;
        char rightLetter;

        for (int i = 0; i < rightAnswerLower.length(); i++) {
            rightLetter = rightAnswerLower.charAt(i);
            if (i < usersAnswer.length()) {
                usersLetter = usersAnswer.charAt(i);
            } else {
                usersLetter = '*';
            }

            String paintedLetter;
            if (rightLetter == usersLetter) {
                paintedLetter = "<font color=" + Color.parseColor("#ff99cc00") + ">" + usersLetter + "</font>";
            } else {
                paintedLetter = "<font color=" + Color.parseColor("#ffff4444") + ">" + usersLetter + "</font>";
            }
            usersAnswerMarked.append(paintedLetter);
        }

        if (usersAnswer.length() > rightAnswerLower.length()) {
            int difference = usersAnswer.length() - rightAnswerLower.length();
            int start = usersAnswer.length() - difference;
            for (int i = start; i < usersAnswer.length(); i++) {
                String l = "<font color=" + Color.parseColor("#ffff4444") + ">" + usersAnswer.charAt(i) + "</font>";
                usersAnswerMarked.append(l);
            }
        }
        return usersAnswerMarked.toString();
    }
}