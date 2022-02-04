package com.davidread.studyhelper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * {@link SubjectDialogFragment} represents a user interface for a subject creation dialog.
 */
public class SubjectDialogFragment extends DialogFragment {

    /**
     * {@link OnSubjectEnteredListener} is an interface that defines the
     * {@link #onSubjectEntered(String)} callback method that should be invoked when a new subject
     * is created in this {@link SubjectDialogFragment}.
     */
    public interface OnSubjectEnteredListener {
        void onSubjectEntered(String subject);
    }

    /**
     * A reference to the activity that shows this {@link OnSubjectEnteredListener}.
     */
    private OnSubjectEnteredListener mListener;

    /**
     * Callback method invoked when this fragment is first attached to its context. It simply
     * initializes the member variables of this class.
     *
     * @param context {@link Context} where the fragment is being attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnSubjectEnteredListener) context;
    }

    /**
     * Callback method invoked to build a custom {@link Dialog} container. It builds an
     * {@link AlertDialog} that allows the user to enter text for a new subject.
     *
     * @return An {@link AlertDialog} instance to be displayed by the fragment.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText subjectEditText = new EditText(requireActivity());
        subjectEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        subjectEditText.setMaxLines(1);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.subject)
                .setView(subjectEditText)
                .setPositiveButton(R.string.create, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Notify listener
                        String subject = subjectEditText.getText().toString();
                        mListener.onSubjectEntered(subject.trim());
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }

    /**
     * Callback method invoked when this fragment is removed from its context. It simply
     * sets {@link #mListener} to null.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
