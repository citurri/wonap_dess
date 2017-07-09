package com.develop.android.wonap.SocialNetwork;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.develop.android.wonap.R;
import com.develop.android.wonap.common.ExifUtil;
import com.develop.android.wonap.ui.LoginActivity;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;
import com.kosalgeek.genasync12.AsyncResponse;
import com.kosalgeek.genasync12.EachExceptionsHandler;
import com.kosalgeek.genasync12.PostResponseAsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.spinner_ciudad) Spinner _ciudad;
    @BindView(R.id.input_apellidos) EditText _apellidos;
    @BindView(R.id.input_documento) EditText _documento;

    @BindView(R.id.spinner_tipo) Spinner _tipo_doc;

    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    @BindView(R.id.image_usuario)
    ImageView _imageAnuncio;
    Boolean exists = false;
    Boolean sucess = false;

    CameraPhoto cameraPhoto;
    GalleryPhoto galleryPhoto;
    final int CAMERA_REQUEST = 1234;
    final int GALLERY_REQUEST = 4321;
    String selectedPhoto = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

        cameraPhoto = new CameraPhoto(this);
        galleryPhoto = new GalleryPhoto(this);

        _imageAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence options[] = new CharSequence[] {"Tomarse foto con la cámara", "Adjuntar una foto de la galería"};

                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                builder.setTitle("Seleccione una opción:");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        Log.v("Picker:", options[which].toString());

                        if (which == 0) {
                            Intent in = null;

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkPermission()) {
                                    try {
                                        in = cameraPhoto.takePhotoIntent();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    startActivityForResult(in, CAMERA_REQUEST);
                                } else {
                                    requestPermission();
                                }
                            } else
                            {
                                try {
                                    in = cameraPhoto.takePhotoIntent();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                startActivityForResult(in, CAMERA_REQUEST);

                            }
                        } else {
                            Intent in = galleryPhoto.openGalleryIntent();
                            startActivityForResult(in, GALLERY_REQUEST);
                        }
                    }
                });
                builder.show();
            }
        });
    }

    protected boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (result1 == PackageManager.PERMISSION_GRANTED & result2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent in = null;
                    try {
                        in = cameraPhoto.takePhotoIntent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startActivityForResult(in, CAMERA_REQUEST);
                } else {
                    Log.e("value", "Permiso negado. No puede guardar imagenes.");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == this.RESULT_OK){
            if(requestCode == CAMERA_REQUEST){
                String photoPath = cameraPhoto.getPhotoPath();
                selectedPhoto = photoPath;
                Log.v("Photo Camera: ", photoPath);
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(128, 128).getBitmap();
                    _imageAnuncio.setImageBitmap(ExifUtil.rotateBitmap(photoPath, bitmap)); //imageView is your ImageView
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
            else if(requestCode == GALLERY_REQUEST) {
                galleryPhoto.setPhotoUri(data.getData());
                String photoPath = galleryPhoto.getPath();
                selectedPhoto = photoPath;
                Log.v("Photo Gallery: ", photoPath);
                try {
                    Bitmap bitmap = ImageLoader.init().from(photoPath).requestSize(128, 128).getBitmap();
                    _imageAnuncio.setImageBitmap(ExifUtil.rotateBitmap(photoPath, bitmap));  //imageView is your ImageView
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(false);
            return;
        }

        String ciudad = _ciudad.getSelectedItem().toString();
        String name = _nameText.getText().toString();
        String apellidos = _apellidos.getText().toString();
        String email = _emailText.getText().toString();
        String documento = _documento.getText().toString();
        String tipo_doc = _tipo_doc.getSelectedItem().toString();
        String password = _passwordText.getText().toString();
        try {
            String encodeImage = "";
            if (!selectedPhoto.equals("")) {
                Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(512, 512).getBitmap();
                encodeImage = ImageBase64.encode(ExifUtil.rotateBitmap(selectedPhoto, bitmap));
            }
            Log.v("Photo Encode: ", encodeImage);

            HashMap<String, String> postdata = new HashMap<String, String>();


            postdata.put("id_facebook", "0");
            postdata.put("ciudad", ciudad);
            postdata.put("nombre", name);
            postdata.put("apellidos", apellidos);
            postdata.put("email", email);
            postdata.put("documento", documento);
            postdata.put("tipo_doc", tipo_doc);
            postdata.put("id_device", LoginActivity.id_registration);
            postdata.put("password", password);
            postdata.put("image_user", encodeImage);

            PostResponseAsyncTask task = new PostResponseAsyncTask(this, postdata, new AsyncResponse() {
                @Override
                public void processFinish(String s) {
                    switch (s) {
                        case "1":
                            Toast.makeText(getApplication(), "Registro exitoso!, Por favor ingrese a la aplicación.", Toast.LENGTH_LONG).show();
                            finish();
                            break;
                        default:
                            Log.v("Result Signup", s);
                            break;
                    }
                }
            });


            String WEB_SERVER = this.getString(R.string.web_server);
            task.setLoadingMessage("Registrando usuario, espere por favor.");
            task.execute(WEB_SERVER + "api/insertUsersApp.php");
            task.setEachExceptionsHandler(new EachExceptionsHandler() {
                @Override
                public void handleIOException(IOException e) {
                    Log.v("Signup Task: ", "No se puede conectar al servidor.");
                }

                @Override
                public void handleMalformedURLException(MalformedURLException e) {
                    Log.v("Signup Task: ", "Error de URL.");
                }

                @Override
                public void handleProtocolException(ProtocolException e) {
                    Log.v("Signup Task: ", "Error de Protocolo.");
                }

                @Override
                public void handleUnsupportedEncodingException(UnsupportedEncodingException e) {
                    Log.v("Signup Task: ", "Error de codificación.");
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onSignupFailed(Boolean existe) {

        if(existe) Toast.makeText(getBaseContext(), "La cuenta de correo electrónico ya se encuentra registrada.", Toast.LENGTH_LONG).show();
        else Toast.makeText(getBaseContext(), "Ocurrio un error en el registro, inténtelo de nuevo.", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        //String ciudad = _ciudad.getSelectedItem().toString();
        String name = _nameText.getText().toString();
        String apellidos = _apellidos.getText().toString();
        String email = _emailText.getText().toString();
        String documento = _documento.getText().toString();
        //String tipo_doc =  _tipo_doc.getSelectedItem().toString();
        String password = _passwordText.getText().toString();


        /*if (username.isEmpty() || username.length() < 5) {
            _username.setError("por lo menos 5 caracteres");
            valid = false;
        } else {
            _username.setError(null);
        }*/

        if (name.isEmpty() || name.length() <  3) {
            _nameText.setError("por lo menos 3 caracteres");
            valid = false;
        } else {
            _nameText.setError(null);
        }


        if (apellidos.isEmpty() || apellidos.length() < 3) {
            _apellidos.setError("por lo menos 3 caracteres");
            valid = false;
        } else {
            _apellidos.setError(null);
        }



        if (documento.isEmpty() || documento.length() < 3) {
            _documento.setError("por lo menos 3 caracteres");
           valid = false;
        } else {
            _documento.setError(null);
        }

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
}