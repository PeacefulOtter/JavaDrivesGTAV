package com.peacefulotter.javadrivesgta.ml.activation;


import com.peacefulotter.javadrivesgta.maths.Matrix2D;

public interface ActivationFunc
{
    Matrix2D forward( Matrix2D z );
    Matrix2D gradient( Matrix2D z );
}
