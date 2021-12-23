import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class RPCClient {

    static Scanner scanner = null;

    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println(" _________________                _________________");
        System.out.println("|   ___________   |              |   ___________   |");
        System.out.println("|  |           |  |              |  |           |  |");
        System.out.println("|  |  CLIENT   |  |     RPC      |  |  SERVER   |  |");
        System.out.println("|  |___________|  |  --------->  |  |___________|  | ");
        System.out.println("|_________________|  <---------  |_________________|");
        System.out.println("      _|____|_ . . . . . . . . . . . . _|____|_      ");
        System.out.println("     /________\\                       /________\\    \n");

        try {
            IComputationService service = RPCClientStub.getRemoteProxyObj(IComputationService.class, new InetSocketAddress("localhost", 8088));

            scanner = new Scanner(System.in);

            loop:
            while (true) {
                System.out.println("Computation Server supports four basic RPC's: calculate_pi(), add(i, j), sort(arrayA), matrix_multiply(matrixA, matrixB, matrixC)");
                System.out.println("\tOptions to enter:");
                System.out.println("\t1. GET pi value         : calculate_pi()");
                System.out.println("\t2. Add two numbers      : add(i, j)");
                System.out.println("\t3. Sort the given array : sort(arrayA)");
                System.out.println("\t4. Matrix multiplication: matrix_multiply(matrixA, matrixB, matrixC)");
                System.out.println("\t5. Exit (Press 5 to check the results)");

                System.out.println("Enter your option:");
                int s = scanner.nextInt();
                switch (s) {
                    case 1:
                        System.out.println("Executing calculate_pi() RPC...");
                        service.calculate_pi();
                        break;
                    case 2:
                        System.out.println("Executing add(i, j) RPC...");
                        System.out.println("Enter 2 numbers :");
                        float num1 = scanner.nextFloat();
                        float num2 = scanner.nextFloat();
                        service.add(num1, num2);
                        break;
                    case 3:
                        System.out.println("Enter number of elements in array:");
                        int n = scanner.nextInt();
                        System.out.printf("Enter %d elements: \n", n);
                        float[] array = new float[n + 1];
                        for (int i = 1; i <= n; i++) {
                            array[i] = scanner.nextFloat();
                        }
                        service.sort(array);
                        break;
                    case 4:
                        System.out.println("Enter number of rows and columns in matrixA:");
                        int rowA = scanner.nextInt();
                        int colA = scanner.nextInt();
                        System.out.println("Enter number of rows and columns in matrixB:");
                        int rowB = scanner.nextInt();
                        int colB = scanner.nextInt();
                        if (colA == rowB) {
                            float[][] matrixA = new float[rowA][colA];
                            System.out.printf("Enter the elements of matrixA(%d X %d):\n", rowA, colA);
                            for (int i = 0; i < rowA; i++) {
                                for (int j = 0; j < colA; j++) {
                                    matrixA[i][j] = scanner.nextFloat();
                                }
                            }
                            float[][] matrixB = new float[rowB][colB];
                            System.out.printf("Enter the elements of matrixB(%d X %d):\n", rowB, colB);
                            for (int i = 0; i < rowB; i++) {
                                for (int j = 0; j < colB; j++) {
                                    matrixB[i][j] = scanner.nextFloat();
                                }
                            }
                            float[][] matrixC = new float[rowA][colB];
                            service.matrix_multiply(matrixA, matrixB, matrixC);
                        } else {
                            System.out.println("Number of columns in matrixA is not equal to number of rows in matrixB. Re-enter the parameters.");
                        }
                        break;
                    case 5:
                        break loop;
                    default:
                        System.out.println("Incorrect option entered! Re-enter the option.");
                }
            }

            IResult resultService = RPCClientStub.getRemoteProxyResultObj(IResult.class, new InetSocketAddress("localhost", 8088));
            loop2: while (true) {
                System.out.println("\nDo you want to see the results?");
                System.out.println("\tCheck out the options below:");
                System.out.println("\t1. PI value");
                System.out.println("\t2. Sum of two numbers");
                System.out.println("\t3. Sorted array");
                System.out.println("\t4. Product of two matrices");
                System.out.println("\t5. Exit");

                System.out.println("Enter your option:");
                int s = scanner.nextInt();
                switch (s) {
                    case 1:
                        System.out.println("\nGetting results of calculate_pi() RPC...");
                        float pi_value = (float) resultService.getResult("calculate_pi");
                        System.out.println("Pi_value received from server: " + pi_value);
                        break;
                    case 2:
                        System.out.println("\nGetting results of add(i, j) RPC...");
                        float sum = (float) resultService.getResult("add");
                        System.out.println("Sum of two numbers received from server: " + sum);
                        break;
                    case 3:
                        System.out.println("\nGetting results of sort(array) RPC...");
                        float[] array = (float[]) resultService.getResult("sort");
                        System.out.println("Sorted array received from the server is:");
                        for (int i = 1; i <= array.length-1; i++) {
                            System.out.print(array[i] + "  ");
                        }
                        System.out.println();
                        break;
                    case 4:
                        System.out.println("\nGetting results of matrix_multiply(matrixA, matrixB, matrixC) RPC...");
                        float[][] matrixC = (float[][]) resultService.getResult("matrix_multiply");
                        System.out.println("Sorted array received from the server is:");
                        for (int i = 0; i < matrixC.length; i++) {
                            for (int j = 0; j < matrixC[i].length; j++) {
                                System.out.print(matrixC[i][j] + "  ");
                            }
                            System.out.println();
                        }
                        System.out.println();
                        break;
                    case 5:
                        break loop2;
                    default:
                        System.out.println("Incorrect option entered! Re-enter the option.");
                }
            }


        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }
}
