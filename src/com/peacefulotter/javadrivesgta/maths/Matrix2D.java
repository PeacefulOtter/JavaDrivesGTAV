package com.peacefulotter.javadrivesgta.maths;


import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;


public class Matrix2D
{
    private static final boolean ALLOW_PARALLEL = true;
    private static final int CORES = Math.max( 2, Math.min( 4, Runtime.getRuntime().availableProcessors() ) );
    private static final int PARALLEL_THRESHOLD = 400 * 400;

    private final double[][] m;
    public final int rows, cols;

    public Matrix2D( int rows, int cols)
    {
        this.m = new double[rows][cols];
        this.rows = rows;
        this.cols = cols;
    }

    public Matrix2D( Matrix2D mat ) {
        this.m = Arrays.copyOf( mat.m, mat.rows );
        this.rows = mat.rows;
        this.cols = mat.cols;
    }

    public Matrix2D( double[][] m )
    {
        this.m = deepCopy( m );
        this.rows = m.length;
        this.cols = m[0].length;
    }

    public static double[][] deepCopy(double[][] original) {
        final double[][] res = new double[original.length][];
        for (int i = 0; i < original.length; i++) {
            res[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return res;
    }

    /**
     * Creates a new matrix of size (cols, rows) and apply a function 'func' to each of its elements
     * @param func: the function
     * @return a new Matrix m = func(new Matrix(rows, cols))
     */
    public static Matrix2D applyFunc( Matrix2DLambda func, int rows, int cols )
    {
        Matrix2D res = new Matrix2D( rows, cols );
        return res.applyFunc( func );
    }

    /**
     * Creates a new matrix of the same size as this and apply a function 'func' to each elements
     * @param func: the function
     * @return a new Matrix m = func(this)
     */
    public Matrix2D applyFunc( Matrix2DLambda func )
    {
        Matrix2D res = new Matrix2D( rows, cols );
        for ( int i = 0; i < rows; i++ )
        {
            for ( int j = 0; j < cols; j++ )
            {
                res.m[i][j] = func.apply( res, i, j );
            }
        }
        return res;
    }

    /**
     * Run a function 'func' to all the elements
     * @param func: the function, return value not used
     */
    public void execFunc( Matrix2DLambdaExec func )
    {
        for ( int i = 0; i < rows; i++ )
        {
            for ( int j = 0; j < cols; j++ )
            {
                func.apply( i, j );
            }
        }
    }

    public void paraExecFunc( Matrix2DLambda func )
    {
        int slice = rows / CORES;
        int rest = rows % CORES;

        IntStream.range(1, CORES + 1).parallel().forEach( core -> {
            System.out.println(((core - 1) * slice) + " " + ( core * slice));
            for ( int i = (core - 1) * slice; i < core * slice; i++ )
                for ( int j = 0; j < cols; j++ )
                    func.apply( this, i, j );
            }
        );

        for ( int i = rows - rest; i < rows; i++ )
            for ( int j = 0; j < cols; j++ )
                func.apply( this, i, j );
    }


    public static Matrix2D genRandomDouble( int rows, int cols ) {
        Random r = new Random();
        return Matrix2D.applyFunc( (mat, i, j) -> r.nextGaussian(), rows, cols);
    }

    public static Matrix2D genRandomDouble( int rows, int cols, double min, double max ) {
        return Matrix2D.applyFunc( (mat, i, j) ->
                ThreadLocalRandom.current().nextDouble( min, max ), rows, cols);
    }

    public static Matrix2D genRandomInt( int width, int height, int min, int max )
    {
        return Matrix2D.applyFunc( (mat, i, j) ->
                ThreadLocalRandom.current().nextInt( min, max + 1 ), height, width);
    }

    public Matrix2D transpose()
    {
        return Matrix2D.applyFunc( (mat, i, j) -> m[j][i], cols, rows);
    }


    public static class ElemIndices
    {
        public double elem;
        public int x, y;

        private ElemIndices( double elem, int x, int y ) { this.elem = elem; this.x = x; this.y = y; }
    }

    public ElemIndices max()
    {
        ElemIndices res = new ElemIndices( -Double.MAX_VALUE, 0, 0 );
        execFunc( (i, j) -> {
            if ( getAt( i, j ) > res.elem )
            {
                res.elem = getAt( i, j );
                res.x = j;
                res.y = i;
            }
        } );
        return res;
    }

    public Matrix2D normalize()
    {
        double mean = mean();
        double variance = variance(mean);
        double std = std(variance);
        if (mean == 0 && std == 0)
            return new Matrix2D( rows, cols );
        return applyFunc( (mat,i,j) -> (m[i][j] - mean) / std );
    }

    public Matrix2D plus( double a )
    {
        return Matrix2D.applyFunc(( mat, i, j) -> m[i][j] + a, rows, cols);
    }

    public Matrix2D sub( double a ) { return plus(-a); }

    public Matrix2D mul( double a )
    {
        return Matrix2D.applyFunc(( mat, i, j) -> m[i][j] * a, rows, cols);
    }

    public Matrix2D div( double a )
    {
        return mul( 1 / a );
    }

    public Matrix2D pow( double a )
    {
        return Matrix2D.applyFunc( (mat, i, j) -> Math.pow(m[i][j], a), rows, cols);
    }


    public Matrix2D plus( Matrix2D other )
    {
        if (rows >1 && other.rows == 1 && cols == other.cols)
        {
            return applyFunc( (mat, i, j) -> m[i][j] + other.m[0][j] );
        }
        else if ( rows != other.rows || cols != other.cols ) throw new AssertionError();
        return Matrix2D.applyFunc( (mat, i, j) -> m[i][j] + other.m[i][j], rows, cols);
    }

    public Matrix2D sub( Matrix2D other )
    {
        return plus(other.mul(-1));
    }

    private Matrix2D mulSameSize( Matrix2D other )
    {
        return applyFunc( (mat, i, j) -> m[i][j] * other.m[i][j] );
    }

    public Matrix2D mul( Matrix2D other )
    {
        if ( rows == other.rows && cols == other.cols )
            return mulSameSize( other );
        else if ( cols != other.rows ) throw new AssertionError();

        return Matrix2D.applyFunc( (mat, i, j) -> {
            double value = 0;
            for ( int k = 0; k < cols; k++ )
            {
                value += this.m[i][k] * other.m[k][j];
            }
            return value;
        }, rows, other.cols);
    }

    public Matrix2D pow( Matrix2D other )
    {
        return Matrix2D.applyFunc( (mat, i, j) -> Math.pow(m[i][j], other.m[i][j]), rows, cols);
    }

    public Matrix2D div( Matrix2D other )
    {
        return Matrix2D.applyFunc( (mat, i, j) -> m[i][j] / other.m[i][j], rows, cols);
    }

    public Matrix2D dot( Matrix2D other )
    {
        return transpose().mul(other);
    }

    /**
     * axis 1 = rows
     */
    public Matrix2D sumRows()
    {
        int[] row = { 0 };
        return Matrix2D.applyFunc( (mat, i, j) -> {
            double sum = 0;
            for ( int k = 0; k < cols; k++ )
                sum += getAt( row[0], k );
            row[0]++;
            return sum;
        }, 1, cols );
    }

    /**
     * axis 0 = cols
     */
    public Matrix2D sumCols()
    {
        int[] col = { 0 };
        return Matrix2D.applyFunc( (mat, i, j) -> {
            double sum = 0;
            for ( int k = 0; k < cols; k++ )
                sum += getAt( k, col[0] );
            col[0]++;
            return sum;
        }, 1, rows );
    }

    public Matrix2D flatten()
    {
        Matrix2D res = new Matrix2D( 1, cols * rows );
        int[] index = { 0 };
        applyFunc( (mat, i, j) -> res.m[0][index[0]++] = mat.m[i][j] );
        return res;
    }

    public Matrix2D selectRows(int a, int b) {
        Matrix2D res = new Matrix2D(b - a, cols );
        for ( int i = a; i < b; i++ )
        {
            for ( int j = 0; j < cols; j++ )
            {
                res.setAt( i - a, j, getAt( i, j ) );
            }
        }
        return res;
    }

    public Matrix2D subMatrix( int x, int y, int width, int height )
    {
        Matrix2D res = new Matrix2D( height, width );
        return res.applyFunc( (mat, i, j) -> this.m[y + i][x + j] );
    }

    public void subMatrix( int x, int y, Matrix2D src )
    {
        src.execFunc( (i, j) ->
            setAt( i + y, j + x, src.getAt( i, j ) )
        );
    }

    public Matrix2D shuffleRows() {
        Matrix2D mat = new Matrix2D(this);
        Collections.shuffle(Arrays.asList(mat.m));
        return mat;
    }

    public Matrix2D shuffleRows(int[] indices) {
        if ( indices.length != rows ) throw new AssertionError();
        return Matrix2D.applyFunc( (mat, i, j) -> m[indices[i]][j], rows, cols);
    }

    // same as np.expand_dims(x, axis=1)
    public Matrix3D expandDims()
    {
        Matrix3D res = new Matrix3D( 1, cols, rows );
        return res.applyFunc( (mat, d, r, c) -> m[d][c] );
    }

    public Matrix2D reshape( int newRows, int newCols )
    {
        if ( newRows * newCols != this.rows * this.cols )
        {
            System.out.println("Reshape from " + shape() + " to (" + newRows + ", " + newCols + ") is impossible");
            return null;
        }

        int pos[] = {0, 0}; // new x, y position
        Matrix2D res = new Matrix2D( newRows, newCols );
        applyFunc( (mat, i, j) -> {
            if ( pos[1] != 0 && pos[1] % newCols == 0 )
            {
                pos[ 0 ]++;
                pos[ 1 ] = 0;
            }
            res.setAt( pos[0], pos[1]++, getAt( i, j ) );
            return 0;
        } );
        return res;
    }

    public Matrix2D getRow( int y )
    {
        return applyFunc( (mat, i, j) -> m[y][j],1, cols );
    }

    public Matrix2D getCol( int x )
    {
        return applyFunc( (mat, i, j) -> m[i][x], rows, 1 );
    }

    public double getAt(int i, int j)
    {
        return m[i][j];
    }

    public void setAt(int i, int j, double val)
    {
        m[i][j] = val;
    }

    public Matrix2D abs()
    {
        return Matrix2D.applyFunc( (mat, i, j) -> Math.abs( mat.m[i][j] ), rows, cols );
    }

    public double sum()
    {
        double[] res = new double[] { 0d };
        applyFunc( (m, i, j) -> {
            res[0] += getAt( i, j );
            return 0;
        } );
        return res[0];
    }

    public double mean()
    {
        return sum() / (rows * cols);
    }

    public double variance() { return variance(mean()); }

    public double variance(double mean)
    {
        double[] res = new double[] { 0d };
        applyFunc( (m, i, j) -> {
            res[0] += Math.pow( getAt( i, j ) - mean, 2);
            return 0;
        } );
        return res[0] / (rows * cols);
    }

    public double std() { return std(variance()); }

    public double std( double variance ) {
        return Math.sqrt( variance );
    }

    public String shape()
    {
        StringJoiner sj = new StringJoiner( ", ", "(", ")" );
        sj.add( "rows: " + rows );
        sj.add( "cols: " + cols );
        return sj.toString();
    }

    @Override
    public String toString()
    {
        StringJoiner main = new StringJoiner("\n", "[", "]");
        for ( int i = 0; i < rows; i++ )
        {
            StringJoiner sj = new StringJoiner(",\t", "[", "]");
            for ( int j = 0; j < cols; j++ )
            {
                sj.add( String.valueOf( m[i][j] ) );
            }
            main.add( sj.toString() );
        }
        return main.toString();
    }
}
