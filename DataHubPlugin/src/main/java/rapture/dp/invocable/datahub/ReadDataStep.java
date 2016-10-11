package rapture.dp.invocable.datahub;

import com.google.common.base.Charsets;

import rapture.common.BlobContainer;
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
        
        String inboundBlob = Kernel.getDecision().getContextValue(ctx, getWorkerURI(), "inboundBlob");
        
        if (inboundBlob == null) { inboundBlob = "blob://demo.blobs.archive/input/2.csv"; }
        
        log.info("Inbound blob to process: " + inboundBlob);
        
        String data = getInboundBlob(ctx, inboundBlob).trim();
       
        Kernel.getDecision().setContextLiteral(ctx, getWorkerURI(), "data", data);

        return "ok";
    }
    
    protected String getInboundBlob(CallingContext ctx, String filename) {
        BlobContainer bc = Kernel.getBlob().getBlob(ctx, filename);
        if (bc != null) {
            return new String(bc.getContent(), Charsets.UTF_8);
        }
        throw new RuntimeException("Failed to find file: " + filename);
    }
}