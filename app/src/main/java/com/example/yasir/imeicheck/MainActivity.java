package com.example.yasir.imeicheck;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    public TextView txt_view;
    public TextView txt_view_Other_Details;
    public TextView txt_view_IPAddress;

    String IPaddress;

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt_view=(TextView)findViewById(R.id.textView);
        txt_view_Other_Details=(TextView)findViewById(R.id.textView3);
        txt_view_IPAddress=(TextView)findViewById(R.id.textView_IP_Address);



        if(isPermissionGranted()) {

            NetwordDetect();

            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

            String IMEINumber1 = tm.getDeviceId(0);
            String IMEINumber2 = tm.getDeviceId(1);


            String info="\n SubscriberID:"+tm.getDeviceId();;
            info+="\n Sim Serial Number:"+tm.getSimSerialNumber();
            info+="\n Network Country ISO:"+tm.getNetworkCountryIso();
            info+="\n SIM Country ISO:"+tm.getSimCountryIso();
            info+="\n NetworkOperatorName:"+tm.getNetworkOperatorName();
            info+="\n getSimOperatorName:"+tm.getSimOperatorName();

            txt_view.setText("SIM1: "+IMEINumber1+"\n"+"SIM2: "+ IMEINumber2);

            txt_view_Other_Details.setText(info);
        }
    }

    private void NetwordDetect() {

        boolean WIFI = false;
        boolean MOBILE = false;

        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = CM.getAllNetworkInfo();

        for (NetworkInfo netInfo : networkInfo) {
            if (netInfo.getTypeName().equalsIgnoreCase("WIFI"))
                if (netInfo.isConnected())
                    WIFI = true;

            if (netInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if (netInfo.isConnected())
                    MOBILE = true;
        }

        if(WIFI == true)
        {
            IPaddress = GetDeviceipWiFiData();
            txt_view_IPAddress.setText("IP Address: "+"\n"+IPaddress);
        }

        if(MOBILE == true)
        {
            IPaddress = GetDeviceipMobileData();
            txt_view_IPAddress.setText("IP Address: "+"\n"+IPaddress);
        }

    }


    public String GetDeviceipMobileData(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface networkinterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = networkinterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("Current IP", ex.toString());
        }
        return null;
    }

    public String GetDeviceipWiFiData()
    {

        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);

        @SuppressWarnings("deprecation")

        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;

    }
}