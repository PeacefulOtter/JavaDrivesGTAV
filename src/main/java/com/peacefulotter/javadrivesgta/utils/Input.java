package com.peacefulotter.javadrivesgta.utils;


import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.CHAR_UNDEFINED;

public class Input
{
    public static void MediaKeyForward(){
        GlobalScreen.postNativeEvent(new NativeKeyEvent(2401,0,176,57369,CHAR_UNDEFINED));

    }
    public static void MediaKeyBack(){
        GlobalScreen.postNativeEvent(new NativeKeyEvent(2401,0,177,57360,CHAR_UNDEFINED));

    }
    public static void MediaKeyPause(){
        GlobalScreen.postNativeEvent(new NativeKeyEvent(2401,0,179,57378,CHAR_UNDEFINED));

    }
}
