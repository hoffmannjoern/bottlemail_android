package uni.leipzig.bm2.filemanager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import android.os.Environment;

public class FileManager {
	private File file;
	private BufferedWriter writer;

	private void createNewFile(File file) throws IOException {
		if(!file.exists())
			file.createNewFile();
	}
	
	private void createOutputStreams (File file) throws IOException {
		writer = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
	}
	
	/**
	 * When given path to file is not an existing directory, this one will be built
	 * in front of build file and creating it
	 * @param pathToFile 
	 * @param nameOfFile
	 * @throws IOException
	 */
	public FileManager(String pathToFile, String nameOfFile) throws IOException {
		File directory = new File(pathToFile);
		if(!directory.exists() || !directory.isDirectory())
			directory.mkdir();
		
		file = new File(pathToFile, nameOfFile);
		
		createNewFile(file);
	}
	
	public FileManager(String nameOfFile) throws IOException {
		file = new File(Environment.getExternalStorageDirectory(), nameOfFile);
		createNewFile(file);
	}
	
	public boolean appendLine(String message) throws IOException {
		createOutputStreams(file);
		
		if(file.exists()) {
			writer.write(message);
			writer.newLine();
			writer.flush();
			writer.close();
			return true;
		}
		return false;
	}
	
	public Vector<String> getAllLines() throws IOException {
		Vector<String> lines = new Vector<String>();
		
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = null;
			
			while((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public String getLine(int number) throws IOException {	
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			int lineCount = 0;
			String line = null;
			
			while((line = in.readLine()) != null) {
				if(++lineCount == number) {
					in.close();
					return line;
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Vector<String> getFirstMessages(int number) throws IOException {
		return getLinesFromTo(0, 10);
	}
	
	public Vector<String> getLinesFromTo(int start, int number) throws IOException {
		Vector<String> lines = new Vector<String>();
			
		try{
			BufferedReader in = new BufferedReader(new FileReader(file));
			int lineCount = 0;
			String line = null;
			
			while((line = in.readLine()) != null) {
				if(++lineCount >= start)
					lines.add(line);
					if(lineCount >= start+number)
						break;
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lines;
	}
	
	public int getLinesCount() throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(file));
		try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 0 : count;
		} finally {
			is.close();
		}
	}
}
