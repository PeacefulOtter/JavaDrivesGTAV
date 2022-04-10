package com.peacefulotter.javadrivesgta.recording;

import com.peacefulotter.javadrivesgta.screen.Renderer;
import com.peacefulotter.javadrivesgta.img_processing.ImageConverter;
import javafx.scene.image.Image;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

public class TrainingVideo extends Renderer
{
    private final Queue<TrainingImage> video = new ArrayDeque<>();

    public int getSize() { return video.size(); }

    public void addImage( TrainingImage image )
    {
        video.add( image );
    }

    public TrainingImage popImage() { return video.poll(); }

    @Override
    public Image render()
    {
        TrainingImage trainingImage = video.poll();
        if ( trainingImage == null ) return null;

        BufferedImage image = trainingImage.getImage();
        return ImageConverter.Buffered2FX( image );
    }
}
