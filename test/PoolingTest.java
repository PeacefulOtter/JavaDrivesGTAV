import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.MaxPooling;

import java.util.List;

class PoolingTest
{
    public static void main( String[] args )
    {
        // forwardTest();
        backwardTest();
    }

    private static void forwardTest()
    {
        Matrix2D mat1 = Matrix2D.genRandomInt( 14, 10, 0, 20 );
        Matrix2D mat2 = Matrix2D.genRandomInt( 11, 9, 0, 20 );
        List<Matrix2D> l1  = List.of( mat1 );
        List<Matrix2D> l2  = List.of( mat2 );
        // Matrix2D mat3 = Matrix2D.genRandomInt( 15, 15, 0, 20 );

        System.out.println(" ========================= Testing Simple Cases  ========================= \n");
        MaxPooling p = new MaxPooling( 2, 2, 0 );
        p.forward( l1 );
        System.out.println();
        p = new MaxPooling( 3, 2, 0 );
        p.forward( l2 );

        System.out.println("\n\n ========================= Testing Kernel Size  ========================= \n");
        p = new MaxPooling( 1, 2, 0 );
        p.forward( l2 );
        System.out.println();
        p = new MaxPooling( 1, 1, 0 );
        p.forward( l1 );
        System.out.println();
        p = new MaxPooling( 5, 2, 0 );
        p.forward( l2 );

        System.out.println("\n\n ========================= Testing Stride  ========================= \n");
        p = new MaxPooling( 2, 1, 0 );
        p.forward( l1 );
        System.out.println();
        p = new MaxPooling( 2, 4, 0 );
        p.forward( l1 );

        System.out.println("\n\n ========================= Testing Padding  ========================= \n");
        p = new MaxPooling( 2, 2, 1 );
        p.forward( l1 );
        System.out.println();
        p = new MaxPooling( 2, 2, 2 );
        p.forward( l1 );
    }

    private static void backwardTest()
    {
        Matrix2D mat1 = Matrix2D.genRandomInt( 14, 10, 0, 20 );
        MaxPooling p = new MaxPooling( 2, 2, 0 );
        List<Matrix2D> fw = p.forward( List.of(mat1) );

        System.out.println( "Original: \n" + mat1 );
        System.out.println( "Forward: \n" + fw.get( 0 ));
        System.out.println( "Backprop: \n" + p.backprop( fw ).get( 0 ));
    }
}