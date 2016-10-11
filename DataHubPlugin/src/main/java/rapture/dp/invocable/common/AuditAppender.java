package rapture.dp.invocable.common;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import rapture.common.CallingContext;
import rapture.kernel.ContextFactory;
import rapture.kernel.Kernel;

public class AuditAppender extends AppenderSkeleton {

    private CallingContext ctx = ContextFactory.getKernelUser();
    private String auditLogUri;

    public AuditAppender(String auditLogUri) {
        this.auditLogUri = auditLogUri;
        setName(AuditAppender.class.getName() + ":" + auditLogUri);
    }

    @Override
    protected void append(LoggingEvent event) {
        Kernel.getAudit().writeAuditEntry(ctx, auditLogUri, "workflow", 1, (String) event.getMessage());
        ThrowableInformation ti = event.getThrowableInformation();
        if (ti != null) {
            Kernel.getAudit().writeAuditEntry(ctx, auditLogUri, "workflow", 1, ExceptionUtils.getStackTrace(ti.getThrowable()));
        }
    }

    public String getAuditLogUri() {
        return auditLogUri;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}