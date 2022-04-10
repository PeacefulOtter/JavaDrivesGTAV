package com.peacefulotter.javadrivesgta;

import com.peacefulotter.javadrivesgta.io.KeyboardHandler;
import com.peacefulotter.javadrivesgta.task.CarManager;
import com.peacefulotter.javadrivesgta.screen.Monitor;
import com.peacefulotter.javadrivesgta.screen.Screens;
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
        System.out.println("Starting..");

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            System.out.println("shutdown hook");
            KeyboardHandler.closeHook();
        } ) );

        Screens screens = new Screens();
        Monitor monitor = new Monitor();
        screens.addMultiRenderer( monitor );

        CarManager task = new CarManager();
        task.loadTrainingDataAndTrain();

        GameLoop gameLoop = new GameLoop( screens, task );

        window.setMinWidth( 500 );
        window.setMinHeight( 300 );
        window.setScene( new Scene( screens ) );
        window.show();

        gameLoop.start();
    }
}
