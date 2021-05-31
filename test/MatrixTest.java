import com.peacefulotter.javadrivesgta.maths.Matrix2D;

public class MatrixTest
{
    public static void main( String[] args )
    {
        // reshape();
        // sum();
        // getRow();
    }

    private static void reshape()
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

    private static void sum()
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

    private static void getRow()
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
