
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * the responder will distinguish different request type and return different header or body and some other option.
 * @author ID 150011754
 *
 */
public class Responder {
	private String requestType;
	private String fileName;
	private String root;
	private String response;
	private File file;
	private LoggingFile log;
	private String contentType = null;
	private String contentLength = null;
	private String server = null;
	private OutputStream output;
	private final String end = "\r\n";
	private static final int SIZE = 1024;
	/**
	 * build a responder will all request information.
	 * @param requestType -the request form the client it may be get or head
	 * @param fileName -when receive a get request, it need to provide the file name it wanted
	 * @param conn -the client socket
	 * @param root -the root of directory of the documents form command-line
	 * @param log -the log file
	 * @throws IOException --throws Exception when get outputStream meet problem
	 */
	public Responder(String requestType, String fileName, Socket conn, String root, LoggingFile log) throws IOException {
		this.requestType = requestType;
		this.fileName = fileName;
		this.root = root;
		this.log = log;
		output = conn.getOutputStream();
	}
	/**
	 * check if the status exist and the request implements. then reply head or hand + body to the client depend on the request.
	 * all header reply by the method sendHeader() and body reply by the sendBytes()
	 * only outpuStream used to response
	 * @throws Exception --throws Exception when call sendHeader() and build new fileInputStream meet problem
	 */
	public void sentResponse() throws Exception {
		String status = null;
		FileInputStream fis = null;
		file = new File(root + fileName);
		if (file.exists()) {
			if (!(requestType.equalsIgnoreCase("GET") || requestType.equalsIgnoreCase("HEAD")) || requestType.equalsIgnoreCase("DELETE")) {
				//when the file is exists but the request is not get,head or the extended delete, return status not Implements
				status = "HTTP/1.1 501 Not Implemented" + end;
				 sendHeader(response, status);
			} else {
				//dealing with different request type :HEAD, GET, DELETE
				if (requestType.equalsIgnoreCase("HEAD")) {
					status = "HTTP/1.1 200 OK" + end;
					sendHeader(response, status);
				}
				if (requestType.equalsIgnoreCase("GET")) {
					status = "HTTP/1.1 200 OK" + end;
					sendHeader(response, status);
					fis = new FileInputStream(file);
					sendBytes(fis);
				}
				// Delete will return different status as it execution result
				if (requestType.equalsIgnoreCase("DELETE")) {
					Path path = Paths.get(root + fileName);
					if (delete(path)) {
					status = "HTTP/1.1 200 OK" + end; //delete succeed
					sendHeader(response, status);
					}
					else {
						status = "HTTP/1.1 202 Accept" + end; //find the file but didn't delete
						sendHeader(response, status);
					}
				}
			}
		} else { //if the file is not exist return status as not found
			status = "HTTP/1.1 404 Not Found" + end;
			sendHeader(response, status);
		}
		output.close();
	}
	/**
	 * Build the header structure with status, server and content type and content length.
	 * 
	 * @param response -the summary of the content length
	 * @param status -the response codes
	 * @throws IOException -throws Exception when output meet problems
	 */
	public void sendHeader(String response, String status) throws IOException {
		server = "Server: MySimpleServer written in Java 6 " + end;
		contentType = "Content-Type: " + getContentType(fileName) + end;
		contentLength = "Content-Length: " + file.length() + end;
		//create a response of string wihch is the whole header
		response = status + server + contentType + contentLength + end;
		output.write(response.getBytes());
		output.flush();
		log.write(response);
	}
	/**
	 * a method to check and return what kind of file is requested by the client.
	 * 
	 * @param fileName -Specifies the fileNmae to check
	 * @return the file type of the file
	 */
	public String getContentType(String fileName) {
		if (fileName.endsWith(".txt")) {
			return "text/plain";
			}
		if (fileName.endsWith(".html") || fileName.endsWith(".html")) {
			return "text/html";
			}
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
        	return "image/jpeg";
        	}
        if (fileName.endsWith(".png")) {
        	return "image/png";
        	}
        if (fileName.endsWith(".gif")) {
        	return "image/gif";
        	}
		return "Application/octet-stream";
	}
	/**
	 * a method which buffers data from a FileInputStream and sends it to the outputStram.
	 * @param input -the FileInputStram to be read
	 * @throws Exception -throws Exception when output meet problem
	 */
	public void sendBytes(FileInputStream input) throws Exception {
		byte[] buffer =  new byte[SIZE];
		int bytes = 0;
		//the server will send the file by size 1024 each time and finish until read all the file
		while ((bytes = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytes);
		}
		output.flush();
	}
	/**
	 * Delete the file with the provided path if succeed return true, if not return false.
	 * @param path -the source path
	 * @return -boolean of result of delete
	 */
	public boolean delete(Path path) {
		try {
			Files.delete(path);
		} catch (IOException e) {
			System.err.println(e);
			return false; //means the server accept the requset but didn't delete it
		}
		return true;
	}
}
