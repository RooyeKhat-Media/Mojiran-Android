/**
 * © 2016 RooyeKhat Media Co all rights reserved
 * Mojiran Project - Online Stream
 * url : http://rooyekhat.co//
 */

package com.Mojiran.Mojiran.Activity;



import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.Mojiran.Mojiran.Network.JSONParser;
import com.Mojiran.Mojiran.Network.OnSwipeTouchListener;
import com.Mojiran.Mojiran.utils.Utilities;
import com.Mojiran.R;
import com.crashlytics.android.Crashlytics;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorOutput;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.PlayerControl;
import com.google.android.exoplayer.util.Util;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.spoledge.aacdecoder.MultiPlayer;
import com.spoledge.aacdecoder.PlayerCallback;

import org.json.JSONArray;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import com.Mojiran.Mojiran.visualizer.VisualizerView;
import com.Mojiran.Mojiran.visualizer.renderer.LineRenderer;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends Activity //implements PlayerCallback
 {

    String currentPlayForNotification="اکنون";
    String nextPlayForNotification="سپس";
    private ProgressDialog progressDialog;
    private static VisualizerView mVisualizerView;
    JSONParser jParser = new JSONParser();
    private static String url_next = "http://mojiran.com/src/getnext.xml.php";
    private static String url_current = "http://mojiran.com/src/getcurrent.xml.php";
    JSONArray products = null;
    //==================
    public static final int exoPlayerIsPlaying=1;
    public static final int exoPlayerIsStop=4;

   private TransparentProgressDialog pDialog;
    TextView txtlocation, txtdate, txttime, txtNext, txtCurrent, txt1, txt2;
    ImageView imglogotype, imgin, imgtt, imgins, imgyt, imgfb, imggplus, imgmenu, imgcopy1, imgcopy2;
    // Typeface fa;
    int header = 0, lastVolume;
    Animation inanim, inanim2;
    LinearLayout headerlayout, lyt;
    Button btnheader;
    String Date;
    String Facebook;
    String Twitter;
    String Youtube;
    String GPlus;
    String HomePage;
    String Linkedin;
    String Instagram;
    String current;
    String next;
    static String programName;
    static String categoryName;
     static String programNameNext;
    static String categoryNameNext;
    boolean doubleBackToExitPressedOnce = false;
    private Typeface iranSans;
    private boolean mute = false;
    AudioManager audio;
    private static Context context;
    private static final int BUFFER_SEGMENT_SIZE = 8 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 256;


    private    String url ="http://stream.mojiran.com/;stream.mp3" ;
    private Uri radioUri = Uri.parse(url);
    private static MediaCodecAudioTrackRenderer audioRenderer;
    private static  int mainAudioSessionId;
     private static PlayerControl playerControl;

    private static boolean isplaying;
    private static MultiPlayer multiPlayer;
    private static MediaPlayer play = new MediaPlayer();
    private static ExoPlayer exoPlayer;
    ImageView imgref;
    Thread thread;
    static Notification notification;
    static NotificationManager notificationManager;
    PendingIntent pendingNotificationIntent;
    BroadcastReceiver mReciver;
    static RemoteViews remoteViews;
    private Tracker mTracker;



    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);


        exoPlayer = ExoPlayer.Factory.newInstance(1);





// Settings for exoPlayer
        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(MainActivity.this, "ExoPlayerDemo");
        DataSource dataSource = new DefaultUriDataSource(MainActivity.this, null, userAgent);
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource){
            @Override
            protected void onAudioSessionId(int audioSessionId) {

                mainAudioSessionId=audioSessionId;
            }

        };
        exoPlayer.prepare(audioRenderer);
        playerControl=new PlayerControl(exoPlayer);



        AnalyticApplication.tracker().send(new HitBuilders.EventBuilder("ui", "open")
                .setLabel("Mainactivity")
                .build());


        new GetCurrent().execute();
        new GetNext().execute();
