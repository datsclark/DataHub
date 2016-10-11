package rapture.server;

import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.webSocket;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.ImmutableMap;

import rapture.app.RaptureAppService;
import rapture.common.CallingContext;
import rapture.common.RaptureFolderInfo;
import rapture.common.SeriesDouble;
import rapture.common.exception.RaptureException;
import rapture.common.impl.jackson.JacksonUtil;
import rapture.common.model.DocumentWithMeta;
import rapture.config.ConfigLoader;
import rapture.kernel.ContextFactory;
import rapture.kernel.Kernel;



public class DataHub {

    private static final Logger log = Logger.getLogger(DataHub.class);
    private CallingContext ctx = ContextFactory.getKernelUser();
    private static final String DYN_PATH = "/";
    private static final String DYN_DOCS = "document://config/rest/structured/";
    private static final String QMG_ROOT = "series://demo.series.archive/qmg/";
    
    private static final String ROOT_PATH = "/demo";
    private static final String QMG_PATH = ROOT_PATH + "/qmg";
    private static final String R15C33_PATH = ROOT_PATH + "/15C33";
    private static final String NETCAP_PATH = ROOT_PATH + "/NETCAP";
    private static final String TRANFILP_PATH = ROOT_PATH + "/TRANFILP";
    private static final String POSITION_PATH = ROOT_PATH + "/POS";
    private static final String TIME_PATH = ROOT_PATH + "/datetime";
    
    private static final String DEMO_INBOUND = "blob://demo.blobs.archive/web_demo/inbound/";

    public static void main(String[] args) {
        try {
            Kernel.initBootstrap(ImmutableMap.of("STD", ConfigLoader.getConf().StandardTemplate), DataHub.class, false);
            RaptureAppService.setupApp("RestServer");
        } catch (RaptureException e) {
            log.error("Failed to start RestServer", e);
            return;
        }
        new DataHub().run();
    }

	private void run() {
        Kernel.setCategoryMembership(ConfigLoader.getConf().Categories);
        
        port(4568);
        //Spark.staticFileLocation("/public");
        webSocket("/echo", EchoWebSocket.class);
        
        setupRoutes();
        
        
        //init(); // Needed if you don't define any HTTP routes after your WebSocket routes
    }

