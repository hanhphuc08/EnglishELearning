package com.example.englishelearning;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.englishelearning.vocabulary.VocabularyLevelActivity;

public class HomeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);

    }
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set click listener for Listening card
        view.findViewById(R.id.listeningCard).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ListeningActivity.class);
            startActivity(intent);
        });
        view.findViewById(R.id.vocabularyCard).setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), VocabularyLevelActivity.class);
            startActivity(intent);
        });
    }
} 