package com.peacefulotter.javadrivesgta.img_processing;

import javafx.embed.swing.SwingFXUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

import static com.peacefulotter.javadrivesgta.img_processing.ImageConverter.AWT2Buffered;
import static com.peacefulotter.javadrivesgta.utils.Settings.CAPTURE_HEIGHT;
import static com.peacefulotter.javadrivesgta.utils.Settings.CAPTURE_WIDTH;

public class ImageEffect
{
    public static void makeGray( BufferedImage img )
    {
        for (int x = 0; x < CAPTURE_WIDTH; ++x)
        {
            for ( int y = 0; y < CAPTURE_HEIGHT; ++y )
            {
                int rgb = img.getRGB( x, y );
                int r = ( rgb >> 16 ) & 0xFF;
                int g = ( rgb >> 8 ) & 0xFF;
                int b = ( rgb & 0xFF );

                // Normalize and gamma correct:
                double rr = Math.pow( r / 255.0, 2.2 );
                double gg = Math.pow( g / 255.0, 2.2 );
                double bb = Math.pow( b / 255.0, 2.2 );

                // Calculate luminance:
                double lum = 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;

                // Gamma compand and rescale to byte range:
                int grayLevel = (int) ( 255.0 * Math.pow( lum, 1.0 / 2.2 ) );
                int gray = ( grayLevel << 16 ) + ( grayLevel << 8 ) + grayLevel;
                img.setRGB( x, y, gray );
            }
        }
    }

    public static void makeGray( java.awt.Image image )
    {
        ImageFilter filter = new GrayFilter(false, 50);
        ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
        java.awt.Image grayAWTImage = Toolkit.getDefaultToolkit().createImage(producer);
        javafx.scene.image.Image grayFXImage = SwingFXUtils.toFXImage( AWT2Buffered( grayAWTImage ), null);
    }

    public static void clearLanes( BufferedImage image)
    {
        Color waypointColor = new Color( 208, 94, 214 );

        for (int x = 0; x < CAPTURE_WIDTH; x++) {
            for (int y = 0; y < CAPTURE_HEIGHT; y++) {
                // REMOVE THE YELLOW LANES
                Color color = new Color( image.getRGB( x, y ) );
                int drw = Math.abs(color.getRed()   - waypointColor.getRed());
                int dgw = Math.abs(color.getGreen() - waypointColor.getGreen());
                int dbw = Math.abs(color.getBlue()  - waypointColor.getBlue());

                int delta = 60;
                if (drw < delta && dgw < delta && dbw < delta )
                    image.setRGB( x, y, new Color( 255, 255, 255 ).getRGB() );
                else
                    image.setRGB( x, y, 0 );
            }
        }
    }

    /**
     * Converts an image to a binary one based on given threshold
     * @param image the image to convert. Remains untouched.
     * @param threshold the threshold in [0,255], suggested value: 128
     * @return a new BufferedImage instance of TYPE_BYTE_GRAY with only 0'S and 255's
     */
    public static BufferedImage toBlackAndWhite(BufferedImage image, int threshold)
        {
        BufferedImage result = new BufferedImage(CAPTURE_WIDTH, CAPTURE_HEIGHT, BufferedImage.TYPE_BYTE_GRAY);
        result.getGraphics().drawImage(image, 0, 0, null);
        WritableRaster raster = result.getRaster();
        int[] pixels = new int[CAPTURE_WIDTH];
        for (int y = 0; y < CAPTURE_HEIGHT; y++)
        {
            raster.getPixels(0, y, CAPTURE_WIDTH, 1, pixels);
            for (int i = 0; i < pixels.length; i++)
            {
                if (pixels[i] < threshold) pixels[i] = 0;
                else pixels[i] = 255;
            }
            raster.setPixels(0, y, CAPTURE_WIDTH, 1, pixels);
        }
        return result;
    }
}
