import com.peacefulotter.javadrivesgta.maths.Matrix3D;
import org.junit.Test;

public class Matrix3DTest
{
    @Test
    public void basics()
    {
        Matrix3D mat = new Matrix3D( 3, 3, 2 );
        System.out.println(mat);
        System.out.println();
        mat.setAt( 0, 1, 1, 1 );
        mat.setAt( 0, 2, 1, 2 );
        mat.setAt( 1, 2, 2, 3 );
        System.out.println(mat);
    }

    @Test
    public void applyFunc()
    {
        Matrix3D mat = new Matrix3D( 3, 3, 2 );
        Matrix3D transformed = mat.applyFunc( (m, d, r, c) -> (d+1) * (r+1) * (c+1) );
        System.out.println(transformed);
        System.out.println(mat); // mat remains unchanged
    }
}
