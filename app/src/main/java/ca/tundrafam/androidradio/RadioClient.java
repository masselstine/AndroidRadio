package ca.tundrafam.androidradio;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by masselst on 02/03/18.
 */

public class RadioClient {
    private static NsdManager nsd_manager = null;
    private static RadioClient instance = null;
    private static NsdManager.DiscoveryListener nsd_discover = null;
    private static NsdManager.ResolveListener nsd_resolve = null;

    private static final String TAG = "RadioClient";
    private static final String SERVICE_TYPE = "_workstation._tcp.";
    private static String service_name = new String("hi");
    private static String host_ip = null;

    protected RadioClient() {
    }

    public static RadioClient getInstance() {
        if (instance == null) {
            instance = new RadioClient();
        }
        return instance;
    }

    public void init(Context context) {
        initResolveListener();
        initDiscoverListener();
        nsd_manager = (NsdManager)context.getSystemService(Context.NSD_SERVICE);

        try {
            nsd_manager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, nsd_discover);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (host_ip != null) {
            fetchURL(new String("http://" + host_ip + ":5000/stop"));
        }
    }

    public void play(String url_str) {
        stop();
        if (host_ip != null && url_str != null) {
            fetchURL(new String("http://" + host_ip + ":5000/play/" + url_str));
        }

    }

    private void fetchURL(final String url_str) {
        new Thread() {
            public void run() {
                doFetchURL(url_str);
            }
        }.start();
    }

    private void doFetchURL(String url_str) {
        try {
            URL url = new URL(url_str);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while (br.readLine() != null) {;}
            conn.disconnect();
        } catch (ProtocolException|MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initDiscoverListener() {
        nsd_discover = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int errorCode) {
                Log.e(TAG, "Start Discovery failed: Error code:" + errorCode);
                nsd_manager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int errorCode) {
                Log.e(TAG, "Stop Discovery failed: Error code:" + errorCode);
                nsd_manager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Log.d(TAG, "Service discovery stopped");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success" + service);
                if (!service.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(service_name)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + service_name);
                } else if (service.getServiceName().contains("chip")){
                    nsd_manager.resolveService(service, nsd_resolve);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost" + service);
            }
        };
    }

    private void initResolveListener() {
        nsd_resolve = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo service, int error) {
                Log.e(TAG, "Resolve failed!");
            }

            @Override
            public void onServiceResolved(NsdServiceInfo service) {
                Log.e(TAG, "Resolved succeeded!");
                host_ip = service.getHost().getHostAddress();
            }
        };
    }
}
