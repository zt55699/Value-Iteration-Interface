package value_iteration;

import java.util.ArrayList;

public class Composer {
    
    public Double [][] utilities;

    

    public Double discountRate;

    private int iterations;

    public char [][] optPolicy;
    // General settings
    private double rewards;            // reward in non-terminal states (used to initialise r[][])
    //private double gamma = 1;          // discount factor
    public double pGood ;       // probability of taking intended action
    public double pBad ; // 2 bad actions, split prob between them
    private static int MaxIter = 10000;             // max number of iterations of Value Iteration
    private static double deltaMin = 1e-30;    // convergence criterion for iteration
 
    // Main data structures
    private static double U[][];  // long-term utility
    private static double Up[][]; // UPrime, used in updates
    private static double R[][];  // instantaneous reward
    private static char  Pi[][];  // policy
     
    private int rMax, cMax;

    public Composer(int rows, int cols, double rewards, double discountRate, double pUp, double pLeft, double pRight) {
        this.rMax = rows;
        this.cMax = cols;
        this.rewards = rewards;
        this.discountRate = discountRate;
        this.pGood = pUp;
        this.pBad = (1-pGood)/2;
        utilities = new Double[rows+1][cols+1];   
        initUtilities();
    }
    
    public void initUtilities() {

    }

    public Double[][] runIteration(int n) {
        this.iterations = n;
        // System.out.println("Util[][]:");
        // for (int r=0; r<utilities.length; r++) {
        //     for (int c=0; c<utilities[0].length; c++) {
        //         System.out.printf("%f ", utilities[r][c]);
        //     }
        //     System.out.println("");
        // }

        runIteration();
        optPolicy = new char[utilities.length][utilities[0].length];

        for(int i = 0; i < Pi.length; i++){
            for(int j = 0; j < Pi[0].length; j++){
                if(Pi[i][j] == 'S')
                    optPolicy[i+1][j+1] ='N';
                else if(Pi[i][j] == 'N')
                    optPolicy[i+1][j+1] ='S';
                else
                    optPolicy[i+1][j+1] = Pi[i][j];
            }
        }

        Double [][] newUtil = new Double[utilities.length][utilities[0].length];

        for(int i = 0; i < R.length; i++){
            for(int j = 0; j < R[0].length; j++)
                    newUtil[i+1][j+1] = U[i][j];
        }
        return newUtil;
    }
    


