package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import java.io.Console;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.IntRange;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import javax.net.SocketFactory;



public class MainActivity extends AppCompatActivity {

    //Configuracion del Servidor
    protected static String server = "192.168.0.162";
    protected static int port = 8088;

    @IntRange(from=1,to=300)
    int camas;

    EditText camasInput;

    @IntRange(from=1,to=300)
    int mesas;

    EditText mesasInput;

    @IntRange(from=1,to=300)
    int sillas;

    EditText sillasInput;

    @IntRange(from=1,to=300)
    int sillones;

    EditText sillonesInput;

    int user;

    EditText userInput;

    String mensaje;

    String mensajeenviar;


    public static KeyPair getRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvvDZHoi0VNfb8uWI+u2Tp/qvP76Gst/ZCDTDueAUl1c1slBd43Wk2t/WSwbkoQp2Gqk2v0/3f5rK7N4pJ0oTkh2QC0tqShxWLfhWy8mH1z4DGXET5jKJBYgxhOJmPMl9ptJDPSIexd5tKoaNrwHX/K2NMn5LyPAPNRK+K8/+7s/4/MQ7dFKVMBDOvzMdB3rYSuYP149Woz+O9ja8qRCO1NTkHHz8v+M8CfLYe8zsyVgXpsTZclWUa1H6lPBjDa9t4R+MAEuJxoZIbcMfg2gcOZU4Wso88mFaqe6ifAQYltIRdId4jE1X7TK1BRf3ntLpnMV7YA+TJJ82K779xzs0dQIDAQAB";
        byte[] publicBytes = Base64.decode(pk);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        System.err.println(pubKey);
        return pubKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String pk = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC+8NkeiLRU19vy5Yj67ZOn+q8/voay39kINMO54BSXVzWyUF3jdaTa39ZLBuShCnYaqTa/T/d/msrs3iknShOSHZALS2pKHFYt+FbLyYfXPgMZcRPmMokFiDGE4mY8yX2m0kM9Ih7F3m0qho2vAdf8rY0yfkvI8A81Er4rz/7uz/j8xDt0UpUwEM6/Mx0HethK5g/Xj1ajP472NrypEI7U1OQcfPy/4zwJ8th7zOzJWBemxNlyVZRrUfqU8GMNr23hH4wAS4nGhkhtwx+DaBw5lThayjzyYVqp7qJ8BBiW0hF0h3iMTVftMrUFF/ee0umcxXtgD5MknzYrvv3HOzR1AgMBAAECggEAIk5lxD2toNzT0PV6whLzh6fb2ukhjHv5o6bPT9M5/+MUa8BSzi5x+z1iZEKNy310sN+cTY1Rm6S1Nw/Hdx6xG3yiR9U5+KzUsxe+iIjiFkM3DVgqyh5kThElTInc8qkFIXb/y7kYrFaFaLGaPUpogyavitX0SdsP8Go4ruiFFmUJiw3y6pMUJ/6yIdSpY2KGiUB74VhLqHSHw3U8pqprCKrUrH9yCdokDkYOeB/lGPkYKojxWQBqtmzm2TJnYcUKPuJFeyPrKmWVAxZz2wVfyAKQM8Oa9JHDswGBlqqYou6toAqPav9UoplTdSnkHeKTABJIn/t64rsmcJaj+RCztQKBgQD4eLgGiU4/kYnuOudUlRrybs+/QkkOqGuGu75AE3Ycio4G2j6VOydNi42V9a9vcAsEbo+1x7Whm3p9lj4a77yu+ONIEm0G1K/at2O5r1NPbfYh5smc9PEbNnlRsmmeLsWkWv54va+9TpZWBYT6dVdXcwFMpwxGs0kcCoBcuMD1AwKBgQDEueOuomb1FnQL7AxnPepeLdrDbDO23TwUZ1KAt6Bd1Hy1kwfYN51uV+ldFmFhwyzqyhZQK+baHhiNlMTV/qzvZCXSGzDAP1Pzux+yrCjqKLsFLwXDDeyZs+pxar5YHAN07r6wgXDNxxJWHgwlMSn6xEX2BDZvriUjPrlB/VpLJwKBgHvDhcabtgIr+ExVwsx6yMVhNNHLrqBCe+zMr2MzTm3BBiWbp/ilUlpp3MiJbC1R3esDN0oQhARPcaAEqkK4j3+IMY3Av9XbMwz6tA3VquWxnBwe3OX1i/NGGv/6omlMWt4XBRIXSeY9stx+O1KWCD9i5Y7M1myQ+SWihXWqAVMJAoGAFZ2bP9gGWg2yiJDSOBHci6acL/bWo9QhQtirfwsuKsErRsQ2C3Lo8HPmZ10LLDPG3rF/zCVWw0alSgE7s4u9MrydTz2/mJAcyF4aOIakJD5/di1Zg7om8iiLrRSef43sb0/AUiKW43VpNV8t1HRXeX9RdJu344ON/xZoQrD01+cCgYAm3epNh9fu9JJ0rHxiH7sR8D1D8YhP1AhIbcwQ38QgRVMGbAvEt5UurCTSr4UPHTrtVc4Ye0U0g99W7tmrfaNIQKYCPiK4xZY3sKeqGU1dUUIU45qUnWErR3fw4Q8aUsQFTMgUBWUQG6HebdROZ2zPx/FV1Q1EMwM+uXioLN2yiw==";
        byte [] pkcs8EncodedBytes = Base64.decode(pk);
        PKCS8EncodedKeySpec keySpec1 = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey privKey = kf.generatePrivate(keySpec1);
        return privKey;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Capturamos el boton de Enviar
        camasInput = (EditText) findViewById(R.id.camasInput);
        camasInput.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        mesasInput = (EditText) findViewById(R.id.mesasInput);
        mesasInput.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        sillasInput = (EditText) findViewById(R.id.sillasInput);
        sillasInput.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        sillonesInput = (EditText) findViewById(R.id.sillonesInput);
        sillonesInput.setFilters(new InputFilter[]{new InputFilterMinMax("0", "300")});
        userInput = (EditText) findViewById(R.id.userInput);
        // Capturamos el boton de Enviar
        View button = findViewById(R.id.button_send);
        // Llama al listener del boton Enviar
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    protected boolean isEmpty(EditText editText) {
        return (editText.getText().toString().equals(""));
    }

