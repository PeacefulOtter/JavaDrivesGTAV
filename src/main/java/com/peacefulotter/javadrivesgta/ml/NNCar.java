package com.peacefulotter.javadrivesgta.ml;


import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.activation.ActivationFunc;
import com.peacefulotter.javadrivesgta.ml.activation.Activations;
import com.peacefulotter.javadrivesgta.ml.loss.Loss;

import java.util.Map;

import static com.peacefulotter.javadrivesgta.utils.Settings.*;

public class NNCar
{
    // Neural Network specifications (hyper parameters)
    public static final int[] DIMENSIONS = new int[] {CAPTURE_WIDTH * CAPTURE_HEIGHT, 1000, 100, 10, 2};
    public static final ActivationFunc[] ACTIVATIONS = new ActivationFunc[] {
            Activations.ReLU, Activations.ReLU, Activations.ReLU, Activations.HyperTan
    };

    private NeuralNetwork nn;

    public NNCar()
    {
        this.nn = new NeuralNetwork( DIMENSIONS, ACTIVATIONS );
    }

    public NNCar( NeuralNetwork nn )
    {
        this.nn = nn;
    }

    /**
     * Simulates the NeuralNetwork
     * @return the prediction of the neural network
     */
    public Matrix2D simulate()
    {
        Matrix2D data = new Matrix2D( 1, DIMENSIONS[0] );
        // data.setAt( 0, nbArrows + 2, angle );
        return nn.predict( data.normalize() );
    }

    public void trainNN( Map<String, Matrix2D> trainingData )
    {
        Matrix2D X = trainingData.get( "X" );
        Matrix2D Y = trainingData.get( "Y" );

        if ( X.cols != DIMENSIONS[ 0 ] ) throw new AssertionError();

        nn.train( X, Y, Loss.MSE, LEARNING_RATE, EPOCHS, BATCH_SIZE, PRINT_PERIOD );
    }

    public NeuralNetwork getCopyNN() { return new NeuralNetwork( nn ); }
}
