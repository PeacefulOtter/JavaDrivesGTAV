package com.peacefulotter.javadrivesgta.io;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class KeyboardHandler extends IOHandler implements NativeKeyListener
{
    private static final Set<KeyCodes> keys = new HashSet<>();

    private enum KeyCodes {
        UP( NativeKeyEvent.VC_Z),
        DOWN(NativeKeyEvent.VC_S),
        LEFT(NativeKeyEvent.VC_Q),
        RIGHT(NativeKeyEvent.VC_D);
        public int key;

        KeyCodes(int key) { this.key = key; }

        public static KeyCodes from( int keyCode )
        {
            for ( KeyCodes code: values() )
            {
                if ( code.key == keyCode )
                    return code;
            }

            return null;
        }
    }

    public KeyboardHandler() { init(); }

    public void init()
    {
        // Clear previous logging configurations.
        LogManager.getLogManager().reset();
        // Get the logger for "org.jnativehook" and set the level to off.
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel( Level.OFF);

        // JNativeHook initialization
        try {
            GlobalScreen.registerNativeHook();
        }
        catch ( NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }

        GlobalScreen.addNativeKeyListener( this );
    }

    public static void closeHook()
    {
        try
        {
            GlobalScreen.unregisterNativeHook();
        }
        catch ( NativeHookException nativeHookException )
        {
            nativeHookException.printStackTrace();
        }
    }


    public void nativeKeyPressed( NativeKeyEvent e )
    {
        // System.out.println( "Key Pressed: " + e.getKeyCode() );

        KeyCodes kc = KeyCodes.from(e.getKeyCode());
        if ( kc != null )
            keys.add( kc );
    }

    public void nativeKeyReleased( NativeKeyEvent e )
    {
        KeyCodes kc = KeyCodes.from(e.getKeyCode());
        if ( kc != null )
            keys.remove( kc );
    }

    public void nativeKeyTyped( NativeKeyEvent e )
    {
        System.out.println( "Key Typed: " + e.getKeyCode() );
    }

    @Override
    public void pollInput()
    {
        setAcceleration( 0 );
        setDirection( 0 );

        for ( KeyCodes code : keys )
        {
            switch ( code )
            {
                case UP:
                    setAcceleration( 1 );
                    break;
                case DOWN:
                    setAcceleration( -1 );
                    break;
                case LEFT:
                    setDirection( -1 );
                    break;
                case RIGHT:
                    setDirection( 1 );
                    break;
                default:
                    break;
            }
        }
    }
}
