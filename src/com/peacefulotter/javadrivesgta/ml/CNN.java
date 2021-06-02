package com.peacefulotter.javadrivesgta.ml;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.maths.Matrix3D;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.CNNLayer;
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
        Matrix3D cnnForward = forward( x );
        return network.predict( cnnForward.flatten() );
    }

    public Matrix3D forward( Matrix2D x )
    {
        Matrix3D pred = new Matrix3D( x.rows, x.cols, 1 );
        pred.setMatrix( 0, x );

        // forward x to the convolution and pooling hidden layers
        for ( CNNLayer layer : layers )
        {
            System.out.println("Forward pass: " + pred.shape());
            pred = layer.forward( pred );
        }


        return pred;
    }

    private void backward( Matrix3D pred, double lr )
    {
        Matrix3D back = pred;
        for ( int i = layers.size() - 1; i >= 0; i-- )
        {
            System.out.println("Backward pass: " + back.shape());
            CNNLayer layer = layers.get( i );
            back = layer.backward( back, lr );
        }
    }

    public void trainOnce( Matrix2D x, Matrix2D y, LossFunc criterion, double lr )
    {
        // forward through the deep network
        Matrix3D pred = forward( x );
        Matrix2D flattened = pred.flatten();

        System.out.println("[CNN] Prediction shape: " + pred.shape() + " flattened: " + flattened.shape());

        // forward and backward through the neural network
        Matrix2D nnGrad = network.trainOnce( flattened, y, criterion, lr );

        Matrix3D grads = new Matrix3D( 1, nnGrad.cols, nnGrad.rows );
        for ( int i = 0; i < nnGrad.rows; i++ )
            grads.setMatrix( i, nnGrad.getRow( i ) );

        System.out.println("[CNN] Grads shape: " + grads.shape() );

        // backward through the deep network
        backward( grads, lr );
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
