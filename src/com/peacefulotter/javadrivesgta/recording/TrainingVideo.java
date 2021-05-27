package com.peacefulotter.javadrivesgta.recording;

import java.util.ArrayDeque;
import java.util.Queue;

public class TrainingVideo
{
    private final Queue<TrainingImage> video = new ArrayDeque<>();

    public int getSize() { return video.size(); }

    public void addImage( TrainingImage image )
    {
        video.add( image );
    }

    public TrainingImage popImage() { return video.poll(); }
}
