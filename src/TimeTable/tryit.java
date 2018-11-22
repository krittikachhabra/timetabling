package TimeTable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

public class tryit {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Vector< Pair<Integer,Integer>>  temp = new Vector< Pair<Integer,Integer>>();
		Map<Integer, Vector<Integer> > timeslot_events = new HashMap<Integer, Vector<Integer> >();
		Vector <Integer> vtemp = new Vector<Integer>();
    	vtemp.addElement(2);
    	vtemp.addElement(3);
    	vtemp.addElement(4);
    	vtemp.addElement(5);
    	vtemp.addElement(6);
    	vtemp.addElement(7);
    	vtemp.addElement(8);
    	System.out.println(timeslot_events.put(3, vtemp));
    	System.out.println(timeslot_events.put(6, vtemp));
//		Vector <Integer> temp = new Vector<Integer>();
		temp.addElement(new Pair (5,8));
		temp.addElement(new Pair (5,8));
		temp.addElement(new Pair (5,8));
		temp.addElement(new Pair (5,8));
		temp.addElement(new Pair (5,8));
		temp.addElement(new Pair (5,8));
		temp.elementAt(0).first = 6;
		temp.elementAt(0).second = 7;
		temp.elementAt(1).first = temp.elementAt(0).first;
		for(int i=0;i<temp.size();i++)
		{
			System.out.print(temp.elementAt(i).first + " " + temp.elementAt(i).second);
			System.out.println(", " + temp.elementAt(i).first + " " + temp.elementAt(i).second);
		}
		temp.elementAt(2).first += 4;
		System.out.println(", " + temp.elementAt(2).first + " " + temp.elementAt(2).second);
//		timeslot_events.put(65,temp2);
		System.out.println(timeslot_events.size());
		temp.get(timeslot_events.get(3).get(0)).second = 9;
		System.out.println(temp.get(timeslot_events.get(3).get(0)).second +" " + vtemp.get(0));
		
		Iterator i = timeslot_events.get(3).iterator();

        while(i.hasNext()){
        	System.out.println("Yeh hai while ");
//        	i.next();
            if( i.next().equals(3))
            {
                System.out.println(i.next());
            	break;
            }
        }
	}
}

