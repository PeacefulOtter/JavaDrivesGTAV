package com.peacefulotter.javadrivesgta.ml;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.CNNLayer;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.Convolution;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.MaxPooling;
import com.peacefulotter.javadrivesgta.ml.loss.LossFunc;

import java.util.ArrayList;
import java.util.List;

public class CNN
{
    private final List<CNNLayer> layers;
    private final NeuralNetwork network;

    private CNN( List<CNNLayer> layers, NeuralNetwork network )
    {
        this.layers = layers;
        this.network = network;
    }

    public Matrix2D predict( Matrix2D x )
    {
        List<Matrix2D> pred = List.of( x );

        // forward x to the convolution and pooling hidden layers
        for ( CNNLayer layer : layers )
        {
            /* Convolution step */
            if ( layer instanceof Convolution )
            {
                List<Matrix2D> predTemp = new ArrayList<>();
                for ( Matrix2D mat : pred )
                    predTemp.addAll( layer.forward( List.of( mat ) ) );
                pred = predTemp;
            }

            /* Max Pooling step */
            else if ( layer instanceof MaxPooling )
            {
                pred = layer.forward( pred );
            } else
            {
                System.out.println( "CNN Layer " + layer.getClass() + " not supported" );
            }

        }

        // From a deep network to a fully connected neural network, need to flatten the pred matrix
        Matrix2D flattened = new Matrix2D( 1, pred.get( 0 ).cols * pred.get( 0 ).rows * pred.size() );
        int[] index = { 0 };
        for ( Matrix2D mat: pred )
            mat.execFunc( (m, i, j) -> {
                flattened.setAt( 0, index[0]++, m.getAt( i, j ) );
                return 0;
            } );

        return flattened;
    }

    private void backward( Matrix2D pred, double lr )
    {
        List<Matrix2D> back = List.of( pred );
        for ( int i = layers.size() - 1; i >= 0; i-- )
        {
            CNNLayer layer = layers.get( i );
            back = layer.backward( back );
        }
    }

    public void trainOnce( Matrix2D x, Matrix2D y, LossFunc criterion, double lr )
    {
        // forward through the deep network
        Matrix2D pred = predict( x );
        // forward and backward through the neural network
        Matrix2D nnPred = network.trainOnce( pred, y, criterion, lr );
        // backward through the deep network
        backward( nnPred, lr );
    }


    public void train(Matrix2D x, Matrix2D y, LossFunc criterion, double lr, int epochs, int printPeriod )
    {
        for ( int epoch = 0; epoch < epochs; epoch++ )
        {
            trainOnce( x, y, criterion, lr );

            if ( (epoch+1) % printPeriod == 0 )
            {
                Matrix2D pred = predict(x);
                double loss = criterion.loss(pred, y);
                System.out.println("Loss at epoch " + (epoch+1) + "/" + epochs + ": " + loss);
            }
        }
    }

    public static class CNNBuilder
    {
        private final List<CNNLayer> layers = new ArrayList<>();
        private NeuralNetwork nn;

        public CNNBuilder addLayer( CNNLayer layer ) { layers.add( layer ); return this; }
        public CNNBuilder setNeuralNetwork( NeuralNetwork nn ) { this.nn = nn; return this; }

        public CNN build()
        {
            if ( nn == null )
            {
                System.out.println("No neural network set");
                return null;
            }

            return new CNN( layers, nn );
        }
    }
}
