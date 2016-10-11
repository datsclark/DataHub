package rapture.dp.invocable.datahub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import rapture.common.CallingContext;
import rapture.dp.invocable.common.AbstractStep;
import rapture.kernel.Kernel;

public class WriteDataStep_copy extends AbstractStep {

    public WriteDataStep_copy(String workerUri, String stepName) {
        super(workerUri, stepName);
    }

    @Override
    public String invoke(CallingContext ctx) {
    	
    	String data = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "data");
    	
    	//log.info(data);

    	SparkSession spark = SparkSession
    			  .builder()
    			  .master("local")
    			  .appName("Demo")
    			  .config("spark.some.config.option", "some-value")
    			  .getOrCreate();
    	
    	// DON"T DELETE
    	//JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());
    	//ArrayList<String> test = new ArrayList<String>();
    	//test.add(data);
    	//val jsonStr = """{ "metadata": { "key": 84896, "value": 54 }}"""
    	//JavaRDD<String> rdd = sc.parallelize(test);

    	try {
    		File temp = File.createTempFile("tempfile", ".tmp");
    		//temp.deleteOnExit();
    		String absolutePath = temp.getAbsolutePath();
    		log.info(absolutePath);
		    BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		    bw.write(data);
		    bw.close();
		    
		    
		    
		    Dataset<Row> df = spark.read().option("header", "true").csv(absolutePath);
		    
		    //.options(header='true')
		   
		    df.printSchema();
		    df.show();
		    
		    log.info(df.schema().toString());
		    
		    
		    
		    Dataset<String> blah = df.toJSON();
		    
		    log.info(blah);
		    
		    //Kernel.getDoc().putDoc(ctx, "document://demo.docs.archive/web_demo/test.json", df.toJSON());
		    
		    
		  
		    
		}catch(IOException e){
	
		    e.printStackTrace();
	
		}

    	log.info("done");
    	
    	spark.stop();
        return "ok";
    }

}
