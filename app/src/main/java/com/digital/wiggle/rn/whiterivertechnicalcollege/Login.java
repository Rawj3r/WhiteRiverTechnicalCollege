package com.digital.wiggle.rn.whiterivertechnicalcollege;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A login screen that offers login via email/password.
 */
public class Login extends AppCompatActivity {

    /**
    */

    // UI references.
    private static AutoCompleteTextView mEmailView;
    private static EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
               new Aunth().execute();
            }
        });

    }




    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    class Aunth extends AsyncTask<String, String, JSONObject> {

        String username = Login.mEmailView.getText().toString();
        String password = Login.mPasswordView.getText().toString();

        JSONParser jsonParser = new JSONParser();
        ProgressDialog progressDialog;

        /**
         * Runs on the UI thread before {@link #doInBackground}.
         *
         * @see #onPostExecute
         * @see #doInBackground
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setMessage("Authenticating...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
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

            if (progressDialog != null){
                progressDialog.dismiss();
            }


            try {

                if (jsonObject != null){
                    String status = jsonObject.getString("logged");
                    String memID = jsonObject.getString("memID");

                    if (status.equals("1")){
                        Toast.makeText(Login.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        SharedPreferences sp = getSharedPreferences("wiggle_whiterivertechnical_user_id", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("memID", memID);
                        editor.commit();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Toast.makeText(getApplicationContext(), "Failed, please check your login credintials", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(Login.this, "Login Error", Toast.LENGTH_SHORT).show();
                }



            } catch (JSONException e) {
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

            try {
                HashMap<String, String> map = new HashMap<>();
                map.put("username", username);
                map.put("password", password);
                map.put("method", "login");
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

