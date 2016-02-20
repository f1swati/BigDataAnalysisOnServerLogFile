/* This Program was developed by Swati Mittal. 
 * Date - 20-Feb-2016
 * 
 * Objective -  
 * The Program analyses the server log file of API calls and provides the list
 * of the top 5 IP addresses (users) which have the maximum API calls for the log 
 * The objective of the program is to read a  huge unsorted server log file (big data)
 * and provide a meaningful analysis with the memory and processing power constraints
 *
 * Constraints:
 * Server is visited by 10,000 users per day
 * Log file size is > 2 GB
 *
 * Additional Feature:
 * In addition to option to analyse the complete file
 * You can also choose the period limit in months from current date which you want to consider
 *  
 * To ensure that program performs in case of higher loads I have tested it for 7 test cases:
 * Test Case 1: TestCase for 20 Million Server Log records - 2.1 GB
 * Test Case 2: TestCase for 50 Million Server Log records - 5.5 GB
 * Test Case 3: TestCase for 1 single IP address with multiple API calls in the log
 * Test Case 4: TestCase for Empty File
 * Test Case 5: TestCase Manual File with less records
 * Test Case 6: TestCase for Date Range within past 3 months empty
 * 
 * The Test Files are present in the TestCases folder
 * Note: The File Generation program can be used to generate the 50 Million record file
 * The file size is 5.5 GB. 
 * The 20 Million and 50 Million files are not being updated to github for size limitation
 * A sample file is stored instead with the same file name but with less number of records
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;



public class ServerLogFileAnalysisMain {
	static FileReader inputFile;
	static BufferedReader br;
	static HashMap<String, Integer> ip = new HashMap<String, Integer>();
	static PriorityQueue<ipAdressCount> priorityQueue;

//	static File in = new File("testCases/TestCase.txt");
	
	//Input Server Log file
	//Test Case 1:
	//static File in = new File("testCases/TestCase_20Mil.txt");
		/* Output - "Output/Output.Txt"
		 * Expected - Program does not terminate in error
		 * Number of records = 20 Million, Number of unique IP = 5 Million, File Size = 2 GB
		 * HashMap has been used which works well for storing on a 8 GB RAM machine
		 * Program displays the IP address with most API calls.
		 */
	//Test Case 2:
	static File in = new File("testcases/TestCase.txt");
		/* Output - "Output/Output.Txt"
		 * Expected output - Program does not terminate in error for 50 million records
		 * Number of records = 50 Million, Number of unique IP = 10 Million, File Size = 5.5 GB
		 * HashMap has been used which works well for storing on a 8 GB RAM machine
		 * Program displays the IP address with most API calls.
		 */
	//Test Case 3:
	//static File in = new File("testCases/TestCase_Single_IP_Records.txt");
		/* Output - "Output/Output.Txt"
		 * Expected Output Program displays only 1 IP address with the number of API call count
		 */
	//Test Case 4:
	//static File in = new File("testCases/TestCase_Empty_File.txt");
		/* Output - "Output/Output.Txt"
		 * Expected Output:
		 * Program displays no IP address and does not terminate
		 */
	//Test Case 5:
	//static File in = new File("testCases/TestCase_Manual_File.txt");
		/* Output - "Output/Output.Txt"
		/* Expected Output:
		 * 110.170.202.179	has the highest count
		 * 122.155.235.234	has the second highest count but is missing in month of Feb 2016
		 * if you select month and enter 1 month then you will not see 122.155.235.234 in the output
		 */
	//Test Case 6:
