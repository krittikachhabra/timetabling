package TimeTable;
import java.util.*;

public class Solution extends Thread
{
    protected Vector<Pair<Integer, Integer>> sln = new Vector<Pair<Integer, Integer>>(); // vector of (timeslot, room) assigned for each event
    protected Map<Integer, Vector<Integer>> timeslot_events = new HashMap<Integer, Vector<Integer>>(); // for each timeslot a vector of events taking place in it
    Problem data; // a pointer to the problem data

    Boolean feasible;
    int scv;   // keeps the number of soft constraint violations (ComputeScv() has to be called)
    int hcv;

    Solution(Problem pd)
    {
        data = pd;
        slnInit();
    }

    private void slnInit()
    {

        Pair initPair = new Pair(-1, -1);
        for (int i = 0; i < (data).n_of_events; i++) {
            sln.addElement(initPair);
        }

    }

    void copy(Solution orig)
    {
        this.sln = orig.sln;
        this.data = orig.data;
        this.timeslot_events = orig.timeslot_events;
        this.feasible = orig.feasible;
        this.scv = orig.scv;
        this.hcv = orig.hcv;
    }

    void RandomInitialSolution()
    {
        for (int i = 0; i < data.n_of_events; i++) {
            int t = (int) ((Math.random() * System.currentTimeMillis()) % Definitions.N_OF_TIMESLOTS);
            sln.elementAt(i).first = t;

            if (timeslot_events.containsKey(t))
                timeslot_events.get(t).addElement(i);
            else {
                Vector<Integer> vtemp = new Vector<Integer>();
                vtemp.addElement(i);
                timeslot_events.put(t, vtemp);
            }
        }
        // and assign rooms to events in each non-empty timeslot
        for (int j = 0; j < Definitions.N_OF_TIMESLOTS; j++) {
            Vector<Integer> temp = new Vector<Integer>();
            temp = timeslot_events.get(j);
            if (temp != null)
                assignRooms(j);
        }

        /*TestOutput test = new TestOutput();
        test.printTSlotEvents(this);*/
    }

    boolean computeFeasibility()
    {
        for (int i = 0; i < data.n_of_events; i++) {
            for (int j = i + 1; j < data.n_of_events; j++) {
                if ((sln.elementAt(i).first == sln.elementAt(j).first) && (sln.elementAt(i).second == sln.elementAt(j).second)) {
                    feasible = false;
                    return false;                                // only one class can be in each room at any timeslot
                }
                if ((data.eventCorrelations[i][j] == 1) && (sln.elementAt(i).first == sln.elementAt(j).first)) {
                    feasible = false;
                    return false;                                // two events sharing students cannot be in the same timeslot
                }
            }
            if (sln.elementAt(i).second != -1)
                if (data.possibleRooms[i][sln.elementAt(i).second] == 0) {
                    feasible = false;
                    return false;                 // each event should take place in a suitable room
                }
        }
        // if none of the previous hard constraint violations occurs the timetable is feasible
        feasible = true;
        return true;
    }

    int computeScv()
    {
        int consecutiveClasses, classesDay;
        boolean attendsTimeslot;

        scv = 0; // set soft constraint violations to zero to start with

        for (int i = 0; i < data.n_of_events; i++) { // classes should not be in the last slot of the day
            if (sln.elementAt(i).first % Definitions.TIMESLOTS_PER_DAY == 8)
                scv += data.studentNumber[i];  // one penalty for each student attending such a class
        }

        for (int j = 0; j < data.n_of_students; j++) { // students should not have more than two classes in a row
            consecutiveClasses = 0;
            for (int i = 0; i < Definitions.N_OF_TIMESLOTS; i++) { // count consecutive classes on a day
                if ((i % 9) == 0) {
                    consecutiveClasses = 0;
                }
                attendsTimeslot = false;
                if (timeslot_events.containsKey(i))
                    for (int k = 0; k < (timeslot_events.get(i)).size(); k++) {
                        if (data.student_events[j][timeslot_events.get(i).get(k)] == 1) {
                            attendsTimeslot = true;
                            consecutiveClasses = consecutiveClasses + 1;
                            if (consecutiveClasses > 2) {
                                scv = scv + 1;
                            }
                            break;
                        }
                    }
                if (!attendsTimeslot)
                    consecutiveClasses = 0;
            }
        }
        for (int j = 0; j < data.n_of_students; j++) { //students should not have a single class on a day
            classesDay = 0;
            for (int d = 0; d < Definitions.N_OF_WORKING_DAYS; d++) {   // for each day
                classesDay = 0;               //number of classes per day
                for (int t = 0; t < Definitions.TIMESLOTS_PER_DAY ; t++) {   // for each timeslot of the day
                    if (timeslot_events.containsKey(Definitions.TIMESLOTS_PER_DAY * d + t))
                        for (int k = 0; k < timeslot_events.get(Definitions.TIMESLOTS_PER_DAY * d + t).size(); k++) {
                            if (data.student_events[j][timeslot_events.get(Definitions.TIMESLOTS_PER_DAY * d + t).get(k)] == 1) {
                                classesDay = classesDay + 1;
                                break;            // it is attending one event in that timeslot, so break and see for the next timeslot
                            }
                        }

                    if (classesDay > 1) // if the student is attending more than one class on that day
                        break;       // go to the next day
                }
                if (classesDay == 1) {
                    scv = scv + 1;
                }
            }
        }
        return scv;
    }

