package yavorikinc.labaccounting;

import android.os.StrictMode;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import java.net.*;

import javax.net.ssl.HttpsURLConnection;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            ":hello", ":world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mLoginView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mLoginView = (AutoCompleteTextView) findViewById(R.id.login);
        //populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /*@Override
    protected void onStop () {
        super.onStop();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }*/

    private void hideKeyboard(){
        View v = getCurrentFocus();
        if(v != null){
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

   /* private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }*/

    /*private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mLoginView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }*/

    /**
     * Callback received when a permissions request has been completed.
     */

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }*/

    /**
     *
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        //get ids of textInputs for handling an errors
        //TextInputLayout password_text_input = (TextInputLayout) findViewById(R.id.password_text_input);
        //TextInputLayout log_text_input = (TextInputLayout) findViewById(R.id.login_text_input);

        // Reset errors.
        mLoginView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String login = mLoginView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            //password_text_input.setError(getString(R.string.error_field_required));
            mPasswordView.setError(getString(R.string.error_field_required));

            focusView = mPasswordView;
            cancel = true;
        }


        // Check for a valid  login.
        if (TextUtils.isEmpty(login)) {
            //log_text_input.setError(getString(R.string.error_field_required));
            mLoginView.setError(getString(R.string.error_field_required));

            focusView = mLoginView;
            cancel = true;
        } else if (!isloginValid(login)) {
            mLoginView.setError(getString(R.string.error_invalid_login));
            focusView = mLoginView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            mAuthTask = new UserLoginTask(login, password);

            //if(mAuthTask.doInBackground()){
                //mAuthTask.onPostExecute(true);
            //} else{
                //mAuthTask.onPostExecute(false);
            //}
            //mAuthTask.onPostExecute(false);

            mAuthTask.execute((Void[]) null);
        }
    }

    private boolean isloginValid(String login) {
        //TODO: Replace this with your own logic
        return true;
        //return login.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only login addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary login addresses first. Note that there won't be
                // a primary login address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> logins = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            logins.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addLoginsToAutoComplete(logins);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addLoginsToAutoComplete(List<String> loginAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, loginAddressCollection);

        mLoginView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */


    public final String TAG = "myTag";


    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mLogin;
        private final String mPassword;
        private final String serverIP;

        private String answer;

        UserLoginTask(String login, String password) {
            mLogin = login;
            mPassword = password;
            this.serverIP = getServerIP() + ":5000/api/";
            answer = "";
        }

        public String getServerIP(){

            //TextView v = (TextView) findViewById(R.id.hello);

            SharedPreferences myPref = getSharedPreferences("myPrefs-ip", MODE_PRIVATE);
            String str = myPref.getString("ip", "192.168.1.103");

            //answer = str;
            //v.setText(str);

            return "http://" + str;
            //Toast.makeText(this,,).show();
        }

        public void sendRequest(){

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            String urlStr = this.serverIP + "authorization";

            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);

            try {

                //Network access
                Thread.sleep(2000);

                URL mUrl = new URL(urlStr);
                HttpURLConnection httpConnection = (HttpURLConnection) mUrl.openConnection();

                String name = mLogin;
                String pass = mPassword;

                //BogdanKpacaBa
                String urlParams = "name="+ name +"&password=" + pass;
                //String urlParams = "name=Bogdan&password=KpacaBa";

                httpConnection.setRequestMethod("POST");

                //httpConnection.setUseCaches(false);

                httpConnection.setDoOutput(true);

                DataOutputStream stream = new DataOutputStream(httpConnection.getOutputStream());

                stream.writeBytes(urlParams);
                //stream.flush();
                stream.close();

                //httpConnection.setAllowUserInteraction(false);
                //httpConnection.setConnectTimeout(100000);
                //httpConnection.setReadTimeout(100000);

                httpConnection.connect();

                int responseCode = httpConnection.getResponseCode();

                //answer += cookieManager.getCookieStore().toString();
                //answer += responseCode + " get: " + cookie;
                //Log.i(TAG, mLogin);

                ///////////////////////////////////////////////////////////

                URL obj = new URL(urlStr);
                //HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

                httpConnection = (HttpURLConnection) obj.openConnection();

                //add reuqest header
                httpConnection.setRequestMethod("GET");

                httpConnection.setRequestProperty("Cookie", TextUtils.join(";", cookieManager.getCookieStore().getCookies()));

                //connection.setRequestProperty("User-Agent", "Mozilla/5.0" );
                //connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                //connection.setRequestProperty("Content-Type", "application/json");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = bufferedReader.readLine()) != null) {
                    response.append(inputLine);
                }
                bufferedReader.close();

                this.answer = response.toString();

                //if (mLogin.equals("hello")) {

                    //Toast.makeText(getBaseContext(), answer,Toast.LENGTH_SHORT).show();

                    // Account exists, return true if the password matches.
                    //return mPassword.equals("world");
                //}
                //return true;

                /*if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    this.answer = sb.toString();

                }*/
                    for (String credential : DUMMY_CREDENTIALS) {
                        String[] pieces = credential.split(":");
                        //if (mLogin.equals("hello")) {

                            //Toast.makeText(getBaseContext(),"asd",Toast.LENGTH_SHORT).show();

                            // Account exists, return true if the password matches.
                            //return mPassword.equals("world");
                        //}
                    }
/*
                    return true;
                } else {

                    //Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_SHORT).show();

                    return false;
                }*/


            } catch (Exception ex) {
                answer = ex.toString();
                return false;
            }

            return true;

            ///////////////////////////////////////////////////////////////////////////////////

            /*

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals("hello")) {

                    Toast.makeText(getBaseContext(),"asd",Toast.LENGTH_SHORT).show();
                    // Account exists, return true if the password matches.
                    return pieces[1].equals("world");
                }
            }

            // TODO: register the new account here.
            return true;*/

        }
/*
        @Override
        runOnUiThread(new Runnable()
        {
            public void run()
            {
                //Toast toast = Toast.makeText(getApplicationContext(), "Something", Toast.LENGTH_SHORT).show();
            }
        });*/

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Toast.makeText(getBaseContext(), answer, Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);
                finish();
                overridePendingTransition(R.anim.diagonaltranslate,R.anim.alpha);

            } else {
                Toast.makeText(getBaseContext(), answer, Toast.LENGTH_LONG).show();

                //mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