//
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


        int record_permission = checkSelfPermission( Manifest.permission.RECORD_AUDIO );
        int modifyAudioPermission = checkSelfPermission( Manifest.permission.MODIFY_AUDIO_SETTINGS);
        List<String> permissions = new ArrayList<String>();
        if( record_permission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.RECORD_AUDIO );
        }

        if( modifyAudioPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.MODIFY_AUDIO_SETTINGS );
        }

        if( !permissions.isEmpty() ) {
            requestPermissions( permissions.toArray( new String[permissions.size()] ), 1 );
        }
        }




        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    handler.post(new Runnable() {
                        public void run() {

                        }
                    });
                    try {
                        Thread.sleep(15000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
        thread.start();

        iranSans = Typeface.createFromAsset(getAssets(), "IRANSansMobile.ttf");


            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                Date = extras.getString("Date");
                Facebook = extras.getString("Facebook");
                Twitter = extras.getString("Twitter");
                Youtube = extras.getString("Youtube");
                GPlus = extras.getString("GPlus");
                HomePage = extras.getString("HomePage");
                Linkedin = extras.getString("Linkedin");
                Instagram = extras.getString("Instagram");
            }

            imgref = (ImageView) findViewById(R.id.img_ref);
            final ImageView imgoff = (ImageView) findViewById(R.id.img_off);
            imgin = (ImageView) findViewById(R.id.img_in);
            imgtt = (ImageView) findViewById(R.id.img_tt);
            imgins = (ImageView) findViewById(R.id.img_ins);
            imgyt = (ImageView) findViewById(R.id.img_yt);
            imgfb = (ImageView) findViewById(R.id.img_fb);
            imggplus = (ImageView) findViewById(R.id.img_gplus);
            imgmenu = (ImageView) findViewById(R.id.img_menu);
            imgcopy1 = (ImageView) findViewById(R.id.imgcopy1);
            imgcopy2 = (ImageView) findViewById(R.id.imgcopy2);
            headerlayout = (LinearLayout) findViewById(R.id.LinearLayout122);
            lyt = (LinearLayout) findViewById(R.id.lyt);

            imglogotype = (ImageView) findViewById(R.id.img_logotype);

          mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
            TextView txtcopyright = (TextView) findViewById(R.id.txt_copyright);
            txtcopyright.setText(Date + " - تمامی حقوق این برنامه متعلق به موج ایران می باشد");

            txtlocation = (TextView) findViewById(R.id.txt_location);
            txtdate = (TextView) findViewById(R.id.txt_date);
            txttime = (TextView) findViewById(R.id.txt_time);
            txtNext = (TextView) findViewById(R.id.txt_next);
            txtCurrent = (TextView) findViewById(R.id.txt_current);
            txt1 = (TextView) findViewById(R.id.txt1);
            txt2 = (TextView) findViewById(R.id.txt2);


        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
            imgref.setImageResource(R.drawable.vol_off);
            mute = true;
        }

        lyt.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {

            public void onSwipeTop() {
            }


            public void onSwipeBottom() {
            }


            public void onSwipeRight() {
                showAlertDialogExit();
            }


            public void onSwipeLeft() {
                showAlertDialogExit();
            }

        });

        imgin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = Linkedin;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imgtt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = Twitter;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imgins.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = Instagram;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imgyt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = Youtube;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imgfb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = Facebook;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imggplus.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = GPlus;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        imgmenu.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = HomePage;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        //fa = Typeface.createFromAsset(getAssets(), "fonts/Yekan.ttf");

        imgin.setVisibility(View.GONE);
        imgtt.setVisibility(View.GONE);
        imgins.setVisibility(View.GONE);
        imgyt.setVisibility(View.GONE);
        imgfb.setVisibility(View.GONE);
        imggplus.setVisibility(View.GONE);
        imgmenu.setVisibility(View.GONE);
        imgcopy1.setVisibility(View.GONE);

        String yy = Utilities.getCurrentShamsidate();
        txtdate.setText(yy);
        gettimenoww();

        inanim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_up);
        inanim2 = AnimationUtils.loadAnimation(MainActivity.this, android.R.anim.fade_out);
        btnheader = (Button) findViewById(R.id.btn_header);

        btnheader.setOnClickListener(new OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                if (header == 0) {
                    Log.i("LOG", "1");

                    header = 1;
                    //btnheader.setBackgroundDrawable(getResources().getDrawable(R.drawable.up));
                    btnheader.setBackgroundResource(R.drawable.up);
                    imgin.startAnimation(inanim);
                    imgtt.startAnimation(inanim);
                    imgins.startAnimation(inanim);
                    imgyt.startAnimation(inanim);
                    imgfb.startAnimation(inanim);
                    imggplus.startAnimation(inanim);
                    imgmenu.startAnimation(inanim);
                    headerlayout.startAnimation(inanim);
                    imgin.setVisibility(View.VISIBLE);
                    imgtt.setVisibility(View.VISIBLE);
                    imgins.setVisibility(View.VISIBLE);
                    imgyt.setVisibility(View.VISIBLE);
                    imgfb.setVisibility(View.VISIBLE);
                    imggplus.setVisibility(View.VISIBLE);
                    imgmenu.setVisibility(View.VISIBLE);
                    imgin.clearAnimation();
                    imgtt.clearAnimation();
                    imgins.clearAnimation();
                    imgyt.clearAnimation();
                    imgfb.clearAnimation();
                    imggplus.clearAnimation();
                    imgmenu.clearAnimation();
                    //headerlayout.clearAnimation();

                    if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

                        txtlocation.setTextSize(15);
                        txtdate.setTextSize(15);
                        txttime.setTextSize(15);

                    } else {

                        txtlocation.setTextSize(11);
                        txtdate.setTextSize(11);
                        txttime.setTextSize(11);
                    }

                } else {
                    Log.i("LOG", "2");

                    header = 0;
                    runOnUiThread(new Runnable() {

                        @SuppressLint("NewApi")
                        @Override
                        public void run() {

                            imgin.startAnimation(inanim2);
                            imgtt.startAnimation(inanim2);
                            imgins.startAnimation(inanim2);
                            imgyt.startAnimation(inanim2);
                            imgfb.startAnimation(inanim2);
                            imggplus.startAnimation(inanim2);
                            imgmenu.startAnimation(inanim2);
                            headerlayout.startAnimation(inanim);
                            imgin.setVisibility(View.GONE);
                            imgtt.setVisibility(View.GONE);
                            imgins.setVisibility(View.GONE);
                            imgyt.setVisibility(View.GONE);
                            imgfb.setVisibility(View.GONE);
                            imggplus.setVisibility(View.GONE);
                            imgmenu.setVisibility(View.GONE);
                        }
                    });
                    btnheader.setBackgroundResource(R.drawable.down);

                    if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
                        txtlocation.setTextSize(19);
                        txtdate.setTextSize(19);
                        txttime.setTextSize(19);

                    } else {
                        txtlocation.setTextSize(14);
                        txtdate.setTextSize(14);
                        txttime.setTextSize(14);

                    }

                }

            }
        });
        txtlocation.setTypeface(iranSans);
        txtdate.setTypeface(iranSans);
        txttime.setTypeface(iranSans);
        txtCurrent.setTypeface(iranSans);
        txtNext.setTypeface(iranSans);
        txt1.setTypeface(iranSans);
        txt2.setTypeface(iranSans);
        txtcopyright.setTypeface(iranSans);
        pDialog = new TransparentProgressDialog(MainActivity.this, R.drawable.loading1);

        imgref.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!mute) {
                    lastVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

                    remoteViews.setImageViewResource(R.id.img_notifi_mute, R.drawable.vol_off);
                    notificationManager.notify(0, notification);


                    imgref.setImageResource(R.drawable.vol_off);
                    mute = true;
                    mVisualizerView.release();
                    Log.i("LOG imgref", "off");
                } else {
                    audio.setStreamVolume(AudioManager.STREAM_MUSIC, lastVolume, 0);
                    remoteViews.setImageViewResource(R.id.img_notifi_mute, R.drawable.vol_on);
                    notificationManager.notify(0, notification);


                    // audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                    imgref.setImageResource(R.drawable.vol_on);
                    mute = false;
                   // mVisualizerView.release();
                    if (isplaying) {
                        mVisualizerView.link(exoPlayer, mainAudioSessionId);
                        addLineRenderer();
                    }


                    Log.i("LOG imgref", "on");
                }


            }
        });


        imgoff.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (isplaying) {
                            isplaying = false;
                         playerControl.pause();
                           //exoPlayer.setPlayWhenReady(false);
                       // exoPlayer.release();
                          // exoPlayer = null;

                        imgoff.setImageResource(R.drawable.play);
                         mVisualizerView.release();
                        remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.play);
                        notificationManager.notify(0, notification);
                        mVisualizerView.release();

                        Log.i("imgoff","finished not null");


                    } else {
                        isplaying=true;
                        imgoff.setImageResource(R.drawable.pause);
                        remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.pause);
                        notificationManager.notify(0, notification);
                       // mVisualizerView.release();
                        Log.i("imgoff","finished  null +" + String.valueOf(mute));

                        new init().execute();


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("imgoff",e.toString());

                    Log.i("LOG", "Error : " + e);
                }
            }
        });


        Handler handlera = new Handler();
        handlera.postDelayed(new Runnable() {


            @Override
            public void run() {
                new init().execute();

            }
        }, 1000);


        mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String str = intent.getStringExtra("mode");