    int computeHCV()
    {
        hcv = 0;
        int roomOverLap = 0;
        int studentScrewed = 0;
        for (int i = 0; i < data.n_of_events; i++) {
            for (int j = i + 1; j < data.n_of_events; j++) {
                if ((sln.elementAt(i).first == sln.elementAt(j).first) && (sln.elementAt(i).second == sln.elementAt(j).second)) { // only one class can be in each room at any timeslot
                    hcv = hcv + 1;
                    roomOverLap = roomOverLap + 1;

                }
                if ((sln.elementAt(i).first == sln.elementAt(j).first) && (data.eventCorrelations[i][j] == 1)) {  // two events sharing students cannot be in the same timeslot
                    hcv = hcv + 1;
                    studentScrewed = studentScrewed + 1;
                }
            }
            //System.out.println("Event = "+i+"timeslot = " + sln.elementAt(i).first + ", room =  " + sln.elementAt(i).second);
            if (sln.elementAt(i).second != -1)
                if (data.possibleRooms[i][sln.elementAt(i).second] == 0)  // an event should take place in a suitable room
                    hcv = hcv + 1;
        }
        //System.out.println("RoomOverLap = "+roomOverLap);
        //System.out.println("Student is screwed = "+studentScrewed);
        return hcv;
    }


    void assignRooms(int t)
    {
//        System.out.println("timeslot " + t);
        MaximumBipartite roomAllocator = new MaximumBipartite(this, t, data);
        int[] result = roomAllocator.matchR;

        for (int i = 0; i < data.n_of_rooms; i++) {
            int event = result[i];
            if (event != -1) {
//                System.out.println("before t = " + this.sln.get(event).first
  //                      + " r = " + this.sln.get(event).second);
                Pair temporary = new Pair(t, i);
                this.sln.set(event, temporary);
                // System.out.println(event+" Alloc from result set "+i + ", ");
            }
        }
        /*System.out.println("Inside solution after maxBp");
        Control controlObj = new Control();
        System.out.println(controlObj.computeHCV(this));*/

        //System.out.println("timeslot = "+t+" = "+Arrays.toString(result));
    }

    int[] firstComeFirstServe(int timeslot)
    {
        int numberOfRooms = data.n_of_rooms;
        int numberOfEvents = timeslot_events.get(timeslot).size();

        int temporary[] = new int[numberOfRooms];

        for (int i = 0; i < numberOfRooms; i++)
            temporary[i] = -1;

        for (int i = 0; i < numberOfRooms; i++) {
            for (int j = 0; j < numberOfEvents; j++) {
                //int eventToBeHeld = timeslot_events.get(timeslot).elementAt((int)(Math.random() * numberOfEvents));
                int eventToBeHeld = timeslot_events.get(timeslot).elementAt(j);

                if (data.possibleRooms[eventToBeHeld][i] == 1) {
                    if (temporary[i] == -1) {
                        boolean allReadyAllocated = false;
                        for (int k = 0; k < i; k++) {
                            if (temporary[k] == eventToBeHeld) {
                                allReadyAllocated = true;
                                break;
                            }

                        }

                        if (!allReadyAllocated)
                            temporary[i] = eventToBeHeld;
                    }
                }
            }
        }
        return temporary;
    }


