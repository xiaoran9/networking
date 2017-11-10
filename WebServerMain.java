import java.io.IOException;
/**
 * the main section to run the project.
 * @author ID 150011754
 *
 */
public class WebServerMain {
	/**
	 * the main method of the webServer.
	 * it will check the input arguments to make sure commanded-Line argument's is correct.
	 * @param args -the command-line arguments <dovument root >and < port>
	 */
	public static void main(String[] args) {
		int port = 0;
		String directory = null;
	if (args.length == 2) {
			try {
			port = Integer.parseInt(args[1]);
			directory = args[0];
			} catch (Exception e) { //if the input type is not incorrect out put usage
				System.out.println("Usage: java WebServerMain <document_root> <port>");
			}
			try {
				//the send port and directory to the NetworkService to create a new thread pool
				new NetworkService(port, directory).service();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else { //make sure there are tow input and
			System.out.println("Usage: java WebServerMain <document_root> <port>");
			}
	}
}
