package rapture.dp.invocable.datahub;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import com.google.gson.Gson;

import rapture.common.CallingContext;
import rapture.datahub.DataHubSocketSessions;
import rapture.dp.invocable.common.AbstractStep;
import rapture.kernel.Kernel;

public class WriteDataStep extends AbstractStep {

    public WriteDataStep(String workerUri, String stepName) {
        super(workerUri, stepName);
    }

    @Override
    public String invoke(CallingContext ctx) {
    	
    	String data = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "data");

    	StringReader in = new StringReader(data);

    	try {
	    	Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);

	    	String jsonOut = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "jsonOut");
	    	Integer hashCode = Integer.parseInt(Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "hashCode"));
	    	
	    	if (jsonOut == null) { jsonOut = "test_map.json"; }

	    	Gson gson = new Gson();

			
	    	Map<String, Object> recordMap = new HashMap<String, Object>();
	    	
	    	recordMap.put("id", "1");
	    	recordMap.put("value", "test");
	    	
	    	List<Map<String, String>> mydata = new ArrayList<Map<String, String>>();
	    	
	    	for (CSVRecord record : records) {
	    			
	    		//long row = record.getRecordNumber();
	    		
	    		mydata.add(record.toMap());

			}
	    	
	    	recordMap.put("data", mydata);
	    	
	    	String jsonUri = "document://demo.docs.archive/web_demo/" + jsonOut;
	    	log.info("Writing JSON to " + jsonUri);

	    	Kernel.getDoc().putDoc(ctx, 
	    					  jsonUri, 
	    					  gson.toJson(recordMap));

	    	log.info("Checking session");
	    	DataHubSocketSessions sessions = new DataHubSocketSessions();
	    	Session session = sessions.getSession(hashCode);
	    	
	    	if (session == null) {
	    		log.info("No web session associated");
	    	} else {
	    		log.info("Updating session...");
	    		
	    		session.getRemote().sendString(String.valueOf(new JSONObject()
        				.put("getData", "ok")
        				.put("docName", jsonOut)
        				));
	    		
	    	}
    	} catch (IOException e) {
    		log.error(e);
    	}
    	

    	
        return "ok";
    }

}
