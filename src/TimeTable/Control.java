package TimeTable;

public class Control
{
    int nrTry, maxTry;
    boolean feasible;
    int bestScv;
    int bestEvaluation;
    int bestHCV = Integer.MAX_VALUE;

    public Solution BestSolution;

    Control()
    {
        nrTry = 0;
        maxTry = Definitions.DEFAULT_MAX_STEPS;
    }

    void setCurrentCost(Solution currentSolution )
    {
        currentSolution.computeScv();
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

    void endTry(Solution bestSolution)
    {
        if (bestSolution.feasible) {
            System.out.println("feasible: evaluation function = " + bestSolution.scv);
            BestSolution = bestSolution;
        }

        else
        {
            int currentHCV = bestSolution.computeHCV();

            if(bestHCV > currentHCV ) {
                bestHCV = currentHCV;
                System.out.println("\n"+nrTry + " : HCV = "+currentHCV);
                computeHCV(bestSolution);
            }

            /*if(nrTry % 10 == 0)
            {
                System.out.println(+nrTry + " : HCV = " + currentHCV);
                //printSolution(bestSolution);
            }*/
        }
    }

    boolean triesLeft() { return ( nrTry < maxTry ); }

    void beginTry()
    {
        ++nrTry;
        feasible = false;
        bestScv = Integer.MAX_VALUE;
        bestEvaluation = Integer.MAX_VALUE;
    }

    void printSolution(Solution bestSolution)
    {
        for(int i = 0 ; i < bestSolution.data.n_of_events ; i++)
                {
                    System.out.println("Event = "+i+" Time = "+bestSolution.sln.elementAt(i).first+
                            " Room = "+bestSolution.sln.elementAt(i).second);
                }

    }

    int computeHCV(Solution bestSolution)
    {
        int hcv = 0;
        int roomOverLap = 0;
        int studentScrewed = 0;
        int roomScrewed = 0;
        for (int i = 0; i < bestSolution.data.n_of_events; i++)
        {
            if(bestSolution.sln.elementAt(i).second == -1) {        // if room not assigned, increment hcv
                hcv = hcv + 1;
            }
            for (int j = i+1; j < bestSolution.data.n_of_events; j++)
            {
                if ( (bestSolution.sln.elementAt(i).second != -1 ) &&
                        (bestSolution.sln.elementAt(i).first == bestSolution.sln.elementAt(j).first) && (bestSolution.sln.elementAt(i).second == bestSolution.sln.elementAt(j).second))
                { // only one class can be in each room at any timeslot
                    hcv = hcv + 1;
                    roomOverLap = roomOverLap + 1;
                    System.out.println(i + " overlap with " + j + ", " + bestSolution.sln.elementAt(i).first
                                    + " " + bestSolution.sln.elementAt(j).second);
                }
                if ((bestSolution.sln.elementAt(i).first == bestSolution.sln.elementAt(j).first) && (bestSolution.data.eventCorrelations[i][j] == 1))
                {  // two events sharing students cannot be in the same timeslot
                    hcv = hcv + 1;
                    studentScrewed = studentScrewed + 1;
                }
            }
            //System.out.println("Event = "+i+"timeslot = " + sln.elementAt(i).first + ", room =  " + sln.elementAt(i).second);
            if(bestSolution.sln.elementAt(i).second != -1)
                if( bestSolution.data.possibleRooms[i][bestSolution.sln.elementAt(i).second]  == 0 )
                    // an event should take place in a suitable room
                {
                    hcv = hcv + 1;
                }
        }
        System.out.println("RoomOverLap = "+roomOverLap);
        System.out.println("Student is screwed = "+studentScrewed);
        System.out.println("Room is screwed = "+(hcv - studentScrewed - roomOverLap));
        return hcv;
    }


    int getRoomOverlap(Solution bestSolution)
    {
        int hcv = 0;
        int roomOverLap = 0;
        int studentScrewed = 0;
        int roomScrewed = 0;
        for (int i = 0; i < bestSolution.data.n_of_events; i++)
        {
            for (int j = i+1; j < bestSolution.data.n_of_events; j++)
            {
                if ( (bestSolution.sln.elementAt(i).second != -1 ) &&
                        (bestSolution.sln.elementAt(i).first == bestSolution.sln.elementAt(j).first) && (bestSolution.sln.elementAt(i).second == bestSolution.sln.elementAt(j).second))
                { // only one class can be in each room at any timeslot
                    hcv = hcv + 1;
                    roomOverLap = roomOverLap + 1;
                    System.out.println(i + " overlap with " + j + ", " + bestSolution.sln.elementAt(i).first
                            + " " + bestSolution.sln.elementAt(j).second);
                }
                if ((bestSolution.sln.elementAt(i).first == bestSolution.sln.elementAt(j).first) && (bestSolution.data.eventCorrelations[i][j] == 1))
                {  // two events sharing students cannot be in the same timeslot
                    hcv = hcv + 1;
                    studentScrewed = studentScrewed + 1;
                }
            }
            //System.out.println("Event = "+i+"timeslot = " + sln.elementAt(i).first + ", room =  " + sln.elementAt(i).second);
            if(bestSolution.sln.elementAt(i).second != -1)
                if( bestSolution.data.possibleRooms[i][bestSolution.sln.elementAt(i).second]  == 0 )
                // an event should take place in a suitable room
                {
                    hcv = hcv + 1;
                }
        }
        System.out.println("RoomOverLap = "+roomOverLap);
        System.out.println("Student is screwed = "+studentScrewed);
        System.out.println("Room is screwed = "+(hcv - studentScrewed - roomOverLap));
        return hcv;
    }
}