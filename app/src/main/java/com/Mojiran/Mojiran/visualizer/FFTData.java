/**
 * © 2016 RooyeKhat Media Co all rights reserved
 * Mojiran Project - Online Stream
 * url : http://rooyekhat.co//
 */
package com.Mojiran.Mojiran.visualizer;

// Data class to explicitly indicate that these bytes are the FFT of audio data
public class FFTData
{

    public FFTData(byte[] bytes)
    {
        this.bytes = bytes;
    }

    public byte[] bytes;
}
