package com.peacefulotter.javadrivesgta.screen;

import com.peacefulotter.javadrivesgta.img_processing.ImageConverter;
import com.peacefulotter.javadrivesgta.img_processing.ImageEffect;
import com.peacefulotter.javadrivesgta.utils.Time;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.awt.image.BufferedImage;

import static com.peacefulotter.javadrivesgta.utils.Settings.*;


public final class Monitor extends GridPane implements MultiRenderer
{
    private static final Rectangle REGION_OF_INTEREST = new Rectangle(TOP_LEFT_X, TOP_LEFT_Y, CAPTURE_WIDTH, CAPTURE_HEIGHT);
    private static Robot ROBOT;

    public static int threshold = 120;

    private final ImageView img1 = new ImageView();
    private final ImageView img2 = new ImageView();
    private final ImageView img3 = new ImageView();
    private final ImageView img4 = new ImageView();

    private static BufferedImage current;

    public Monitor()
    {
        setAlignment( Pos.CENTER );
        HBox box = new HBox();
        box.getChildren().addAll(  img1, img2, img3, img4  );
        getChildren().add( box );
        render();
    }

    static
    {
        try
        {
            // GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // GraphicsDevice[] devices = g.getScreenDevices();
            ROBOT = new Robot();
        } catch ( AWTException e )
        {
            e.printStackTrace();
        }
    }

    public void render()
    {
        // ROBOT.keyPress( KeyEvent.VK_Z );
        double startTime = Time.getNanoTime();
        BufferedImage capture = captureScreen();
        double captureTime = Time.getNanoTime() - startTime;

        BufferedImage minimap = new BufferedImage( CAPTURE_WIDTH, CAPTURE_HEIGHT, BufferedImage.TYPE_INT_RGB );
        int[] pixels = new int[CAPTURE_WIDTH * CAPTURE_HEIGHT * 3];
        capture.getData().getPixels( 0, 0, CAPTURE_WIDTH, CAPTURE_HEIGHT, pixels );
        minimap.getRaster().setPixels( 0, 0, CAPTURE_WIDTH, CAPTURE_HEIGHT, pixels );
        ImageEffect.clearLanes( minimap );
        BufferedImage blackImg = ImageEffect.toBlackAndWhite( capture, threshold );

        Image finalImg = ImageConverter.Buffered2FX( capture );
        Image finalBlack = ImageConverter.Buffered2FX( blackImg );
        Image finalMinimap = ImageConverter.Buffered2FX( minimap );

        // System.out.println( "Capture time: " + captureTime / Time.SECOND + ", Canny time: " + cannyTime  / Time.SECOND );
        // System.out.println( "Total frame time: " + (captureTime + cannyTime) / Time.SECOND);
        img1.setImage( finalImg );
        img2.setImage( finalBlack );
        img3.setImage( finalMinimap );

        Monitor.current = minimap;
    }

    public static BufferedImage getCapture() { return current; }

    public static BufferedImage captureScreen() {
        return ROBOT.createScreenCapture(REGION_OF_INTEREST);
    }
}
