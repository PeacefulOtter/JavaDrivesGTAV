package com.peacefulotter.javadrivesgta.io;

import com.peacefulotter.javadrivesgta.processing.ImageConverter;
import com.peacefulotter.javadrivesgta.recording.TrainingImage;
import com.peacefulotter.javadrivesgta.recording.TrainingVideo;
import com.peacefulotter.javadrivesgta.utils.Settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import static com.peacefulotter.javadrivesgta.utils.Settings.CAPTURE_WIDTH;

public class FileHandler
{
    private InputStream getStream( String fileName )
    {
        return Objects.requireNonNull( getClass().getResourceAsStream( fileName ) );
    }

    public static TrainingVideo importFromFile( String fileName )
    {
        int imageSize = CAPTURE_WIDTH * Settings.CAPTURE_HEIGHT * 3;
        TrainingVideo video = new TrainingVideo();

        try ( FileInputStream br = new FileInputStream( fileName ) )
        {
            int size = br.read();
            System.out.println("[FileHandler] (importFromFile) filename: " + fileName + ", size: " + size);
            byte[] img = new byte[ imageSize ];

            for ( int i = 0; i < size; i++ )
            {
                TrainingImage trainingImage = new TrainingImage();
                br.read( img, 0, imageSize );
                trainingImage.setImage( ImageConverter.Pixels2Buffered( img ) );
                trainingImage.setAcceleration( br.read() );
                trainingImage.setDirection( br.read() );
                video.addImage( trainingImage );
            }
        } catch ( IOException e )
        {
            e.printStackTrace();
        }

        return video;
    }

    public static void writeToFile( TrainingVideo video, String fileName )
    {
        if ( !Settings.RECORD_CAPTURE )
            return;

        int size = video.getSize();
        System.out.println("[FileHandler] (writeToFile) size: " + size);

        try ( FileOutputStream os = new FileOutputStream( "res/" + fileName ) )
        {
            os.write( size );
            TrainingImage image;
            for ( int i = 0; i < size; i++ )
            {
                image = video.popImage();
                os.write( ImageConverter.Buffered2Bytes( image.getImage() ) );
                os.write( image.getAcceleration() );
                os.write( image.getDirection() );
            }
        } catch ( IOException e )
        {
            e.printStackTrace();
        }

        System.out.println("done " + video.getSize());
    }
}
