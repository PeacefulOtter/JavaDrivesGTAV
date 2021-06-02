import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.maths.Matrix3D;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.MaxPooling;
import org.junit.Test;

class PoolingTest
{
    @Test
    public void forwardTest()
    {
        Matrix3D l1  = new Matrix3D( 10, 14, 1 );
        l1.setMatrix( 0, Matrix2D.genRandomInt( 14, 10, 0, 20 ) );
        Matrix3D l2  = new Matrix3D( 9, 11, 1 );
        l2.setMatrix( 0, Matrix2D.genRandomInt( 11, 9, 0, 20 ) );
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

    @Test
    public void backwardTest()
    {
        Matrix3D l1  = new Matrix3D( 10, 14, 1 );
        l1.setMatrix( 0, Matrix2D.genRandomInt( 14, 10, 0, 20 ) );
        MaxPooling p = new MaxPooling( 2, 2, 0 );
        Matrix3D fw = p.forward( l1 );

        System.out.println( "Original: \n" + l1 );
        System.out.println( "Forward: \n" + fw.getMatrix( 0 ));
        System.out.println( "Backprop: \n" + p.backward( fw, 1 ).getMatrix( 0 ));
    }
}