package com.example.lovesticker.sticker.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lovesticker.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EmojiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EmojiFragment extends Fragment {


    public EmojiFragment() {
        // Required empty public constructor
    }


    public static EmojiFragment newInstance(String param1, String param2) {
        EmojiFragment fragment = new EmojiFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emoji, container, false);
    }
}