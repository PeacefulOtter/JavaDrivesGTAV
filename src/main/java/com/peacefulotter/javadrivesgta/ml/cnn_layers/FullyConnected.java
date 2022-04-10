package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.maths.Matrix3D;
import com.peacefulotter.javadrivesgta.ml.activation.ActivationFunc;

public class FullyConnected implements CNNLayer
{
    private Matrix2D weights, biases;
    private final ActivationFunc activation;

    private Matrix2D lastInput;
    private int shapeX, shapeY, shapeZ;

    public FullyConnected( int fromSize, int toSize, ActivationFunc activation )
    {
        this.weights = Matrix2D.genRandomDouble( fromSize, toSize, -1, 1 );
        this.biases = new Matrix2D( 1, toSize );
        this.activation = activation;
    }



    @Override
    public Matrix3D forward( Matrix3D images )
    {
        shapeX = images.cols;
        shapeY = images.rows;
        shapeZ = images.depth;

        Matrix2D flattened = images.flatten();
        lastInput = flattened;

        Matrix2D output = flattened.dot( weights ).plus( biases );
        Matrix2D activated = activation.forward( output );
        /*
        self.last_input_shape = input.shape         # keep track of last input shape before flattening
                                                    # for later backward propagation

        self.last_input = input                     # keep track of last input and output for later backward propagation
        self.last_output = output */
        Matrix3D res = new Matrix3D( activated.rows, activated.cols, 1 );
        res.setMatrix( 0, activated );
        return res;
    }

    @Override
    public Matrix3D backward( Matrix3D din, double learningRate )
    {
        Matrix2D deriv= activation.gradient( din.getMatrix( 0 ) );
        // Matrix3D derivExpanded = deriv.expandDims();

        Matrix2D dw = lastInput.dot( deriv.transpose() );
        Matrix2D db = din.getMatrix( 0 ).sumRows().reshape( biases.rows, biases.cols );

        weights = weights.sub( dw.mul( learningRate ) );
        biases  = biases.sub(  db.mul( learningRate ) );
        /*

        self.last_input = np.expand_dims(self.last_input, axis=1)
        din = np.expand_dims(din, axis=1)

        dw = np.dot(self.last_input, np.transpose(din))           # loss gradient of final dense layer weights
            db = np.sum(din, axis=1).reshape(self.biases.shape)       # loss gradient of final dense layer biases

        self.weights -= learning_rate * dw                        # update weights and biases
        self.biases -= learning_rate * db

        dout = np.dot(self.weights, din)
        return dout.reshape(self.last_input_shape)*/


        Matrix3D dout = null; // weights.dot( din ).reshape( shapeX, shapeY, shapeZ );

        return dout;
    }

    @Override
    public Matrix3D getWeights()
    {
        return null;
    }
}
