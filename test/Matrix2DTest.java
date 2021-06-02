import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.utils.Time;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

public class Matrix2DTest
{
    @Test
    public void expandDims()
    {
        Matrix2D mat = Matrix2D.genRandomInt( 3, 4, 0, 5 );
        System.out.println(mat);
        System.out.println(mat.expandDims());
    }

    @Test
    public void parallel()
    {
        int k = 400;
        Matrix2D mat = Matrix2D.genRandomDouble( k, k );
        Set<Integer> is = new HashSet<>();
        Set<Integer> js = new HashSet<>();
        mat.paraExecFunc( (m, i, j) -> { is.add( i ); js.add( j ); return 0; } );

        System.out.println( is.size() + " " + js.size() ); // SIZE IS WRONG
        System.out.println(is);
        System.out.println(js);
    }

    @Test
    public void parallelBenchmark()
    {
        for ( int k = 100; k < 700; k += 100 )
        {
            Matrix2D mat = Matrix2D.genRandomDouble( k, k );

            double t = Time.getNanoTime();
            mat.applyFunc( (m, i, j) -> 5 * 29 * Math.cos( 30 ) * Math.sqrt( 25998 ) );
            double sequential = Time.getNanoTime() - t;

            t = Time.getNanoTime();
            mat.paraExecFunc( (m, i, j) -> 5 * 29 * Math.cos( 30 ) * Math.sqrt( 25998 ) );
            double parallel = Time.getNanoTime() - t;

            System.out.println( " === Test for shape: " + mat.shape());
            System.out.println("Sequential: " + sequential + ", Parallel: " + parallel + ", Delta: " + (sequential - parallel));
        }
    }

    @Test
    public void reshape()
    {
        Matrix2D mat = Matrix2D.genRandomInt( 4, 6, 0, 10 );
        Matrix2D reshaped = mat.reshape( 4, 6 );
        System.out.println( mat );
        System.out.println(reshaped);

        System.out.println();

        mat = Matrix2D.genRandomInt( 6, 4, 0, 10 );
        reshaped = mat.reshape( 6, 4 );
        System.out.println( mat );
        System.out.println(reshaped);
    }

    @Test
    public void sum()
    {
        Matrix2D mat = new Matrix2D( 2, 2 );
        mat.setAt( 0, 0, 0 );
        mat.setAt( 0, 1, 1 );
        mat.setAt( 1, 1, 5 );
        mat.setAt( 1, 0, 0 );
        System.out.println(mat);
        System.out.println("Over cols (axis=0)");
        System.out.println(mat.sumCols());
        System.out.println("Over rows (axis=1)");
        System.out.println(mat.sumRows());
    }

    @Test
    public void getRow()
    {
        Matrix2D mat = new Matrix2D( 2, 2 );
        mat.setAt( 0, 0, 0 );
        mat.setAt( 0, 1, 1 );
        mat.setAt( 1, 1, 5 );
        mat.setAt( 1, 0, 0 );
        System.out.println(mat);
        System.out.println("Row 0");
        System.out.println(mat.getRow(0));
        System.out.println("Row 1");
        System.out.println(mat.getRow(1));
    }
}
