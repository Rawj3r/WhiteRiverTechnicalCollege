package com.digital.wiggle.rn.whiterivertechnicalcollege;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DiscusionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discusions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DiscusionsActivity.this);
            }
        });
    }

    public String getUserID(){
        SharedPreferences sharedPreferences = getSharedPreferences("wiggle_whiterivertechnical_user_id", Context.MODE_PRIVATE);
        String userID = sharedPreferences.getString("memID", "");
        return userID;
    }

    private void showDialog(Context context){
        // custom dialog
        final Dialog dialog = new Dialog(context);
//        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        dialog.setContentView(R.layout.make_post_layout);
//        dialog.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

        final AutoCompleteTextView subject = (AutoCompleteTextView)dialog.findViewById(R.id.edit_subject_post);
        final AutoCompleteTextView content = (AutoCompleteTextView)dialog.findViewById(R.id.edit_body_post);
        Button submit = (Button)dialog.findViewById(R.id.make_post);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject1 = subject.getText().toString();
                String content1 = content.getText().toString();
                new MakePost().execute(subject1, content1);
                Intent intent = new Intent(DiscusionsActivity.this, DiscusionsActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.show();
    }

    private class MakePost extends AsyncTask<String, String, JSONObject> {

        private JSONParser jsonParser = new JSONParser();
        private ProgressDialog progressDialog;



        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(DiscusionsActivity.this);
            progressDialog.setMessage("Requesting...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param jsonObject The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            if (progressDialog != null){
                progressDialog.dismiss();
            }

            try {
                if (jsonObject != null){
                    String message = jsonObject.getString("message");
                    Toast.makeText(DiscusionsActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected JSONObject doInBackground(String... params) {

            try{
                HashMap<String, String> map = new HashMap<>();
                map.put("method", "post");
                map.put("userID", getUserID());
                map.put("post", params[1]);
                map.put("heading", params[0]);

                JSONObject jsonObject = jsonParser.makeHttpRequest(Constants.URL, "POST", map);

                if (jsonObject != null){
                    Log.e("JSON result", jsonObject.toString());
                    return jsonObject;
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
}
