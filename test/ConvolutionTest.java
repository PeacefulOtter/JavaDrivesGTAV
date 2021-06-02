import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.maths.Matrix3D;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.Convolution;
import org.junit.Test;

public class ConvolutionTest
{
    @Test
    public void forwardTest()
    {
        Matrix3D l1  = new Matrix3D( 5, 5, 1 );
        l1.setMatrix( 0, Matrix2D.genRandomInt( 5, 5, 0, 1 ) );
        Matrix3D l2  = new Matrix3D( 9, 11, 1 );
        l2.setMatrix( 0, Matrix2D.genRandomInt( 11, 9, 0, 20 ) );
        // Matrix2D mat3 = Matrix2D.genRandomInt( 15, 15, 0, 20 );

        System.out.println( " ========================= Testing Simple Cases  ========================= \n" );
        Convolution p = new Convolution( 3, 3, 1, 0 );
        System.out.println( l1 );
        Matrix3D forw = p.forward( l1 );
        forw.forEach( System.out::println );
        Matrix3D back = p.backward( forw, 1 );
        back.forEach( System.out::println );
    }
}
