package com.glorystar.ledtest;

public class MainActivity {
    static {
        System.loadLibrary("ledsetJni");
    }

    public static native void setLedState(int led, int state);
}
