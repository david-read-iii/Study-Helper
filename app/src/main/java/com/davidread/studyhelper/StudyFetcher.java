package com.davidread.studyhelper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link StudyFetcher} uses Volley's networking library to get {@link Subject} and {@link Question}
 * object data from zyBook's servers.
 */
public class StudyFetcher {

    /**
     * {@link OnStudyDataReceivedListener} is an interface that defines methods that
     * {@link StudyFetcher} invokes when it receives {@link Subject} objects, receives
     * {@link Question} objects, or experiences a network error.
     */
    public interface OnStudyDataReceivedListener {
        void onSubjectsReceived(List<Subject> subjectList);

        void onQuestionsReceived(Subject subject, List<Question> questionList);

        void onErrorResponse(VolleyError error);
    }

    /**
     * {@link String} base URL used to contact zyBook's servers.
     */
    private final String WEBAPI_BASE_URL = "https://wp.zybooks.com/study-helper.php";

    /**
     * {@link String} identifier for log messages in this class.
     */
    private final String TAG = "StudyFetcher";

    /**
     * {@link RequestQueue} for enqueuing network requests to zyBook's servers.
     */
    private final RequestQueue mRequestQueue;

    /**
     * Constructs a new {@link StudyFetcher}.
     *
     * @param context {@link Context} for setting up {@link #mRequestQueue}.
     */
    public StudyFetcher(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Asynchronously fetches {@link Subject} objects from zyBook's servers and invokes methods in
     * the passed listener given the fetch result.
     *
     * @param listener {@link OnStudyDataReceivedListener} specifying what to do when the fetch
     *                 is complete.
     */
    public void fetchSubjects(final OnStudyDataReceivedListener listener) {

        String url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
                .appendQueryParameter("type", "subjects").build().toString();

        // Request all subjects.
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> listener.onSubjectsReceived(jsonToSubjects(response)),
                error -> listener.onErrorResponse(error)
        );

        mRequestQueue.add(request);
    }

    /**
     * Converts a {@link JSONObject} into a {@link List} of {@link Subject} objects.
     *
     * @param json {@link JSONObject} from zyBook's servers.
     * @return A {@link List} of {@link Subject} objects.
     */
    private List<Subject> jsonToSubjects(JSONObject json) {

        // Create a list of subjects.
        List<Subject> subjectList = new ArrayList<>();

        try {
            JSONArray subjectArray = json.getJSONArray("subjects");

            for (int i = 0; i < subjectArray.length(); i++) {
                JSONObject subjectObj = subjectArray.getJSONObject(i);

                Subject subject = new Subject(subjectObj.getString("subject"));
                subject.setUpdateTime(subjectObj.getLong("updatetime"));
                subjectList.add(subject);
            }
        } catch (Exception e) {
            Log.e(TAG, "Field missing in the JSON data: " + e.getMessage());
        }

        return subjectList;
    }

    /**
     * Asynchronously fetches {@link Question} objects for a {@link Subject} from zyBook's servers
     * and invokes methods in the passed listener given the fetch result.
     *
     * @param subject  {@link Subject} for which to fetch {@link Question} objects for.
     * @param listener {@link OnStudyDataReceivedListener} specifying what to do when the fetch
     *                 is complete.
     */
    public void fetchQuestions(final Subject subject, final OnStudyDataReceivedListener listener) {

        String url = Uri.parse(WEBAPI_BASE_URL).buildUpon()
                .appendQueryParameter("type", "questions")
                .appendQueryParameter("subject", subject.getText())
                .build().toString();

        // Request questions for this subject.
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> listener.onQuestionsReceived(subject, jsonToQuestions(response)),
                error -> listener.onErrorResponse(error));

        mRequestQueue.add(jsObjRequest);
    }

    /**
     * Converts a {@link JSONObject} into a {@link List} of {@link Question} objects.
     *
     * @param json {@link JSONObject} from zyBook's servers.
     * @return A {@link List} of {@link Question} objects.
     */
    private List<Question> jsonToQuestions(JSONObject json) {

        // Create a list of questions.
        List<Question> questionList = new ArrayList<>();

        try {
            JSONArray questionArray = json.getJSONArray("questions");

            for (int i = 0; i < questionArray.length(); i++) {
                JSONObject questionObj = questionArray.getJSONObject(i);

                Question question = new Question();
                question.setText(questionObj.getString("question"));
                question.setAnswer(questionObj.getString("answer"));
                question.setSubjectId(0);
                questionList.add(question);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Field missing in the JSON data: " + e.getMessage());
        }

        return questionList;
    }
}