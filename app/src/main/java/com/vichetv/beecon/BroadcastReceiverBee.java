package com.vichetv.beecon;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.ArrayList;

public class BroadcastReceiverBee extends BroadcastReceiver {
    protected  MainActivity mainActivity;
    ArrayList<BluetoothDevice> devices= new ArrayList<>();
    JSONArray listToSend = new JSONArray();
    Date date = new Date();
    public BroadcastReceiverBee(MainActivity mainActivity){
        super();
        this.mainActivity = mainActivity;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        System.out.println(action);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            try{
                if(device != null && !devices.contains(device)){
                    int rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE);
                    devices.add(device);
                    JSONObject dd = new JSONObject();
                    dd.put("addresseMacDispositif",device.getAddress());
                    dd.put("addresseMacBalise","");
                    dd.put("timestamp", date.getTime());
                    dd.put("attenuation", rssi);
                    this.listToSend.put(dd);
                }
                mainActivity.handleNewBluetoothDevice(this.devices);
                Log.i("In action_found", device.getName());
            }
            catch(Exception e){}
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            Log.i("In discovery started", "Discovery started");
        }
        else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            Log.i("In discovery finished", "Discovery finished");
            //for(BluetoothDevice bt : devices) System.out.println(bt.getAddress());
            sendToAPI(context,this.listToSend);
        }

    }

    public void sendToAPI(Context context, JSONArray devices) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            String URL = "http://192.168.1.13:4910/position";
            final String requestBody = listToSend.toString();

            System.out.println(requestBody);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.toString());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = "";
                    if (response != null) {
                        responseString = String.valueOf(response.statusCode);
                        // can get more details such as response.headers
                    }
                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };
            requestQueue.add(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<BluetoothDevice> getDevices() {
        return devices;
    }


}
