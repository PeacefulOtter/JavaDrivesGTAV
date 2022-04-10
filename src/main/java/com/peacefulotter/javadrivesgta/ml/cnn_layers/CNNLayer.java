package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix3D;

public interface CNNLayer
{
    default int bounded( int origin, int max )
    {
        return origin < 0 ? 0 : Math.min( origin, max );
    }
    default int getDepth() { return 0; }

    Matrix3D forward( Matrix3D images );
    Matrix3D backward( Matrix3D din, double learningRate );
    Matrix3D getWeights();
}
