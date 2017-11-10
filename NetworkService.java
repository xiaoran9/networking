import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 *  The NetworkService will create a thread pool for a server.
 *  it can listen many client by a special port given by the user
 *  the pool only shutdown when meet some exception
 * @author ID 150011754
 *
 */
public class NetworkService  {
	private final ServerSocket server;
	private final ExecutorService pool;
	private final int poolSize = 10;
	private String directory;
	/**
	 * Build a thread pool with the input port, and the maximal size of the pool is 10.
	 * @param port -the port server should listen
	 * @param directory -the server will serve documents to clients
	 * @throws IOException -if there is a problem when build a pool and server
	 */
	public NetworkService(int port, String directory)  throws IOException {
		server = new ServerSocket(port);
		this.directory = directory;
		//create a pool and the size is 10
		pool = Executors.newFixedThreadPool(poolSize);
		System.out.println("Start");
	}
	/**
	 * Always wait a client connect to the server.
	 */
	public void service() {
		try {
			while (true) {
				Socket client;
				try {
					// if receive a connection form client, it will accept();
					client = server.accept();
					pool.execute(new Request(client, directory));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			pool.shutdown(); // if there are some exception ,shutdown the tread pool
		}
	}
}
