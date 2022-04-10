package com.peacefulotter.javadrivesgta.recording;

import com.peacefulotter.javadrivesgta.io.FileHandler;
import com.peacefulotter.javadrivesgta.task.Task;
import com.peacefulotter.javadrivesgta.utils.Settings;

import java.awt.image.BufferedImage;
import java.lang.reflect.RecordComponent;

public class Recording extends Task
{
    private int files; // number of files saved
    private final TrainingVideo video;

    public Recording() {
        this.files = 0;
        this.video = new TrainingVideo();
    }

    public TrainingVideo getVideo() { return video; }

    public void loadVideos( int from, int to )
    {
        for ( int i = from; i < to; i++ )
            addVideo(  FileHandler.importFromFile( "res/dataset/out" + i + ".txt" ) );
    }

    private void saveVideoToFile()
    {
        String fileName = "out" + files + ".txt";
        FileHandler.writeToFile( video, fileName );
        files += 1;
    }

    public void addImage( BufferedImage image, int acceleration, int direction )
    {
        TrainingImage trainingImage = new TrainingImage();
        trainingImage.setImage( image );
        trainingImage.setAcceleration( acceleration );
        trainingImage.setDirection( direction );
        video.addImage( trainingImage );

        if ( video.getSize() >= Settings.MAX_IMAGES )
            saveVideoToFile();
    }

    public void addVideo( TrainingVideo video )
    {
        TrainingImage image;
        while ( (image = video.popImage()) != null )
        {
            this.video.addImage( image );
        }
    }

    @Override
    public void action() {

    }
}
