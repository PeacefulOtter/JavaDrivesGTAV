package com.peacefulotter.javadrivesgta.io;

import com.peacefulotter.javadrivesgta.img_processing.ImageConverter;
import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.recording.TrainingImage;
import com.peacefulotter.javadrivesgta.recording.TrainingVideo;
import com.peacefulotter.javadrivesgta.utils.Settings;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.peacefulotter.javadrivesgta.utils.Settings.CAPTURE_WIDTH;

public class FileHandler
{
    private InputStream getStream( String fileName )
    {
        return Objects.requireNonNull( getClass().getResourceAsStream( fileName ) );
    }

    public static List<Map<String, Matrix2D>> loadTrainingData( int from, int to )
    {
        List<Map<String, Matrix2D>> data = new ArrayList<>();

        for ( int i = from; i < to; i++ )
        {
            TrainingVideo video = importFromFile( "res/dataset/out" + i + ".txt" );
            for ( int j = 0; j < video.getSize(); j++ )
            {
                Map<String, Matrix2D> sample = new HashMap<>();
                TrainingImage image = video.popImage();
                Matrix2D xMat = ImageConverter.Buffered2Matrix( image.getImage() );
                sample.put( "X", xMat );
                Matrix2D yMat = new Matrix2D( 1, 2 );
                yMat.setAt( 0, 0, image.getAcceleration() );
                yMat.setAt( 0, 1, image.getDirection() );
                sample.put( "Y", yMat);
                data.add( sample );
            }
        }

        return data;
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
