package rapture.dp.invocable.tranfilp;

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
        Kernel.getDecision().setContextLiteral(ctx, getWorkerURI(), "input", getBlobAsString(ctx, "tranfilp.csv"));
        return "ok";
    }
}