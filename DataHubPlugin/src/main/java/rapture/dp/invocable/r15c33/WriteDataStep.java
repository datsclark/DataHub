package rapture.dp.invocable.r15c33;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import rapture.common.CallingContext;
import rapture.dp.invocable.common.AbstractStep;
import rapture.kernel.Kernel;

public class WriteDataStep extends AbstractStep {
	
	private static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss.S";
	public String dtm = new DateTime(DateTimeZone.UTC).toString(DateTimeFormat.forPattern(TIME_PATTERN));

    public WriteDataStep(String workerUri, String stepName) {
        super(workerUri, stepName);
    }

    @Override
    public String invoke(CallingContext ctx) {
         String data = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "data");
         
         String load_id = new String();
         int lineCount = 0;
         List<String> columns = new ArrayList<>();

         Scanner scanner = new Scanner(data);
         log.info("Processing Data...");
         while (scanner.hasNextLine()) {
             String line = scanner.nextLine();
             if (lineCount == 0) {
            	 ++lineCount;
            	 load_id = line.toString();
            	 log.info("Processing for " + load_id);

            	 continue;
             } else if (lineCount == 1) {
            	 log.info("Reading Header...");
            	 log.info(line.toString());
                 columns = Arrays.asList(StringUtils.splitPreserveAllTokens(line.toString().toLowerCase().replace(" ", "_"), "|"));
                 ++lineCount;
                 continue;
             }
             List<String> values = Arrays.asList(StringUtils.splitPreserveAllTokens(line, "|"));
             Map<String, Object> vmap = new HashMap<>();
             if (columns.size() == values.size()) {
                 for (int i = 0; i < columns.size()-1; i++) {
                     vmap.put(columns.get(i), values.get(i));
                 }
                 
                 vmap.put("Load_dtm", dtm);
                 vmap.put("Load_id", load_id);
                 
                 Kernel.getStructured().insertRow(ctx, "//demo/t15c33", vmap);
             }
             ++lineCount;
         }
         
         log.info("Inserted " + lineCount + " rows");
         scanner.close();
         return "ok";
    }

}
