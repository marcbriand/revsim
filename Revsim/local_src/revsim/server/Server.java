package revsim.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;

import revsim.driver.Driver;
import revsim.mvc.Model;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {
	
	static Driver driver;
	static String resourceDir;

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
        server.createContext("/api/nodes", new NodeHandler());
        server.createContext("/scripts", new ScriptsHandler());
        server.createContext("/images", new ImageHandler());
        server.createContext("/", new BaseHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
	}
	
	private static String pastePath(String dir, String path) {
		if (dir.endsWith("/")) {
			if (path.startsWith("/"))
				return dir + path.substring(1);
			else
				return dir + path;
		}
		else {
			if (path.startsWith("/"))
				return dir + path;
			else
				return dir + "/" + path;
		}
	}
	
	public static void start(String resourceDir, Driver driver) throws Exception {
		
		Server.resourceDir = resourceDir;
		Server.driver = driver;
		HttpServer server = HttpServer.create(new InetSocketAddress(3000), 0);
        server.createContext("/api/nodes", new NodeHandler());
        server.createContext("/scripts", new ScriptsHandler());
        server.createContext("/images", new ImageHandler());
        server.createContext("/", new BaseHandler());
        server.setExecutor(null); // creates a default executor
        server.start();	
    }
	
	static void fetchFile(String filepath, HttpExchange t) {
		try {
			String path = pastePath(resourceDir, filepath);
		    InputStream istream = new FileInputStream(path);
		    byte[] buf = new byte[1000];
		    StringBuilder sb = new StringBuilder();
		    while(true) {
			    int numRead = istream.read(buf, 0, 1000);
			    if (numRead < 0)
				    break;
			    String part = new String(buf, 0, numRead, "UTF-8");
			    sb.append(part);
		    }
		    istream.close();
		    String s = sb.toString();
		    byte[] outBytes = s.getBytes("UTF-8");
            t.sendResponseHeaders(200, outBytes.length);
		    OutputStream os = t.getResponseBody();
		    os.write(outBytes);
		    os.close();
		}
		catch(Exception e) {
			System.out.println("BaseHandler exception: " + e);
		}		
	}
	
	static void fetchBinaryFile(String filepath, HttpExchange t) {
		try {
			String path = pastePath(resourceDir, filepath);
			File f = new File(path);
			t.sendResponseHeaders(200, f.length());
		    InputStream istream = new FileInputStream(path);	    
		    OutputStream os = t.getResponseBody();
		    byte[] buf = new byte[1000];
		    while(true) {
			    int numRead = istream.read(buf, 0, 1000);
			    if (numRead < 0)
				    break;
			    os.write(buf, 0, numRead);
		    }
		    istream.close();
		    os.close();
		}
		catch(Exception e) {
			System.out.println("BaseHandler exception: " + e);
		}		
		
	}
	
	static class BaseHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) {
            fetchFile("index.html", t);
		}
	}
	
	static class ScriptsHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) {
			URI uri = t.getRequestURI();
			String path = uri.getPath();
			System.out.println("Path = " + path);
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			Headers rh = t.getResponseHeaders();
			rh.add("Content-Type", "application/javascript; charset=UTF-8");
			fetchFile(path, t);
		}
	}
	
	static class ImageHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) {
			URI uri = t.getRequestURI();
			String path = uri.getPath();
			if (path.startsWith("/")) {
				path = path.substring(1);
			}
			System.out.println("handling image request, path = " + path);

			// find extension
			int lastdot = path.lastIndexOf('.');
			String ext;
			String contentType = "image/jpg";
			if (lastdot >= 0) {
				ext = path.substring(lastdot + 1);
				contentType = "image/" + ext;
			}
			Headers rh = t.getResponseHeaders();
			rh.add("Content-Type", contentType);
			rh.add("cache-control", "public, max-age=86400");
//			rh.add("Expires", "Tue, 12 Jan 2016 12:00:00 GMT");
			fetchBinaryFile(path, t);
		}
	}
	
	static String getParam(HttpExchange e, String name) {
		String queryString = e.getRequestURI().getQuery();
		String [] params = queryString.split("&");
		for (String param : params) {
			String [] keyValue = param.split("=");
			if (keyValue.length > 0) {
				if (keyValue[0].equals(name)) {
					if (keyValue.length > 1)
						return keyValue[1];
					else
						return null;
				}
			}
		}
		return null;
	}
	
	static class NodeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
        	System.out.println("frame = " + getParam(t, "frame"));
        	String frameStr = getParam(t, "frame");
        	if (frameStr != null) {
        		long frame = Long.parseLong(frameStr);
        		driver.skipTo(frame);
        		Model model = driver.getCurrentModel();
        		String response = model.serialize();
        		System.out.println("json:");
        		System.out.println(response);
        		t.sendResponseHeaders(200, response.length());
                OutputStream os = t.getResponseBody();
                os.write(response.getBytes());
                os.close();
        	}
        }
    }

}