    private void setupRoutes() {
        // TODO: check auth
        before((req, res) -> {
        	res.header("Access-Control-Allow-Origin", "*");
        	//res.type("application/json");
            //log.info("not checking auth...");
            
        });
        
        
        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });


        post("/upload", "multipart/form-data", (req, res) -> {
        	try {
        		
            	req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            	req.raw().setAttribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            	log.info("processing upload....");
            	String fileName = new String();
            	
            	String csvFlag = "NO";
            	
	        	Collection<Part> parts = req.raw().getParts();
	        	for (Part part : parts) {
	        		//There should only be 1 in current web-side code
	        		
	        		String contentDisp = part.getHeader("Content-Disposition");
	        		fileName = contentDisp.replaceFirst("(?i)^.*filename=\"([^\"]+)\".*$", "$1");
	        		
	        		String contentType = part.getHeader("content-type");
	        		
	        		InputStream is = req.raw().getPart("upload").getInputStream();
	            	byte[] theFile = IOUtils.toByteArray(is);
	            	
	            	Kernel.getBlob().putBlob(ctx, DEMO_INBOUND + fileName, theFile, contentType);
	            	
	            	log.info("type: " + contentType);
	            	
	            	if (contentType.equals("text/csv")) {
	            		
	            		csvFlag = "YES";
	            	}	
	            	
	            }
	        	
	        	String rtn = "{ \"status\": \"server\", \"sname\" : \"" + 
	        			DEMO_INBOUND + 
	        			fileName + "\", \"csv\" : \"" + csvFlag +"\" }";
	        	log.info(rtn);
	        	return rtn;
	        	
        	}  catch (Exception e) {
            	log.info(e);
            	return "{ \"status\": \"error\" }";
        	}
        	
        });
        
        post("/upload_save", (req, res) -> {
        	
        	try {
        	
        	String filename = req.queryParams("files");
        	String hashCode = req.queryParams("session");
        	String csvFlag = req.queryParams("csvflag");
        	
        	log.info(csvFlag);
        	
        	
        	if (csvFlag.equals("YES")) {
	        	log.info("Run workflow now");
	    		Map<String, String> params = new HashMap<String, String>();
		        params.put("inboundBlob", DEMO_INBOUND + filename);
		        params.put("jsonOut", filename + ".json");
		        params.put("hashCode", hashCode);
		        
	    		Kernel.getDecision().createWorkOrder(ctx, "workflow://workflows/web_demo_curate", params);
	    		return "Saved and proceessing.";
        	} else {
        		return "Saved.";
        	}
        	
        	} catch (Exception e) {
        		e.printStackTrace();
        		return "Failed to upload";
        	}
        });
        
        get("/upload_response", (req, res) -> {
            log.info("Upload Response");
            System.out.println(req.params());
            
            
            String my_uri = "document://demo.docs.archive/web_demo/" + req.queryParams("docname");
            
            System.out.println("Doc: " + my_uri);
            
            List<JSONObject> myoutput = new ArrayList<JSONObject>();	
            
            try {
	            
	            DocumentWithMeta doc = Kernel.getDoc().getDocAndMeta(ctx, my_uri);
	            log.info(doc.getContent());
	            
	            JSONObject jobj = new JSONObject(doc.getContent());
	            
	            //List<Map<String, JSONObject>> myoutput = new ArrayList<Map<String, JSONObject>>();
	            	
	            Iterator<?> keys = jobj.keys();

	            while( keys.hasNext() ) {
	                String key = (String)keys.next();
	                if ( jobj.get(key) instanceof JSONObject ) {
	                	JSONObject jsonObj = (JSONObject) jobj.get(key);
	                	log.info(jsonObj);
	                	//log.info("data" + jsonObj.getJSONArray("data"));
	                	myoutput.add(jsonObj);
	                }
	            }
	            return jobj;
	            
            } catch (Exception e) {
            	//e.printStackTrace();
            	return "error";
            }
  
	       
        });
        
        
        get("/testcode", (req, res) -> {
        	
        	String value = req.queryParams("value");
        	
        	return value;
        	
/*
        	try { 
        		DataHubSocketSessions sessions = new DataHubSocketSessions();
        		
        		Session session = sessions.getSession(value);
            	
            	session.getRemote().sendString(String.valueOf(new JSONObject()
        				.put("getData", "ok")
        				));
           	 
            	log.info("test code ..." + value);
            	return "ok";
        	} catch (Exception e) {
        		//e.printStackTrace();
        		return "No session found";
        	}
*/      	 
        	
        });
        
        get(DYN_PATH + "get/*", (req, res) -> {
        	log.info("Dynamic Magic...");
        	String[] vals = req.splat()[0].split("/");
        	
        	List<JSONObject> conns = getRepos();
        	log.info("Conns" + conns);
        	
        	for (JSONObject obj: conns) {
        		JSONArray cols_raw = obj.getJSONArray("cols");
    	       	List<String> cols = new ArrayList<String>();
    	       	for (int i=0; i<cols_raw.length(); i++) {
    	       	    cols.add( cols_raw.getString(i) );
    	       	}
    	       	 
    	       	String unique_name = obj.getString("unique_name");
    	       	
    	       	if ( vals[0].equals(unique_name)) {
    	       		String uri = obj.getString("uri");
    	       		String where = " "; //obj.getString("where");
    	       		log.info("go for launch " + uri);
    	       		try {
    	       		return JacksonUtil.jsonFromObject(Kernel.getStructured().selectRows(ctx, 
    	       												uri, 
    	       												cols,
    	       												where, 
    	       												cols, 
    	       												false, 
    	       												-1));
    	       		} catch (Exception e) {
    	       			log.error("Failed to retrieve data" + e);
    	       			continue;
    	       		}
    	       	} else {
    	       		continue;
    	       	}
        	}
        	
        	return "no dice";
        });
        
        get(TIME_PATH, (req, res) -> {
            log.info("Getting system time");
            DateTime now = new DateTime(DateTimeZone.UTC);
            return JacksonUtil.jsonFromObject(ImmutableMap.of("datetime", now.toString()));
        });
        
        get(TRANFILP_PATH, (req, res) -> {
        	log.info("Getting TRANFILP Data");
        	Map<String,String> table_desc = Kernel.getStructured().describeTable(ctx, "//demo/tranfilp");
        	List<String> cols = new ArrayList<String>(table_desc.keySet());
            return JacksonUtil.jsonFromObject(Kernel.getStructured().selectRows(ctx, "//demo/tranfilp", cols, " ", cols, false, -1));
        });
        
        get(R15C33_PATH, (req, res) -> {
            log.info("Getting R15C33 Data ...");
            List<String> cols = Arrays.asList("cusip", 
            								  "market_value", 
            								  "availpar_short",
            								  "pool_nbr", 
            								  "secname", 
            								  "sectype", 
            								  "step_description", 
            								  "lgl_veh", 
            								  "factor", 
            								  "iss_date", 
            								  "aloc_class_long", 
            								  "aloc_class_short", 
            								  "coupon", 
            								  "step", 
            								  "sec_nbr", 
            								  "availpar_long", 
            								  "orig_par", 
            								  "mat_date", 
            								  "mkt_price");
            try {
            	List<Map<String, Object>> rows = Kernel.getStructured().selectRows(ctx, 
											"//demo/T15C33", 
											cols, 
											"load_dtm = (select max(load_dtm) from demo.t15c33)", 
											cols, 
											false, 
											-1);
            	return JacksonUtil.jsonFromObject(rows);
            } catch (RaptureException e) {
            	log.error(e);
            	return "Failed to read data: " + e;
            } 
            
        });
        
        get(NETCAP_PATH, (req, res) -> {
            log.info("Getting netcaprepo Data");
            List<String> cols = Arrays.asList("legal_veh", 
            								  "account", 
            								  "contracttype", 
            								  "contractdate", 
            								  "contractnbr", 
            								  "contractsfx", 
            								  "portfolio", 
            								  "sec_type", 
            								  "security_id", 
            								  "maturity_date", 
            								  "coupon_rate", 
            								  "par", 
            								  "market_value", 
            								  "contract_value", 
            								  "haircut_value", 
            								  "short", 
            								  "long", 
            								  "excess_market", 
            								  "end_date");
            return JacksonUtil.jsonFromObject(Kernel.getStructured().selectRows(ctx, "//demo/netcaprepo", cols, " ", cols, false, -1));
        });
        
        get(POSITION_PATH + "/:portfolio", (req, res) -> {
            String portfolio = req.params(":portfolio");
            if (portfolio == "") { 
            	return "Please enter valid portfolio name";
            }
            log.info("Getting TB_POS Data");
            List<String> cols = Arrays.asList("portfolio", 
            								  "sec_nbr", 
            								  "position_type", 
            								  "portfolio", 
            								  "co_par", 
            								  "se_par", 
            								  "se_principal");
            String where = "portfolio = '" + portfolio + "'";
            log.info(where);
            
            return JacksonUtil.jsonFromObject(Kernel.getStructured().selectRows(ctx, "//demo/tb_position", cols, where, cols, false, -1));
        });

        get(QMG_PATH, (req, res) -> {
            log.info("Getting sector code list");
            List<String> cols = Arrays.asList("code", "industry", "name");
            try  {
                List<Map<String, Object>> r = Kernel.getStructured().selectRows(ctx, "//demo.structured/sectorcodes", cols, " ", cols, true, -1);
                log.info("Here: " +r);
            } catch (Throwable t) {
            	log.error(t);
            	t.printStackTrace();
            }
 
            return JacksonUtil.jsonFromObject(Kernel.getStructured().selectRows(ctx, "//demo.structured/sectorcodes", cols, " ", cols, true, -1));
        });
        
        get(QMG_PATH + "/:sector", (req, res) -> {
        	String seriesUri = QMG_ROOT + req.params(":sector");
        	log.info("Getting list of sector series for " + seriesUri);
        	Map<String, RaptureFolderInfo> seriesList = Kernel.getSeries().listSeriesByUriPrefix(ctx, seriesUri, 1);
        	return JacksonUtil.jsonFromObject(ImmutableMap.of(req.params(":sector"), seriesList));
        });

        get(QMG_PATH + "/:sector/:series", (req, res) -> {
            log.info("Getting sector series");
            
            String sector = (String) req.params(":sector");
            String seriesName = "/" + (String) req.params(":series");
            String seriesUri = QMG_ROOT + sector + seriesName;
            log.info(seriesUri);
            
            if (Kernel.getSeries().seriesExists(ctx, seriesUri)) {
            	List<SeriesDouble> series = Kernel.getSeries().getPointsAsDoubles(ctx, seriesUri);
                return JacksonUtil.jsonFromObject(ImmutableMap.of(req.params(":sector"), series));
            } else {
                res.status(404);
                return "No series data for: " + req.params(":symbol");
            }
        });
    }
    
    
    private List<JSONObject> getRepos() {
    	//
    	// Requires a document in DYN_DOCS folder 
    	// Format is JSON
    	// example:
    	// {
    	//  "unique_name" : "pos",
    	//  "uri" : "//demo/tb_position",
    	//  "table" : "demo.tb_position",
    	//  "cols" : [ "portfolio", "se_par" ]
    	// }
    	
		Map<String, RaptureFolderInfo> docs = Kernel.getDoc().listDocsByUriPrefix(ctx, DYN_DOCS, 1);
		
		List<JSONObject> conns = new ArrayList<JSONObject>();
    	for (RaptureFolderInfo rf : docs.values()) {
             if (! rf.isFolder()) {
            	 log.info(rf);
            	 
            	 JSONObject obj = new JSONObject(Kernel.getDoc().getDoc(ctx, DYN_DOCS + rf.getName()));
            	 
            	 conns.add(obj);
            	 
             }
    	}
		return conns;
    }
    
}