    //compute hard constraint violations that can be affected by moving event e from its timeslot
    private int eventAffectedHcv(int e)
    {
        int aHcv = 0; // set to zero the affected hard constraint violations for event e
        int t = sln.elementAt(e).first; // t timeslot where event e is
        for (int i = 0; i < timeslot_events.get(t).size(); i++)
        {
            for(int j= i+1;  j < timeslot_events.get(t).size(); j++)
            {
                if (sln.elementAt(timeslot_events.get(t).get(i)).second == sln.elementAt(timeslot_events.get(t).get(j)).second) {
                    aHcv = aHcv + 1; // adds up number of room clashes in the timeslot of the given event (rooms assignement are affected by move for the whole timeslot)
                    //cout << "room + timeslot in common "  <<aHcv <<" events " << timeslot_events[t][i] << " and " << timeslot_events[t][j] << endl;
                }
            }
            if(timeslot_events.get(t).get(i) != e)
            {
                if(data.eventCorrelations[e][timeslot_events.get(t).get(i)] == 1) {
                    aHcv = aHcv + 1;  // adds up number of incompatible (because of students in common) events in the same timeslot
                    // the only hcv of this type affected when e is moved are the ones involving e
                    //cout << "students in common " << aHcv <<" event " << timeslot_events[t][i] << endl;
                }
            }
        }
        // the suitable room hard constraint is taken care of by the assignroom routine
        return aHcv;
    }

    private int affectedRoomInTimeslotHcv(int t)
    {
        int roomHcv = 0;
        for(int i= 0;  i < timeslot_events.get(t).size(); i++)
        {
            for(int j= i+1;  j < timeslot_events.get(t).size(); j++)
            {
                if (sln.elementAt(timeslot_events.get(t).get(i)).second == sln.elementAt(timeslot_events.get(t).get(j)).second)
                    roomHcv += 1;
            }
        }
        return roomHcv;
    }

