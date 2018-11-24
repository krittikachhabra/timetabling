package TimeTable;

import java.util.Vector;

public class Ant extends Thread
{
    int fitness;
    Solution solution;
    MMAsProblem problem;

    Ant(MMAsProblem problem)
    {
        this.problem = problem;
        solution = new Solution(problem);
        fitness = -1;
    }

    public void run()
    {
        for (int i=0;i<problem.n_of_events;i++)
        {
            int e = problem.sorted_event_list.get(i);

            double range = 0.0;

            for (int j=0;j<Definitions.N_OF_TIMESLOTS;j++)
                range = range + problem.event_timeslot_pheromone[e][j];

            double rnd = Math.random() * range;

            double total = 0.0;
            int timeslot = -1;
            for(int j=0;j<Definitions.N_OF_TIMESLOTS;j++)
            {
                total = total + problem.event_timeslot_pheromone[e][j];
                if (total>=rnd)
                {
                    timeslot = j;
                    break;
                }
            }
            Pair tempNew = new Pair(timeslot, -1);
            solution.sln.set(e, tempNew);
            //System.out.println("Inside ant, timeslot assigned = " + timeslot + " event = " + e);

            if(solution.timeslot_events.containsKey(timeslot))
            	solution.timeslot_events.get(timeslot).addElement(e);
            else
            {
            	Vector <Integer> temp = new Vector<>();
            	temp.addElement(e);
            	solution.timeslot_events.put(timeslot, temp);
            }
        }

        for(int i=0;i<Definitions.N_OF_TIMESLOTS;i++)
        {
            if (solution.timeslot_events.get(i) != null)
            {
                solution.assignRooms(i);
            }
        }

    }

    void depositPheromone()
    {
        for (int i=0;i<problem.n_of_events;i++)
        {
            int timeslot = solution.sln.get(i).first;
//            System.out.println(i + " " + timeslot + " this is it.");
            problem.event_timeslot_pheromone[i][timeslot] += 1.0;
        }
    }

    int computeFitness()
    {
        fitness = solution.computeHCV();
        return fitness;
    }
}
