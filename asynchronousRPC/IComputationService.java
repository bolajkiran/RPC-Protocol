public interface IComputationService {

        Object calculate_pi();

        Object add(float i, float j);

        Object sort(float[] array);

        Object matrix_multiply(float[][] matrixA, float[][] matrixB, float[][] matrixC);
}
