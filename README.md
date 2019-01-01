# Java-Spark-CSV-To-Json
<p>
  <h3>Description</h3>
</p>
This is a simple Java Spark 2 example that uses Spark Datasets to load a CSV File and then convert it to JSON. The code is offered as is with no warranties or support.
<p>
  The program uses a directory within the default path named /resource/free-zipcode-database.csv as the default file. This file   is loaded into a SparkSession and assigned to a Spar Dataset. 
</p>  
<p>
  Once the file is loaded, the program will create a new Dataset that will copy on the following fields:
    <ol>
      <li> City</li>
      <li> State </li>
      <li> Zipcode </li>
      <li> Lat</li>
      <li>Long</li>
    </ol>
</p>
<p>
  The Dataset is then converted to a Json Dataset using the Dataset.coalesce(1).write().json(<filePathAndName>). This creates a single file structure that contains a sub-directory named json under the resource folder. This is the Hadoop or HDFS structure you would want when using a HDFS cluster.
</p>
<p>
  However, since we want to use this Json file as a local file system file, we use a mergeToSingleFile function that will move the part file into one json file and delete the files under the /resource/json folder and the folder itself. This will produce a single json file in the resource folder named free-zipcode-database.json.
</p>  

