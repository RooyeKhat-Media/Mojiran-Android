/**
 * © 2016 RooyeKhat Media Co all rights reserved
 * Mojiran Project - Online Stream
 * url : http://rooyekhat.co//
 */

package com.Mojiran.Mojiran.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.Mojiran.Mojiran.Network.ConnectionDetector;
import com.Mojiran.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Splash extends Activity {

    MediaPlayer mediaPlayer;
    ProgressBar progressBar1;
    int max = 0, durr = 0, state;
    Handler handler;
    private ConnectionDetector con;
    String Date, Facebook, Twitter, Youtube, GPlus, HomePage, Linkedin, Version, UpdateLink, Instagram;
    TextView txtmatn, txtcopyrightt;
    private Typeface iranSans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash);


//        Tracker t = ((GoogleAnalyticsApp) getApplication()).getTracker(TrackerName.APP_TRACKER);
//        t.setScreenName("Splash");
//        t.send(new HitBuilders.AppViewBuilder().build());

        handler = new Handler();
        progressBar1 = (ProgressBar) findViewById(R.id.progress);
        progressBar1.getProgressDrawable().setColorFilter(Color.parseColor("#666666"), Mode.SRC_IN);
        txtcopyrightt = (TextView) findViewById(R.id.txt_copyrightt);
        //Typeface fa = Typeface.createFromAsset(getAssets(), "fonts/Yekan.ttf");
       iranSans = Typeface.createFromAsset(getAssets(), "IRANSansMobile.ttf");
        txtcopyrightt.setTypeface(iranSans);

        try {
            /*
            mediaPlayer = new MediaPlayer();
            //Uri uri = Uri.parse("file:///android_asset/song.mp3");
            Uri uri = Uri.parse("android.resource://com.example.mojiran/raw/mojiranlogo.mp3");
            mediaPlayer.setDataSource(this, uri);
            */

            mediaPlayer = MediaPlayer.create(this, R.raw.mojiranlogo);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i("LOG", "END");
                    new checkinternet().execute();
                }
            });

            int maxx = mediaPlayer.getDuration();
            max = maxx / 1000 % 60;
            progressBar1.setMax(max);

            progressset();

        } catch (IllegalStateException e) {
            Log.d("ERROR", "IllegalStateException: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            Log.d("ERROR", "IllegalArgumentException: " + e.getMessage());
        } catch (SecurityException e) {
            Log.d("ERROR", "SecurityException: " + e.getMessage());
        }

        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                new checkinternet().execute();
            }
        }, 10000);


        mediaPlayer = MediaPlayer.create(this, R.raw.mojiranlogo);
        mediaPlayer.start();
        */
        // progressset();

    }


    private void progressset() {

        int dtur = mediaPlayer.getCurrentPosition();
        durr = dtur / 1000 % 60;
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                progressBar1.setProgress(durr);
            }
        });
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                progressset();
            }
        }, 1000);
    }


    private void noconect() {
        Dialog dialog = new Dialog(Splash.this);
        LayoutInflater inflater = Splash.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.noconected, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogView);

        Button btntryagain = (Button) dialogView.findViewById(R.id.btn_tryagain);
        Button btnsetting = (Button) dialogView.findViewById(R.id.btn_setting);
        Button btnexit = (Button) dialogView.findViewById(R.id.btn_exit);

        btntryagain.setTypeface(iranSans);
        btnsetting.setTypeface(iranSans);
        btnexit.setTypeface(iranSans);

        btntryagain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Splash.this, Splash.class);
                startActivity(intent);
                finish();
            }
        });
        btnsetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
            }
        });
        btnexit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }


    class loadinfo extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... args) {

            try {

                URL url = new URL("http://mojiran.com/get.php");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("Mojiran");

                for (int i = 0; i < nodeList.getLength(); i++) {

                    Node node = nodeList.item(i);
                    Element fstElmnt = (Element) node;

                    NodeList nameList = fstElmnt.getElementsByTagName("Linkedin");
                    Element nameElement = (Element) nameList.item(0);
                    nameList = nameElement.getChildNodes();
                    Linkedin = nameList.item(0).getNodeValue();

                    NodeList websiteList = fstElmnt.getElementsByTagName("Twitter");
                    Element websiteElement = (Element) websiteList.item(0);
                    websiteList = websiteElement.getChildNodes();
                    Twitter = websiteList.item(0).getNodeValue();

                    NodeList websiteList1 = fstElmnt.getElementsByTagName("Youtube");
                    Element websiteElement1 = (Element) websiteList1.item(0);
                    websiteList1 = websiteElement1.getChildNodes();
                    Youtube = websiteList1.item(0).getNodeValue();

                    NodeList websiteList2 = fstElmnt.getElementsByTagName("Facebook");
                    Element websiteElement2 = (Element) websiteList2.item(0);
                    websiteList2 = websiteElement2.getChildNodes();
                    Facebook = websiteList2.item(0).getNodeValue();

                    NodeList websiteList3 = fstElmnt.getElementsByTagName("GPlus");
                    Element websiteElement3 = (Element) websiteList3.item(0);
                    websiteList3 = websiteElement3.getChildNodes();
                    GPlus = websiteList3.item(0).getNodeValue();

                    NodeList websiteList4 = fstElmnt.getElementsByTagName("HomePage");
                    Element websiteElement4 = (Element) websiteList4.item(0);
                    websiteList4 = websiteElement4.getChildNodes();
                    HomePage = websiteList4.item(0).getNodeValue();

                    NodeList nameList1 = fstElmnt.getElementsByTagName("Date");
                    Element nameElement1 = (Element) nameList1.item(0);
                    nameList1 = nameElement1.getChildNodes();
                    Date = nameList1.item(0).getNodeValue();

                    NodeList nameList4 = fstElmnt.getElementsByTagName("Version");
                    Element nameElement4 = (Element) nameList4.item(0);
                    nameList4 = nameElement4.getChildNodes();
                    Version = nameList4.item(0).getNodeValue();

                    NodeList nameList5 = fstElmnt.getElementsByTagName("UpdateLink");
                    Element nameElement5 = (Element) nameList5.item(0);
                    nameList5 = nameElement5.getChildNodes();
                    UpdateLink = nameList5.item(0).getNodeValue();

                    NodeList nameList6 = fstElmnt.getElementsByTagName("Instagram");
                    Element nameElement6 = (Element) nameList6.item(0);
                    nameList6 = nameElement6.getChildNodes();
                    Instagram = nameList6.item(0).getNodeValue();
                }
            } catch (Exception e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Toast.makeText(Splash.this, "شما به اینترنت متصل نیستید", Toast.LENGTH_LONG).show();
                        // noconect();
                    }
                });
                System.out.println("XML Pasing Excpetion = " + e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    checkversion();
                    txtcopyrightt.setText(Date + " - 1394 - تمامی حقوق این برنامه متعلق به موج ایران می باشد");
                }
            });
            //Log.i("LOG", Date + "\n" + HomePage + "\n" + GPlus + "\n" + Facebook + "\n" + Youtube + "\n" + Twitter + "\n" + Linkedin + "\n" + Version + "\n" + UpdateLink);
        }
    }


    class checkinternet extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... args) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://google.com/");
            //httppost.setEntity(new UrlEncodedFormEntity(""));

            try {
                HttpResponse response = httpclient.execute(httppost);
                state = response.getStatusLine().getStatusCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            if (state == 405) {

                new loadinfo().execute();

            } else {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        noconect();
                        Toast.makeText(Splash.this, "شما به اینترنت متصل نیستید", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


    private void checkversion() {

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int verCode = pInfo.versionCode;
            Log.i("LOG", "check 2");
            //if (Integer.parseInt(Version) == verCode) {
            Log.i("LOG", "check 3");
            finish();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            Intent intent = new Intent(Splash.this, MainActivity.class);
            intent.putExtra("Date", Date);
            intent.putExtra("Facebook", Facebook);
            intent.putExtra("Twitter", Twitter);
            intent.putExtra("Youtube", Youtube);
            intent.putExtra("GPlus", GPlus);
            intent.putExtra("HomePage", HomePage);
            intent.putExtra("Linkedin", Linkedin);
            intent.putExtra("Instagram", Instagram);
            startActivity(intent);
//            } else {
//                Log.i("LOG", "check 4");
//                havetoupdate();
//            }
        } catch (Exception e) {
            Log.i("LOG", "error");
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(Splash.this, "شما به اینترنت متصل نیستید", Toast.LENGTH_LONG).show();
                    // noconect();
                }
            });
        }
    }


    private void havetoupdate() {
        Dialog dialog = new Dialog(Splash.this);
        LayoutInflater inflater = Splash.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.update, null);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(dialogView);

        Button btn_update = (Button) dialogView.findViewById(R.id.btn_update);
        Button btn_exitt = (Button) dialogView.findViewById(R.id.btn_exitt);
        Button btn_bazar = (Button) dialogView.findViewById(R.id.btn_bazar);

        btn_update.setTypeface(iranSans);
        btn_exitt.setTypeface(iranSans);
        btn_bazar.setTypeface(iranSans);

        txtmatn = (TextView) dialogView.findViewById(R.id.txttozihat);

        txtmatn.setTypeface(iranSans);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                txtmatn.setText("\u202Bنسخه ای که شما نصب کرده اید نسخه قدیمی موج ایران است ، هم اکنون نسخه جدید آن در دسترس میباشد ، لطفا ابتدا بروزرسانی کرده و سپس به موج ایران گوش دهید .");
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = UpdateLink;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        btn_bazar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent1 = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://cafebazaar.ir/app/com.Mojiran/?l=fa"));
                startActivity(browserIntent1);
            }
        });
        btn_exitt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        dialog.setCancelable(false);
        dialog.show();

    }


    @Override
    protected void onStart() {
        super.onStart();
        //GoogleAnalytics.getInstance(Splash.this).reportActivityStart(this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        //GoogleAnalytics.getInstance(Splash.this).reportActivityStop(this);
    }
}