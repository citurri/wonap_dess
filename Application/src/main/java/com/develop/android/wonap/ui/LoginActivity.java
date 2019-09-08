package com.develop.android.wonap.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.annotation.TargetApi;
import android.os.Build;

import com.develop.android.wonap.R;
import com.develop.android.wonap.SocialNetwork.SignupActivity;
import com.develop.android.wonap.SocialNetwork.SplashFragment;
import com.develop.android.wonap.common.Utils;
//import com.develop.android.wonap.database.ParseJSON;
import com.develop.android.wonap.database.ParseJSON;
import com.develop.android.wonap.service.SharedPrefManager;
import com.develop.android.wonap.service.UtilityService;
import com.facebook.AccessToken;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends FragmentActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
    private static final int PERMISSION_REQ = 0;
    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;

    ProgressDialog progressDialog;

    //GCM
    String PROJECT_NUMBER = "783306555122"; // SENDER ID GCM
    public static String id_registration;

    //LOGIN INTERNO
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password)
    EditText _passwordText;
    @BindView(R.id.cbShowPwd)
    CheckBox _mCbShowPwd;
    @BindView(R.id.btn_login)
    Button _loginButton;
    @BindView(R.id.link_signup)
    TextView _signupLink;
    Boolean sucess = false;
    Boolean empresa = false;
    String ciudad = "";
    String nombre_completo = "";
    Integer id_user = 0;
    String email_user;
    String password_user;
    String image_user;
   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

       //INICIAMOS LOS VALORES POR DEFECTO DE AJUSTES
       //PreferenceManager.setDefaultValues(this, R.xml.ajustes_layout, false);

       // Check fine location permission has been granted
       if (!Utils.checkFineLocationPermission(this)) {
           // See if user has denied permission in the past
           if (ActivityCompat.shouldShowRequestPermissionRationale(
                   this, Manifest.permission.ACCESS_FINE_LOCATION)) {
               // Show a simple snackbar explaining the request instead
               new AlertDialog.Builder(this)
                       .setTitle("Permiso de ubicación para WONAP")
                       .setMessage("Por favor permita que WONAP acceda a su ubicación, este dato es utilizado para mostrarle las ofertas y lugares más cercanos a usted.")
                       .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               //Prompt the user once explanation has been shown
                               requestFineLocationPermission();
                           }
                       })
                       .create()
                       .show();
           } else {
               // Otherwise request permission from user
               if (savedInstanceState == null) {
                   requestFineLocationPermission();

               }
           }
       } else {
           UtilityService.requestLocation(this);
           // Otherwise permission is granted (which is always the case on pre-M devices)
           //fineLocationPermissionGranted();
       }


       //BASE DE DATOS
       BaseInit();


        //MOSTRAR O ESCONDER CONTRASEÑA
       _mCbShowPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               // checkbox status is changed from uncheck to checked.
               if (!isChecked) {
                   // show password
                   _passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
               } else {
                   // hide password
                   _passwordText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
               }
           }
       });

       //LOGIN INTERNO
       _loginButton.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {
               login();
           }
       });

       _signupLink.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {

               if (Utils.isConn(LoginActivity.this)) {
                   // Start the Signup activity
                   Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                   startActivityForResult(intent, REQUEST_SIGNUP);
               } else {

                   Toast.makeText(getApplicationContext(), "Por favor, conéctese a internet....", Toast.LENGTH_SHORT).show();
               }

           }
       });



    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Verificando sus datos de ingreso...");
        progressDialog.show();

        email_user = _emailText.getText().toString();
        password_user = _passwordText.getText().toString();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new ValidateUser().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            new ValidateUser().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void BaseInit() {
        if (Utils.isConn(getApplicationContext())) {
            try {
                if(!Utils.doesDatabaseExist(this, "WonapDatabaseLocal")) {
                    CrearBase();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Por favor, conéctese a internet....", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();

        FragmentManager fm = getSupportFragmentManager();
        SplashFragment splashFragment = (SplashFragment) fm.findFragmentById(R.id.splashFragment);
        fragments[SPLASH] = splashFragment;
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.commit();


        //Ver si el usuario ya esta logueado
        VerificarLogueos();

        showFragment(SPLASH, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //UtilityService.requestLocation(this);
        isResumed = true;
    }


    @Override
    public void onPause() {
        super.onPause();
        isResumed = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
        //if (authClient != null) authClient.onActivityResult(requestCode, resultCode, data);
        //if (callbackManager != null )fragments[SPLASH].onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        boolean userSkippedLogin = false;
        outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }


    protected void VerificarLogueos() {

        final AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            // if the user already logged in, try to show the selection fragment
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            int id_usuario = preferences.getInt("id_usuario", 0);

            if(id_usuario > 0) {
                Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
                finish();
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setAction("FROM_MAIN_ACTIVITY");
                startActivity(i);
            }
        } else if (Utils.WonapLogin(this)) {
            // if the user already logged in, try to show the selection fragment
            Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
            finish();
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setAction("LOGIN_NORMAL");
            startActivity(i);
        }
     }






     private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

   private void CrearBase() throws SQLException, ClassNotFoundException {
       String param1 =  "getValuesApp.php";
       new ParseJSON(this, false, "Consulta", true, false).execute(param1);
   }

    public void onLoginSuccess(Boolean empresa) {
        //Recuperamos el nuevo id en caso de que no existia y guardamos en global
        //int id_usuario = new WonapDatabaseLocal(this).getIdUsuarioLogin(email_user, password_user, "00000", "00000");

        //Guardamos ID del usuario
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("id_usuario", id_user);
        editor.putString("ciudad", ciudad);
        editor.putString("nombre_completo", nombre_completo);
        editor.putString("image_user", image_user);


        //Guardamos la sessión del usuario
        if(empresa) editor.putInt("session_usuario", 12345);
        else
        editor.putInt("session_usuario", 15879);
        editor.apply();

        Intent i = new Intent(LoginActivity.this, PrincipalActivity.class);
        finish();
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setAction("LOGIN_NORMAL");
        startActivity(i);
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login Fallido, revise su información por favor.", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Ingrese una direccion de correo válida");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("entre 4 y 20 caracteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    private class ValidateUser extends AsyncTask<Void, Void, Void> {


        String mStrings;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            mStrings = "[]";

            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(getString(R.string.web_server)+"api/validateUser.php?user="+ email_user.trim() +"&pass="+password_user.trim());
                Log.v("VaLIDATE_URL",url.toString());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(15000);
                con.setReadTimeout(15000);
                StringBuilder sb = new StringBuilder();
                bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));
                String json;
                while ((json = bufferedReader.readLine()) != null) {
                    sb.append(json + "\n");
                }
                mStrings = sb.toString().trim();
                Log.v("JSON",mStrings);
                try {
                    JSONArray arr = new JSONArray(mStrings);
                    if (arr.length() != 0) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = (JSONObject) arr.get(i);
                            id_user = obj.getInt("id_user");
                            ciudad = obj.getString("ciudad");
                            nombre_completo = obj.getString("nombre");
                            empresa = obj.getBoolean("empresa");
                            image_user = obj.getString("image_user");
                        }
                        sucess = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.v("JSON", "Loop:" + uri);
            } catch (Exception e) {
                Log.v("JSON",e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (progressDialog.isShowing())
                progressDialog.dismiss();
            if(sucess)
                onLoginSuccess(empresa);
            else onLoginFailed();
        }

    }

    /**
     +     * Permissions request result callback
     +     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQ:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //fineLocationPermissionGranted();
                    UtilityService.requestLocation(this);
                }
        }
    }
    /**
     * Request the fine location permission from the user
     */
    private void requestFineLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQ);
    }

    /**
     * Run when fine location permission has been granted
     */
    private void fineLocationPermissionGranted() {
        //UtilityService.removeGeofences(getApplicationContext());
        //UtilityService.addGeofences(getApplicationContext());
        //UtilityService.requestLocation(getApplicationContext());
    }

}


