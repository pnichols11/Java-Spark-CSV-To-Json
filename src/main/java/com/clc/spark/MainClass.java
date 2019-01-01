/**
 * 
 */
package com.clc.spark;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 * @author nichols
 *
 */
public class MainClass {
   private static Logger LOG =Logger.getLogger(MainClass.class.getName());
   
   
   public static void main (String args[]) {
	   //Get the current file Context
	    File file= new File("");
	    String zipCodeFile=file.getAbsolutePath();
	    //Create a Spark Session and initialize the session
	    System.out.println("\n\tLoading Spark Session.... Please wait...");
		SparkSession spark = SparkSession
             .builder()
             .appName("Spark CSV to JSON Example")
             //master is set to local and [*] tells the Spark Session to use all CPU cores
             .master("local[*]")
             .getOrCreate();
		//Load a CSV File
		 //We are creating the DataSet data, to read a csv file and to ignore malformed records.
		 final Dataset<Row> data = spark.read().format("csv").option("header",true).option("mode","DROPMALFORMED").load(zipCodeFile + "/resource/free-zipcode-database.csv");
		 //This Dataset will pull only the columns we care about.
		 final Dataset<Row> dfCityStateZip = data.select(("City"), ("State"), ("Zipcode"), ("Lat"), ("Long"));
		 //We are going to write the CSV Dataset to a Json File.
		 //This will create several chunk files by default, which is preferred
		 //for Hadoop HDFS. However, we want to use this locally, therefore
		 // we will use the coalesce function and write only once.
		 //This will create a directory with several SUCCESS files and crc checksum.
		 dfCityStateZip.coalesce(1).write().json(zipCodeFile+"/resource/json");
		 //Merge this to a single file.
	      mergeToSingleFile(zipCodeFile+"/resource/json/", zipCodeFile+"/resource/","free-zipcode-database.json");
	 
   }
   /**
    * This merge function will delete the temp Spark directory structures and all files and copy
    * the single file we want for local use.
    * 
    * @param src
    * @param dest
    * @param newFileName
    */
   private static void mergeToSingleFile(String src, String dest,String newFileName) {
	    LOG.info("Entering mergeToSingleFile.");
	      try {
	    	 //See if the current merge file exist. If so, delete it.	
	    	   File dFile= new File(dest+newFileName);
	    		   if(dFile.exists()) {
	    			 LOG.info("Deleting existing file: "+dFile.getName());
	    			   dFile.delete();
	    		   }	
	    	 //Get the src file directory and iterate through it looking for our single .json file	   
	    	     File file=new File(src);
	    	//Create the destination file
	    	     File destFile=new File(dest+newFileName);
	    	//Get all files in the Spark created directory where the files have been written.     
	    		 File[] srcFile=file.listFiles();
	       //Loop through the files. If it ends with .json, that is one we want to copy.
	       //Otherwise, delete the file and finally delete the directory.		 
	    		 for (File f:srcFile) {
	    			   if ( f.isFile()){
	    			      String fileExt=f.getName().substring(f.getName().length()-5, f.getName().length());
	    			       if ( fileExt.equalsIgnoreCase(".json")){
	    			    	  LOG.info("JSON File Exists. Moving to default directory.");
	    				      FileUtils.copyFile(f, destFile);
	    			       }
	    			       LOG.info("Deleting file: "+f.getName());
	    			       f.delete();
	    			   }    
	    		   }
	    		   LOG.info("Delete the Spark output directory.");
	    		   file.delete();
	     }	 
	     catch(IOException ioex) {
	        LOG.error("Error writing file.");
	    	LOG.error(ioex);
	     }
	     System.out.println("New Json File Written.");
   }
	
}
