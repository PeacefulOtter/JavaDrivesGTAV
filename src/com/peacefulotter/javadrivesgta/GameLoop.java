package com.peacefulotter.javadrivesgta;

import com.peacefulotter.javadrivesgta.io.IOHandler;
import com.peacefulotter.javadrivesgta.utils.Time;
import javafx.animation.AnimationTimer;


public class GameLoop extends AnimationTimer
{
    private static final double FRAMES_CAP = 240;
    private static final double FRAME_TIME = 1.0 / FRAMES_CAP;

    private final Screens screens;
    private final Task task;
    private final IOHandler handler;

    private int frames;
    private double framesCounter, lastTime, unprocessedTime;

    public GameLoop( Screens screens, Task task, IOHandler handler )
    {
        this.screens = screens;
        this.task = task;
        this.handler = handler;
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
            // get inputs
            handler.pollInput();
            // render the screen
            screens.render();
            // do the task
            task.action( handler.getAcceleration(), handler.getDirection() );
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