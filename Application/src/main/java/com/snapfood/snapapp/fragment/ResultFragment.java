package com.snapfood.snapapp.fragment;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snapfood.snapapp.R;

import java.text.SimpleDateFormat;

import io.confirm.confirmsdk.models.IdBioModel;
import io.confirm.confirmsdk.models.IdClassificationModel;
import io.confirm.confirmsdk.models.IdIssuanceModel;
import io.confirm.confirmsdk.models.IdModel;

public class ResultFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_MODEL = "result_model";

    private IdModel mModel;

    private Button  mCloseButton;

    private TextView mStatusLabel;

    private TextView mFirstNameLabel;
    private TextView mLastNameLabel;
    private TextView mAddressLabel;
    private TextView mCityLabel;
    private TextView mStateLabel;
    private TextView mZipLabel;
    private TextView mDOBLabel;

    private TextView mIssTypeLabel;
    private TextView mIssStateLabel;

    private TextView mNumberLabel;
    private TextView mDateIssuedLabel;
    private TextView mExpirationLabel;

    private TextView mServerLabel;
    private LinearLayout mFailureLayout;
    private TextView mFailureLabel;

    public ResultFragment() {
        // Required empty public constructor
    }

    public static ResultFragment newInstance(IdModel model) {
        ResultFragment fragment = new ResultFragment();
        fragment.mModel = model;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_confirm_result, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {

        mCloseButton = (Button)v.findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().getFragmentManager().popBackStack();
            }
        });
        mStatusLabel = (TextView)v.findViewById(R.id.status_label);
            mStatusLabel.setText(mModel.status);

        IdBioModel bio = mModel.identity.bio;
        IdClassificationModel classification = mModel.identity.classification;
        IdIssuanceModel issuance = mModel.identity.issuance;

        mFirstNameLabel = (TextView)v.findViewById(R.id.first_name);
            mFirstNameLabel.setText(bio.firstName);
        mLastNameLabel = (TextView)v.findViewById(R.id.last_name);
            mLastNameLabel.setText(bio.lastName);
        mAddressLabel = (TextView)v.findViewById(R.id.address);
            mAddressLabel.setText(bio.address);
        mCityLabel = (TextView)v.findViewById(R.id.city);
            mCityLabel.setText(bio.city);
        mStateLabel = (TextView)v.findViewById(R.id.state);
            mStateLabel.setText(bio.state);
        mZipLabel = (TextView)v.findViewById(R.id.zip);
            mZipLabel.setText(bio.zip);

        SimpleDateFormat df =  new SimpleDateFormat("MM/dd/yyyy");

        mDOBLabel = (TextView)v.findViewById(R.id.dob);
        if (bio.dob != null)
            mDOBLabel.setText(df.format(bio.dob));

        mIssTypeLabel = (TextView)v.findViewById(R.id.iss_type);
            mIssTypeLabel.setText(classification.type);
        mIssStateLabel = (TextView)v.findViewById(R.id.iss_state);
            mIssStateLabel.setText(classification.state);

        mNumberLabel = (TextView)v.findViewById(R.id.number);
            mNumberLabel.setText(issuance.number);
        mDateIssuedLabel = (TextView)v.findViewById(R.id.issued);
            if (issuance.issued != null)
                mDateIssuedLabel.setText(df.format(issuance.issued));
            else
                mDateIssuedLabel.setText("N/A");
        mExpirationLabel = (TextView)v.findViewById(R.id.expiration);
            if (issuance.expiration != null)
                mExpirationLabel.setText(df.format(issuance.expiration));
            else
                mExpirationLabel.setText("N/A");

        mServerLabel = (TextView)v.findViewById(R.id.server_header);
        mFailureLayout = (LinearLayout)v.findViewById(R.id.failure_layout);
        mFailureLabel = (TextView)v.findViewById(R.id.failure);

        if (mModel.failureReasons.size() > 0) {
            mFailureLabel.setText(mModel.failureReasons.get(0));
        } else {
            mServerLabel.setVisibility(View.INVISIBLE);
            mFailureLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
