package TimeTable;

import java.io.IOException;
import java.io.PrintStream;

public class MMAs
{
    public static void main(String args[])throws IOException
    {
        int n_of_ants = Definitions.DEFAULT_N_OF_ANTS;
        Ant[] ant = new Ant[n_of_ants];

        double pheromone_evaporation = Definitions.DEFAULT_PHEROMONE_EVAPORATION;
        double minimal_pheromone = Definitions.DEFAULT_MINIMAL_PHEROMONE;

        MMAsProblem problem = new MMAsProblem(pheromone_evaporation, minimal_pheromone);

        Control control = new Control();

        Solution best_solution = new Solution(problem);
        int cnt = 1;
        while (control.triesLeft())
        {
            System.out.println("try :" + cnt);
            cnt++;

            control.beginTry();
            problem.pheromoneReset();

            best_solution.RandomInitialSolution();
            best_solution.computeFeasibility();
            control.setCurrentCost(best_solution);

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < 60*1000)
            {

                for (int i=0;i<n_of_ants;i++)
                {
                    ant[i] = new Ant(problem);
                    ant[i].start();
                    try {
                    	ant[i].join();
                    	}
                    catch(Exception e)
                    {
                    	e.printStackTrace();
                    }
//                    ant[i].run();
                }

                problem.evaporatePheromone();

                int best_fitness = 99999;
                int ant_idx = -1;
                for (int i=0;i<n_of_ants;i++)
                {
                    int fitness = ant[i].computeFitness();
                    if (fitness<best_fitness)
                    {
                        best_fitness = fitness;
                        ant_idx = i;
                    }
                }

                /*System.out.println("before local search idx = " +  ant_idx);
                control.computeHCV(ant[ant_idx].solution);*/

               /* for(int i = 0 ; i < ant[ant_idx].solution.data.n_of_events ; i++)
                {
                    if(sln.elementAt(i).second == -1)
                        counter = counter + 1;
                    else
                        System.out.println("Event = "+i+" Time = "+sln.elementAt(i).first+
                                " Room = "+sln.elementAt(i).second);
                }*/

                System.out.println("Be LS Room = ");
                ant[ant_idx].solution.localSearch(Integer.MAX_VALUE,120);
                
                ant[ant_idx].solution.computeFeasibility();
                if (ant[ant_idx].solution.feasible)
                {
                    ant[ant_idx].solution.computeScv();
                    if (ant[ant_idx].solution.scv<=best_solution.scv)
                    {
                        best_solution.copy(ant[ant_idx].solution);
                        best_solution.hcv = 0;
                        control.setCurrentCost(best_solution);
                    }
                }

                else
                {
                    ant[ant_idx].solution.computeHCV();
                    if (ant[ant_idx].solution.hcv<=best_solution.hcv)
                    {
                        best_solution.copy(ant[ant_idx].solution);
                        control.setCurrentCost(best_solution);
                        best_solution.scv = Integer.MAX_VALUE;
                    }
                }

                Solution tmp_solution = ant[ant_idx].solution;
                ant[ant_idx].solution = best_solution;
                problem.pheromoneMinMax();

                ant[ant_idx].computeFitness();
                ant[ant_idx].depositPheromone();
                ant[ant_idx].solution = tmp_solution;

            }

            control.endTry(best_solution);
            
        }
    }
}