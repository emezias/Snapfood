package com.snapfood.snapapp.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snapfood.snapapp.OnboardActivity;
import com.snapfood.snapapp.R;

/**
 * Created by emezias on 1/14/17.
 * Placeholder for Confirm.IO logic
 */

public class ConfirmID extends DialogFragment {
    public static final String TAG = "ConfirmID";

    public ConfirmID() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_confirm, container);

        v.findViewById(R.id.confirm_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OnboardActivity)getActivity()).saveCardData();
                dismiss();
            }
        });
        v.setMinimumWidth(getResources().getDisplayMetrics().widthPixels-150);
        return v;
    }

}
