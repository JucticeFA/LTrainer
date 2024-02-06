package com.nikolai4.ltrainer_tabletest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.model.competition.Competition;
import com.nikolai4.ltrainer_tabletest.model.competition.EnRuTable;
import com.nikolai4.ltrainer_tabletest.model.competition.FragmentState;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModel;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModelFactory;
import com.nikolai4.ltrainer_tabletest.viewmodels.WordViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnRuTableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnRuTableFragment extends Fragment {
    private TextView answerText1;
    private TextView answerText2;
    private TextView answerText3;
    private TextView answerText4;
    private TextView examplesText;
    private TextView nextText;

    private CompetitionViewModel competitionViewModel;
    private WordViewModel wordViewModel;
    private final List<TextView> answerButtons = new ArrayList<>(4);

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnRuTableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnRuTableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnRuTableFragment newInstance(String param1, String param2) {
        EnRuTableFragment fragment = new EnRuTableFragment();
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
        return inflater.inflate(R.layout.fragment_en_ru_table, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViews(view);

        setAnswerButtons();

        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
//        competitionViewModel = ViewModelProviders.of(requireActivity()).get(CompetitionViewModel.class);
        CompetitionViewModelFactory factory = new CompetitionViewModelFactory(requireActivity().getApplication(), 5);
        competitionViewModel = ViewModelProviders.of(requireActivity(), factory).get(CompetitionViewModel.class);

        competitionViewModel.getLiveDataCompetition().observe(getViewLifecycleOwner(), c -> {
            Log.d("countofcreates", "2) EnRuTableFragment: ");
            if (c.getCurrentTable() instanceof EnRuTable) {
                EnRuTable table = c.getEnRuTable();
                Log.d("answersforquestion", "1) table.getAnswers(): " + table.getAnswers());
                if (table.getAnswers().isEmpty()) {
                    table.inventAnswersForQuestion(wordViewModel);
                    Log.d("answersforquestion", "2) table.getAnswers(): " + table.getAnswers());
                }
                tieDataToViews(table);
                setAnswerClickListener(answerButtons, c);
                setNextClickListener(nextText, c);
            }
        });
    }

    public void setNextClickListener(TextView nextText, Competition competition) {
        // 17.01.24
        nextText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnRuTable table = competition.getEnRuTable();
                FragmentState.EnRuState state = table.getFragmentState();

                if (!state.isAnswered()) {
                    Word currentWord = competition.getCurrentWord();
                    String rightAnswer = table.getRightAnswer();

                    table.convertDataToState(rightAnswer);
                    competition.addMistake(currentWord, table);
                } else {
                    competition.checkAnswer(competition.getCurrentWord(), table);
                    competition.nextQuestion();

                    if (competition.getCurrentTable() instanceof EnRuTable) {
                        competition.setTableChanged(false);
                    } else {
                        competition.setTableChanged(true);
                    }
                }
                competitionViewModel.setLiveDataCompetition(competition);
            }
        });
    }

    private void setAnswerClickListener(List<TextView> buttons, Competition competition) {
        for (int i = 0; i < buttons.size(); i++) {
            TextView currentButton = buttons.get(i);
            currentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EnRuTable table = competition.getEnRuTable();
                    String rightAnswer = table.getRightAnswer();
                    String usersAnswer;
                    FragmentState.EnRuState state = table.getFragmentState();
                    Word currentWord;
                    boolean isAnswered = state.isAnswered();

                    if (!isAnswered) {
                        usersAnswer = currentButton.getText().toString();
                        currentWord = competition.getCurrentWord();

                        table.convertDataToState(usersAnswer);

                        for (int j = 0; j < buttons.size(); j++) {
                            buttons.get(j).setBackgroundResource(state.getButtonColors().get(j));
                        }
                        if (!usersAnswer.equalsIgnoreCase(rightAnswer)) {
                            competition.addMistake(currentWord, table);
                        }
                        else {
                            try {
                                competition.mistakeByWord(currentWord).fix();
                            } catch (Competition.NoExistingMistakeException e) {
                                e.printStackTrace();
                            }
                        }
                        setNextTextLabel(state.isAnswered());
                    }
                }
            });
        }
    }

    private void tieDataToViews(EnRuTable table) {
        FragmentState.EnRuState state = table.getFragmentState();
        List<Integer> colors = state.getButtonColors();
        for (int i = 0; i < answerButtons.size(); i++) {
            String answer = table.getAnswers().get(i);
            TextView currentButton = answerButtons.get(i);
            currentButton.setText(answer);
            currentButton.setBackgroundResource(colors.get(i));
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

    private void setViews(View view) {
        answerText1 = view.findViewById(R.id.answer_1_enru_training);
        answerText2 = view.findViewById(R.id.answer_2_enru_training);
        answerText3 = view.findViewById(R.id.answer_3_enru_training);
        answerText4 = view.findViewById(R.id.answer_4_enru_training);
        examplesText = view.findViewById(R.id.examples_enru_training);
        nextText = view.findViewById(R.id.next_enru_training);
    }

    private void setAnswerButtons() {
        answerButtons.clear();
        answerButtons.add(answerText1);
        answerButtons.add(answerText2);
        answerButtons.add(answerText3);
        answerButtons.add(answerText4);
    }
}