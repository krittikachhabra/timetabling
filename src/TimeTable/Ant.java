package TimeTable;

import java.util.Vector;

public class Ant {
    int fitness;
    Solution solution;
    MMAsProblem problem;

    Ant(MMAsProblem problem, Random rnd)
    {
    	System.out.println("Ant called");
        // memeber variables initialization
        this.problem = problem;
        solution = new Solution(problem,rnd);
        fitness = -1;
    }

    void Move()
    {
        // itarate through all the events to complete the path
        for (int i=0;i<problem.n_of_events;i++)
        {
            // chose next event from the list
            int e = problem.sorted_event_list.get(i);

            // finding the range for normalization
            double range = 0.0;
            for (int j=0;j<Definitions.N_OF_TIMESLOTS;j++)
                range += problem.event_timeslot_pheromone[e][j];

            // choose a random number between 0.0 and sum of the pheromone level
            // for this event and current sum of heuristic information
//            double yu = ;
//            System.out.println("solution.rg.next() = " + (yu*range));
            double rnd = solution.rg.next() * range;

            // choose a timeslot for the event based on the pheromone table and the random number
            double total = 0.0;
            int timeslot = -1;
            for(int j=0;j<Definitions.N_OF_TIMESLOTS;j++)
            {
                // check the pheromone
                total += problem.event_timeslot_pheromone[e][j];
//            	System.out.println(total);
                if (total>=rnd)
                {
                    timeslot = j;
                    break;
                }
            }
            // put an event i into timeslot t
            
            solution.sln.get(e).first = timeslot;
            
            if(solution.timeslot_events.containsKey(timeslot))
            	solution.timeslot_events.get(timeslot).addElement(e);
            else
            {
            	Vector <Integer> vtemp = new Vector<Integer>();
            	vtemp.addElement(e);
            	solution.timeslot_events.put(timeslot, vtemp);
            }
            
//            solution.timeslot_events.get(timeslot).addElement(e);
        }

        // assign rooms to events in each non-empty timeslot
        for(int i=0;i<Definitions.N_OF_TIMESLOTS;i++)
            if(solution.timeslot_events.get(i) != null)
                solution.assignRooms(i);
    }

    void depositPheromone()
    {
        // calculate pheromone update
        for (int i=0;i<problem.n_of_events;i++)
        {
            int timeslot = solution.sln.get(i).first;
            problem.event_timeslot_pheromone[i][timeslot] += 1.0;
        }
    }

    int computeFitness()
    {
        // simple fitness function
        fitness = solution.computeHCV();
        return fitness;
    }
}