//            Catching The play_pause ImageViews & textViews:

                if (str.equals("current")) {
                    Log.i("Log", "current");
                    changeNotificationTitle(true);


                } else if (str.equals("next")) {
                    Log.i("Log", "next");
                    changeNotificationTitle(true);
                }

                if (str.equals("play")) {

                    try {
                       Log.i("logremoteplay" , String.valueOf(isplaying));
                        if (isplaying) {
                            isplaying=false;
                            playerControl.pause();
                            Log.i ("stop stop","did");

                         //   exoPlayer = null;

                            Log.i ("stop null","did");
                            imgoff.setImageResource(R.drawable.play);
                            remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.play);
                                 mVisualizerView.release();

                            remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.play);
                            notificationManager.notify(0, notification);
                        } else {
                            isplaying=true;
                            Log.i ("stop preinit","did");
                            new init().execute();
                            Log.i ("stop after init","did");
                            imgoff.setImageResource(R.drawable.pause);
                            remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.pause);
                            notificationManager.notify(0, notification);



                        }
                    } catch (Exception e) {


                        Log.i("LOG", "Error : " + e);
                    }

                    //   playMedia(true);

                }
                if (str.equals("mute")) {

                    if (!mute) {
                        lastVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                        // audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                        imgref.setImageResource(R.drawable.vol_off);
                        remoteViews.setImageViewResource(R.id.img_notifi_mute, R.drawable.vol_off);
                        notificationManager.notify(0, notification);
                        mute = true;
                        mVisualizerView.release();
                        Log.i("LOG imgref", "off");
                    } else {
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, lastVolume, 0);
                        // audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                        remoteViews.setImageViewResource(R.id.img_notifi_mute, R.drawable.vol_on);

                        imgref.setImageResource(R.drawable.vol_on);
                        notificationManager.notify(0, notification);
                        mute = false;
                        if (isplaying) {
                            mVisualizerView.link(exoPlayer, mainAudioSessionId);
                            addLineRenderer();
                        }
                        Log.i("LOG imgref", "on");

                    }


                }


            }
        };

        registerReceiver(mReciver, new IntentFilter("ActionPlay"));
        registerReceiver(mReciver, new IntentFilter("ActionMute"));

        Shownotification("");
    }


    private void showAlertDialogExit() {

        final Dialog di = new Dialog(MainActivity.this);
        di.requestWindowFeature(Window.FEATURE_NO_TITLE);
        di.setContentView(R.layout.dialog_about_us);
        di.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        di.show();

        TextView txtHeader = (TextView) di.findViewById(R.id.txt_header);
        TextView txtAbout = (TextView) di.findViewById(R.id.txt_about);

        txtHeader.setTypeface(iranSans);
        txtAbout.setTypeface(iranSans);
    }


    class GetCurrent extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... args) {
            try {
                Log.i("LOG current", "1");
                URL url = new URL(url_current);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Log.i("LOG current", "2");
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("data");

                for (int i = 0; i < nodeList.getLength(); i++) {

                    Node node = nodeList.item(i);
                    Element fstElmnt = (Element) node;

                    NodeList nameList1 = fstElmnt.getElementsByTagName("prog_name");
                    Element nameElement1 = (Element) nameList1.item(0);
                    nameList1 = nameElement1.getChildNodes();
                    programName = nameList1.item(0).getNodeValue();

                    NodeList nameList2 = fstElmnt.getElementsByTagName("cat_name");
                    Element nameElement2 = (Element) nameList2.item(0);
                    nameList2 = nameElement2.getChildNodes();
                    categoryName = nameList2.item(0).getNodeValue();

                }
            } catch (Exception e) {
                Log.i("LOG current", "3 e : " + e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "اطلاعات برنامه فعلی قابل مشاهده نیست", Toast.LENGTH_LONG).show();
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
                    currentPlayForNotification=categoryName + "\n" + programName;
                    txtCurrent.setText(categoryName + "\n" + programName);
                }
            });
        }
    }


    class GetNext extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(String... args) {
            try {

                Log.e("LOG", "1");
                URL url = new URL(url_next);
                Log.e("LOG", "2");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                Log.e("LOG", "3");
                DocumentBuilder db = dbf.newDocumentBuilder();
                Log.e("LOG", "4");
                Document doc = db.parse(new InputSource(url.openStream()));
                Log.e("LOG", "5");
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("data");

                Log.e("LOG", "6");
                for (int i = 0; i < nodeList.getLength(); i++) {

                    Node node = nodeList.item(i);
                    Element fstElmnt = (Element) node;

                    Log.e("LOG", "1");
                    NodeList nameList1 = fstElmnt.getElementsByTagName("prog_name");
                    Element nameElement1 = (Element) nameList1.item(0);
                    nameList1 = nameElement1.getChildNodes();
                    programNameNext = nameList1.item(0).getNodeValue();

                    NodeList nameList2 = fstElmnt.getElementsByTagName("cat_name");
                    Element nameElement2 = (Element) nameList2.item(0);
                    nameList2 = nameElement2.getChildNodes();
                    categoryNameNext = nameList2.item(0).getNodeValue();
                    Log.e("LOG", "1");

                }
            } catch (Exception e) {
                Log.i("LOG", "3 e : " + e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "اطلاعات برنامه بعد قابل مشاهده نیست", Toast.LENGTH_LONG).show();
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
                    nextPlayForNotification=categoryNameNext + "\n" + programNameNext;
                    txtNext.setText(categoryNameNext + "\n" + programNameNext);
                }
            });

        }
    }

    //=======================================================================================================

    public void Shownotification(String notificationTitle) {



        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notification = new Notification(R.drawable.logo, null, System.currentTimeMillis());
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_view);

        PendingIntent contentIntent =  PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Intent notoficationIntent = new Intent(getApplicationContext(), MainActivity.class);

        pendingNotificationIntent = PendingIntent.getBroadcast(this, 0, notoficationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.contentView = remoteViews;
        notification.contentIntent = pendingNotificationIntent;

       // boolean playing = false;

       // Set Icon to Notification Image Button
        int imgPlayPause = (isplaying ? R.drawable.pause : R.drawable.play);
        notification.contentView.setImageViewResource(R.id.img_notifi_play_pause, imgPlayPause);

        int imgmute = (mute ? R.drawable.vol_off : R.drawable.vol_on);
        notification.contentView.setImageViewResource(R.id.img_notifi_mute, imgmute);


//      playing the track
        Intent intentPlay = new Intent("ActionPlay");
        intentPlay.putExtra("mode", "play");
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 1, intentPlay, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_notifi_play_pause, pendingIntentPlay);




