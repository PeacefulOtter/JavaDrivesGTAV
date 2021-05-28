package com.peacefulotter.javadrivesgta;

import com.peacefulotter.javadrivesgta.io.ControllerHandler;
import com.peacefulotter.javadrivesgta.io.IOHandler;
import com.peacefulotter.javadrivesgta.io.KeyboardHandler;
import com.peacefulotter.javadrivesgta.ml.CarManager;
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

        // INPUT HANDLERS
        IOHandler handler;
        if ( Settings.POLL_KEYBOARD )
            handler = new KeyboardHandler();
        else
            handler = new ControllerHandler();


        Screens screens = new Screens();
        Monitor monitor = new Monitor();
        screens.addMultiRenderer( monitor );

        CarManager task = new CarManager();
        task.loadTrainingDataAndTrain();

        GameLoop gameLoop = new GameLoop( screens, task, handler );

        window.setMinWidth( 500 );
        window.setMinHeight( 300 );
        window.setScene( new Scene( screens ) );
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

        gameLoop.start();
    }
}
