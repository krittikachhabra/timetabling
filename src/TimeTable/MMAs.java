package TimeTable;

import java.io.IOException;

public class MMAs
{
    public static void main(String args[])throws IOException
    {
    	System.out.println("Starting at MMAs");
        int n_of_ants = Definitions.DEFAULT_N_OF_ANTS;
        Ant[] ant = new Ant[n_of_ants];

        double pheromone_evaporation = Definitions.DEFAULT_PHEROMONE_EVAPORATION;
        double minimal_pheromone = Definitions.DEFAULT_MINIMAL_PHEROMONE;

        MMAsProblem problem = new MMAsProblem( pheromone_evaporation, minimal_pheromone);

        // create a Random object
        Control control = new Control();
        Random rnd = new Random();

        // create a buffer for holding global best solution
        Solution best_solution = new Solution((Problem)problem, rnd);

    	System.out.println(control.triesLeft());
        // run a number of tries, control knows how many tries there should be done
        while (control.triesLeft())
        {
            // tell control we are starting a new try
            control.beginTry();

            // reset the pheromone level to the initial value;
            problem.pheromoneReset();

            // initialize best solution with random value
            best_solution.RandomInitialSolution();
            best_solution.computeFeasibility();
            control.setCurrentCost(best_solution);

            // do if we still have time for current try
            //while ()
            {
                // create a set of ants
                for (int i=0;i<n_of_ants;i++)
                    ant[i] = new Ant(problem, rnd);

                // let the ants do the job - create some solutions
                for (int i=0;i<n_of_ants;i++)
                    ant[i].Move();

                // evaporate the pheromone
                problem.evaporatePheromone();

                // find the the best solution
                int best_fitness = 99999;
                int ant_idx = -1;
                for (int i=0;i<n_of_ants;i++)
                {
                    int fitness = ant[i].computeFitness();
                    if (fitness<best_fitness) {
                        best_fitness = fitness;
                        ant_idx = i;
                    }
                }

                // apply local search until local optimum is reached or a time limit reached
                ant[ant_idx].solution.localSearch(Definitions.DEFAULT_MAX_STEPS);

                // and see if the solution is feasible
                ant[ant_idx].solution.computeFeasibility();

                // output the new best solution, if found
                if (ant[ant_idx].solution.feasible) {

                    ant[ant_idx].solution.computeScv();
                    if (ant[ant_idx].solution.scv<=best_solution.scv) {
                        best_solution.copy(ant[ant_idx].solution);
                        best_solution.hcv = 0;
                        control.setCurrentCost(best_solution);
                    }
                }
			else {
                    ant[ant_idx].solution.computeHCV();
                    if (ant[ant_idx].solution.hcv<=best_solution.hcv)
                    {
                        best_solution.copy(ant[ant_idx].solution);
                        control.setCurrentCost(best_solution);
                        best_solution.scv = 99999;
                    }
                }

                // perform pheromone update with the global best solution
                Solution tmp_solution = ant[ant_idx].solution;
                ant[ant_idx].solution = best_solution;

                // and deposit pheromone for this best one
                ant[ant_idx].computeFitness();
                problem.pheromoneMinMax();
                ant[ant_idx].depositPheromone();

                ant[ant_idx].solution = tmp_solution;

                // let the ants die
                for(int i=0;i<n_of_ants;i++)
                    ant[i]  = null;

            }
            // end try - output the best solution found
            control.endTry(best_solution);
        }

        problem = null;
        ant = null;
        best_solution = null;
        rnd = null;
    }

}
