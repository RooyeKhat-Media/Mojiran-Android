/**
 * © 2016 RooyeKhat Media Co all rights reserved
 * Mojiran Project - Online Stream
 * url : http://rooyekhat.co//
 */
package com.Mojiran.Mojiran.visualizer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer.ExoPlayer;
import com.spoledge.aacdecoder.MultiPlayer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.Mojiran.Mojiran.visualizer.renderer.Renderer;
//import com.spoledge.aacdecoder.MultiPlayer;


/**
 * Â© 2016 RooyeKhat Media Co all rights reserved
 * A class that draws visualizations of data received from a {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture } and
 * {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
 */
public class VisualizerView extends View {

    private byte[]        mBytes;
    private byte[]        mFFTBytes;
    private Rect          mRect       = new Rect();
    private Visualizer    mVisualizer;

    private Set<Renderer> mRenderers;

    private Paint         mFlashPaint = new Paint();
    private Paint         mFadePaint  = new Paint();




    public VisualizerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);
        init();

    }


    public VisualizerView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }


    public VisualizerView(Context context)
    {
        this(context, null, 0);
    }


    private void init() {
        mBytes = null;
        mFFTBytes = null;

        mFlashPaint.setColor(Color.argb(122, 255, 255, 255));
        mFadePaint.setColor(Color.argb(100, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
        mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));

        mRenderers = new HashSet<Renderer>();
    }


    /**
     * Links the visualizer to a player
     *
     * @param player - MediaPlayer instance to link to
     */
    public void link(ExoPlayer player ,int id)
    {


        if (player == null)
        {
            throw new NullPointerException("Cannot link to null MediaPlayer");
        }

        try {

            // Create the Visualizer object and attach it to our media player.
            mVisualizer = new Visualizer(id);
            if ( !isEnabled()) {
                mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            } else {
                // setEnabled(false);
                // mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            }

            // Pass through Visualizer data to VisualizerView

            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {

                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {

                    updateVisualizer(bytes);
                }


                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
//
             //      updateVisualizerFFT(bytes);
                }
            };

            mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);

            // Enabled Visualizer and disable when we're done with the stream
            mVisualizer.setEnabled(true);

//            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
//            {
//              @Override
//              public void onCompletion(MediaPlayer mediaPlayer)
//              {
//                mVisualizer.setEnabled(false);
//              }
//            });

        }
        catch (Exception e) {

            Log.e("LOG", "ERROR : " + e);
            e.printStackTrace();
        }
    }


    public void addRenderer(Renderer renderer)
    {
        if (renderer != null)
        {
            mRenderers.add(renderer);
            //New invalidate();
            invalidate();
        }
    }


    public void clearRenderers()
    {
        mRenderers.clear();
    }


    /**
     * Call to release the resources used by VisualizerView. Like with the
     * MediaPlayer it is good practice to call this method
     */

    /*
     *   در کدهای قبل �?قط
     *   mVisualizer.release();
     *   وجود داشت
     */
    public void release()
    {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }


    /**
     * Pass data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
     *
     * @param bytes
     */
    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }


    /**
     * Pass FFT data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See {@link Visualizer.OnDataCaptureListener#onFftDataCapture }
     *
     * @param bytes
     */
    public void updateVisualizerFFT(byte[] bytes) {
        mFFTBytes = bytes;
        invalidate();
    }

    boolean mFlash = false;


    /**
     * Call this to make the visualizer flash. Useful for flashing at the start
     * of a song/loop etc...
     */
    public void flash() {
        mFlash = true;
        invalidate();
    }

    Bitmap mCanvasBitmap;
    Canvas mCanvas;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Create canvas once we're ready to draw
        mRect.set(0, 0, getWidth(), getHeight());

        if (mCanvasBitmap == null)
        {
            mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
        }
        if (mCanvas == null)
        {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        if (mBytes != null) {
            // Render all audio renderers
            AudioData audioData = new AudioData(mBytes);
            for (Renderer r: mRenderers)
            {
                r.render(mCanvas, audioData, mRect);
            }
        }

        if (mFFTBytes != null) {
            // Render all FFT renderers
            FFTData fftData = new FFTData(mFFTBytes);
            for (Renderer r: mRenderers)
            {
                r.render(mCanvas, fftData, mRect);
            }
        }

        // Fade out old contents
        mCanvas.drawPaint(mFadePaint);

        if (mFlash)
        {
            mFlash = false;
            mCanvas.drawPaint(mFlashPaint);
        }

        canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);

        //New invalidate();
        invalidate();
    }
}

