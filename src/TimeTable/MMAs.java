package TimeTable;

import java.io.IOException;

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

        while (control.triesLeft())
        {
            control.beginTry();
            problem.pheromoneReset();

            best_solution.RandomInitialSolution();
            best_solution.computeFeasibility();
            control.setCurrentCost(best_solution);

            long startTime = System.currentTimeMillis();

            while ((System.currentTimeMillis() - startTime) < 5000)
            {

                Control poop = new Control();
                for (int i=0;i<n_of_ants;i++)
                {
                    ant[i] = new Ant(problem);
                    //ant[i].start();
                    ant[i].run();
                }

                int iop = poop .computeHCV(best_solution);
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
//                ant[ant_idx].solution.localSearch(100,2);
                int ui = poop.computeHCV(ant[ant_idx].solution);

                /*System.out.println("Before statistics for best solution");
                System.out.println(control.computeHCV(best_solution));*/

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

                /*System.out.println("After statistics for best solution");
                System.out.println(control.computeHCV(best_solution));*/
            }

            control.endTry(best_solution);
            System.exit(0);
        }
    }
}