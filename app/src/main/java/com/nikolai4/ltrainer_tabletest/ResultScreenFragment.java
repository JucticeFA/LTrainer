package com.nikolai4.ltrainer_tabletest;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nikolai4.ltrainer_tabletest.model.Word;
import com.nikolai4.ltrainer_tabletest.model.competition.FragmentState;
import com.nikolai4.ltrainer_tabletest.viewmodels.CompetitionViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultScreenFragment extends Fragment {
    private LinearLayoutCompat rightWrongContainer;
    private LinearLayoutCompat rightContainer;
    private LinearLayoutCompat wrongContainer;
    private LinearLayoutCompat rightLabelsContainer;
    private LinearLayoutCompat wrongLabelsContainer;
    private TextView congratulationText;
    private TextView timeLabel;
    private TextView rightWrongLabel;
    private TextView slashLabel;
    private TextView rightsNumberText;
    private TextView wrongsNumberText;
    private TextView rightWordsLabel;
    private TextView wrongWordsLabel;

    private CompetitionViewModel competitionViewModel;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultScreenFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TableResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultScreenFragment newInstance(String param1, String param2) {
        ResultScreenFragment fragment = new ResultScreenFragment();
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
        return inflater.inflate(R.layout.fragment_table_result, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setViews(view);

        competitionViewModel = ViewModelProviders.of(requireActivity()).get(CompetitionViewModel.class);

        competitionViewModel.getLiveDataCompetition().observe(getViewLifecycleOwner(), competition -> {
            FragmentState.ResultState state = competition.getResultState();
            tieDataToViews(state);
            fillRightWrongContainers(state);
        });
    }

    private void tieDataToViews(FragmentState.ResultState state) {
        congratulationText.setText(state.getCongratulation());
        rightsNumberText.setText(String.valueOf(state.getRightsNumber()));
        wrongsNumberText.setText(String.valueOf(state.getWrongsNumber()));
        timeLabel.setText(state.getTime());
    }

    private void fillRightWrongContainers(FragmentState.ResultState state) {
        List<Word> wrongWords = state.getWrongWords();
        List<Word> rightWords = state.getRightWords();
        int wrongLines = state.getWrongsNumber();
        int rightLines = state.getRightsNumber();

        if (wrongLines > 0) {
            wrongLabelsContainer.setVisibility(View.VISIBLE);
            wrongContainer.getViewTreeObserver().addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    String currentRepeat;
                    String nextRepeat;

                    List<LinearLayout> linesList = new ArrayList<>();

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;

                    wrongContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    for (int j = 0; j < wrongLines; j++) {
                        linesList.add(new LinearLayout(requireContext()));
                        linesList.get(j).setLayoutParams(params);
//                        linesList.get(j).setBackground(AppCompatResources.getDrawable(requireContext(), R.color.teal_700));
                        linesList.get(j).setGravity(Gravity.CENTER_HORIZONTAL);
                        linesList.get(j).setOrientation(LinearLayout.HORIZONTAL);
                        wrongContainer.addView(linesList.get(j));
                    }

                    for (int i = 0; i < wrongLines; i++) {
                        TextView wordView = new TextView(requireContext());
                        TextView repeatView = new TextView(requireContext());
                        TextView nextRepeatView = new TextView(requireContext());
                        // add it!
                        TextView mistakesNumber = new TextView(requireContext());

                        currentRepeat = String.valueOf(wrongWords.get(i).getRepeatNumber());
                        nextRepeat = String.valueOf(wrongWords.get(i).getNextRepeat());

                        LinearLayout.LayoutParams wordViewParams = new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
                        wordViewParams.gravity = Gravity.CENTER_HORIZONTAL;

                        LinearLayout.LayoutParams repeatViewParams = new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                        repeatViewParams.gravity = Gravity.CENTER_HORIZONTAL;

                        LinearLayout.LayoutParams nextRepeatViewParams = new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                        nextRepeatViewParams.gravity = Gravity.CENTER_HORIZONTAL;

//                        wordView.setGravity(Gravity.CENTER_HORIZONTAL);
                        wordView.setLayoutParams(wordViewParams);
                        wordView.setPadding(10,0,0,0);
//                        wordView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        String expression = wrongWords.get(i).getExpression();
                        String translate = wrongWords.get(i).getTranslates().get(0);
                        String combined = expression+"\n"+translate;
                        SpannableString spanString = new SpannableString(combined);
                        spanString.setSpan(new RelativeSizeSpan(
                                1.7f), 0, expression.length(), 0); // set size
                        spanString.setSpan(new ForegroundColorSpan
                                (Color.parseColor("#ffff4444")), 0, expression.length(), 0);// set color
                        wordView.setText(spanString);
//                        wordView.setText(wrongWords.get(i).getExpression());
                        wordView.setTextSize(11);
                        wordView.setBackground(AppCompatResources.getDrawable(requireContext(),
                                R.drawable.text_view_result_shape));

                        repeatView.setGravity(Gravity.CENTER);
                        repeatView.setLayoutParams(repeatViewParams);
                        repeatView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        repeatView.setText(currentRepeat);
                        repeatView.setTextSize(18);
                        repeatView.setBackground(AppCompatResources.getDrawable(requireContext(),
                                R.drawable.text_view_result_shape));

                        nextRepeatView.setGravity(Gravity.CENTER);
                        nextRepeatView.setLayoutParams(nextRepeatViewParams);
                        nextRepeatView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        nextRepeatView.setText(nextRepeat);
                        nextRepeatView.setTextSize(18);
                        nextRepeatView.setBackground(AppCompatResources.getDrawable(requireContext(),
                                R.drawable.text_view_result_shape));

                        // places wordViews into a certain line
                        linesList.get(i).addView(wordView);
                        linesList.get(i).addView(repeatView);
                        linesList.get(i).addView(nextRepeatView);
                    }
                }
            });
        }

        if (rightLines > 0) {
            rightLabelsContainer.setVisibility(View.VISIBLE);
            rightContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    String currentRepeat;
                    String nextRepeat;

                    List<LinearLayout> linesList = new ArrayList<>();

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER_HORIZONTAL;

                    rightContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    for (int j = 0; j < rightLines; j++) {
                        linesList.add(new LinearLayout(requireContext()));
                        linesList.get(j).setLayoutParams(params);
//                        linesList.get(j).setBackground(AppCompatResources.getDrawable(requireContext(), R.color.teal_700));
                        linesList.get(j).setGravity(Gravity.CENTER_HORIZONTAL);
                        linesList.get(j).setOrientation(LinearLayout.HORIZONTAL);
                        rightContainer.addView(linesList.get(j));
                    }

                    for (int i = 0; i < rightLines; i++) {
                        TextView wordView = new TextView(requireContext());
                        TextView repeatView = new TextView(requireContext());
                        TextView nextRepeatView = new TextView(requireContext());

                        currentRepeat = String.valueOf(rightWords.get(i).getRepeatNumber());
                        nextRepeat = String.valueOf(rightWords.get(i).getNextRepeat());

                        LinearLayout.LayoutParams wordViewParams = new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.WRAP_CONTENT, 3f);
                        wordViewParams.gravity = Gravity.CENTER_HORIZONTAL;

                        LinearLayout.LayoutParams repeatViewParams = new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                        repeatViewParams.gravity = Gravity.CENTER_HORIZONTAL;

                        LinearLayout.LayoutParams nextRepeatViewParams = new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                        nextRepeatViewParams.gravity = Gravity.CENTER_HORIZONTAL;

//                        wordView.setGravity(Gravity.CENTER_HORIZONTAL);
                        wordView.setLayoutParams(wordViewParams);
                        wordView.setPadding(10,0,0,0);
//                        wordView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        String expression = rightWords.get(i).getExpression();
                        String translate = rightWords.get(i).getTranslates().get(0);
                        String combined = expression+"\n"+translate;
                        SpannableString spanString = new SpannableString(combined);
                        spanString.setSpan(new RelativeSizeSpan(
                                1.7f), 0, expression.length(), 0);
                        spanString.setSpan(new ForegroundColorSpan(
                                Color.parseColor("#ff99cc00")), 0, expression.length(), 0);
                        wordView.setText(spanString);
//                        wordView.setText(rightWords.get(i).getExpression());
                        wordView.setTextSize(11);
                        wordView.setBackground(AppCompatResources.getDrawable(requireContext(),
                                R.drawable.text_view_result_shape));

                        repeatView.setGravity(Gravity.CENTER);
                        repeatView.setLayoutParams(repeatViewParams);
                        repeatView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        repeatView.setText(currentRepeat);
                        repeatView.setTextSize(18);
                        repeatView.setBackground(AppCompatResources.getDrawable(requireContext(),
                                R.drawable.text_view_result_shape));

                        nextRepeatView.setGravity(Gravity.CENTER);
                        nextRepeatView.setLayoutParams(nextRepeatViewParams);
                        nextRepeatView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        nextRepeatView.setText(nextRepeat);
                        nextRepeatView.setTextSize(18);
                        nextRepeatView.setBackground(AppCompatResources.getDrawable(requireContext(),
                                R.drawable.text_view_result_shape));

                        // places wordViews into a certain line
                        linesList.get(i).addView(wordView);
                        linesList.get(i).addView(repeatView);
                        linesList.get(i).addView(nextRepeatView);
                    }
                }
            });
        }
    }

    private void setViews(View view) {
        congratulationText = view.findViewById(R.id.training_congratulations);
        rightWrongContainer = view.findViewById(R.id.right_wrong_number_container);
        rightContainer = view.findViewById(R.id.right_words_container);
        wrongContainer = view.findViewById(R.id.wrong_words_container);
        rightLabelsContainer = view.findViewById(R.id.right_words_labels_container);
        wrongLabelsContainer = view.findViewById(R.id.wrong_words_labels_container);
        rightWrongLabel = view.findViewById(R.id.right_wrong_words_label);
        timeLabel = view.findViewById(R.id.result_time_label);
        slashLabel = view.findViewById(R.id.slash);
        rightWordsLabel = view.findViewById(R.id.right_words_label);
        wrongWordsLabel = view.findViewById(R.id.wrong_words_label);
        rightsNumberText = view.findViewById(R.id.right_words_number);
        wrongsNumberText = view.findViewById(R.id.wrong_words_number);
    }
}