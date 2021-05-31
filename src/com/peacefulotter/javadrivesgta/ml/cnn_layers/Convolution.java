package com.peacefulotter.javadrivesgta.ml.cnn_layers;

import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.activation.Activations;

import java.util.ArrayList;
import java.util.List;

public class Convolution implements CNNLayer
{
    private final int depth, kernelSize, stride, padding;
    private final List<Matrix2D> filters;

    private List<Matrix2D> lastImages;
    private int srcHeight, srcWidth;

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
        List<Matrix2D> convoluted = new ArrayList<>( depth * images.size() );
        lastImages = new ArrayList<>( images );

        srcHeight = images.get( 0 ).rows;
        srcWidth  = images.get( 0 ).cols;

        // Output dimensions after the Convolution
        int h = 1 + (srcHeight - kernelSize + 2 * padding) / stride;
        int w = 1 + (srcWidth  - kernelSize + 2 * padding) / stride;

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
                        int matX = bounded( x, srcWidth );
                        int matY = bounded( y, srcHeight );
                        int matWidth = bounded(x + kernelSize, srcWidth ) - matX;
                        int matHeight = bounded(y + kernelSize, srcHeight ) - matY;
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
        }

        return convoluted;
    }


    /**
     * @param gradients: List of gradient after the forward step and of size: filters.size()
     * @return A list of size 1 representing the backward propagation of a convolution step
     */
    @Override
    public List<Matrix2D> backward( List<Matrix2D> gradients, double learningRate )
    {
        List<Matrix2D> backprop = new ArrayList<>();
        List<Matrix2D> dHs = new ArrayList<>();

        for ( Matrix2D grad: gradients )
            dHs.add( Activations.ReLU.gradient( grad ) );

        int gradWidth = dHs.get( 0 ).cols;
        int gradHeight = dHs.get( 0 ).rows;

        for ( Matrix2D lastImage: lastImages )
        {
            for ( int f = 0; f < filters.size(); f++ )
            {
                Matrix2D filter = filters.get( f );
                Matrix2D dH = dHs.get( f );
                Matrix2D deltaImage = new Matrix2D( srcHeight, srcWidth ); // input image delta (dX)
                Matrix2D deltaFilter = new Matrix2D( kernelSize, kernelSize ); // filter delta (dW)

                int j = 0;
                for ( int x = -padding; x < srcWidth + padding && j < gradWidth; x += stride )
                {
                    int i = 0;
                    for ( int y = -padding; y < srcWidth + padding && i < gradHeight; y += stride )
                    {
                        // coordinates to avoid OutOfBounds Exceptions
                        int matX = bounded( x, srcWidth );
                        int matY = bounded( y, srcHeight );
                        int matWidth = bounded(x + kernelSize, srcWidth ) - matX;
                        int matHeight = bounded(y + kernelSize, srcHeight ) - matY;

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
                backprop.add( deltaImage );
            }
        }

        return backprop;
    }

    @Override
    public List<Matrix2D> getWeights()
    {
        return filters;
    }

    @Override
    public int getDepth() { return depth; }
}