//         //Intent muteButton
        Intent intentMute = new Intent("ActionMute");
        intentMute.putExtra("mode","mute");
        PendingIntent pendingIntentMute = PendingIntent.getBroadcast(this, 4, intentMute, 0);
        remoteViews.setOnClickPendingIntent(R.id.img_notifi_mute, pendingIntentMute);



        Intent intentCurrent = new Intent(this, mReciver.getClass());
        intentCurrent.putExtra("mode", "current");
        PendingIntent currentIntent = PendingIntent.getBroadcast(this, 3, intentCurrent, 0);
        remoteViews.setOnClickPendingIntent(R.id.txt_notifi_current, currentIntent);

        Intent intentNext = new Intent(this,mReciver.getClass());
        intentNext.putExtra("mode", "next");
        PendingIntent nextIntent = PendingIntent.getBroadcast(this, 2, intentNext, 0);
        remoteViews.setOnClickPendingIntent(R.id.txt_notifi_next, nextIntent);


        notificationManager.notify(0, notification);

    }
//    Making The Broadcast:

//    public static class Receiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String str = intent.getStringExtra("mode");
////            Catching The play_pause ImageViews & textViews:
//            if (str.equals("current")) {
//                Log.i("Log", "current");
//                changeNotificationTitle(true);
//
//
//            } else if (str.equals("next")) {
//                Log.i("Log", "next");
//                changeNotificationTitle(true);
//            }
//
//            if (str.equals("play")) {
//                playMedia(true);
//
//            }
//        }
//    }

    //========================================================================================================

    public void changeNotificationTitle(Boolean changeNotificationTitle) {


        String nameNext = nextPlayForNotification;
        String nameCurrent = currentPlayForNotification;

    //    if (changeNotificationTitle) {

            remoteViews.setTextViewText(R.id.txt_notifi_next, nameNext);
            notificationManager.notify(0, notification);

     //   } else {

            remoteViews.setTextViewText(R.id.txt_notifi_current, nameCurrent);
            notificationManager.notify(0, notification);
      //  }
    }

    //=====================================================================================================

