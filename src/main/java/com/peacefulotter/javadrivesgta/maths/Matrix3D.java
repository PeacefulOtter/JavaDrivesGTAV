package com.peacefulotter.javadrivesgta.maths;

import java.util.Iterator;
import java.util.StringJoiner;

public class Matrix3D implements Iterable<Matrix2D>
{
    private final double[][][] m;

    public final int rows, cols, depth;

    public Matrix3D( int rows, int cols, int depth )
    {
        this.m = new double[depth][rows][cols];
        this.rows = rows;
        this.cols = cols;
        this.depth = depth;
    }

    public Matrix3D( Matrix3D other )
    {
        depth = other.depth;
        rows  = other.rows;
        cols  = other.cols;
        this.m = new double[depth][rows][cols];
        other.execFunc( (d, r, c) -> m[d][r][c] = other.getAt( d, r, c ) );
    }

    public Matrix3D applyFunc( Matrix3DLambda func )
    {
        Matrix3D res = new Matrix3D( rows, cols, depth );
        for ( int d = 0; d < depth; d++ )
        {
            for ( int r = 0; r < rows; r++ )
            {
                for ( int c = 0; c < cols; c++ )
                {
                    res.setAt( d, r, c, func.apply( this, d, r, c ) );
                }
            }
        }
        return res;
    }

    public void execFunc( Matrix3DLambdaExec func )
    {
        for ( int d = 0; d < depth; d++ )
        {
            for ( int r = 0; r < rows; r++ )
            {
                for ( int c = 0; c < cols; c++ )
                {
                    func.apply( d, r, c );
                }
            }
        }
    }

    // to 1D array basically
    public Matrix2D flatten()
    {
        // From a deep network to a fully connected neural network, need to flatten the pred matrix
        Matrix2D flattened = new Matrix2D( 1, cols * rows * depth );
        int[] index = { 0 };
        execFunc( (d, r, c) -> flattened.setAt( 0, index[0]++, getAt( d, r, c ) ) );
        return flattened;
    }

    public Matrix3D reshape( int newRows, int newCols, int newDepth )
    {
        if ( newRows * newCols * newDepth != rows * cols * depth )
        {
            System.out.println("[Matrix3D] Cannot reshape to this size");
            return null;
        }
        Matrix3D res = new Matrix3D( newRows, newCols, newDepth );
        // TODO
        return res;
    }

    public void setMatrix( int d, Matrix2D mat )
    {
        if ( mat.rows != rows || mat.cols != cols )
        {
            System.out.println( "Could not set this matrix at depth " + d );
            return;
        }
        mat.applyFunc( (m, i, j) -> { setAt( d, i, j, m.getAt( i, j ) ); return 0; } );
    }

    public Matrix2D getMatrix( int depth )
    {
        return new Matrix2D( m[depth] );
    }

    public void setAt( int d, int r, int c, double val )
    {
        m[d][r][c] = val;
    }

    public double getAt( int d, int r, int c )
    {
        return m[d][r][c];
    }

    public String shape()
    {
        StringJoiner sj = new StringJoiner( ", ", "(", ")" );
        sj.add( "depth: " + depth );
        sj.add( "rows: " + rows );
        sj.add( "cols: " + cols );
        return sj.toString();
    }

    @Override
    public String toString()
    {
        StringJoiner mat3 = new StringJoiner(",\n", "[", "]");
        for ( int d = 0; d < depth; d++ )
        {
            StringJoiner mat2 = new StringJoiner("\n", "[", "]");
            for ( int r = 0; r < rows; r++ )
            {
                StringJoiner mat1 = new StringJoiner(",\t", "[", "]");
                for ( int c = 0; c < cols; c++ )
                {
                    mat1.add( String.valueOf( m[d][r][c] ) );
                }
                mat2.add( mat1.toString() );
            }
            mat3.add( mat2.toString() );
        }

        return mat3.toString();
    }

    @Override
    public Iterator<Matrix2D> iterator()
    {
        return new MatrixIterator();
    }

    private class MatrixIterator implements Iterator<Matrix2D>
    {
        private int d = 0;

        @Override
        public boolean hasNext()
        {
            return d < depth;
        }

        @Override
        public Matrix2D next()
        {
            return new Matrix2D( m[d++] );
        }
    }
}
