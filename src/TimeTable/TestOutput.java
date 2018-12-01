package TimeTable;

public class TestOutput {
    Solution slnTest;

    public void printSln(Solution slnInput)
    {
        slnTest = slnInput;
        System.out.println("(Event,TimeSlot,Room)");
        for(int lc=0;lc<slnTest.sln.size();lc++){
            System.out.print("(" + lc  + "," + slnTest.sln.elementAt(lc).first
                            + "," + slnTest.sln.elementAt(lc).second + ")");
        }
    }

    public void printTSlotEvents(Solution slnInput)
    {
        slnTest = slnInput;
        System.out.println("TimeSlot : List of events");

        for(int lc=0;lc<slnTest.timeslot_events.size();lc++){
            System.out.print(lc + " : ");

            for(int ilc=0;ilc<slnTest.timeslot_events.get(lc).size();ilc++)
                System.out.print(slnTest.timeslot_events.get(lc).elementAt(ilc) + " ");

            System.out.print("; ");
        }
    }


}