package com.peacefulotter.javadrivesgta.ml.cnn_layerss;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class MaxPooling
{
    private final int kernelSize, stride, padding;

    private final Queue<Integer> maxIndices;
    private int shapeX, shapeY;

    public MaxPooling( int kernelSize, int stride, int padding  )
    {
        this.stride = stride;
        this.padding = padding;
        this.kernelSize = kernelSize;
        maxIndices = new ArrayDeque<>();
    }

    private int bounded( int origin, int max )
    {
        return origin < 0 ? 0 : Math.min( origin, max );
    }

    public List<Matrix2D> forward( List<Matrix2D> images )
    {
        maxIndices.clear();
        List<Matrix2D> downsampled = new ArrayList<>( images.size() );

        int baseHeight = images.get( 0 ).rows;
        int baseWidth  = images.get( 0 ).cols;

        // save the images shape for the backward propagation
        shapeX = baseWidth;
        shapeY = baseHeight;

        // Output dimensions after the max pooling
        int h = 1 + (baseHeight - kernelSize + 2 * padding) / stride;
        int w = 1 + (baseWidth  - kernelSize + 2 * padding) / stride;

        for ( Matrix2D image: images )
        {
            Matrix2D DSImage = new Matrix2D( h, w );
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
                    Matrix2D.ElemIndices maxPool = image.subMatrix( matX, matY, matWidth, matHeight ).max();
                    // store the max into then new matrix
                    DSImage.setAt( i++, j, maxPool.elem );
                    System.out.println( (maxPool.x + matX) + " " + (maxPool.y+matY) );
                    // save the indices where the max was found, used in backprop
                    maxIndices.add( maxPool.x + matX );
                    maxIndices.add( maxPool.y + matY );
                }
                j += 1;
            }

            downsampled.add( DSImage );
        }
        return downsampled;
    }


    public List<Matrix2D> backprop( List<Matrix2D> din )
    {
        List<Matrix2D> dout = new ArrayList<>( din.size() );

        for ( Matrix2D image: din )
        {
            Matrix2D DSImage = new Matrix2D( shapeY, shapeX );
            int j = 0;
            for ( int x = -padding; x < shapeX + padding && j < image.cols; x += stride )
            {
                int i = 0;
                for ( int y = -padding; y < shapeY + padding && i < image.rows; y += stride )
                {
                    int maxX = maxIndices.poll();
                    int maxY = maxIndices.poll();
                    DSImage.setAt( maxY, maxX, image.getAt( i, j ) );
                    i++;
                }
                j++;
            }

            dout.add( DSImage );
        }

        return dout;
    }
}
