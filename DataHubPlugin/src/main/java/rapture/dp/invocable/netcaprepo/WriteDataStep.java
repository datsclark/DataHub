package rapture.dp.invocable.netcaprepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import rapture.common.CallingContext;
import rapture.dp.invocable.common.AbstractStep;
import rapture.kernel.Kernel;

public class WriteDataStep extends AbstractStep {

    public WriteDataStep(String workerUri, String stepName) {
        super(workerUri, stepName);
    }

    @Override
    public String invoke(CallingContext ctx) {
        String header = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "header");
        String data = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "data");

        Scanner headerScanner = new Scanner(header);
        List<String> columns = new ArrayList<>();
        if (headerScanner.hasNextLine()) {
            String line = headerScanner.nextLine();
            columns = Arrays.asList(line.split("|"));
        }
        headerScanner.close();

        Scanner scanner = new Scanner(data);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            List<String> values = Arrays.asList(StringUtils.splitPreserveAllTokens(line, "|"));
            Map<String, Object> vmap = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                vmap.put(columns.get(i), values.get(i));
            }
            Kernel.getStructured().insertRow(ctx, "//demo/netcaprepo", vmap);
        }
        scanner.close();
        return "ok";
    }

}
