
//Set appropriate package name

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;

import scala.Tuple2;

/**
 * This class uses RDDs to obtain word count for each word; json files are treated as text file
 * The year-month is obtained as a dataset of String
 * */

public class WordCount {
	public static void main(String[] args) {
			
		//Input dir - should contain all input json files
		String inputPath="/home/neo/Documents/Sem 5/DBIS/lab7b/newsdata"; //Use absolute paths 
		
		//Ouput dir - this directory will be created by spark. Delete this directory between each run
		String outputPath="/home/neo/Documents/Sem 5/DBIS/lab7b/output";   //Use absolute paths
		
		String entityPath = "/home/neo/Documents/Sem 5/DBIS/lab7b/entities.txt";
		String positivePath = "/home/neo/Documents/Sem 5/DBIS/lab7b/positive-words.txt";
		String negativePath = "/home/neo/Documents/Sem 5/DBIS/lab7b/negative-words.txt";

		Set<String> entity = new HashSet<>();
		Set<String> positive = new HashSet<>();
		Set<String> negative = new HashSet<>();
		
		SparkSession sparkSession = SparkSession.builder()
				.appName("Lab7B")		//Name of application
				.master("local")								//Run the application on local node
				.config("spark.sql.shuffle.partitions","2")		//Number of partitions
				.getOrCreate();
		
		try {
			Scanner sc = new Scanner(new File(entityPath));
			while(sc.hasNext()) {
				entity.add(sc.next());
			}
			sc.close();
			
			sc = new Scanner(new File(positivePath));
			while(sc.hasNext()) {
				positive.add(sc.next());
			}
			sc.close();

			sc = new Scanner(new File(negativePath));
			while(sc.hasNext()) {
				negative.add(sc.next());
			}
			sc.close();

			Dataset<Row> inputDataset=sparkSession.read().option("multiLine", true).json(inputPath);
			StructType structType = new StructType();
		    structType = structType.add("source_name", DataTypes.StringType, false); // false => not nullable
		    structType = structType.add("year_month", DataTypes.StringType, false); // false => not nullable
		    structType = structType.add("entity", DataTypes.StringType, false); // false => not nullable
		    structType = structType.add("sentiment", DataTypes.IntegerType);
		    ExpressionEncoder<Row> dateRowEncoder = RowEncoder.apply(structType);

		    Dataset<Row> myDataset=inputDataset.flatMap(new FlatMapFunction<Row,Row>(){
				public Iterator<Row> call(Row row) throws Exception {
					// The first 7 characters of date_published gives the year-month 
					String yearMonthPublished = ((String)row.getAs("date_published")).substring(0, 7);
	                String source = ((String)row.getAs("source_name"));
					String body = ((String)row.getAs("article_body"));
					body = body.toLowerCase().replaceAll("[^A-Za-z]", " ");
					body = body.replaceAll(" ( )+", " ");
					body = body.trim();
					List<String> wordList = Arrays.asList(body.split(" ")); //Get words
					List<Row> rowList = new ArrayList<Row>();
					for (int i = 0; i < wordList.size(); i++) {
						int sentiment_present = 0;
						if (entity.contains(wordList.get(i))) {
							for (int j = Math.max(0, i-5); j < Math.min(i+6, wordList.size()); j++) {
								if (positive.contains(wordList.get(j))){
									sentiment_present = 1;
									rowList.add(RowFactory.create(source, yearMonthPublished, wordList.get(i), 1));
								}
								else if (negative.contains(wordList.get(j))){
									sentiment_present = 1;
									rowList.add(RowFactory.create(source, yearMonthPublished, wordList.get(i), -1));
								}
							}
							if (sentiment_present == 0)
								rowList.add(RowFactory.create(source, yearMonthPublished, wordList.get(i), 0));
						}
					}
					return rowList.iterator();
				}
			
			}, dateRowEncoder);

			myDataset = myDataset.groupBy("source_name", "year_month", "entity", "sentiment").count().as("count");
			Dataset<Row> output = myDataset.groupBy("source_name", "year_month", "entity").agg(functions.sum(myDataset.col("sentiment").multiply(myDataset.col("count"))).alias("overall_sentiment"), functions.sum("count").alias("overall_support"));
			output = output.filter(output.col("overall_support").gt(4));
			output = output.drop(output.col("overall_support"));
			output = output.orderBy(functions.abs(output.col("overall_sentiment")).desc());

			// output.show();
			output.toJavaRDD().saveAsTextFile(outputPath);
		}

		catch(FileNotFoundException exception){
			exception.printStackTrace();
		}
	}
}
