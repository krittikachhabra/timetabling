package TimeTable;

public class Random
{
    int IA = 16807;
    int IM = 2147483647;
    double AM = (1.0/IM);
    int IQ = 127773;
    int IR = 2836;
    long seed;
    Random(final int arg)
    {
    	seed = arg;
    }
    double next() { return ran01(seed);}

    double ran01( long idum )
    {
        long k;
        double ans;

        k =(idum)/IQ;
        idum = IA * (idum - k * IQ) - IR * k;

        if (idum < 0 )
            idum += IM;

        ans = AM * (idum);

        return ans;
    }

    int[] generate_array(int size)
    {
        int  i, j, help;

        int[] v = new int[size];

        for ( i = 0 ; i < size; i++ )
           v[i] = i;

        for ( i = 0 ; i < size-1 ; i++)
        {
            j = (int) ( ran01((long)(100*Math.random()) * (size - i)));
            assert( i + j < size );
            help = v[i];
            v[i] = v[i+j];
            v[i+j] = help;
         }

        for (i = 0 ; i < size ; i++ )
            System.out.println(v[i]);
        return v;
    }

}
