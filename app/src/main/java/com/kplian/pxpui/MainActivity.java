package com.kplian.pxpui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.kplian.pxpui.Utils.Constants;
import com.kplian.pxpui.commons.Encryption;
import com.kplian.pxpui.models.CurrentPosition;
import com.kplian.pxpui.models.SignedUser;
import com.kplian.pxpui.models.SingleUserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import im.delight.android.webview.AdvancedWebView;

import com.google.android.gms.location.LocationRequest;
import com.kplian.pxpui.models.firebase.Token;
import com.kplian.pxpui.services.MobileNotificationManager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements AdvancedWebView.Listener,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    MobileNotificationManager mobileNotificationManager;

    private AdvancedWebView mWebView;

    private LinearLayout llProgressBar;

    private SharedPreferences mPreferences;

    private LinearLayout previewLoadingLinearLayout;

    private EditText tokenEditText;

    /**
     * Obtener la posision actual usando los servicio de Google (GoogleServices)
     */
    public void getCurrentLocation() {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            Log.d("Location", latitude + " --  " + longitude);

                            CurrentPosition currentPosition = new CurrentPosition();
                            currentPosition.setLat(latitude);
                            currentPosition.setLng(longitude);

                            Gson gson = new Gson();
                            String json = gson.toJson(currentPosition);

                            String requestBody = "'userCurrentPosition', " + "'" + json + "'";
                            String jsFunction = "javascript:callMethodFromDevice(" + requestBody + ")";
                            Log.d("jsFunction: ", jsFunction);
                            mWebView.loadUrl(jsFunction);
                        }
                    }
                }, Looper.getMainLooper());
    }

    /**
     * Metodo que se ejecuta cuando se aceptan los permisos de localizacion
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                getCurrentLocation();
            } else {
//                Toast.makeText(this, "Permisos denegados!", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Metodo que se ejecuta cuando se aceptan los permisos de localizacion
     */
    //    Traking de la posicion
    @Override
    public void onLocationChanged(Location location) {
        Log.d("123", "Latitude:" + location.getLatitude() + ", Longitude:" + location.getLongitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }

    /**
     * Metodo que verifica los permisos localizacion en tiempo de ejecucion
     */
    private void displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i("TAG", "All location settings are satisfied.");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, 1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i("TAG", "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendToWebView("onMobileFocusIn", "");
//        getCurrentLocation();
    }

    /**
     * Instanciar servicios de FireBase
     */
    public void initiateFirebaseMessageService() {
        mobileNotificationManager = MobileNotificationManager.getInstance(this);
        mobileNotificationManager.registerNotificationChannelChannel(
                getString(R.string.NEWS_CHANNEL_ID),
                getString(R.string.CHANNEL_NEWS),
                getString(R.string.CHANNEL_DESCRIPTION));

        FirebaseMessaging.getInstance().isAutoInitEnabled();

        /*
         * Metodo que se ejecuta cuando se instancia FireBase, retorna el token generado
         * llama al metodo bridge JS para enviar el token generado por usuario
         */
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isComplete()) {
                    return;
                }
                Log.i("111111", task.getResult().getToken());
                tokenEditText.setText(task.getResult().getToken());

                Token token = new Token();
                token.setToken(task.getResult().getToken());

                Gson gson = new Gson();
                String json = gson.toJson(token);

                String requestBody = "'userFirebaseToken', " + "'" + json + "'";
                String jsFunction = "javascript:callMethodFromDevice(" + requestBody + ")";
                Log.d("jsFunction: ", jsFunction);
                mWebView.loadUrl(jsFunction);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tokenEditText = findViewById(R.id.tokenEditText);
        previewLoadingLinearLayout = findViewById(R.id.previewLoadingLinearLayout);

        getSupportActionBar().hide();

        displayLocationSettingsRequest(MainActivity.this);
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION
            );
        }

        llProgressBar = findViewById(R.id.llProgressBar);

        mWebView = (AdvancedWebView) findViewById(R.id.webview);

        mWebView.clearCache(true);

        mWebView.setListener(this, this);
        mWebView.loadUrl(Constants.BASE_URL);

        /*
        * Instancia de la interfaz de JS para la llamada a metodos dentro del webView, llamada mobile
        * */
        mWebView.addJavascriptInterface(new WebViewJavaScriptInterface(), "Mobile");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onResume() {
        super.onResume();
        try {
            mWebView.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // ...
    }

    @SuppressLint("NewApi")
    @Override
    protected void onPause() {
        mWebView.onPause();
        // ...
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null)
            mWebView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mWebView.onActivityResult(requestCode, resultCode, intent);
        // ...
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setMessage(getResources().getString(R.string.exit_app));
            builder.setPositiveButton(getResources().getString(R.string.exit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
        }
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
    }


    /**
     * Listener de cuando el web view ya esta cargado
     * se llaman a los metods necesarios para el webview
     * */

    @Override
    public void onPageFinished(String url) {

        previewLoadingLinearLayout.setVisibility(View.GONE);

        if (checkFacebookLogin()) {
            llProgressBar.setVisibility(View.VISIBLE);
            facebookSignInNative();
        } else if (checkGoogleLogin()) {
            llProgressBar.setVisibility(View.VISIBLE);
            googleSingInNative();
        } else if (checkSavedCredentials()) {
            llProgressBar.setVisibility(View.VISIBLE);
            credentialsSignIn();
        }

        getCurrentLocation();

        initiateFirebaseMessageService();

        if (llProgressBar.getVisibility() == View.VISIBLE) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    llProgressBar.setVisibility(View.GONE);
                }
            }, 5000);
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    ===========================================================
//    ===========================================================
//    ===========================================================

    /**
     * Verifica si ya existe un sesion activa
     */
    public boolean checkSavedCredentials() {
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        if (encryption != null) {
            String user = encryption.decryptOrNull(sharedpreferences.getString(Constants.PREFERENCES_U, ""));
            String pass = encryption.decryptOrNull(sharedpreferences.getString(Constants.PREFERENCES_P, ""));
            String lang = encryption.decryptOrNull(sharedpreferences.getString(Constants.PREFERENCES_L, ""));
            return user.length() > 0 && pass.length() > 0 && lang.length() > 0;
        }
        return false;
    }

    /**
     * Inicia sesion con las credenciales guardadas
     */
    public void credentialsSignIn() {
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
        if (encryption != null) {
            String user = encryption.decryptOrNull(sharedpreferences.getString(Constants.PREFERENCES_U, ""));
            String pass = encryption.decryptOrNull(sharedpreferences.getString(Constants.PREFERENCES_P, ""));
            String lang = encryption.decryptOrNull(sharedpreferences.getString(Constants.PREFERENCES_L, ""));

            SignedUser signedUser = new SignedUser();
            signedUser.setUsername(user);
            signedUser.setPassword(pass);
            signedUser.setLanguage(lang);

            Gson gson = new Gson();
            String json = gson.toJson(signedUser);

            sendToWebView("vouzSignIn", json);

        }
    }

    public void facebookSignInNative() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            String email = object.getString("email");

                            AccessToken accessToken = AccessToken.getCurrentAccessToken();

                            SingleUserModel singleUserModel = new SingleUserModel(
                                    "",
                                    "",
                                    email,
                                    accessToken.getToken(),
                                    "",
                                    "",
                                    "facebook",
                                    Locale.getDefault().getDisplayLanguage(),
                                    "android"
                            );


                            Gson gson = new Gson();
                            String json = gson.toJson(singleUserModel);
                            Log.d(">>>>", json);

                            String requestBody = "'facebookSignIn', " + "'" + json + "'";
                            String jsFunction = "javascript:callMethodFromDevice(" + requestBody + ")";
                            Log.d("jsFunction: ", jsFunction);
                            mWebView.loadUrl(jsFunction);

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    llProgressBar.setVisibility(View.GONE);
                                }
                            }, 3000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday"); // id,first_name,last_name,email,gender,birthday,cover,picture.type(large)
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void googleSingInNative() {

        Log.d("googleSingIn", "googleSingIn");
        try {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

            if (account != null) {
                SingleUserModel singleUserModel = new SingleUserModel(
                        "",
                        "",
                        account.getEmail() != null ? account.getEmail() : "",
                        account.getIdToken(),
                        "",
                        "",
                        "google",
                        Locale.getDefault().getDisplayLanguage(),
                        "android"
                );
                Gson gson = new Gson();
                String json = gson.toJson(singleUserModel);
                Log.d("googleSingIn>>>>>>>>", json);
                Log.d("account.getId()", account.getId());

                String requestBody = "'googleSignIn', " + "'" + json + "'";
                String jsFunction = "javascript:callMethodFromDevice(" + requestBody + ")";
                Log.d("jsFunction: ", jsFunction);
                mWebView.loadUrl(jsFunction);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llProgressBar.setVisibility(View.GONE);
                        return;
                    }
                }, 3000);
                return;
            }

        } catch (Exception e) {
            Log.d("1111", e.getMessage());
        }

    }

    /**
     * Metod que envia al informacion al web view
     * estructura:
     * javascript:callMethodFromDevice(nombre_del_metodo_en_PXP.js, objecto dinamico json como cadena)
     * Metodo Bridge para la coneccion con el WebView Nativo -> WebView
     */
    public void sendToWebView(String method, String jsonObject) {
        String requestBody = "'" + method + "', " + "'" + jsonObject + "'";
        String jsFunction = "javascript:callMethodFromDevice(" + requestBody + ")";
        Log.d("jsFunction: ", jsFunction);
        mWebView.loadUrl(jsFunction);
    }

    public void refreshWebView() {
        Log.d("refresh web", "refresh web view from activityt");
        mWebView.reload();
    }

    public boolean checkGoogleLogin() {
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//        return account != null;
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    public boolean checkFacebookLogin() {
        return AccessToken.getCurrentAccessToken() != null;
    }


    /**
     * Clase Bridge para la coneccion con el WebView WebView -> Nativo
     */
    public class WebViewJavaScriptInterface {

        @JavascriptInterface
        public void getUserCurrentPosition() {
            Log.d("getUserCurrentPosition", "getUserCurrentPosition");
            getCurrentLocation();
            Thread.currentThread().interrupt();
        }

        @JavascriptInterface
        public void saveUserCredentials(final String username, final String password, final String language) {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

            Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            if (encryption != null) {
                editor.putString(Constants.PREFERENCES_U, encryption.encryptOrNull(username));
                editor.putString(Constants.PREFERENCES_P, encryption.encryptOrNull(password));
                editor.putString(Constants.PREFERENCES_L, encryption.encryptOrNull(language));
                editor.apply();
            }
        }

        @JavascriptInterface
        public void deleteUserCredentials() {
            Log.d("deleteUserCredentials", "deleteUserCredentials");
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Constants.PREFERENCES_U, "");
            editor.putString(Constants.PREFERENCES_P, "");
            editor.putString(Constants.PREFERENCES_L, "");
            editor.apply();
        }

        @JavascriptInterface
        public void saveWebSocketURL(final String data, final String id_usuario, final String nombre_usuario) {
            SharedPreferences sharedpreferences = getSharedPreferences(Constants.PREFERENCES, Context.MODE_PRIVATE);

            Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            if (encryption != null) {
                editor.putString(Constants.PREFERENCES_S, encryption.encryptOrNull(data));
                editor.putString(Constants.PREFERENCES_UID, encryption.encryptOrNull(id_usuario));
                editor.putString(Constants.PREFERENCES_NU, encryption.encryptOrNull(nombre_usuario));
                editor.apply();
            }
        }

        @JavascriptInterface
        public void hideLoadingDialog() {
            llProgressBar.setVisibility(View.GONE);

        }
    }
}
