package com.peacefulotter.javadrivesgta.ml.loss;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;

public interface LossFunc
{
    double loss( Matrix2D pred, Matrix2D y );
    Matrix2D gradient( Matrix2D pred, Matrix2D y );
}
