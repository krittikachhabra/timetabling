package TimeTable;
import java.io.*;
import java.util.*;
import java.util.HashMap;

public class Control
{
    Map< String, String > parameters = new HashMap<>();

    int nrTry, maxTry;
    boolean feasible;
    int currentScv;
    int bestScv;
    int bestEvaluation;
    int seed;
    double timeLimit;
    int problemType;
    int maxSteps;
    double LS_limit;
    double prob1, prob2, prob3;

    Control()throws IOException
    {
    	System.out.println("Control called");
        File file = new File("C:\\Users\\Krittika\\Desktop\\test.txt");
        BufferedReader in = new BufferedReader(new FileReader(file));
        nrTry = 0;
        maxTry = Definitions.DEFAULT_MAX_STEPS;
    }

    void setCurrentCost(Solution currentSolution )
    {
        int currentScv = currentSolution.scv;

        if( currentSolution.feasible && currentScv < bestScv )
        {
            bestScv = currentScv;
            bestEvaluation = currentScv;
        }
        else if(!currentSolution.feasible)
        {
            int currentEvaluation = (currentSolution.computeHCV() * 1000000) + currentSolution.computeScv();
            if(currentEvaluation < bestEvaluation)
            {
                bestEvaluation = currentEvaluation;
            }
        }
    }

    void endTry(Solution bestSolution) {
        System.out.println("begin solution " + nrTry);

        if (bestSolution.feasible) {
            System.out.println("feasible: evaluation function = " + bestSolution.scv);
            for (int i = 0; i < bestSolution.data.n_of_events; i++)
                System.out.println("fdfh"+bestSolution.sln.get(i).first);
            System.out.println("This " + bestSolution.data.n_of_events);
            for (int i = 0; i < bestSolution.data.n_of_events; i++)
                System.out.println("fdfh"+bestSolution.sln.get(i).second);
        }
        else {
            System.out.println("unfeasible: evaluation function = " + (bestSolution.computeHCV() * 1000000) + bestSolution.computeScv());
        }
    }

    int getSeed() { return seed;}
    boolean triesLeft() { return ( nrTry < maxTry ); }
    void beginTry()
    {
        System.out.println("begin try " +(++nrTry));
        feasible = false;
        bestScv = Integer.MAX_VALUE;
        bestEvaluation = Integer.MAX_VALUE;
    }
}
