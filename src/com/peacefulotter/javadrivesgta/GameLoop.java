package com.peacefulotter.javadrivesgta;

import com.peacefulotter.javadrivesgta.io.ControllerHandler;
import com.peacefulotter.javadrivesgta.io.IOHandler;
import com.peacefulotter.javadrivesgta.io.KeyboardHandler;
import com.peacefulotter.javadrivesgta.recording.Recording;
import com.peacefulotter.javadrivesgta.utils.Settings;
import com.peacefulotter.javadrivesgta.utils.Time;
import javafx.animation.AnimationTimer;


public class GameLoop extends AnimationTimer
{
    private static final double FRAMES_CAP = 240;
    private static final double FRAME_TIME = 1.0 / FRAMES_CAP;

    private final Monitor monitor;
    private IOHandler handler;

    private int frames;
    private double framesCounter, lastTime, unprocessedTime;

    private static final Recording recording = new Recording();

    public GameLoop( Monitor monitor )
    {
        this.monitor = monitor;

        if ( Settings.POLL_KEYBOARD )
            handler = new KeyboardHandler();
        else if ( Settings.POLL_CONTROLLER )
            handler = new ControllerHandler();

        if ( handler != null )
            handler.init();
    }

    @Override
    public void start()
    {
        frames = 0;
        framesCounter = 0;
        lastTime = Time.getNanoTime();
        unprocessedTime = 0; // time in seconds since the start

        super.start();
    }

    @Override
    public void handle( long now )
    {
        boolean render = false;

        double startTime = Time.getNanoTime(); // delta between two updates
        double passedTime = startTime - lastTime;
        lastTime = startTime;
        unprocessedTime += passedTime / Time.SECOND;
        framesCounter += passedTime;


        while ( unprocessedTime > FRAME_TIME )
        {
            unprocessedTime -= FRAME_TIME;
            render = true;

            if (framesCounter >= Time.SECOND)
            {
                frames = 0;
                framesCounter = 0;
            }
        }

        if ( render )
        {
            frames++;

            if ( handler != null )
            {
                handler.pollInput();
                if ( Settings.RECORD_CAPTURE && frames % Settings.CAPTURE_FREQUENCY == 0 )
                {
                    recording.addImage( monitor.render(), handler.getAcceleration(), handler.getDirection() );
                    return;
                }
            }

            monitor.render();
        } else
        {
            try
            {
                Thread.sleep( 1 );
            } catch ( InterruptedException e )
            {
                e.printStackTrace();
            }
        }
    }
}