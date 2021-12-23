public class ComputationServiceImpl implements IComputationService {

    @Override
    public float calculate_pi() {
        return (3.141592f);
    }

    @Override
    public float add(float i, float j) {
        return (i+j);
    }

    @Override
    public float[] sort(float[] array) {
        Sort.mergeSort(array, 1, array.length - 1);
        return array;
    }

    @Override
    public float[][] matrix_multiply(float[][] matrixA, float[][] matrixB, float[][] matrixC) {
        matrixC = Matrix.performMatrixMultiplication(matrixA, matrixB, matrixC);
        return matrixC;
    }

    private static class Sort {
        static void mergeSort(float[] array, int startIndex, int endIndex) {
            if (startIndex == endIndex) {
                return;
            }
            int middleIndex = (startIndex + endIndex) / 2;
            mergeSort(array, startIndex, middleIndex);
            mergeSort(array, middleIndex + 1, endIndex);

            // merge two sorted arrays
            merge(array, startIndex, middleIndex, endIndex);
        }

        static void merge(float[] array, int startIndex, int middleIndex, int endIndex) {
            int n1 = middleIndex - startIndex + 1;
            int n2 = endIndex - middleIndex;

            float[] leftAuxiliaryArray = new float[n1 + 2];
            float[] rightAuxiliaryArray = new float[n2 + 2];

            for (int i = 1; i <= n1; i++) {
                leftAuxiliaryArray[i] = array[startIndex + i - 1];
            }
            for (int j = 1; j <= n2; j++) {
                rightAuxiliaryArray[j] = array[middleIndex + j];
            }
            leftAuxiliaryArray[n1 + 1] = Float.MAX_VALUE;
            rightAuxiliaryArray[n2 + 1] = Float.MAX_VALUE;
            int i = 1;
            int j = 1;
            for (int k = startIndex; k <= endIndex; k++) {
                if (leftAuxiliaryArray[i] <= rightAuxiliaryArray[j]) {
                    array[k] = leftAuxiliaryArray[i];
                    i = i + 1;
                } else {
                    array[k] = rightAuxiliaryArray[j];
                    j = j + 1;
                }
            }
        }
    }

    private static class Matrix {
        static float[][] performMatrixMultiplication(float[][] matrixA, float[][] matrixB, float[][] matrixC) {
            //fills output matrix with 0's
            for(short l = 0; l < matrixA.length; l++) {
                for(short m = 0; m < matrixB[0].length; m++) {
                    matrixC[l][m] = 0;
                }
            }
            //takes the dot product of the rows and columns and adds them to output matrix
            for(short i = 0; i < matrixA.length; i++) {
                for(short j = 0; j < matrixB[0].length; j++) {
                    for(short k = 0; k < matrixA[0].length; k++) {
                        matrixC[i][j] += matrixA[i][k] * matrixB[k][j];
                    }
                }
            }
            return matrixC;
        }
    }
}

