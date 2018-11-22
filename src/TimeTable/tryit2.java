package TimeTable;

import java.util.Vector;

public class tryit2 {
    public static void main(String args[])
    {
        example obj = new example();
        obj.u = 9;
        obj.y = 8;
        obj.cf = 9.78;
        obj.ft = 8.34f;
        obj.pop = "Hi how are you?";
        obj.some.addElement(2);
        obj.some.addElement(3);
        obj.some.addElement(4);
        obj.some.addElement(5);
        obj.some.addElement(6);
        obj.some.addElement(7);
        obj.some.addElement(8);
        example obj2 = new example();
        obj2 = obj;
        System.out.println("obj.u " + obj2.u);
        System.out.println("obj.y " + obj2.y);
        System.out.println("obj.cf " + obj2.cf);
        System.out.println("obj.ft " + obj2.ft);
        System.out.println("obj.pop " + obj2.pop);
        System.out.println("obj.some " + obj2.some);
    }
}
class example{
    int u;
    int y;
    double cf;
    float ft;
    String pop;
    Vector some = new Vector<Integer>();
}
