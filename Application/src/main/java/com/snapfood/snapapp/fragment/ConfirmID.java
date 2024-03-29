package com.snapfood.snapapp.fragment;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.snapfood.snapapp.MenuActivity;
import com.snapfood.snapapp.R;

import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmSubmitListener;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.models.IdModel;

/**
 * Created by emezias on 1/14/17.
 * Placeholder for Confirm.IO logic
 */

public class ConfirmID extends Fragment implements ConfirmCaptureListener, ConfirmSubmitListener {
    public static final String TAG = "ConfirmID";
    private AppCompatActivity mActivity = null;

    public static ConfirmID newInstance(String name) {
        //TODO show the current scan status and the name
        Bundle args = new Bundle();
        args.putString(TAG, name);
        ConfirmID fragment = new ConfirmID();
        fragment.setArguments(args);
        return fragment;
    }

    public ConfirmID() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_confirm, container, false);
        mActivity = (MenuActivity) getActivity();
        v.findViewById(R.id.confirm_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mActivity != null)
                ConfirmCapture.getInstance().beginCapture(ConfirmID.this, mActivity);
            }
        });
        return v;
    }

    @Override
    public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
        doSubmit(payload);
    }

    @Override
    public void onConfirmCaptureDidCancel() {
        if (mActivity == null) return;
        Toast.makeText(mActivity, getString(R.string.cancel_scan), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirmSubmitError(String error) {
        Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
        if (mActivity == null) return;
        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
        //setButtonVisibility(true);
        ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
    }

    @Override
    public void onConfirmSubmitSuccess(IdModel model) {
        if (mActivity == null) {
            return;
        }
        if (model.didPass()) {
            // Request completed - document deemed authentic
            showResults(model);
        } else if (model.didFail()) {
            // Request completed - document deemed potentially fraudulent
            showResults(model);
        } else {
            // Request completed, but Confirm was unable to provide an authentication status for
            // the document. This is usually due to image or document damage
            // Failure
            if (mActivity == null) return;
            Toast.makeText(mActivity, "ID Verification could not take place.", Toast.LENGTH_SHORT).show();
        }
        //dismiss();
        ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
    }

    /**
     * Submit payload object to Confirm  API.
     * @param payload
     */
    private void doSubmit(ConfirmPayload payload) {
        String apiKey = getString(R.string.confirm_key); // Please put valid API key in here.
        ConfirmSubmitTask task = new ConfirmSubmitTask(this, payload, apiKey);
        task.execute();

        //setButtonVisibility(false);
        if (mActivity == null) return;
        Toast.makeText(mActivity, "Submitting images please be patient...", Toast.LENGTH_SHORT).show();
    }

    // ------------------- adapted from Confirm sample app part -------------------
    private void showResults(final IdModel model) {
        if (mActivity == null) return;
        FragmentManager fm = mActivity.getFragmentManager();
        ResultFragment fragment =
                (ResultFragment)fm.findFragmentById(R.id.confirm_result_fragment);

        if (fragment == null) {
            try {
                fragment = ResultFragment.newInstance(model);
                fm.beginTransaction()
                        .add(R.id.menu_content_frame, fragment)
                        .addToBackStack("Results")
                        .commit();
            } catch (Exception e) {
                Log.e(TAG, "Exception = " + e.getLocalizedMessage());
            } finally {

            }
        }
    }
}
