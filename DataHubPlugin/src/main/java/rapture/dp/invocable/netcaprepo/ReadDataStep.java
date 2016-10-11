package rapture.dp.invocable.netcaprepo;

import rapture.common.CallingContext;
import rapture.dp.invocable.common.AbstractStep;
import rapture.kernel.Kernel;

public class ReadDataStep extends AbstractStep {

    public ReadDataStep(String workerUri, String stepName) {
        super(workerUri, stepName);
    }

    @Override
    public String invoke(CallingContext ctx) {
        log.info("Running ReadDataStep...");
        String header = getBlobAsString(ctx, "NETCAPREPO_head.txt").trim();
        String data = getBlobAsString(ctx, "NETCAPREPO_data.txt").trim();
        Kernel.getDecision().setContextLiteral(ctx, getWorkerURI(), "header", header);
        Kernel.getDecision().setContextLiteral(ctx, getWorkerURI(), "data", data);

        return "ok";
    }
}