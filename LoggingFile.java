import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
/**
 * the loggingFile creating a text file to record the track of request and response.
 * @author ID 150011754
 *
 */
public class LoggingFile {
	private File file;
	private BufferedWriter writer;
	/**
	 * the class used to create a log.txt file.
	 * @throws IOException -throws IO Exception when create file and create writer meet problems
	 */
	public LoggingFile() throws IOException {
	 file = new File("log.txt");
	 file.createNewFile();
	 writer = new BufferedWriter(new FileWriter(file, true));
	}
	/**
	 * Write context into the file with the current time.
	 * @param context -the input context
	 * @throws IOException -throws IO Exception when writer meet problem
	 */
	public void write(String context) throws IOException {
		data();
		writer.write(context);
		writer.write("\n");
		writer.flush();
	}
	/**
	 * Get the time from the system.
	 * @throws IOException - when writer data meet problem
	 */
	private void data() throws IOException {
		Date date = new Date();
		writer.write(date.toString());
		writer.write("\n");
	}
	/**
	 * close the writer.
	 * @throws IOException -throws IO Exception if can't close bufferedWriter
	 */
	public void clear() throws IOException {
		writer.close();
	}
}
