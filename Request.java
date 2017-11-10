import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;
/**
 * Build a request which receive the request and separate it to lines and send the request code to responder.
 * @author ID150011754
 *
 */
public class Request extends Thread {
	private Socket conn;
	private InputStream input;
	private String root;
	private BufferedReader reader;
	private String end = "\r\n";
	/**
	 * Build a request handler with client socket and directory.
	 * @param socket -the client socket
	 * @param directory -the root of the resource
	 * @throws Exception -throw the exception
	 */
	public Request(Socket socket, String directory) throws Exception {
		this.conn = socket;
		this.root = directory;
		input = conn.getInputStream();
		reader = new BufferedReader(new InputStreamReader(input));
	}
	/**
	 * the run method of the tread.
	 */
	public void run() {
		try {
			process();
		}
		catch (Exception e) {
			System.out.println("Error trying to process reuqest..");
			System.out.println(e);
			e.printStackTrace();
		}
	}
	/**
	 * Processes the request sent form the client.
	 * reads and checks the request and reply by Responder class
	 * @throws Exception -when create logging file create responder and get requset meet problem
	 */
	private void process() throws Exception {
		//Create the log file
		LoggingFile log = new LoggingFile();
		//Separate request by sentence and get the first line of<requestType> <Resource name>< protocol version>
		String[] requestLines = readRequest(conn, log).split(System.getProperty("line.separator"));
	    String   request = requestLines[0];
		//Separate request by word
		StringTokenizer st = new StringTokenizer(request);
		String requestType = st.nextToken();
		String fileName = st.nextToken();
		//Create the Responder
		Responder respond = new Responder(requestType, fileName, conn, root, log);
		respond.sentResponse();
		cleanup();
		log.clear();
		input.close();
		conn.close();
	}
	/**
	 * read the request for socket.
	 * @param socket - the socket reading from
	 * @return return the request by string without whitespace
	 * @throws IOException -when call input stream meet some problem
	 */
	private String readRequest(Socket s, LoggingFile log) throws IOException  {
		String receive = "";
		while (!receive.endsWith(end + end)) {
			byte[] bytes = new byte[input.available()];
            input.read(bytes);
            receive += new String(bytes);
		}
		//Save all the receive into the log file
		log.write(receive);
		return receive.trim();
	}
	/**
	 * used to close the inputStream socket and BufferedReader.
	 */
	private void cleanup() {
		System.out.println(" ... cleaning up and exiting ... ");
		try {
			reader.close();
			input.close();
			conn.close();
		} catch (IOException ioe) {
			System.out.println("cleanup " + ioe.getMessage());
		}
	}
}
