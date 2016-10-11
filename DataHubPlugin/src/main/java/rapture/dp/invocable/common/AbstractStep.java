package rapture.dp.invocable.common;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;

import rapture.common.BlobContainer;
import rapture.common.CallingContext;
import rapture.common.RaptureURI;
import rapture.common.Scheme;
import rapture.common.dp.AbstractInvocable;
import rapture.common.dp.Worker;
import rapture.common.dp.WorkerStorage;
import rapture.dp.InvocableUtils;
import rapture.kernel.Kernel;

public abstract class AbstractStep extends AbstractInvocable<Object> {

    protected Logger log;

    private static final String ARCHIVE = "blob://demo.blobs.archive/input/";

    public AbstractStep(String workerUri, String stepName) {
        super(workerUri, stepName);
        if (log == null) {
            log = Logger.getLogger(workerUri + "/" + stepName);
            Appender app = new AuditAppender(getAuditLogUri(workerUri, stepName));
            log.removeAppender(app);
            log.addAppender(app);
        }
    }

    private String getAuditLogUri(String workerUri, String stepName) {
        RaptureURI uri = new RaptureURI(workerUri, Scheme.WORKORDER);
        Worker worker = WorkerStorage.readByFields(uri.getShortPath(), uri.getElement());
        if (worker == null) {
            return InvocableUtils.getWorkflowAuditLog("unknown", uri.getShortPath(), stepName);
        }
        return InvocableUtils.getWorkflowAuditLog(worker.getAppStatusNameStack().get(0), worker.getWorkOrderURI(), stepName);
    }

    protected String getBlobAsString(CallingContext ctx, String filename) {
        BlobContainer bc = Kernel.getBlob().getBlob(ctx, ARCHIVE + filename);
        if (bc != null) {
            return new String(bc.getContent(), Charsets.UTF_8);
        }
        throw new RuntimeException("Failed to find file: " + filename);
    }
}