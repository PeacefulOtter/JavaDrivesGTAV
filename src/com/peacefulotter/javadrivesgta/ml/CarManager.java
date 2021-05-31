package com.peacefulotter.javadrivesgta.ml;

import com.peacefulotter.javadrivesgta.Monitor;
import com.peacefulotter.javadrivesgta.Task;
import com.peacefulotter.javadrivesgta.img_processing.ImageConverter;
import com.peacefulotter.javadrivesgta.io.FileHandler;
import com.peacefulotter.javadrivesgta.maths.Matrix2D;

import java.util.List;
import java.util.Map;

public class CarManager extends Task
{
    private static final int FROM_FILE = 0;
    private static final int TO_FILE = 5;

    private final CNNCar car = new CNNCar();

    public void loadTrainingDataAndTrain()
    {
        System.out.println("[CarManager] (loadTrainingDataAndTrain) Loading dataset from " + FROM_FILE + " to " + TO_FILE);
        // load the training data
        List<Map<String, Matrix2D>> data = FileHandler.loadTrainingData( FROM_FILE, TO_FILE );
        // and train the car on it
        for ( Map<String, Matrix2D> sample: data )
            this.car.train( sample );
    }

    @Override
    public void action( int acc, int dir )
    {
        Matrix2D inputImage = ImageConverter.Buffered2Matrix( Monitor.getCapture() );
        Matrix2D sim = this.car.simulate( inputImage );
        // simulate controller inputs;
    }
}