//	static File in = new File("testCases/TestCase_Date_Range_with_past_3_months_empty.txt");
		/* Expected Output:
		 * Program displays no IP address and does not terminate when the months option is selected
		 * and less than 3 months are entered
		 */
	
	public static void main(String[] args) throws IOException, ParseException {

		Scanner input = new Scanner(System.in);
		long startTime, estimatedTime;
		int count = 1;

		System.out.println("Press '1' to enter number of past months to be considered for analysis ");
		System.out.println("Press any other key to analyse the complete log");

		String choice = input.next();

		double bytes = in.length();
		double kilobytes = (bytes / 1024);
		double megabytes = (kilobytes / 1024);
		double gigabytes = (megabytes / 1024);

		// Print Input File Name and Size
		System.out.println("Input File Name : " + in.getName());
		System.out.println("File Size : " + gigabytes + "GB");
		
		switch (choice) {
		case "1":
			System.out.println("Enter Number of month");
			int inputMonth = input.nextInt();
			startTime = System.nanoTime();
			//Function to only consider the IP address API calls in the given period
			timeRelevantLogFileAnalysis(inputMonth);
			break;
		default:
			startTime = System.nanoTime();
			//Function to consider the IP address API calls during entire duration
			entireDurationLogFileAnalysis();
			break;
		}
		
		// Print the unique IP address count in the input file
		double size =  ip.size()/1000000;
		if (ip.size() > 500000)
			System.out.println("Number of Unique IP Address : " + size + " Million");
		else
			System.out.println("Number of Unique IP Address : " + ip.size());	
		System.out.println();
		
		if (ip.size() == 0)
			System.out.println("Input file is empty for the selected period");
		/* minHeap implementation using Priority Queue
		 * minHeap is used to store the 5 IP address with maximum API call count
		 * The minimum count of the maximums is the node of the heap and makes it easier to remove
		 * when iterating a hashMap in case of a new maximum found 
		 */ 
		Comparator<ipAdressCount> queueComparator = new ipComparator();
		priorityQueue = new PriorityQueue<ipAdressCount>(5, queueComparator);
		for (String key1 : ip.keySet()) {

			int value1 = ip.get(key1);
			ipAdressCount temp = new ipAdressCount(key1, value1);

			if (count <= 5) {
				priorityQueue.add(temp);
				count++;
			} else if (value1 > priorityQueue.peek().count) {
				priorityQueue.poll();
				priorityQueue.add(temp);
			}
		}

		//Calculating time consumed for the entire algorithm run
		estimatedTime = System.nanoTime() - startTime;
		
		// to print the ip Address with their count
		print();
		
		
		
		System.out.println("\nAlgorithm Elapsed Time: " + TimeConverter.convertTimeToString(estimatedTime) + "\n");
	}
	
	private static void print() {
		int[] arrayCount = new int[5];
		String[] arrayIP = new String[5];
		int k = 0;
		for (ipAdressCount increment : priorityQueue) {
		arrayCount[k]=increment.count;
		arrayIP[k] = increment.ip;
		k++;
		}
		
		for(int i=0; i < 5; i++){
            for(int j=1; j < (5-i); j++){
                    if(arrayCount[j-1] < arrayCount[j]){
                          int temp = arrayCount[j-1];
                    		arrayCount[j-1] = arrayCount[j];
                    		arrayCount[j] = temp;
                    		String temp2 = arrayIP[j-1];
                    		arrayIP[j-1] = arrayIP[j];
                    		arrayIP[j] = temp2;
                    	}
            }
		}
		
		
		System.out.println("IP Address" + "		|" + "Count");
		for(int i=0; i < 5; i++){
	        System.out.println(arrayIP[i] + "		|" +arrayCount[i]);
		}
		
				
	}

	private static void entireDurationLogFileAnalysis() {

		String[] key;
		int value;
		String sCurrentLine = " ";

		// Reads the record from input file and stores the IP address in the hashMap
		// if the IP address already exists in the hashMAP it increases the count by 1
		try {
			inputFile = new FileReader(in);
			br = new BufferedReader(inputFile);

			while ((sCurrentLine = br.readLine()) != null) {

				key = sCurrentLine.split("\\s+");
				if (ip.containsKey(key[0])) {

					value = ip.get(key[0]) + 1;
					ip.put(key[0], value);

				} else {
					ip.put(key[0], 1);
				}
			}
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void timeRelevantLogFileAnalysis(int inputMonth) throws ParseException {
		try {
			inputFile = new FileReader(in);
			br = new BufferedReader(inputFile);

			String sCurrentLine = " ";
			String[] key;
			int value;

			ArrayList<String> list = new ArrayList<String>();

			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");

			Date now = new Date();
			String date = dateFormat.format(now);

			//Calculate System Date and Time
			Date currentDate = dateFormat.parse(date);

			//Parse Input record to find the time stamp and IP address
			while ((sCurrentLine = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(sCurrentLine, " []");

				while (st.hasMoreTokens()) {
					list.add(st.nextToken().toString());

				}

				//Convert token from timestamp in input to date format
				Date first = dateFormat.parse(list.get(3));
				//Calculate difference between system data and record date
				long diff = currentDate.getTime() - first.getTime();

				//Convert the difference in months
				long month = (diff / (24 * 60 * 60 * 1000)) * 12 / 365;
				
				//Check to see if the difference in months is <= the number of months in the user input
				if (ip.containsKey(list.get(0))) {
					value = ip.get(list.get(0)) + 1;
					ip.put(list.get(0), value);
				} else if (month <= inputMonth) {
					ip.put(list.get(0), 1);
				}
				list.clear();
			}
			inputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
