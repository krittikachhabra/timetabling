package TimeTable;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;

public class MMAsProblem extends Problem
{
    double evap;
    double phe_max;
    double phe_min;
    double[][] event_timeslot_pheromone; // matrix keeping pheromone between events and timeslots
    ArrayList<Integer> sorted_event_list = new ArrayList<Integer>();// vector keeping sorted lists of events

    public MMAsProblem(double evap, double phe_min) throws IOException
    {
        super();
        event_timeslot_pheromone = new double[n_of_events][Definitions.N_OF_TIMESLOTS];
        this.evap = evap;
        this.phe_min = phe_min;

        if (evap < 1.0)
            phe_max = 1.0 / (1.0 - evap);

        else
            phe_max = 999999;

        int event_correlation[] = new int[n_of_events];
        for (int i = 0; i < n_of_events; i++)
        {
            event_correlation[i] = 0;
            for (int j = 0; j < n_of_events; j++)
                event_correlation[i] += eventCorrelations[i][j];
        }

        for (int i = 0; i < n_of_events; i++)
        {
            int max_correlation = -1;
            int event_index = -1;
            for (int j = 0; j < n_of_events; j++)
            {
                if (event_correlation[j] > max_correlation)
                {
                    max_correlation = event_correlation[j];
                    event_index = j;
                }
            }
            event_correlation[event_index] = -1;
            sorted_event_list.add(event_index);

        }
    }

    void pheromoneReset()
    {
        // initialize pheromon levels between events and timeslots to the maximal values
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < Definitions.N_OF_TIMESLOTS; j++)
            {
                event_timeslot_pheromone[i][j] = phe_max;
            }
        }
    }

    void evaporatePheromone()
    {
        // evaporate some pheromone
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < Definitions.N_OF_TIMESLOTS; j++)
            {
                event_timeslot_pheromone[i][j] *= evap;
            }
        }
    }

    void pheromoneMinMax()
    {
        // limit pheromone values according to MAX-MIN
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < Definitions.N_OF_TIMESLOTS; j++)
            {
                if (event_timeslot_pheromone[i][j] < phe_min)
                {
                    event_timeslot_pheromone[i][j] = phe_min;
                }

                if (event_timeslot_pheromone[i][j] > phe_max)
                {
                    event_timeslot_pheromone[i][j] = phe_max;
                }
            }
        }
    }
}
