import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MatrixMultiplier {
    public static void onMult(int m_ar, int m_br) {
        double[] pha = new double[m_ar*m_ar];
        double[] phb = new double[m_ar*m_ar];
        double[] phc = new double[m_ar*m_ar];

        //FirstMatrix
        for(int i = 0; i< m_ar;i++)
            for(int j = 0; j< m_ar; j++)
                pha[i*m_ar+j] = 1.0;

        //Second Matrix
        for(int i = 0; i< m_br;i++)
            for(int j = 0; j< m_br; j++)
                phb[i*m_br + j] = i + 1.0;

        Instant start = Instant.now();

        for(int i = 0;i <m_ar; i++){
            for(int j = 0; j<m_br; j++){
                double temp = 0;
                for(int k = 0; k< m_ar; k++){
                    temp += pha[i*m_ar + k] * phb[k*m_br + j];
                }
                phc[i*m_ar + j] = temp;
            }
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start,finish).toMillis();

        System.out.println("Time elapsed: " + timeElapsed + "ms");

        System.out.println("Result matrix:");
        for(int i = 0; i< 1; i++)
            for(int j = 0;j <Math.min(10,m_br);j++)
                System.out.print(phc[j]);
        System.out.println();
    }

    public static void onMultLine(int m_ar, int m_br) {
        double[] pha = new double[m_ar*m_ar];
        double[] phb = new double[m_ar*m_ar];
        double[] phc = new double[m_ar*m_ar];

        //FirstMatrix
        for(int i = 0; i< m_ar;i++)
            for(int j = 0; j< m_ar; j++)
                pha[i*m_ar+j] = 1.0;

        //Second Matrix
        for(int i = 0; i< m_br;i++)
            for(int j = 0; j< m_br; j++)
                phb[i*m_br + j] =  i + 1.0;

        //Initialize Result Matrix
        for (int i = 0; i < m_ar*m_ar; i++)
            phc[i] = 0.0;

        Instant start = Instant.now();

        for(int i=0; i<m_ar; i++) {
            for(int k=0; k<m_ar; k++) {
                for(int j=0; j<m_br; j++) {
                    phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
                }
            }
        }

        Instant finish = Instant.now();

        long timeElapsed = Duration.between(start,finish).toMillis();

        System.out.println("Time elapsed: " + timeElapsed + "ms");

        System.out.println("Result matrix:");
        for(int i = 0; i< 1; i++)
            for(int j = 0;j <Math.min(10,m_br);j++)
                System.out.print(phc[j]);
        System.out.println();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int lin, col, blockSize;
        int op;
        int n_min, n_max, inc;

        op = 1;
        do {
            System.out.println("\n1. Multiplication");
            System.out.println("2. Line Multiplication");
            System.out.println("3. Block Multiplication");
            System.out.println("Selection?: ");
            op = scanner.nextInt();
            if (op == 0) break;

            System.out.println("Minimum dimension: lins=cols?: ");
            n_min = scanner.nextInt();
            System.out.println("Maximum dimension: lins=cols?: ");
            n_max = scanner.nextInt();
            System.out.println("Step?: ");
            inc = scanner.nextInt();

            switch (op) {
                case 1:
                    for (int n = n_min; n <= n_max; n+=inc) {
                        System.out.println("\nDimensions: " + n + "*" + n);
                        lin = n;
                        col = n;
                        onMult(lin, col);
                    }
                    break;
                case 2:
                    for (int n = n_min; n <= n_max; n+=inc) {
                        System.out.println("\nDimensions: " + n + "*" + n);
                        lin = n;
                        col = n;
                        onMultLine(lin, col);
                    }
                    break;
                case 3:
                    /*
                    cout << "Block Size? ";
                    cin >> blockSize;
                    OnMultBlock(lin, col, blockSize);
                    break;
                    */
            }
        } while (op != 0);
    }
}