//    public static void playMedia(Boolean updateNotification) throws IOException {
//
//        if (isplaying) {
//            isplaying = false;
//            Log.i("play", "log");
//            remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.play);
//
//            if (exoPlayer.getPlaybackState()== exoPlayerIsPlaying) {
//                mVisualizerView.release();
//                exoPlayer.stop();
//                //multiPlayer = null;
//
//
//            }
//            Log.i("play", "Log");
//            notificationManager.notify(0, notification);
//        } else {
//            isplaying = true;
//            //multiPlayer = new MultiPlayer((PlayerCallback) context, 1500, 700);
////           // multiPlayer.setPlayerCallback(MainActivity.this);
//           if (multiPlayer!=null) {
//               multiPlayer.playAsync("http://stream.mojiran.com/;stream.mp3");
//               play.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//
//
//
//           }
//            remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.pause);
//            notificationManager.notify(0, notification);
//
//
//        }
//
//
//    }

//===========================================================================================================
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("destroy","worked");
        clearNotification();

        super.onBackPressed();


        if (exoPlayer != null) {
            mVisualizerView.release();
            exoPlayer.stop();
            exoPlayer = null;

        }

    }

     @Override
     protected void onStop() {
         super.onStop();
         Log.i("destroy","onStop");
     }

     @Override
     protected void onPause() {
         super.onPause();
         Log.i("destroy","onPause");
     }

     private void clearNotification(){
         NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
         notificationManager.cancel(0);
     }



     private void addLineRenderer() {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.rgb(0, 0, 0));

        Paint lineFlashPaint = new Paint();
        lineFlashPaint.setStrokeWidth(0.1f);
        lineFlashPaint.setAntiAlias(false);
        lineFlashPaint.setColor(Color.rgb(0, 0, 0));
        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
        mVisualizerView.addRenderer(lineRenderer);
    }

