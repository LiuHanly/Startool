package com.example.vod;

public class MainActivity {
    static {
        System.loadLibrary("iotest");
    }

    public static native void output(int tag);
    public static native int input(int tag);
}
