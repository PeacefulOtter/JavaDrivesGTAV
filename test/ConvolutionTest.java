import com.peacefulotter.javadrivesgta.maths.Matrix2D;
import com.peacefulotter.javadrivesgta.ml.activation.Activations;
import com.peacefulotter.javadrivesgta.ml.cnn_layers.Convolution;

import java.util.List;

public class ConvolutionTest
{
    public static void main( String[] args )
    {
        forwardTest();
    }

    private static void forwardTest()
    {
        Matrix2D mat1 = Matrix2D.genRandomInt( 5, 5, 0, 1 );
        Matrix2D mat2 = Matrix2D.genRandomInt( 11, 9, 0, 20 );
        List<Matrix2D> l1 = List.of( mat1 );
        List<Matrix2D> l2 = List.of( mat2 );
        // Matrix2D mat3 = Matrix2D.genRandomInt( 15, 15, 0, 20 );

        System.out.println( " ========================= Testing Simple Cases  ========================= \n" );
        Convolution p = new Convolution( 3, 3, 1, 0, Activations.ReLU );
        p.forward( List.of( mat1 ) );
    }
}
