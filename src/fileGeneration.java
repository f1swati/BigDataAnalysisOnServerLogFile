/*
 * This Program was developed by Swati Mittal. 
 * Date - 20-Feb-2016
 * 
 * The Program generates the input test file for testing Most frequent Site visitor program.
 * The objective of the program is to generate a simulated server log file of visitors
 *
 * Constraints:
 * Server is visited by 10,000 users per day
 * Server Log File is for 1 year
 *
 * To ensure that program performs in case of higher loads I have used following settings:
 * The current settings of the program generates 50 million record log over the period of 6 years
 * The Number of unique IP Address are also restricted to approximately 10 Million
 * 
 * Variables: Change the following variables in code for required test file
 * int numberOfRecords = 50000000;
 * String lowerDatelimit = "2010-01-01 00:00:00";
 * String upperDatelimit = "2016-02-20 00:00:00";
 * 
 * 
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;

public class fileGeneration {

	public static void main(String[] args) throws IOException {

		// Change the following parameters for different file sizes and log
		// time stamp
		int numberOfRecords = 40000000;
		String lowerDatelimit = "2010-01-01 00:00:00";
		String upperDatelimit = "2016-02-20 00:00:00";

		// Test File Path and Name
	//	File newFile = new File("testCases/TestCase.txt");
		File newFile = new File("testcases/TestCase.txt");
		FileWriter fw = new FileWriter(newFile);
		BufferedWriter bw = new BufferedWriter(fw);

		String finalLine = null;

		//Math.Random function used to generate Random IP address and Date
		for (int i = 1; i <= numberOfRecords; i++) {
			
			//Random IP Address with Limits
			String ipAddress = (int) (100 + (Math.random() * (180 - 100))) + "."
					+ (int) (128 + (Math.random() * (172 - 128))) + "." 
					+ (int) (200 + (Math.random() * (240 - 200)))
					+ "." + (int) (128 + (Math.random() * (254 - 100)));
			
			//Date within limits defined and random generated using Math.random
			long offset = Timestamp.valueOf(lowerDatelimit).getTime();
			long end = Timestamp.valueOf(upperDatelimit).getTime();
			long diff = end - offset + 1;
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
			Timestamp timeStamp = new Timestamp(offset + (long) (Math.random() * diff));
			String date = dateFormat.format(timeStamp);
			
			//Server API Calls added
			String link = "GET /news/personal-technology/?category=all&page=1 HTTP/1.1";
			
			//Return Code from Server
			String returnCode = "200 6459";
			
			//To increase some random IP counts copy the previous log line after 30,000 records
			if (i % 300001 != 0) {
				finalLine = ipAddress + " - - " + " [" + date + "] " + "\"" + link + "\"" + " " + returnCode;
			}
			
			//Write to File
			bw.write(finalLine);
			bw.append("\r\n");
		}
		//Close the file
		bw.flush();
		bw.close();
	}
}
