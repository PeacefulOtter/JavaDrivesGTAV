package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;

import java.util.List;

public interface CNNLayer
{
    default int bounded( int origin, int max )
    {
        return origin < 0 ? 0 : Math.min( origin, max );
    }

    List<Matrix2D> forward( List<Matrix2D> images );
    List<Matrix2D> backward( List<Matrix2D> din );
}
