package com.peacefulotter.javadrivesgta.ml;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.activation.ActivationFunc;
import com.peacefulotter.javadrivesgta.ml.loss.LossFunc;

import java.util.*;

class NeuralNetwork
{
    private final HashMap<Integer, Matrix2D> w;
    private final HashMap<Integer, Matrix2D> b;
    private final HashMap<Integer, ActivationFunc> activations;

    private final int layers;
    private final int[] dimensions;
    private final ActivationFunc[] activationFuncs;

    public NeuralNetwork( int[] dimensions, ActivationFunc[] activations )
    {
        this.layers = dimensions.length;
        this.dimensions = dimensions;
        this.activationFuncs = activations;

        this.w = new HashMap<>();
        this.b = new HashMap<>();
        this.activations = new HashMap<>();

        for (int i = 1; i < this.layers; i++)
        {
            this.w.put(i, Matrix2D.genRandomDouble(dimensions[i - 1], dimensions[i]).div((float)Math.sqrt(dimensions[i-1])));
            this.b.put(i, new Matrix2D(1, dimensions[i]));
            this.activations.put(i + 1, activations[i - 1] );
        }
    }

    public NeuralNetwork( NeuralNetwork other )
    {
        this.layers = other.dimensions.length;
        this.dimensions = Arrays.copyOf( other.dimensions, layers );
        this.activationFuncs = Arrays.copyOf( other.activationFuncs, other.activationFuncs.length );

        this.w = new HashMap<>();
        this.b = new HashMap<>();
        this.activations = new HashMap<>();

        for (int i = 1; i < this.layers; i++)
        {
            this.w.put(i, new Matrix2D( other.getW( i ) ));
            this.b.put(i, new Matrix2D( other.getB( i ) ));
            this.activations.put(i + 1, other.activations.get( i + 1 ));
        }
    }

    public Matrix2D predict(Matrix2D x)
    {
        HashMap<String, HashMap<Integer, Matrix2D>> f = forward( x );
        HashMap<Integer, Matrix2D> a = f.get( "a" );
        return a.get( layers );
    }

    public HashMap<String, HashMap<Integer, Matrix2D>> forward( Matrix2D x ) {
        HashMap<Integer, Matrix2D> z = new HashMap<>();
        HashMap<Integer, Matrix2D> a = new HashMap<>();
        a.put( 1, x );

        for (int i = 1; i < layers; i++)
        {
            z.put( i + 1, a.get( i ).mul( w.get( i ) ).plus( b.get( i ) ) );
            a.put( i + 1, activations.get( i + 1 ).forward(z.get(i + 1)));
        }
        return new HashMap<>()
        {{
            put( "z", z );
            put( "a", a );
        }};
    }

    private HashMap<String, Matrix2D> insertParam(Matrix2D dw, Matrix2D db) {
        HashMap<String, Matrix2D> param = new HashMap<>();
        param.put( "dw", dw );
        param.put( "db", db );
        return param;
    }

    public HashMap<Integer, HashMap<String, Matrix2D>> backprop( LossFunc loss, HashMap<Integer, Matrix2D> z, HashMap<Integer, Matrix2D> a, Matrix2D y) {
        Matrix2D pred = a.get( layers );
        Matrix2D db = loss.gradient(pred, y).mul(activations.get(layers).gradient(pred));
        Matrix2D dw = a.get( layers - 1 ).dot( db );

        HashMap<Integer, HashMap<String, Matrix2D>> deltaParams = new HashMap<>();
        deltaParams.put( layers - 1, insertParam( dw, db ) );

        for (int i = layers - 1; i >= 2; i--)
        {
            db = db.mul(w.get(i).transpose()).mul(activations.get(i).gradient(z.get(i)));
            dw = a.get( i - 1 ).dot( db );
            deltaParams.put( i - 1, insertParam( dw, db ) );
        }

        return deltaParams;
    }

    private void updateWeights(int i, double lr, HashMap<String, Matrix2D> params ) {
        w.put( i, w.get( i ).sub( params.get( "dw" ).mul( lr ) ) );
        b.put( i, b.get( i ).sub( params.get( "db" ).mul( lr ) ) );
    }

    public Matrix2D trainOnce( Matrix2D x, Matrix2D y, LossFunc criterion, double lr )
    {
        HashMap<String, HashMap<Integer, Matrix2D>> f = forward( x );
        HashMap<Integer, Matrix2D> z = f.get( "z" );
        HashMap<Integer, Matrix2D> a = f.get( "a" );

        HashMap<Integer, HashMap<String, Matrix2D>> newParams = backprop( criterion, z, a, y );
        for ( Integer k: newParams.keySet() )
            updateWeights( k, lr, newParams.get(k) );

        return newParams.get( layers - 1 ).get( "dw" );
    }

    private int[] generateRandomIndices(int length) {
        List<Integer> l = new ArrayList<>();
        for (int i = 0; i < length; i++ )
            l.add( i );
        Collections.shuffle( l );
        int[] res = new int[length];
        for (int i = 0; i < length; i++)
            res[i] = l.get( i );
        return res;
    }


    public void train(Matrix2D x, Matrix2D y, LossFunc criterion, double lr, int epochs, int batchSize, int printPeriod )
    {
        for ( int epoch = 0; epoch < epochs; epoch++ )
        {
            int[] indices = generateRandomIndices(x.rows);
            Matrix2D shuffleX = x.shuffleRows(indices);
            Matrix2D shuffleY = y.shuffleRows(indices);

            for ( int i = 0; i < x.rows / batchSize; i++ )
            {
                int k = i * batchSize;
                int l = k + batchSize;
                Matrix2D trainX = shuffleX.selectRows( k, l );
                Matrix2D trainY = shuffleY.selectRows( k, l );
                trainOnce( trainX, trainY, criterion, lr );
            }

            if ( (epoch+1) % printPeriod == 0 )
            {
                Matrix2D pred = predict(x);
                double loss = criterion.loss(pred, y);
                System.out.println("Loss at epoch " + (epoch+1) + "/" + epochs + ": " + loss);
            }
        }
    }


    public Matrix2D getW( int i )
    {
        return w.get( i );
    }

    public Matrix2D getB( int i )
    {
        return b.get( i );
    }

    public void setW( int i, Matrix2D val )
    {
        w.put( i, val );
    }

    public void setB( int i, Matrix2D val )
    {
        b.put( i, val );
    }
}
