package com.peacefulotter.javadrivesgta.img_processing;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

public class ImageTransformation
{
    private static final CannyEdgeDetector detector = new CannyEdgeDetector();

    static {
        detector.setLowThreshold(0.8f);
        detector.setHighThreshold(1f);
    }

    public static BufferedImage CannyFilter( BufferedImage img )
    {
        detector.setSourceImage(img);
        detector.process();
        return detector.getEdgesImage();
    }

    /**
     * FIXME: unfinished
     * @param img
     * @param delta
     * @return
     */
    public static BufferedImage HoughLinesTransform( BufferedImage img, double delta )
    {
        BufferedImage canny = CannyFilter( img );
        int imageWidth = img.getWidth();
        int imageHeight = img.getHeight();
        BufferedImage accumulatorMatrix = new BufferedImage( imageWidth, imageHeight, img.getType(), (IndexColorModel) img.getColorModel() );
        for ( int x = 0; x < imageWidth; x++ )
        {
            for ( int y = 0; y < imageHeight; y++ )
            {
                for ( int theta = 0; theta < 180; theta += delta )
                {
                    double thetaRad = theta * Math.PI / 180;
                    double rho = x * Math.cos( thetaRad ) + y * Math.sin( thetaRad );
                    int matX = (int) (rho * Math.cos(thetaRad));
                    int matY = (int) (rho * Math.sin(thetaRad));
                    accumulatorMatrix.setRGB( matX, matY, accumulatorMatrix.getRGB( matX, matY ) + 1 );
                }
            }
        }
        return accumulatorMatrix;
    }
}
