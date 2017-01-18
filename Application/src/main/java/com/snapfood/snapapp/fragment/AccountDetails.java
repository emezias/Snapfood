package com.snapfood.snapapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.snapfood.snapapp.MenuActivity;
import com.snapfood.snapapp.R;

/**
 * Created by emezias on 1/14/17.
 * Placeholder for Confirm.IO logic
 */

public class AccountDetails extends Fragment {
    public static final String TAG = "AccountDetails";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MenuActivity) getActivity()).getSupportActionBar().setTitle(R.string.acct_title);
        ArrayAdapter myAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.states));
        final AppCompatAutoCompleteTextView tv = (AppCompatAutoCompleteTextView) view.findViewById(R.id.acct_state_picker);
        tv.setAdapter(myAdapter);

        view.findViewById(R.id.acct_save).setOnClickListener(saveButton);
    }

    final View.OnClickListener saveButton = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getContext(), "Save Login is a work in progress", Toast.LENGTH_SHORT).show();
        }
    };

}
