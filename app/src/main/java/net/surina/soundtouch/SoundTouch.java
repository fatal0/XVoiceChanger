////////////////////////////////////////////////////////////////////////////////
///
/// Example class that invokes native SoundTouch routines through the JNI
/// interface.
///
/// Author        : Copyright (c) Olli Parviainen
/// Author e-mail : oparviai 'at' iki.fi
/// WWW           : http://www.surina.net
///
////////////////////////////////////////////////////////////////////////////////
//
// $Id: SoundTouch.java 211 2015-05-15 00:07:10Z oparviai $
//
////////////////////////////////////////////////////////////////////////////////

package net.surina.soundtouch;

public final class SoundTouch
{
    // Native interface function that returns SoundTouch version string.
    // This invokes the native c++ routine defined in "soundtouch-jni.cpp".
    public native final static String getVersionString();

    private native final void setTempoChange(long handle, float tempo);

    private native final void setPitchSemiTones(long handle, float pitch);

    private native final void setRateChange(long handle, float rate);

    private native final void setSampleRate(long handle, int rate);

    private native final void setChannels(long handle, int channel);

    private native final int putSamples(long handle, short[] data, int size);

    private native final short[] receiveSamples(long handle);

    private native final int flush(long handle);

    public native final static String getErrorString();

    private native final static long newInstance();

    private native final void deleteInstance(long handle);

    long handle = 0;


    public SoundTouch()
    {
        handle = newInstance();
    }


    public void close()
    {
        deleteInstance(handle);
        handle = 0;
    }


    public void setTempoChange(float tempo)
    {
        setTempoChange(handle, tempo);
    }


    public void setPitchSemiTones(float pitch)
    {
        setPitchSemiTones(handle, pitch);
    }

    public void setRateChange(float rate)
    {
        setRateChange(handle, rate);
    }

    public void setSampleRate(int rate)
    {
        setSampleRate(handle, rate);
    }

    public void setChannels(int channel) {
        setChannels(handle, channel);
    }


    public int putSamples(short[] data, int size)
    {
        return putSamples(handle, data, size);
    }

    public short[] receiveSamples()
    {
        return receiveSamples(handle);
    }

    public int flush()
    {
        return flush(handle);
    }

    // Load the native library upon startup
    static
    {
        //System.loadLibrary("soundtouch");
        System.load("/data/data/gg.xvoicechanger/files/libsoundtouch.so");
    }
}