package com.peacefulotter.javadrivesgta.img_processing;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.peacefulotter.javadrivesgta.utils.Settings.CAPTURE_HEIGHT;
import static com.peacefulotter.javadrivesgta.utils.Settings.CAPTURE_WIDTH;

public class ImageConverter
{
    public static Matrix2D Buffered2Matrix( BufferedImage img )
    {
        return Matrix2D.applyFunc( (mat, i, j) -> img.getRGB( j, i ), CAPTURE_HEIGHT, CAPTURE_WIDTH );
    }

    public static byte[] Buffered2Bytes( BufferedImage img ) throws IOException
    {
        int area = CAPTURE_WIDTH * CAPTURE_HEIGHT * 3;
        byte[] byteData = new byte[area];

        int index = 0;
        for ( int x = 0; x < CAPTURE_WIDTH; x++ )
        {
            for ( int y = 0; y < CAPTURE_HEIGHT; y++ )
            {
                Color color = new Color( img.getRGB( x, y ) );
                byteData[index++] = (byte) color.getRed();
                byteData[index++] = (byte) color.getGreen();
                byteData[index++] = (byte) color.getBlue();
            }
        }

        return byteData;
    }

    private static int twosComplement( byte b )
    {
        int pixel = b;
        if ( pixel < 0 )
            pixel = -( ( pixel ^ 0xFF ) + 1 );
        return pixel;

    }

    public static BufferedImage Pixels2Buffered( byte[] img )
    {
        BufferedImage buffered = new BufferedImage( CAPTURE_WIDTH, CAPTURE_HEIGHT, BufferedImage.TYPE_INT_RGB );
        int index = 0;
        for ( int x = 0; x < CAPTURE_WIDTH; x++ )
        {
            for ( int y = 0; y < CAPTURE_HEIGHT; y++ )
            {
                int red = twosComplement( img[index++] );
                int green = twosComplement( img[index++] );
                int blue = twosComplement( img[index++] );

                buffered.setRGB( x, y, new Color( red, green, blue ).getRGB() );
            }
        }
        return buffered;
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage AWT2Buffered( java.awt.Image img)
    {
        if (img instanceof BufferedImage)
            return (BufferedImage) img;

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        // Return the buffered image
        return bimage;
    }

    public static javafx.scene.image.Image AWT2FX( java.awt.Image image )
    {
        return SwingFXUtils.toFXImage( AWT2Buffered( image ), null);
    }

    public static javafx.scene.image.Image Buffered2FX( BufferedImage image )
    {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }
}
