package com.snapfood.snapapp;

import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.snapfood.snapapp.fragment.ResultFragment;

import io.confirm.confirmsdk.ConfirmCapture;
import io.confirm.confirmsdk.ConfirmCaptureListener;
import io.confirm.confirmsdk.ConfirmPayload;
import io.confirm.confirmsdk.ConfirmSubmitListener;
import io.confirm.confirmsdk.ConfirmSubmitTask;
import io.confirm.confirmsdk.models.IdModel;


/**
 * A simple launcher activity offering access to the individual samples in this project.
 */
public class CheckIDActivity extends MenuActivity implements ConfirmCaptureListener, ConfirmSubmitListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_id);
        setMenu();
        findViewById(R.id.confirm_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmCapture.getInstance().beginCapture(CheckIDActivity.this, CheckIDActivity.this);
            }
        });
    }

    @Override
    public void onConfirmCaptureDidComplete(ConfirmPayload payload) {
        doSubmit(payload);
    }

    @Override
    public void onConfirmCaptureDidCancel() {
        Toast.makeText(this, getString(R.string.cancel_scan), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConfirmSubmitError(String error) {
        Log.e(TAG, "onConfirmSubmitError = (" + error + ")");
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        //setButtonVisibility(true);
        ConfirmCapture.getInstance().cleanup(); // Purge details of the capture
    }

    @Override
    public void onConfirmSubmitSuccess(IdModel model) {
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
            Toast.makeText(this, "ID Verification could not take place.", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, "Submitting images please be patient...", Toast.LENGTH_SHORT).show();
    }

    // ------------------- adapted from Confirm sample app part -------------------
    private void showResults(final IdModel model) {
        FragmentManager fm = getFragmentManager();
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