    // Creación de un cuadro de dialogo para confirmar pedido
    private void showDialog() throws Resources.NotFoundException {
        CheckBox sabanas = (CheckBox) findViewById(R.id.checkBox_sabanas);

        if (!sabanas.isChecked()) {
            // Mostramos un mensaje emergente;
            Toast.makeText(getApplicationContext(), "Selecciona al menos un elemento", Toast.LENGTH_SHORT).show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Enviar")
                    .setMessage("Se va a proceder al envio")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                // Catch ok button and send information
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (Build.VERSION.SDK_INT > 9)
                                    {
                                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                        StrictMode.setThreadPolicy(policy);
                                    }
                                    // 1. Extraer los datos de la vista
                                    if(isEmpty(camasInput)){
                                        camas = 0;
                                    } else {
                                        camas = Integer.parseInt(camasInput.getText().toString());

                                    }
                                    if(isEmpty(sillasInput)){
                                        sillas = 0;
                                    } else {
                                        sillas = Integer.parseInt(sillasInput.getText().toString());

                                    }
                                    if(isEmpty(sillonesInput)){
                                        sillones = 0;
                                    } else {
                                        sillones = Integer.parseInt(sillonesInput.getText().toString());

                                    }
                                    if(isEmpty(mesasInput)){
                                        mesas = 0;
                                    } else {
                                        mesas = Integer.parseInt(mesasInput.getText().toString());

                                    }

                                    if(isEmpty(userInput)){
                                        user = 0;
                                    } else {
                                        user = Integer.parseInt(userInput.getText().toString());

                                    }

                                    mensaje = camas  + "," + sillas + "," + sillones + "," + mesas + "," + user + "";
                                    // 2. Firmar los datos
                                    byte[] firma = new byte[0];
                                    try{

                                        PrivateKey privateKey = getPrivateKey();


                                        Signature sg = Signature.getInstance("SHA256withRSA");
                                        sg.initSign(privateKey);

                                        sg.update("1234".getBytes());
                                        firma = sg.sign();


                                    }

                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    // 3. Enviar los datos
                                    //Primero, operaciones pertinentes para convertir los datos
                                    byte[] hexEncodedArray = Hex.encode(firma);
                                    String str_firma = new String(hexEncodedArray, Charset.forName("UTF-8"));
                                    Toast.makeText(MainActivity.this, str_firma, Toast.LENGTH_SHORT).show();

                                    try {

                                        SocketFactory socketFactory = (SocketFactory) SocketFactory.getDefault();

                                        Socket socket = (Socket) socketFactory.createSocket(server, port);

                                        PrintWriter output = new PrintWriter(new OutputStreamWriter(
                                                socket.getOutputStream()));

                                        output.println(mensaje);
                                        output.println(str_firma);
                                        output.flush();


                                        output.close();
                                        socket.close();
                                        Toast.makeText(MainActivity.this, "Petición enviada correctamente", Toast.LENGTH_SHORT).show();
                                    } catch (Exception ioException) {
                                        ioException.printStackTrace();
                                    }

                                }
                            }

                    )
                    .

                            setNegativeButton(android.R.string.no, null)

                    .

                            show();
        }
    }


}
