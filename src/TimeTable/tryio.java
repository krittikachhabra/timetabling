package TimeTable;

//Java program to demonstrate redirection in System.out.println() 
import java.io.*; 

public class tryio 
{ 
	public static void main(String arr[]) throws FileNotFoundException 
	{ 
		// Creating a File object that represents the disk file. 
		
		System.out.println("This will be written to the text file"); 
		System.out.println("yuuyu");

		// Use stored value for output stream 
		System.setOut(console); 
		System.out.println("This will be written on the console!"); 
	} 
} 
