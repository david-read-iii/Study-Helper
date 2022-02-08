package com.davidread.studyhelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * {@link SubjectActivity} provides a user interface for viewing {@link Subject} objects provided
 * by {@link #mStudyDb}.
 */
public class SubjectActivity extends AppCompatActivity
        implements SubjectDialogFragment.OnSubjectEnteredListener {

    /**
     * {@link StudyDatabase} to get and put {@link Subject} objects.
     */
    private StudyDatabase mStudyDb;

    /**
     * {@link SubjectAdapter} for adapting {@link Subject} objects to be shown in a
     * {@link #mRecyclerView}.
     */
    private SubjectAdapter mSubjectAdapter;

    /**
     * {@link RecyclerView} for showing {@link Subject} objects.
     */
    private RecyclerView mRecyclerView;

    /**
     * Int array containing color values to apply to item views of {@link #mRecyclerView}.
     */
    private int[] mSubjectColors;

    /**
     * {@link Subject} currently selected when an item view of {@link #mRecyclerView} is long
     * clicked.
     */
    private Subject mSelectedSubject;

    /**
     * Int adapter position of {@link #mSelectedSubject} set when an item view of
     * {@link #mRecyclerView} is long clicked.
     */
    private int mSelectedSubjectPosition = RecyclerView.NO_POSITION;

    /**
     * {@link ActionMode} representing the contextual app bar user interface started when an item
     * view of {@link #mRecyclerView} is long clicked. Is initialized when the contextual app bar
     * is visible on screen.
     */
    private ActionMode mActionMode = null;

    /**
     * Reference to this app's {@link SharedPreferences}. General settings about the app can be
     * retrieved from here.
     */
    private SharedPreferences mSharedPrefs;

    /**
     * Callback method invoked when this activity is created. It initializes member variables and
     * partially sets up {@link #mRecyclerView}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        mStudyDb = StudyDatabase.getInstance(getApplicationContext());
        mSubjectColors = getResources().getIntArray(R.array.subjectColors);

        mRecyclerView = findViewById(R.id.subject_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        // Set the light/dark theme based on value in SharedPreferences.
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean darkTheme = mSharedPrefs.getBoolean("dark_theme", false);
        if (darkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    /**
     * Callback method invoked when this activity comes to the foreground. It initializes
     * {@link #mRecyclerView} with a new {@link SubjectAdapter}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        // Load subjects here in case settings changed.
        mSubjectAdapter = new SubjectAdapter(loadSubjects());
        mRecyclerView.setAdapter(mSubjectAdapter);
    }

    /**
     * Callback method invoked when this activity's app bar is created. It inflates the app bar's
     * menu layout with a "Settings" action button.
     *
     * @param menu {@link Menu} in which you place your items.
     * @return True for the menu to be displayed. False otherwise.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subject_menu, menu);
        return true;
    }

    /**
     * Callback method invoked when an action button in the app bar is clicked.
     *
     * @param item {@link MenuItem} invoking this hook.
     * @return True if the click is handled here.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // If "Import" is clicked, start the ImportActivity.
        if (item.getItemId() == R.id.import_questions) {
            Intent intent = new Intent(this, ImportActivity.class);
            startActivity(intent);
            return true;
        }

        // If "Settings" is clicked, start the SettingsActivity.
        else if (item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback method invoked by {@link SubjectDialogFragment} when the user wants to add a new
     * {@link Subject} to {@link #mStudyDb}.
     *
     * @param subjectText {@link String} containing the text of the new {@link Subject} to be added.
     */
    @Override
    public void onSubjectEntered(String subjectText) {
        if (subjectText.length() > 0) {

            // Create new Subject.
            Subject subject = new Subject(subjectText);

            // Add new Subject to database.
            long subjectId = mStudyDb.subjectDao().insertSubject(subject);

            // Assign id to new Subject.
            subject.setId(subjectId);

            // Add new Subject with id to RecyclerView.
            mSubjectAdapter.addSubject(subject);
        }
    }

    /**
     * Invoked when the "Add Subject"
     * {@link com.google.android.material.floatingactionbutton.FloatingActionButton} is clicked. It
     * shows a new {@link SubjectDialogFragment}.
     */
    public void addSubjectClick(View view) {
        FragmentManager manager = getSupportFragmentManager();
        SubjectDialogFragment dialog = new SubjectDialogFragment();
        dialog.show(manager, "subjectDialog");
    }

    /**
     * Returns the {@link List} of {@link Subject} objects stored in {@link #mStudyDb}. Sort order
     * is determined by this app's {@link SharedPreferences}.
     */
    private List<Subject> loadSubjects() {
        String order = mSharedPrefs.getString("subject_order", "alpha");
        if (order.equals("alpha")) {
            return mStudyDb.subjectDao().getSubjects();
        } else if (order.equals("new_first")) {
            return mStudyDb.subjectDao().getSubjectsNewerFirst();
        } else {
            return mStudyDb.subjectDao().getSubjectsOlderFirst();
        }
    }

    /**
     * {@link SubjectHolder} is a model class that describes a single band item view and metadata
     * about its place within a {@link RecyclerView}.
     */
    private class SubjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        /**
         * {@link Subject} associated with this {@link SubjectHolder}.
         */
        private Subject mSubject;

        /**
         * {@link TextView} to display the name of {@link #mSubject}.
         */
        private TextView mTextView;

        /**
         * Constructs a new {@link SubjectHolder}.
         *
         * @param inflater For inflating layouts.
         * @param parent   Parent {@link ViewGroup} of the {@link RecyclerView}.
         */
        public SubjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mTextView = itemView.findViewById(R.id.subject_text_view);
        }

        /**
         * Binds a new {@link Subject} to this {@link SubjectHolder}.
         *
         * @param subject  {@link Subject} to bind to this {@link SubjectHolder}.
         * @param position Int position of this {@link SubjectHolder} in the {@link RecyclerView}.
         */
        public void bind(Subject subject, int position) {
            mSubject = subject;
            mTextView.setText(subject.getText());

            if (mSelectedSubjectPosition == position) {
                // Make selected subject stand out.
                mTextView.setBackgroundColor(Color.RED);
            } else {
                // Make the background color dependent on the length of the subject string.
                int colorIndex = subject.getText().length() % mSubjectColors.length;
                mTextView.setBackgroundColor(mSubjectColors[colorIndex]);
            }
        }

        /**
         * Invoked when the {@link View} held by this {@link SubjectHolder} is clicked. It starts
         * the {@link QuestionActivity} while passing {@link #mSubject} as an argument.
         */
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SubjectActivity.this, QuestionActivity.class);
            intent.putExtra(QuestionActivity.EXTRA_SUBJECT_ID, mSubject.getId());
            startActivity(intent);
        }

        /**
         * Invoked when the {@link View} held by this {@link SubjectHolder} is long clicked. It
         * displays a contextual app bar in {@link SubjectActivity} for the {@link View} that
         * invoked this callback.
         *
         * @return True if this callback consumed the long click. False otherwise.
         */
        @Override
        public boolean onLongClick(View view) {

            // Do not display contextual app bar if already visible.
            if (mActionMode != null) {
                return false;
            }

            mSelectedSubject = mSubject;
            mSelectedSubjectPosition = getAdapterPosition();

            // Re-bind the selected item.
            mSubjectAdapter.notifyItemChanged(mSelectedSubjectPosition);

            // Show the contextual app bar.
            mActionMode = SubjectActivity.this.startActionMode(mActionModeCallback);

            return true;
        }

        /**
         * {@link ActionMode.Callback} that specifies callbacks for the contextual app bar displayed
         * when an item view of {@link #mRecyclerView} is long clicked.
         */
        private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

            /**
             * Callback invoked when action mode if first created. It specifies the {@link Menu} to
             * be shown in the contextual app bar.
             *
             * @param mode  {@link ActionMode} being created.
             * @param menu  {@link Menu} used to populate action buttons.
             * @return True if the action mode should be created. False if entering this mode
             * should be aborted.
             */
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Provide context menu for CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;
            }

            /**
             * Callback invoked to refresh an action mode's action menu when it is invalidated. It
             * does nothing.
             *
             * @param mode  {@link ActionMode} being refreshed.
             * @param menu  {@link Menu} used to populate action buttons.
             * @return True if anything was updated. False otherwise.
             */
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            /**
             * Callback invoked when an action button is clicked.
             *
             * @param mode The current action mode.
             * @param item The item that was clicked.
             * @return True if this callback handles the event. False otherwise.
             */
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.delete) {
                    // Delete from the database and remove from the RecyclerView.
                    mStudyDb.subjectDao().deleteSubject(mSelectedSubject);
                    mSubjectAdapter.removeSubject(mSelectedSubject);

                    // Close the contextual app bar.
                    mode.finish();
                    return true;
                }
                return false;
            }

            /**
             * Callback invoked when an action mode is destroyed.
             * @param mode The action mode being destroyed.
             */
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;

                // Deselect item if not deleted.
                mSubjectAdapter.notifyItemChanged(mSelectedSubjectPosition);
                mSelectedSubjectPosition = RecyclerView.NO_POSITION;
            }
        };
    }

    /**
     * {@link SubjectAdapter} provides a binding from a {@link List} of {@link Subject} objects to a
     * {@link RecyclerView}.
     */
    private class SubjectAdapter extends RecyclerView.Adapter<SubjectHolder> {

        /**
         * {@link List} of {@link Subject} objects to adapt.
         */
        private List<Subject> mSubjectList;

        /**
         * Constructs a new {@link SubjectAdapter}.
         *
         * @param subjects {@link List} of {@link Subject} objects to adapt.
         */
        public SubjectAdapter(List<Subject> subjects) {
            mSubjectList = subjects;
        }

        /**
         * Callback method invoked when {@link RecyclerView} needs a new empty {@link SubjectHolder}
         * to represent a {@link Subject}.
         *
         * @param parent   {@link ViewGroup} into which the new {@link View} will be added after it
         *                 is bound to an adapter position.
         * @param viewType The view type of the new {@link View}.
         * @return A new {@link SubjectHolder}.
         */
        @NonNull
        @Override
        public SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new SubjectHolder(layoutInflater, parent);
        }

        /**
         * Callback method invoked when {@link RecyclerView} needs to bind data to a
         * {@link SubjectHolder} at a certain position index.
         *
         * @param holder   {@link SubjectHolder} to be bound.
         * @param position The {@link SubjectHolder} object's position index in the adapter.
         */
        @Override
        public void onBindViewHolder(SubjectHolder holder, int position) {
            holder.bind(mSubjectList.get(position), position);
        }

        /**
         * Returns the total number of items this adapter is adapting.
         *
         * @return The total number of items this adapter is adapting.
         */
        @Override
        public int getItemCount() {
            return mSubjectList.size();
        }

        /**
         * Adds a new {@link Subject} to {@link #mSubjectList} and animates its insertion in
         * {@link #mRecyclerView}.
         *
         * @param subject {@link Subject} to be inserted.
         */
        public void addSubject(Subject subject) {
            mSubjectList.add(0, subject);
            notifyItemInserted(0);
            mRecyclerView.scrollToPosition(0);
        }

        /**
         * Removes a {@link Subject} from {@link #mSubjectList} and animates its deletion from
         * {@link #mRecyclerView}.
         *
         * @param subject {@link Subject} to be deleted.
         */
        public void removeSubject(Subject subject) {
            int index = mSubjectList.indexOf(subject);
            if (index >= 0) {
                mSubjectList.remove(index);
                notifyItemRemoved(index);
            }
        }
    }
}