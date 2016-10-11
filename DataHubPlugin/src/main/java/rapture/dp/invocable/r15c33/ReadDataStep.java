package rapture.dp.invocable.r15c33;

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
        String data = getBlobAsString(ctx, "ALOC15C33_data.txt").trim();
        Kernel.getDecision().setContextLiteral(ctx, getWorkerURI(), "data", data);

        return "ok";
    }
}