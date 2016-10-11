package rapture.server;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.json.JSONObject;

import rapture.datahub.DataHubSocketSessions;


@WebSocket
public class EchoWebSocket {
	
	private static final Logger log = Logger.getLogger(EchoWebSocket.class);

    // Store sessions if you want to, for example, broadcast a message to all users
    //private static final Queue<Session> sessions = new ConcurrentLinkedQueue<>();
    
	DataHubSocketSessions sessions = new DataHubSocketSessions();

    @OnWebSocketConnect
    public void connected(Session session) {
        sessions.addSession(session);
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        sessions.removeSession(session);
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        try {
            session.getRemote().sendString(String.valueOf(new JSONObject()
                .put("userMessage", message)
                .put("hashCode", session.hashCode()))
        );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void sendUpdate(Session session, String message) throws IOException {
    	log.info(message);
    	try {
    		session.getRemote().sendString(String.valueOf(new JSONObject()
    				.put("userMessage", message)));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

}
