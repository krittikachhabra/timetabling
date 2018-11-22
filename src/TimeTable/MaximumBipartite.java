package TimeTable;

import java.util.*;
import java.lang.*;
import java.io.*;

public class MaximumBipartite {
        // M is number of events
        // and N is number of rooms
        int M,N;
        boolean bpGraph[][];
        public int matchR[];

        // A DFS based recursive function that
        // returns true if a matching for
        // vertex u is possible
        boolean bpm(int u, boolean seen[], int matchR[])
        {
            // Try every job one by one
            for (int v = 0; v < N; v++)
            {
                // If applicant u is interested
                // in job v and v is not visited
                if (bpGraph[u][v] && !seen[v])
                {

                    // Mark v as visited
                    seen[v] = true;

                    // If job 'v' is not assigned to
                    // an applicant OR previously
                    // assigned applicant for job v (which
                    // is matchR[v]) has an alternate job available.
                    // Since v is marked as visited in the
                    // above line, matchR[v] in the following
                    // recursive call will not get job 'v' again
                    if (matchR[v] < 0 || bpm(matchR[v], seen, matchR))
                    {
                        matchR[v] = u;
                        return true;
                    }
                }
            }
            return false;
        }

        // Returns maximum number
        // of matching from M to N
        int maxBPM()
        {
            // An array to keep track of the
            // event assigned to room.
            // The value of matchR[i] is the
            // event number assigned to room i,
            // the value -1 indicates nobody is assigned.
            matchR = new int[N];

            // Initially all jobs are available
            for(int i = 0; i < N; ++i)
                matchR[i] = -1;

            // Count of jobs assigned to applicants
            int result = 0;
            for (int u = 0; u < M; u++)
            {
                // Mark all jobs as not seen
                // for next applicant.
                boolean seen[] =new boolean[N] ;
                for(int i = 0; i < N; ++i)
                    seen[i] = false;

                // Find if the applicant 'u' can get a job
                if (bpm(u, seen, matchR))
                    result++;
            }
            return result;
        }

        MaximumBipartite(Solution sln, int timeSlot, Problem data)
        {
            M = sln.timeslot_events.get(timeSlot).size();
            N = data.n_of_rooms;
            bpGraph = new boolean[M][N];  // no of events X no of rooms

            for(int row = 0;row<M;row++)
            {
                for(int col = 0; col < N; col ++)
                {
                    bpGraph[row][col] = (data.possibleRooms[sln.timeslot_events.get(timeSlot).elementAt(row)][col] == 1);
                }
            }

            /*System.out.println("****************************************ss************");
            System.out.println("Configurations:-");
            System.out.println("Timeslot " + timeSlot + ", scheduled events : ");
            for(int lc:sln.timeslot_events.get(timeSlot))
            {
                System.out.print(lc + " ");
            }
            System.out.println("\n****************************************************");*/

            // call bpm. This sets matchR
            int noOfMathchings = maxBPM();

            for(int lc=0;lc<N;lc++)
            {
                if(matchR[lc]!=-1)
                    matchR[lc] = sln.timeslot_events.get(timeSlot).elementAt(matchR[lc]);
            }

            /*System.out.println("Inside Max Bipartite");
            Control controlObj = new Control();
            System.out.println(controlObj.computeHCV(sln));*/

//            System.out.println("Matchings:-");
//            for(int lc=0;lc<N;lc++)
//            {
//                if(matchR[lc]!=-1)
//                System.out.print(matchR[lc]+ " Allocated room " + lc + ", ");
//            }
//            System.out.println();

            //System.out.println(timeSlot + "(timeslot). Number of events to assign rooms" + M);
            //System.out.println("Number of matchings by assignRooms(max bipartite) : "+ noOfMathchings);
        }
    }
