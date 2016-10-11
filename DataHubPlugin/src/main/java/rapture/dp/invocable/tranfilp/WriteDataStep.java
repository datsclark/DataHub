package rapture.dp.invocable.tranfilp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import rapture.common.CallingContext;
import rapture.dp.invocable.common.AbstractStep;
import rapture.kernel.Kernel;

public class WriteDataStep extends AbstractStep {

    public WriteDataStep(String workerUri, String stepName) {
        super(workerUri, stepName);
    }

    @Override
    public String invoke(CallingContext ctx) {
        String input = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "input");
        Scanner scanner = new Scanner(input);
        List<String> columns = new ArrayList<>();
        int lineCount = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (lineCount == 0) {
                columns = Arrays.asList(line.split(","));
                ++lineCount;
                continue;
            }
            List<String> values = Arrays.asList(line.split(","));
            Map<String, Object> vmap = new HashMap<>();
            for (int i = 0; i < columns.size(); i++) {
                vmap.put(columns.get(i), values.get(i));
            }
            Kernel.getStructured().insertRow(ctx, "//demo/tranfilp", vmap);
            ++lineCount;
        }
        scanner.close();
        return "ok";
    }

}