    public void runIteration(){
        int r,c;
        double delta = 0;
 
        // policy: initially null
        Pi = new char[rMax][cMax]; 
         
        // initialise U'
        Up = new double[rMax][cMax]; // row, col
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                Up[r][c] = 0;
            }
        }
        // Don't initialise U: will set U=Uprime in iterations
        U = new double[rMax][cMax];
         
        // initialise R: set everything to Ra and then override the terminal states
        R = new double[rMax][cMax]; // row, col
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                if (utilities[r+1][c+1]!=null)
                    R[r][c] = utilities[r+1][c+1];
                else
                    R[r][c] = -999;
            }
        }


        // R[0][3] =  100;  // positive sink state
        // R[1][3] = -100;  // negative sink state
        // R[1][1] =    0;  // unreachable state
        // System.out.println("R[][]:");
        // for (r=0; r<rMax; r++) {
        //     for (c=0; c<cMax; c++) {
        //         System.out.print(R[r][c]);
        //     }
        //     System.out.println("");
        // }

        // Now perform Value Iteration.
        int n = 0;
        do
        {
            // Simultaneous updates: set U = Up, then compute changes in Up using prev value of U.
            duplicate(Up, U); // src, dest
            n++;
            delta = 0;
            for (r=0; r<rMax; r++) {
                for (c=0; c<cMax; c++) {
                    updateUPrime(r, c);
                    double diff = Math.abs(Up[r][c] - U[r][c]);
                    if (diff > delta)
                        delta = diff;
                }
            }
        } while (delta > deltaMin && n < iterations+1);
         
        // Display final matrix
        System.out.println("After " + (n-1) + " iterations:\n");
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                System.out.printf("% 6.1f\t", U[r][c]);
            }
            System.out.print("\n");
        }
 
        // Before displaying the best policy, insert chars in the sinks and the non-moving block
        Pi[rMax-1][cMax-1] = '+'; Pi[rMax-2][cMax-1] = '-';
         
        System.out.println("\nBest policy:\n");
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                System.out.print(Pi[r][c] + "   ");
            }
            System.out.print("\n");
        }
    }


    public void updateUPrime(int r, int c)
    {
        // IMPORTANT: this modifies the value of Up, using values in U.
         
        double a[] = new double[4]; // 4 actions
     
        // If at a sink state or unreachable state, use that value
        if ((r==rMax-1 && c==cMax-1) || (r==rMax-2 && c==cMax-1) || R[r][c] == -999) {
            Up[r][c] = R[r][c];
            if(R[r][c] == -999)
                Pi[r][c] = '#';
            //System.out.printf(" Up[%d][%d] = R[%d][%d] = %f;\n", r,c,r,c,R[r][c]);
        }
        else {
            a[0] = aNorth(r,c)*pGood + aWest(r,c)*pBad + aEast(r,c)*pBad;
            a[1] = aSouth(r,c)*pGood + aWest(r,c)*pBad + aEast(r,c)*pBad;
            a[2] = aWest(r,c)*pGood + aSouth(r,c)*pBad + aNorth(r,c)*pBad;
            a[3] = aEast(r,c)*pGood + aSouth(r,c)*pBad + aNorth(r,c)*pBad;
             
            int best = maxindex(a);
             
            Up[r][c] = R[r][c] + discountRate * a[best];
            if(a[0]!= 0 && a[1]!=0 && a[2]!=0 && a[3]!=0){
                 // update policy
                Pi[r][c] = (best==0 ? 'N' : (best==1 ? 'S' : (best==2 ? 'W': 'E')));
            }
        }
    }
     
    public int maxindex(double a[]) 
    {
        int b=0;
        for (int i=1; i<a.length; i++)
            b = (a[b] > a[i]) ? b : i;
        return b;
    }
     
    public double aNorth(int r, int c)
    {
        // can't go north if at row 0 or 
        if ((r==0) || (r>1 && (R[r-1][c]==-999)))
            return U[r][c];
        return U[r-1][c];
    }

    public double aSouth(int r, int c)
    {
        // can't go south if at row 2 or 
        if ((r==rMax-1) || (r < rMax -1 && (R[r+1][c]==-999)))
            return U[r][c];
        return U[r+1][c];
    }
 
    public double aWest(int r, int c)
    {
        // can't go west if at col 0 or 
        if ((c==0) || (c > 1 && (R[r][c-1]==-999)))
            return U[r][c];
        return U[r][c-1];
    }
 
    public double aEast(int r, int c)
    {
        // can't go east if at col 3 or 
        if ((c==cMax-1) || (c<cMax-1 && (R[r][c+1]==-999)))
            return U[r][c];
        return U[r][c+1];
    }
     
    public void duplicate(double[][]src, double[][]dst)
    {
        // Copy data from src to dst
        for (int x=0; x<src.length; x++) {
            for (int y=0; y<src[x].length; y++) {
                dst[x][y] = src[x][y];
            }
        }
    }




    public Double[][] policyIteration(int n, char init) {
        this.iterations = n;

        runPolicyIteration(init);
        optPolicy = new char[utilities.length][utilities[0].length];

        for(int i = 0; i < Pi.length; i++){
            for(int j = 0; j < Pi[0].length; j++){
                if(Pi[i][j] == 'S')
                    optPolicy[i+1][j+1] ='N';
                else if(Pi[i][j] == 'N')
                    optPolicy[i+1][j+1] ='S';
                else
                    optPolicy[i+1][j+1] = Pi[i][j];
            }
        }

        Double [][] newUtil = new Double[utilities.length][utilities[0].length];

        for(int i = 0; i < R.length; i++){
            for(int j = 0; j < R[0].length; j++)
                    newUtil[i+1][j+1] = U[i][j];
        }
        return newUtil;
    }

    public void runPolicyIteration(char init){
        int r,c;
 
        // policy: initially null
        Pi = new char[rMax][cMax]; 
         
        // initialise U'
        Up = new double[rMax][cMax]; // row, col
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                Up[r][c] = 0;
            }
        }
        // Don't initialise U: will set U=Uprime in iterations
        U = new double[rMax][cMax];
         
        // initialise R: set everything to Ra and then override the terminal states
        R = new double[rMax][cMax]; // row, col
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                if (utilities[r+1][c+1]!=null)
                    R[r][c] = utilities[r+1][c+1];
                else
                    R[r][c] = -999;
            }
        }

        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {    
                // If at a sink state or unreachable state, use that value
                if ((r==rMax-1 && c==cMax-1) || (r==rMax-2 && c==cMax-1) || R[r][c] == -999) {
                    if(R[r][c] == -999)
                        Pi[r][c] = '#';
                    //System.out.printf(" Up[%d][%d] = R[%d][%d] = %f;\n", r,c,r,c,R[r][c]);
                }
                else {
                    if(init == 'S')
                        Pi[r][c] ='N';
                    else if(init == 'N')
                        Pi[r][c] ='S';
                    else
                        Pi[r][c] = init;
                }
            }
        }
        // Now perform Policy Iteration.
        int n = 0;
        boolean unchanged;
        while(n < iterations)
        {
            // Simultaneous updates: set U = Up, then compute changes in Up using prev value of U.
            
            n++;
            for (r=0; r<rMax; r++) {
                for (c=0; c<cMax; c++) {
                    PolicyEvaluation(r, c);
                }
            }
            duplicate(Up, U); // src, dest

            unchanged = true;
            for (r=0; r<rMax; r++) {
                for (c=0; c<cMax; c++) {    
                    double maxCurrentU, policyU = 0.0;
                    double a[] = new double[4]; // 4 actions
                    // If at a sink state or unreachable state, use that value
                    if ((r==rMax-1 && c==cMax-1) || (r==rMax-2 && c==cMax-1) || R[r][c] == -999) {
                        Up[r][c] = R[r][c];
                        if(R[r][c] == -999)
                            Pi[r][c] = '#';
                        //System.out.printf(" Up[%d][%d] = R[%d][%d] = %f;\n", r,c,r,c,R[r][c]);
                    }
                    else {
                        a[0] = aNorth(r,c)*pGood + aWest(r,c)*pBad + aEast(r,c)*pBad;
                        a[1] = aSouth(r,c)*pGood + aWest(r,c)*pBad + aEast(r,c)*pBad;
                        a[2] = aWest(r,c)*pGood + aSouth(r,c)*pBad + aNorth(r,c)*pBad;
                        a[3] = aEast(r,c)*pGood + aSouth(r,c)*pBad + aNorth(r,c)*pBad;
                        
                        int best = maxindex(a);

                        maxCurrentU = a[best];
                        // if(Pi[r][c] == 'N'){
                        //     policyU = a[0];
                        // } else if(Pi[r][c] == 'S'){
                        //     policyU  = a[1];
                        // } else if(Pi[r][c] == 'W'){
                        //     policyU  = a[2];
                        // } else if(Pi[r][c] == 'E'){
                        //     policyU = a[3];
                        // }
                        if (maxCurrentU > U[r][c]){
                            Pi[r][c] = (best==0 ? 'N' : (best==1 ? 'S' : (best==2 ? 'W': 'E')));
                            unchanged = false;
                        }
 
                    }
                    
                }
            }

        } 
 
        // Before displaying the best policy, insert chars in the sinks and the non-moving block
        Pi[rMax-1][cMax-1] = '+'; Pi[rMax-2][cMax-1] = '-';
         
        System.out.println("\nBest policy:\n");
        for (r=0; r<rMax; r++) {
            for (c=0; c<cMax; c++) {
                System.out.print(Pi[r][c] + "   ");
            }
            System.out.print("\n");
        }
    }

    public void PolicyEvaluation(int r, int c)
    {
        // IMPORTANT: this modifies the value of Up, using values in U.
         
        double a[] = new double[4]; // 4 actions
     
        // If at a sink state or unreachable state, use that value
        if ((r==rMax-1 && c==cMax-1) || (r==rMax-2 && c==cMax-1) || R[r][c] == -999) {
            Up[r][c] = R[r][c];
            if(R[r][c] == -999)
                Pi[r][c] = '#';
            //System.out.printf(" Up[%d][%d] = R[%d][%d] = %f;\n", r,c,r,c,R[r][c]);
        }
        else {  
            a[0] = aNorth(r,c)*pGood + aWest(r,c)*pBad + aEast(r,c)*pBad;
            a[1] = aSouth(r,c)*pGood + aWest(r,c)*pBad + aEast(r,c)*pBad;
            a[2] = aWest(r,c)*pGood + aSouth(r,c)*pBad + aNorth(r,c)*pBad;
            a[3] = aEast(r,c)*pGood + aSouth(r,c)*pBad + aNorth(r,c)*pBad;
            if(Pi[r][c] == 'N'){
                Up[r][c] = R[r][c] + discountRate * a[0];
            } else if(Pi[r][c] == 'S'){
                Up[r][c] = R[r][c] + discountRate * a[1];
            } else if(Pi[r][c] == 'W'){
                Up[r][c] = R[r][c] + discountRate * a[2];
            } else if(Pi[r][c] == 'E'){
                Up[r][c] = R[r][c] + discountRate * a[3];
            }

        }
    }

}