//=============================================================================================================
//    @Override
//    @Override
//    public void playerStarted() {
//        Shownotification("");
//        changeNotificationTitle(true);
//        isplaying = true;
//        remoteViews.setImageViewResource(R.id.img_notifi_play_pause, R.drawable.pause);
//        Log.i("LOG remote pause", "Log");
//        notificationManager.notify(0, notification);
//
//    }
//
//
////=================================================================================================================
//    @Override
//    public void playerPCMFeedBuffer(boolean b, int i, int i1) {
//
//    }
//
//
//    @Override
//    public void playerStopped(int i) {
//        notificationManager.notify(0, notification);
//
//    }
//
//
//    @Override
//    public void playerException(Throwable throwable) {
//
//    }
//
//
//    @Override
//    public void playerMetadata(String s, String s1) {
//
//    }
//
//
//    @Override
//    public void playerAudioTrackCreated(AudioTrack audioTrack) {
//        //Log.d("log", "");
//
//    }


    private class TransparentProgressDialog extends Dialog {

        private ImageView iv;


        public TransparentProgressDialog(Context context, int resourceIdOfImage) {
            super(context, R.style.AppTheme);
            WindowManager.LayoutParams wlmp = getWindow().getAttributes();
            wlmp.gravity = Gravity.CENTER_HORIZONTAL;
            getWindow().setAttributes(wlmp);
            setTitle(null);
            setCancelable(false);
            setOnCancelListener(null);
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            iv = new ImageView(context);
            iv.setImageResource(resourceIdOfImage);
            layout.addView(iv, params);
            addContentView(layout, params);
        }


        @Override
        public void show() {
            super.show();
            RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
            anim.setInterpolator(new LinearInterpolator());
            anim.setRepeatCount(Animation.INFINITE);
            anim.setDuration(3000);
            iv.setAnimation(anim);
            iv.startAnimation(anim);
        }
    }


    class init extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }


        @Override
        protected String doInBackground(String... args) {




                playerControl.start();

              //  exoPlayer.setPlayWhenReady(true);

                int o = exoPlayer.getPlaybackState();






//            PlayerControl playerControl=new PlayerControl(exoPlayer);
//            int o=playerControl.getAudioSessionId();
//            audioSessionId=playerControl.getAudioSessionId();
           // multiPlayer = new MultiPlayer(MainActivity.this, 1500, 700);
            //multiPlayer.playAsync("http://stream.mojiran.com/;stream.mp3");

            //try {
              //  play.setDataSource("http://stream.mojiran.com/;stream.mp3");
               // play.prepare();

              //  play.start();
               // float f=(float) 0.0001;
               // play.setVolume(0,f);

           // } catch (IOException e) {
             //   e.printStackTrace();
           // }


            isplaying = true;
            Shownotification("");
            changeNotificationTitle(true);
            //playMedia(true);
            return null;

        }


        @Override
        protected void onPostExecute(String file_url) {
           if (!mute) {
               Log.i("mute" ,String.valueOf(mute));
               mVisualizerView.link(exoPlayer, mainAudioSessionId);
               addLineRenderer();
           }


        }
    }


    private void gettimenoww() {

        Time time = new Time();
        time.setToNow();

        String minute;
        String hour;

        hour = "" + time.hour;
        minute = "" + time.minute;

        if (time.minute < 10) {
            minute = "0" + time.minute;
        }

        if (time.hour < 10) {
            hour = "0" + time.hour;
        }

        txttime.setText(hour + " : " + minute);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                gettimenoww();
            }
        }, 60000);
    }

    //=================================================================================================

//  Sync Vol_off image with phone volume when press volume up or down on phone

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) || (keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
            Log.i("LOG key", "KEY");
            if (volume == 0) {
                Log.i("LOG key", "OFF");
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                imgref.setImageResource(R.drawable.vol_off);
                remoteViews.setImageViewResource(R.id.img_notifi_mute, R.drawable.vol_off);
                notificationManager.notify(0, notification);
                mVisualizerView.release();

                mute = true;
            } else {
                Log.i("LOG key", "ON");
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);
                imgref.setImageResource(R.drawable.vol_on);
                if (mute)
                {
                    mVisualizerView.link(exoPlayer , mainAudioSessionId);
                    addLineRenderer();
                }

                mute = false;
                lastVolume = volume;
                remoteViews.setImageViewResource(R.id.img_notifi_mute, R.drawable.vol_on);
                notificationManager.notify(0, notification);



            }
            return true;
        }

        return super.onKeyUp(keyCode, event);

    }




    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج دو مرتبه کلیک نمایید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
    public void forceerror (View view){

        throw new RuntimeException("This is my exception");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}