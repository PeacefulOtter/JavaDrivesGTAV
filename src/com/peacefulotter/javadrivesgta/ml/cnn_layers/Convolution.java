package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.activation.Activations;

import java.util.ArrayList;
import java.util.List;

public class Convolution implements CNNLayer
{
    private final int depth, kernelSize, stride, padding;
    private final List<Matrix2D> filters;

    public Convolution( int depth, int kernelSize, int stride, int padding )
    {
        this.depth = depth;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.padding = padding;

        this.filters = new ArrayList<>( depth );
        for ( int i = 0; i < depth; i++ )
            filters.add( Matrix2D.genRandomDouble( kernelSize, kernelSize, -1, 1 ) );
    }

    @Override
    public List<Matrix2D> forward( List<Matrix2D> images )
    {
        Matrix2D image = images.get( 0 );
        System.out.println("here");
        List<Matrix2D> convoluted = new ArrayList<>( depth );

        int baseHeight = image.rows;
        int baseWidth  = image.cols;

        // Output dimensions after the Convolution
        int h = 1 + (baseHeight - kernelSize + 2 * padding) / stride;
        int w = 1 + (baseWidth  - kernelSize + 2 * padding) / stride;

        for ( Matrix2D filter: filters )
        {
            Matrix2D conv = new Matrix2D( h, w );

            int j = 0;
            for ( int x = -padding; x < image.cols + padding && j < w; x += stride )
            {
                int i = 0;
                for ( int y = -padding; y < image.rows + padding && i < h; y += stride )
                {
                    // coordinates to avoid OutOfBounds Exceptions
                    int matX = bounded( x, baseWidth );
                    int matY = bounded( y, baseHeight );
                    int matWidth = bounded(x + kernelSize, baseWidth ) - matX;
                    int matHeight = bounded(y + kernelSize, baseHeight ) - matY;
                    // get the part of the matrix we want and take its maximum
                    Matrix2D patch = image.subMatrix( matX, matY, matWidth, matHeight );

                    double feature = 0;
                    for ( int k = 0; k < kernelSize; k++ )
                        for ( int l = 0; l < kernelSize; l++ )
                            feature +=  filter.getAt( k, l ) * patch.getAt( k, l );

                    // store the max into then new matrix
                    conv.setAt( i++, j, feature );
                    i++;
                }
                j++;
            }

            Matrix2D activatedConv = Activations.ReLU.forward( conv );
            convoluted.add( activatedConv );
        }


        return convoluted;
    }


    @Override
    public List<Matrix2D> backward( List<Matrix2D> din )
    {
        return null;
    }
}
