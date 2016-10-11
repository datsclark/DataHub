package rapture.datahub;

import java.util.HashMap;

import org.eclipse.jetty.websocket.api.Session;


//@ApplicationScoped
public class DataHubSocketSessions {
    private static HashMap<Integer, Session> sessions = new HashMap<Integer, Session>();
    //private final Set<Device> devices = new HashSet<>();
    
    public void addSession(Session session) {
        sessions.put(session.hashCode(), session);
        
    }

    public void removeSession(Session session) {
        sessions.remove(session.hashCode());
    }
    
    public Session getSession(Integer hashCode) {
    	try {
	    	Session session = sessions.get(hashCode);
	    	return session;
    	} catch (Exception e) {
    		return null;
    	}
        
    }
}
