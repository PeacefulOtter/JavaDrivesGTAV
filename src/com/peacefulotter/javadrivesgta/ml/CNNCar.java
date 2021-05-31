package com.peacefulotter.javadrivesgta.ml;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.activation.ActivationFunc;
import com.peacefulotter.javadrivesgta.ml.activation.Activations;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.Convolution;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.MaxPooling;
import com.peacefulotter.javadrivesgta.ml.loss.Loss;

import java.util.Map;

import static com.peacefulotter.javadrivesgta.utils.Settings.*;
import static com.peacefulotter.javadrivesgta.utils.Settings.PRINT_PERIOD;

public class CNNCar
{
    private static final int[] DIMENSIONS = { 12350, 100, 10, 2 };
    private static final ActivationFunc[] ACTIVATIONS = { Activations.ReLU, Activations.ReLU, Activations.ReLU, Activations.HyperTan };
    private final CNN cnn;

    public CNNCar()
    {
        this.cnn = new CNN.CNNBuilder()
                .addLayer( new Convolution( 5, 8, 1, 0 ) )
                .addLayer( new MaxPooling( 2, 2, 0 ) )
                .addLayer( new Convolution( 10, 8, 1, 0 ) )
                .addLayer( new MaxPooling( 2, 2, 0 ) )
                .setNeuralNetwork( new NeuralNetwork( DIMENSIONS, ACTIVATIONS ) )
                .build();
    }

    public Matrix2D simulate( Matrix2D img )
    {
        Matrix2D data = new Matrix2D( 0, 0 );
        // add data
        return cnn.predict( data );
    }

    public void train( Map<String, Matrix2D> trainingData )
    {
        Matrix2D X = trainingData.get( "X" );
        Matrix2D Y = trainingData.get( "Y" );

        cnn.train( X, Y, Loss.MSE, LEARNING_RATE, EPOCHS, PRINT_PERIOD );
    }
}
