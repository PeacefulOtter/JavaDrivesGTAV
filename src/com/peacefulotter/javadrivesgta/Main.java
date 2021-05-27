package com.peacefulotter.javadrivesgta;

import com.peacefulotter.javadrivesgta.io.FileHandler;
import com.peacefulotter.javadrivesgta.io.KeyboardHandler;
import com.peacefulotter.javadrivesgta.recording.Recording;
import com.peacefulotter.javadrivesgta.utils.Settings;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
    public static void main(String[] args) {
        launch( args );
    }


    @Override
    public void start( Stage window ) throws Exception
    {
        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("shutdown hook");
            KeyboardHandler.closeHook();
        } ) );

        Monitor monitor = new Monitor();
        GameLoop gameLoop = new GameLoop( monitor );
        Scene scene = new Scene( monitor );
        // scene.setOnKeyPressed( Input::KeyPressedHandler );
        // scene.setOnKeyReleased( Input::KeyReleasedHandler );

        window.setMinWidth( 500 );
        window.setMinHeight( 300 );
        window.setScene( scene );
        window.show();

        if ( Settings.RECORD_CAPTURE )
        {
            for ( int i = 2; i >= 0; i-- )
            {
                System.out.println( i );
                Thread.sleep( 1000 );
            }
            System.out.println(" Starting now");
        }
        else
        {
            Recording recording = new Recording();
            recording.addVideo(  FileHandler.importFromFile( "res/out0.txt" ) );
            recording.addVideo(  FileHandler.importFromFile( "res/out1.txt" ) );
            recording.addVideo(  FileHandler.importFromFile( "res/out2.txt" ) );
            recording.addVideo(  FileHandler.importFromFile( "res/out3.txt" ) );
            recording.addVideo(  FileHandler.importFromFile( "res/out4.txt" ) );
            recording.addVideo(  FileHandler.importFromFile( "res/out5.txt" ) );
            recording.addVideo(  FileHandler.importFromFile( "res/out6.txt" ) );
            monitor.setVideo( recording.getVideo() );
        }

        gameLoop.start();
    }
}
