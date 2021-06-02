package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.maths.Matrix3D;
import com.peacefulotter.javadrivesgta.ml.activation.Activations;

public class Convolution implements CNNLayer
{
    private final int depth, kernelSize, stride, padding;
    private final Matrix3D filters;

    private Matrix3D lastImages;

    public Convolution( int depth, int kernelSize, int stride, int padding )
    {
        this.depth = depth;
        this.kernelSize = kernelSize;
        this.stride = stride;
        this.padding = padding;

        this.filters = new Matrix3D( kernelSize, kernelSize, depth );
        for ( int i = 0; i < depth; i++ )
            filters.setMatrix( i, Matrix2D.genRandomDouble( kernelSize, kernelSize, -1, 1 ) );
    }

    @Override
    public Matrix3D forward( Matrix3D images )
    {
        lastImages = new Matrix3D( images );

        // Output dimensions after the Convolution
        int h = 1 + (images.rows - kernelSize + 2 * padding) / stride;
        int w = 1 + (images.cols  - kernelSize + 2 * padding) / stride;

        Matrix3D convoluted = new Matrix3D( h, w, depth * images.depth );
        int a = 0;
        for ( Matrix2D image: images )
        {
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
                        int matX = bounded( x, lastImages.cols );
                        int matY = bounded( y, lastImages.rows );
                        int matWidth = bounded(x + kernelSize, lastImages.cols ) - matX;
                        int matHeight = bounded(y + kernelSize, lastImages.rows ) - matY;
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
                convoluted.setMatrix( a++, activatedConv );
            }
        }

        return convoluted;
    }


    /**
     * @param gradients: List of gradient after the forward step and of size: filters.size()
     * @return A list of size 1 representing the backward propagation of a convolution step
     */
    @Override
    public Matrix3D backward( Matrix3D gradients, double learningRate )
    {
        Matrix3D backprop = new Matrix3D( gradients.rows, gradients.cols, gradients.depth );
        Matrix3D dHs =  new Matrix3D( gradients.rows, gradients.cols, gradients.depth );

        int a = 0;
        for ( Matrix2D grad: gradients )
            dHs.setMatrix( a++, Activations.ReLU.gradient( grad ) );

        int gradWidth = dHs.cols;
        int gradHeight = dHs.rows;
        a = 0;
        for ( Matrix2D lastImage: lastImages )
        {
            for ( int f = 0; f < filters.depth; f++ )
            {
                Matrix2D filter = filters.getMatrix( f );
                Matrix2D dH = dHs.getMatrix( f );
                Matrix2D deltaImage = new Matrix2D( lastImage.rows, lastImage.cols ); // input image delta (dX)
                Matrix2D deltaFilter = new Matrix2D( kernelSize, kernelSize ); // filter delta (dW)

                int j = 0;
                for ( int x = -padding; x < lastImage.cols + padding && j < gradWidth; x += stride )
                {
                    int i = 0;
                    for ( int y = -padding; y < lastImage.rows + padding && i < gradHeight; y += stride )
                    {
                        // coordinates to avoid OutOfBounds Exceptions
                        int matX = bounded( x, lastImages.cols );
                        int matY = bounded( y, lastImages.rows );
                        int matWidth = bounded(x + kernelSize, lastImages.cols ) - matX;
                        int matHeight = bounded(y + kernelSize, lastImages.rows ) - matY;

                        // get the part of the matrix we want and take its maximum
                        Matrix2D patch = lastImage.subMatrix( matX, matY, matWidth, matHeight );
                        // patch can have dimensions less than kernelSize, we fix this by filling the gaps with 0s
                        Matrix2D sizedPatch = new Matrix2D( kernelSize, kernelSize );
                        sizedPatch.subMatrix( kernelSize - matWidth, kernelSize - matHeight, patch );
                        // update delta filter - compute: dW += X[h:h+f, w:w+f] * dH(h,w)
                        deltaFilter = deltaFilter.plus( sizedPatch.mul( dH.getAt( i, j ) ).sumCols() );

                        // update backprop - compute: dX[h:h+f, w:w+f] += W * dH(h,w)
                        Matrix2D backpropPatch = deltaImage.subMatrix( matX, matY, matWidth, matHeight );
                        sizedPatch.subMatrix( kernelSize - matWidth, kernelSize - matHeight, backpropPatch );
                        sizedPatch = sizedPatch.plus( filter.mul( dH.getAt( i, j ) ) );
                        deltaImage.subMatrix( matX, matY, sizedPatch );

                        i++;
                    }
                    j++;
                }

                filter.sub( deltaFilter.mul( learningRate ) ); // update filters
                backprop.setMatrix( a, deltaImage );
            }
        }

        return backprop;
    }

    @Override
    public Matrix3D getWeights()
    {
        return filters;
    }

    @Override
    public int getDepth() { return depth; }
}
