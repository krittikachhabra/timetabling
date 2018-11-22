package TimeTable;

import java.io.* ;

public class Problem
{
    public int n_of_events;
    public int n_of_rooms;
    public int n_of_features;
    public int n_of_students;

    public int roomSize[];
    public int[][] student_events; // student attendance matrix
    public int[][] eventCorrelations; // matrix keeping pre-processed information on events having students in common
    public int[][] room_features; // matrix keeping information on features satisfied by rooms
    public int[][] event_features; // matrix keeping information on features required by events

    public int[][] possibleRooms; // matrix keeping pre-processed information on which room are suitable for each event
    public int[] studentNumber;


    private void Room(BufferedReader in)throws IOException
    {
        roomSize = new int[n_of_rooms];
        for(int i = 0; i< n_of_rooms; i++)//fill the RoomSize
        {
            roomSize[i] = Integer.parseInt(in.readLine());
        }
    }

    private void StudentEvent(BufferedReader in)throws IOException
    {
        student_events = new int[n_of_students][n_of_events];
        for (int i = 0; i < n_of_students; i++)//Fill student i has to attend event j
        {
            for (int j = 0; j < n_of_events; j++)
            {
                student_events[i][j] = Integer.parseInt(in.readLine());
            }
        }
    }

    private void StudentNumber()
    {
        studentNumber = new int[n_of_events];
        for (int i = 0; i < n_of_events; i++)
        {
            int sum = 0;
            for (int j = 0; j < n_of_students; j++)
            {
                sum = sum + student_events[j][i];
            }
            studentNumber[i] = sum;
        }
    }

    private void EventCorrelation()
    {
        // calculate event correlations in terms of students in common and store them in the eventCorrelations matrix
        eventCorrelations = new int[n_of_events][n_of_events];
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < n_of_events; j++)
            {
                eventCorrelations[i][j] = 0;
            }
        }
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < n_of_events; j++)
            {
                for (int k = 0; k < n_of_students; k++)
                {
                    if ((student_events[k][i] == 1) && (student_events[k][j] == 1))
                    {
                        eventCorrelations[i][j] = 1;
                        break;
                    }
                }
            }
        }

    }

    private void RoomFeatures(BufferedReader in)throws IOException
    {
        // read features satisfied by each room and store them in the room_features matrix
        room_features = new int[n_of_rooms][n_of_features];
        for (int i = 0; i < n_of_rooms; i++)
        {
            for (int j = 0; j < n_of_features; j++)
            {
                room_features[i][j] = Integer.parseInt(in.readLine());
            }
        }
    }

    private void EventFeature(BufferedReader in)throws IOException
    {
        // read features required by each event and store them in the event_features matrix
        event_features = new int[n_of_events][n_of_features];
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < n_of_features; j++)
            {
                event_features[i][j]= Integer.parseInt(in.readLine());
            }
        }
    }

    private void PossibleRooms()
    {
        // pre-process which rooms are suitable for each event
        possibleRooms = new int[n_of_events][n_of_rooms];
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < n_of_rooms; j++)
            {
                possibleRooms[i][j] = 0;
            }
        }

        int k = 0;
        for (int i = 0; i < n_of_events; i++)
        {
            for (int j = 0; j < n_of_rooms; j++)
            {
                if((roomSize[j] >= studentNumber[i]))
                {
                    for(k = 0; k < n_of_features; k++)
                    {
                        if(event_features[i][k] == 1 && room_features[j][k] == 0)
                            break;
                    }

                    if(k == n_of_features)
                        possibleRooms[i][j] = 1;
                }
            }
        }
    }

    public Problem()throws IOException
    {
        String FilePath = "resources/test.txt";
        File file = new File(FilePath);
        BufferedReader in = new BufferedReader(new FileReader(file));
        n_of_events = Integer.parseInt(in.readLine());
        n_of_rooms = Integer.parseInt(in.readLine());
        n_of_features = Integer.parseInt(in.readLine());
        n_of_students = Integer.parseInt(in.readLine());

        Room(in);
        StudentEvent(in);
        StudentNumber();
        EventCorrelation();
        RoomFeatures(in);
        EventFeature(in);
        PossibleRooms();
    }
}
