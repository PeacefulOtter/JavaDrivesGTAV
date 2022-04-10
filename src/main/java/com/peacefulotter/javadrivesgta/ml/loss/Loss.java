package com.peacefulotter.javadrivesgta.ml.loss;


import com.peacefulotter.javadrivesgta.maths.Matrix2D;

public class Loss
{
    public static final LossFunc MSE = new MSEClass();

    private static class MSEClass implements LossFunc
    {
        public double loss( Matrix2D pred, Matrix2D y )
        {
            return pred.sub(y).pow(2).mean();
        }

        public Matrix2D gradient( Matrix2D pred, Matrix2D y )
        {
            return pred.sub( y ).mul( 2d / (pred.rows * pred.cols) );
        }
    }

}