    // evaluate the number of soft constraint violation involving event e
    private int eventScv(int e)
    {
        int eScv = 0;
        int t = sln.elementAt(e).first;
        boolean foundRow;
        int singleClasses = data.studentNumber[e]; // count each student in the event to have a single class on that day
        int otherClasses = 0;

        if( t%9 == 8) // classes should not be in the last slot of the day
            eScv += data.studentNumber[e];

        for(int i = 0; i < data.n_of_students; i++){
            if(data.student_events[i][e] == 1){ // student should not have more than two classes in a row
                if( t%9 < 8)
                {          // check timeslots before and after the timeslot of event e
                    foundRow = false;
                    for(int j = 0; j < timeslot_events.get(t+1).size(); j++)
                    {
                        if( data.student_events[i][timeslot_events.get(t+1).get(j)] == 1)
                        {
                            if(t%9 < 7){
                                for(int k =0; k < timeslot_events.get(t+2).size(); k++)
                                {
                                    if(data.student_events[i][timeslot_events.get(t+2).get(k)] == 1)
                                    {
                                        eScv += 1;
                                        foundRow = true;
                                        break;
                                    }
                                }
                            }
                            if(t%9 > 0)
                            {
                                for(int k =0; k < (int)timeslot_events.get(t-1).size(); k++)
                                {
                                    if( data.student_events[i][timeslot_events.get(t-1).get(k)] == 1)
                                    {
                                        eScv += 1;
                                        foundRow = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(foundRow)
                            break;
                    }
                }
                if(t%9 >1)
                {
                    foundRow = false;
                    for(int j = 0; j < timeslot_events.get(t-1).size(); j++)
                    {
                        for(int k =0; k < timeslot_events.get(t-2).size(); k++)
                        {
                            if( data.student_events[i][timeslot_events.get(t-1).get(j)] == 1 && data.student_events[i][timeslot_events.get(t-2).get(k)] == 1)
                            {
                                eScv += 1;
                                foundRow = true;
                                break;
                            }
                        }
                        if(foundRow)
                            break;
                    }
                }

                otherClasses = 0; // set other classes on the day to be zero for each student
                for(int s = t - (t%9); s < t-(t%9)+9; s++){ // students should not have a single class in a day
                    if( s != t){
                        for(int j = 0; j < timeslot_events.get(s).size(); j++)
                        {
                            if(data.student_events[i][timeslot_events.get(s).get(j)] == 1)
                            {
                                otherClasses += 1;
                                break;
                            }
                        }
                        if( otherClasses > 0)
                        { // if the student has other classe on the day
                            singleClasses -= 1;  // do not count it in the number of student of event e having a single class on that day
                            break;
                        }
                    }
                }
            }
        }
        eScv += singleClasses;

        return eScv;

    }

    // compute the number of single classes that event e "solves" in its timeslot
    // obviously when the event is taken out of its timeslot this is also the number
    // of single classes introduced by the move in the day left by the event
    private int singleClassesScv(int e)
    {
        int t = (sln.get(e)).first;
        int classes, singleClasses = 0;
        for(int i = 0; i < data.n_of_students; i++)
        {
            if(data.student_events[i][e] == 1)
            {
                classes = 0;
                for(int s = t - (t%9); s < t - (t%9) + 9; s++){
                    if(classes > 1)
                        break;
                    if( s != t){ // we are in the feasible region so there are not events sharing students in the same timeslot
                        for(int j = 0; j < (int)timeslot_events.get(s).size(); j++)
                        {
                            if(data.student_events[i][timeslot_events.get(s).get(j)] == 1){
                                classes += 1;
                                break;
                            }
                        }
                    }
                }
                // classes = 0 means that the student under consideration has a single class in the day (for event e) but that W
                // but we are not interested in that here (it is counted in eventScv(e))
                if(classes == 1)
                    singleClasses +=1;
            }
        }
        return singleClasses;
    }

    void Move1(int e, int t)
    {
        //move event e to timeslot t
        int tslot =  sln.elementAt(e).first;
        sln.elementAt(e).first = t;

        Iterator i = timeslot_events.get(tslot).iterator();
        int counter = 0;
        while(i.hasNext())
        {
            counter = counter + 1;
            if( i.next().equals(e) || counter >= timeslot_events.get(tslot).size())
                break;

        }
        timeslot_events.get(tslot).remove(i); // erase event e from the original timeslot
        if(timeslot_events.containsKey(t))
        	timeslot_events.get(t).addElement(e); // and place it in timeslot t
        else
        {
        	Vector<Integer> arg1 = new Vector<Integer>();
        	arg1.addElement(e);
			timeslot_events.put(t, arg1);
        }
        // reorder in label order events in timeslot t

//        System.out.println("Start Sort timeslot_events");
        Collections.sort(timeslot_events.get(t));
//        System.out.println("Sorted timeslot_events");

        // reassign rooms to events in timeslot t
        assignRooms(t);
        // do the same for the original timeslot of event e if it is not empty
        if(timeslot_events.get(tslot).size() > 0)
            assignRooms(tslot);
    }

    void Move2(int e1, int e2)
    {
        //swap timeslots between event e1 and event e2
        int t = sln.elementAt(e1).first;
        sln.elementAt(e1).first = sln.elementAt(e2).first;
        sln.elementAt(e2).first = t;
        Iterator i = timeslot_events.get(t).iterator();

        while(i.hasNext()){
//        	i.next();
            if( i.next().equals(e1))
                break;
        }
        timeslot_events.get(t).remove(i);
        timeslot_events.get(t).addElement(e2);
        i = timeslot_events.get(sln.elementAt(e1).first).iterator();
        while(i.hasNext()){
//        	i.next();
            if( i.next().equals(e2))
                break;
        }
        timeslot_events.get(sln.elementAt(e1).first).remove(i);
        timeslot_events.get(sln.elementAt(e1).first).addElement(e1);

        //sort(timeslot_events[t].begin(),timeslot_events[t].end());
        Collections.sort(timeslot_events.get(t));
        Collections.sort(timeslot_events.get(sln.get(e1).first));
        assignRooms( sln.elementAt(e1).first);
        assignRooms( sln.elementAt(e2).first);
    }

    void Move3(int e1, int e2, int e3)
    {
        // permute event e1, e2, and e3 in a 3-cycle
        int t = sln.elementAt(e1).first;
        sln.elementAt(e1).first = sln.elementAt(e2).first;
        sln.elementAt(e2).first = sln.elementAt(e3).first;
        sln.elementAt(e3).first = t;
        Iterator i = timeslot_events.get(t).iterator();
        while(i.hasNext()){
//        	i.next();
            if(i.next().equals(e1))
                break;
        }
        timeslot_events.get(t).remove(i);
        timeslot_events.get(t).addElement(e3);
        i = timeslot_events.get(sln.elementAt(e1).first).iterator();
        while(i.hasNext()){
//        	i.next();
            if( i.next().equals(e2))
                break;
        }
        timeslot_events.get(sln.elementAt(e1).first).remove(i);
        timeslot_events.get(sln.elementAt(e1).first).addElement(e1);
        i = timeslot_events.get(sln.elementAt(e2).first).iterator();
        while(i.hasNext()){
        	i.next();
            if( i.equals(e3))
                break;
        }
        timeslot_events.get(sln.elementAt(e2).first).remove(i);
        timeslot_events.get(sln.elementAt(e2).first).addElement(e2);

        Collections.sort(timeslot_events.get(sln.get(e1).first));
        Collections.sort(timeslot_events.get(sln.get(e2).first));
        Collections.sort(timeslot_events.get(sln.get(e3).first));

        assignRooms( sln.get(e1).first);
        assignRooms( sln.get(e2).first);
        assignRooms( sln.get(e3).first);
    }

    void randomMove()
    {
        //pick at random a type of move: 1, 2, or 3
        int moveType, e1;
        moveType = (int)(Math.random()*3) + 1;
        e1 = (int)(Math.random()*(data.n_of_events));
        if(moveType == 1){  // perform move of type 1
            int t = (int)(Math.random()*45);
            Move1( e1, t);
            //cout<< "event " << e1 << " in timeslot " << t << endl;
        }
        else if(moveType == 2)
        { // perform move of type 2
            int e2 = (int)(Math.random()*(data.n_of_events));
            while(e2 == e1) // take care of not swapping one event with itself
                e2 = (int)(Math.random()*(data.n_of_events));
            Move2( e1, e2);
            // cout << "e1 "<< e1 << " e2 " << e2 << endl;
        }
        else{ // perform move of type 3
            int e2 = (int)(Math.random()*(data.n_of_events));
            while(e2 == e1)
                e2 = (int)(Math.random()*(data.n_of_events));
            int e3 = (int)(Math.random()*(data.n_of_events));
            while(e3 == e1 || e3 == e2) // take care of having three distinct events
                e3= (int)(Math.random()*(data.n_of_events));
            //cout<<"e1 " << e1 << " e2 " << e2 << " e3 " << e3<< endl;
            Move3( e1, e2, e3);
        }
    }

    void localSearch(int maxSteps,double Seconds)
    {
        //int maxSteps,
        double prob1 = 1.0;
        double prob2 = 1.0;
        double prob3 = 0.0;

        long FinishTime = System.currentTimeMillis() + (long)(1000*Seconds);

        // perform local search with given time limit and probabilities for each type of move
        // timer.resetTime(); // reset time counter for the local search

        int eventList[] = new int[data.n_of_events]; // keep a list of events to go through
        for(int i = 0; i < data.n_of_events; i++)
            eventList[i] = i;

        for(int i = 0; i < data.n_of_events; i++)
        { // scramble the list of events to obtain a random order
            int j = (int)(Math.random()*data.n_of_events);
            int h = eventList[i];
            eventList[i] = eventList[j];
            eventList[j] = h;
        }
        int neighbourAffectedHcv = 0; // partial evaluation of neighbour solution hcv
        int neighbourScv = 0; // partial evaluation of neighbour solution scv
        int evCount = 0;     // counter of events considered
        int stepCount = 0; // set step counter to zero
        boolean foundbetter = false;
        computeFeasibility();

//        System.out.println("Computed Feasibility");

        if(!feasible )
        { // if the timetable is not feasible try to solve hcv
            for( int i = 0; evCount < data.n_of_events; i = (i+1)% data.n_of_events)
            {
                if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                    break;
                int currentHcv = eventHcv(eventList[i]);
                if(currentHcv == 0 ){ // if the event on the list does not cause any hcv
                    evCount++; // increase the counter
                    continue; // go to the next event
                }
                // otherwise if the event in consideration caused hcv
                int currentAffectedHcv;
                int t_start = (int)(Math.random()*45); // try moves of type 1
                int t_orig = sln.get(eventList[i]).first;

                for(int h = 0, t = t_start; h < 45; t= (t+1)%45, h++)
                {
                    if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                        break;

                    if(Math.random() < prob1)
                    {
                        // with given probability
                        stepCount++;
                        //System.out.println("stepCount = "+stepCount);
                        Solution neighbourSolution = new Solution( data );
                        neighbourSolution.copy( this );
                        //cout<< "event " << eventList[i] << " timeslot " << t << endl;
                        neighbourSolution.Move1(eventList[i],t);
                        neighbourAffectedHcv = neighbourSolution.eventAffectedHcv(eventList[i]) + neighbourSolution.affectedRoomInTimeslotHcv(t_orig);
                        currentAffectedHcv = eventAffectedHcv(eventList[i]) + affectedRoomInTimeslotHcv(t);
                        if( neighbourAffectedHcv < currentAffectedHcv){
                            //cout<<"current hcv " << computeHcv() << "neighbour " << neighbourSolution.computeHcv()<< endl;
                            copy( neighbourSolution );
                            neighbourSolution = null;
                            evCount = 0;
                            foundbetter = true;
                            break;
                        }
                        neighbourSolution = null;
                    }
                }

//                System.out.println("Exisited Prob1");

                if(foundbetter)
                {
                    foundbetter = false;
                    continue;
                }

                if(prob2 != 0)
                {
                    for(int j= (i+1)%data.n_of_events; j != i ;j = (j+1)%data.n_of_events){ // try moves of type 2
//                    	System.out.println("entered for");
                        if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                            break;
                        if(Math.random() < prob2)
                        { // with given probability
//                        	System.out.println("inside if");
                            stepCount++;
                            Solution neighbourSolution = new Solution( data);
                            neighbourSolution.copy( this );
                            neighbourSolution.Move2(eventList[i],eventList[j]);
                            //cout<< "event " << eventList[i] << " second event " << eventList[j] << endl;
                            neighbourAffectedHcv = neighbourSolution.eventAffectedHcv(eventList[i])+neighbourSolution.eventAffectedHcv(eventList[j]);
                            currentAffectedHcv = eventAffectedHcv(eventList[i]) + eventAffectedHcv(eventList[j]);

                            if( neighbourAffectedHcv < currentAffectedHcv)
                            {
                                //cout<<"current hcv " << computeHcv() << "neighbour " << neighbourSolution.computeHcv()<< endl;
                                copy( neighbourSolution );
                                neighbourSolution = null;
                                evCount = 0;
                                foundbetter = true;
                                break;
                            }
                            neighbourSolution = null;
                        }
                    }
                    if(foundbetter)
                    {
                        foundbetter = false;
                        continue;
                    }
                }
                if(prob3 != 0)
                {
                    for(int j= (i+1)%data.n_of_events; j != i; j = (j+1)%data.n_of_events){ // try moves of type 3
                        if( stepCount > maxSteps)
                            break;
                        for(int k= (j+1)%data.n_of_events; k != i ; k = (k+1)%data.n_of_events){
                            if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                                break;
                            if(Math.random() < prob3)
                            { // with given probability
                                stepCount++;
                                currentAffectedHcv = eventAffectedHcv(eventList[i]) + eventAffectedHcv(eventList[j]) + eventAffectedHcv(eventList[k]);
                                Solution neighbourSolution = new Solution( data);
                                neighbourSolution.copy( this );
                                neighbourSolution.Move3(eventList[i],eventList[j], eventList[k]); //try one of the to possible 3-cycle
                                //cout<< "event " << eventList[i] << " second event " << eventList[j] << " third event "<< eventList[k] << endl;
                                neighbourAffectedHcv = neighbourSolution.eventAffectedHcv(eventList[i])+ neighbourSolution.eventAffectedHcv(eventList[j])
                                        + neighbourSolution.eventAffectedHcv(eventList[k]);

                                if( neighbourAffectedHcv < currentAffectedHcv )
                                {
                                    copy( neighbourSolution );
                                    neighbourSolution = null;
                                    evCount = 0;
                                    foundbetter = true;
                                    break;
                                }
                                neighbourSolution = null;
                            }
                            if( stepCount > maxSteps)
                                break;
                            if(Math.random() < prob3)
                            {  // with given probability
                                stepCount++;
                                currentAffectedHcv = eventAffectedHcv(eventList[i]) + eventAffectedHcv(eventList[k]) + eventAffectedHcv(eventList[j]);
                                Solution neighbourSolution = new Solution( data );
                                neighbourSolution.copy( this );
                                neighbourSolution.Move3(eventList[i],eventList[k], eventList[j]); //try one of the to possible 3-cycle
                                //cout<< "event " << eventList[i] << " second event " << eventList[j] << " third event "<< eventList[k] << endl;
                                neighbourAffectedHcv = neighbourSolution.eventAffectedHcv(eventList[i])+ neighbourSolution.eventAffectedHcv(eventList[k])
                                        + neighbourSolution.eventAffectedHcv(eventList[j]);

                                if( neighbourAffectedHcv < currentAffectedHcv )
                                {
                                    copy( neighbourSolution );
                                    neighbourSolution = null;
                                    evCount = 0;
                                    foundbetter = true;
                                    break;
                                }
                                neighbourSolution = null;
                            }
                        }
                        if(foundbetter)
                            break;
                    }
                    if(foundbetter)
                    {
                        foundbetter = false;
                        continue;
                    }
                }
                evCount++;
            }
        }

//        System.out.println(" if(!feasible ) terminated");

        computeFeasibility();
        if(feasible)
        { // if the timetable is feasible
            evCount = 0;
            int neighbourHcv;
            for( int i = 0; evCount < data.n_of_events; i = (i+1)% data.n_of_events)
            { //go through the events in the list
                if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                    break;
                int currentScv = eventScv(eventList[i]);
                //cout << "event " << eventList[i] << " cost " << currentScv<<endl;
                if(currentScv == 0 ){ // if there are no scv
                    evCount++; // increase counter
                    continue;  //go to the next event
                }
                // otherwise try all the possible moves
                int t_start = (int)(Math.random()*45); // try moves of type 1
                for(int h= 0, t = t_start; h < 45; t= (t+1)%45, h++){
                    if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                        break;
                    if(Math.random() < prob1){ // each with given propability
                        stepCount++;
                        Solution neighbourSolution = new Solution( data);
                        neighbourSolution.copy( this );
                        neighbourSolution.Move1(eventList[i],t);
                        //cout<< "event " << eventList[i] << " timeslot " << t << endl;
                        neighbourHcv =  neighbourSolution.eventAffectedHcv(eventList[i]); //count possible hcv introduced by move
                        if(neighbourHcv == 0){ // consider the move only if no hcv are introduced
                            //cout<< "reintroduced hcv" << neighbourSolution.computeHcv()<< endl;
                            neighbourScv = neighbourSolution.eventScv(eventList[i])  // respectively Scv involving event e
                                    + singleClassesScv(eventList[i]) // + single classes introduced in day of original timeslot
                                    - neighbourSolution.singleClassesScv(eventList[i]); // - single classes "solved" in new day
                            //cout<< "neighbour cost " << neighbourScv<<" " << neighbourHcv<< endl;
                            if( neighbourScv < currentScv){
                                //cout<<"current scv " << computeScv() << "neighbour " << neighbourSolution.computeScv()<< endl;
                                copy( neighbourSolution );
                                neighbourSolution = null;
                                evCount = 0;
                                foundbetter = true;
                                break;
                            }
                        }
                        neighbourSolution = null;
                    }
                }
                if(foundbetter)
                {
                    foundbetter = false;
                    continue;
                }
                if(prob2 != 0){
                    for(int j= (i+1)%data.n_of_events; j != i ;j = (j+1)%data.n_of_events){ //try moves of type 2
                    	if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                            break;
                        if(Math.random() < prob2)
                        { // with the given probability
                            stepCount++;
                            Solution neighbourSolution = new Solution( data);
                            neighbourSolution.copy( this );
                            //cout<< "event " << eventList[i] << " second event " << eventList[j] << endl;
                            neighbourSolution.Move2(eventList[i],eventList[j]);
                            //count possible hcv introduced with the move
                            neighbourHcv = neighbourSolution.eventAffectedHcv(eventList[i]) + neighbourSolution.eventAffectedHcv(eventList[j]);
                            if( neighbourHcv == 0){ // only if no hcv are introduced by the move
                                //cout<< "reintroduced hcv" << neighbourSolution.computeHcv()<< endl;
                                // compute alterations on scv for neighbour solution
                                neighbourScv =  neighbourSolution.eventScv(eventList[i]) + singleClassesScv(eventList[i]) - neighbourSolution.singleClassesScv(eventList[i])
                                        + neighbourSolution.eventScv(eventList[j]) + singleClassesScv(eventList[j]) - neighbourSolution.singleClassesScv(eventList[j]);
                                // cout<< "neighbour cost " << neighbourScv<<" " << neighbourHcv<< endl;
                                if( neighbourScv < currentScv + eventScv(eventList[j])){ // if scv are reduced
                                    //cout<<"current scv " << computeScv() << "neighbour " << neighbourSolution.computeScv()<< endl;
                                    copy( neighbourSolution ); // do the move
                                    neighbourSolution = null;
                                    evCount = 0;
                                    foundbetter = true;
                                    break;
                                }
                            }
                            neighbourSolution = null;
                        }
                    }
                    if(foundbetter)
                    {
                        foundbetter = false;
                        continue;
                    }
                }
                if(prob3 != 0){
                    for(int j= (i+1)%data.n_of_events; j != i; j = (j+1)%data.n_of_events){ //try moves of type 3
                        if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                            break;
                        for(int k= (j+1)%data.n_of_events; k != i ; k = (k+1)%data.n_of_events){
                            if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                                break;
                            if(Math.random() < prob3){ // with given probability try one of the 2 possibles 3-cycles
                                stepCount++;
                                Solution neighbourSolution = new Solution( data);
                                neighbourSolution.copy( this );
                                neighbourSolution.Move3(eventList[i],eventList[j], eventList[k]);
                                // cout<< "event " << eventList[i] << " second event " << eventList[j] << " third event "<< eventList[k] << endl;
                                // compute the possible hcv introduced by the move
                                neighbourHcv = neighbourSolution.eventAffectedHcv(eventList[i]) + neighbourSolution.eventAffectedHcv(eventList[j])
                                        + neighbourSolution.eventAffectedHcv(eventList[k]);
                                if(neighbourHcv == 0){ // consider the move only if hcv are not introduced
                                    // compute alterations on scv for neighbour solution
                                    neighbourScv = neighbourSolution.eventScv(eventList[i]) + singleClassesScv(eventList[i]) - neighbourSolution.singleClassesScv(eventList[i])
                                            + neighbourSolution.eventScv(eventList[j]) + singleClassesScv(eventList[j]) - neighbourSolution.singleClassesScv(eventList[j])
                                            + neighbourSolution.eventScv(eventList[k]) + singleClassesScv(eventList[k]) - neighbourSolution.singleClassesScv(eventList[k]);
                                    // cout<< "neighbour cost " << neighbourScv<<" " << neighbourHcv<< endl;
                                    if( neighbourScv < currentScv+eventScv(eventList[j])+eventScv(eventList[k])){
                                        copy( neighbourSolution );
                                        neighbourSolution = null;
                                        evCount = 0;
                                        foundbetter = true;
                                        break;
                                    }
                                }
                                neighbourSolution = null;
                            }
                            if(stepCount > maxSteps ||System.currentTimeMillis()>FinishTime)
                                break;
                            if(Math.random() < prob3){ // with the same probability try the other possible 3-cycle for the same 3 events
                                stepCount++;
                                Solution neighbourSolution = new Solution( data );
                                neighbourSolution.copy( this );
                                neighbourSolution.Move3(eventList[i],eventList[k], eventList[j]);
                                // cout<< "event " << eventList[i] << " second event " << eventList[k] << " third event "<< eventList[j] << endl;
                                // compute the possible hcv introduced by the move
                                neighbourHcv = neighbourSolution.eventAffectedHcv(eventList[i]) + neighbourSolution.eventAffectedHcv(eventList[k])
                                        + neighbourSolution.eventAffectedHcv(eventList[j]);
                                if(neighbourHcv == 0){ // consider the move only if hcv are not introduced
                                    // compute alterations on scv for neighbour solution
                                    neighbourScv = neighbourSolution.eventScv(eventList[i]) + singleClassesScv(eventList[i]) - neighbourSolution.singleClassesScv(eventList[i])
                                            + neighbourSolution.eventScv(eventList[k]) + singleClassesScv(eventList[k]) - neighbourSolution.singleClassesScv(eventList[k])
                                            + neighbourSolution.eventScv(eventList[j]) + singleClassesScv(eventList[j]) - neighbourSolution.singleClassesScv(eventList[j]);
                                    // cout<< "neighbour cost " << neighbourScv<<" " << neighbourHcv<< endl;
                                    if( neighbourScv < currentScv+eventScv(eventList[k])+eventScv(eventList[j])){
                                        copy( neighbourSolution );
                                        neighbourSolution = null;
                                        evCount = 0;
                                        foundbetter = true;
                                        break;
                                    }
                                }
                                neighbourSolution = null;
                            }
                        }
                        if(foundbetter)
                            break;
                    }
                    if(foundbetter){
                        foundbetter = false;
                        continue;
                    }
                }
                evCount++;
            }
        }
    }

    private int eventHcv(int e)
    {
        int eHcv = 0; // set to zero hard constraint violations for event e
        int t = sln.elementAt(e).first; // note the timeslot in which event e is
        for (int i = 0; i < timeslot_events.get(t).size(); i++)
        {
            if ((timeslot_events.get(t).get(i)!=e))
            {
                if (sln.elementAt(e).second == sln.elementAt(timeslot_events.get(t).get(i)).second)
                {
                    eHcv = eHcv + 1; // adds up number of events sharing room and timeslot with the given one
                    //cout << "room + timeslot in common "  <<eHcv <<" event " << i << endl;
                }
                if(data.eventCorrelations[e][timeslot_events.get(t).get(i)] == 1)
                {
                    eHcv = eHcv + 1;  // adds up number of incompatible( because of students in common) events in the same timeslot
                    //cout << "students in common " << eHcv <<" event " << i << endl;
                }
            }
        }
        // the suitable room hard constraint is taken care of by the assignroom routine
        return eHcv;
    }
}