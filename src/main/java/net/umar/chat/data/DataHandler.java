/**
 * DataHandler
 * @author UWO_CS2212_Team_11
 * cs2212-111@gaul.csd.uwo.ca
 * Created 13/02/2014
 * Last Modified 04/02/2014 -ctam68
 */
package net.umar.chat.data;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataHandler {
	
	
	/**
	 * This method writes a line to an output stream
	 * @param os - Output stream that the line is written to
	 * @param text - Message that is send to the output stream
	 */
	public static void writeLine(OutputStream os, String text){
//            try {
//                os.flush();
//            } catch (IOException ex) {
//                Logger.getLogger(DataHandler.class.getName()).log(Level.SEVERE, null, ex);
//            }
		PrintWriter pw = new PrintWriter(os, true);
		pw.println(text);
	}
	
	/**
	 * This method writes data to an output stream
	 * @param os - Output stream the data goes though
	 * @param data - data
	 * @throws IOException
	 */
	public static void write(OutputStream os, byte[] data) throws IOException{
		os.write(data, 0, data.length);
	}
        
        public static void writeBytes(OutputStream os, byte[] data) throws IOException {
//            for (int i = 0; i < data.length; i++) {
                os.write(data, 0, data.length);
//            }
        }
	
	/**
	 * This methods reads a line from an input stream
	 * @param is - input stream that the method reads from
	 * @return the line that is read
	 */
	public static String readLine(InputStream is){
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			if (br.ready()){
				String toReturn = br.readLine();

				return toReturn;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * This method reads a specific number of bytes
	 * @param is - input stream the method reads the bytes from
	 * @param num - number of bytes to read from the input stream
	 * @return the data in the inputstream
	 */
	public static byte[] readBytes(InputStream is, int num){
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			if (br.ready()){
				byte[] bytes = new byte[num];
//				for (int i = 0; i < num; i++) {
//					bytes[i] = (byte) br.read();
//					
//				}
                                is.read(bytes, 0, num);
//                                br.read(bytes, 0, num);

				return bytes;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
                
		
		return null;
	}
	
	/**
	 * This method writes properties files
	 * @param filename - the inputstream filename
	 * @param properties - the properties to be read from
	 * @param values - the linked values with the properties
	 * @throws IOException
	 */
	public static void writePropertiesFile(String filename, String[] properties, String[] values) throws IOException {
            InputStream in = new FileInputStream(filename);
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(filename);
            for (int i = 0; i < properties.length; i++) {
                    props.setProperty(properties[i], values[i]);
            }

            props.store(out, null);
            out.close();
	}
	
}
