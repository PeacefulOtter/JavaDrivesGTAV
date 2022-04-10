package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.maths.Matrix3D;

import java.util.ArrayDeque;
import java.util.Queue;

public class MaxPooling implements CNNLayer
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

    @Override
    public Matrix3D forward( Matrix3D images )
    {
        System.out.println("[MaxPooling] images shape: " + images.shape() );
        maxIndices.clear();

        int baseHeight = images.rows;
        int baseWidth  = images.cols;

        // save the images shape for the backward propagation
        shapeX = baseWidth;
        shapeY = baseHeight;

        // Output dimensions after the max pooling
        int h = 1 + (baseHeight - kernelSize + 2 * padding) / stride;
        int w = 1 + (baseWidth  - kernelSize + 2 * padding) / stride;

        Matrix3D downsampled = new Matrix3D( h, w, images.depth );

        int a = 0;
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
                    // save the indices where the max was found, used in backprop
                    maxIndices.add( maxPool.x + matX );
                    maxIndices.add( maxPool.y + matY );
                }
                j += 1;
            }

            downsampled.setMatrix( a++, DSImage );
        }

        return downsampled;
    }

    @Override
    public Matrix3D backward( Matrix3D din, double learningRate )
    {
        System.out.println("[MaxPooling] din shape: " + din.shape() + ", " + shapeX + " " + shapeY);
        Matrix3D dout = new Matrix3D( shapeY, shapeX, din.depth );

        System.out.println( maxIndices.size() );

        int a = 0;
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

            dout.setMatrix( a++, DSImage );
        }

        System.out.println( maxIndices.size() );

        return dout;
    }

    @Override
    public Matrix3D getWeights()
    {
        return new Matrix3D( 0, 0, 0 );
    }
}
