import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Serializer 
{
	/*
	 * Any object that implements the [Serializable interface](https://docs.oracle.com/javase/8/docs/api/java/io/Serializable.html)
	 * can be written to and read from file using the methods below.
	 * 
	 * See also [Serializable Objects] (https://docs.oracle.com/javase/tutorial/jndi/objects/serial.html)
	 * for an explanation of what serialization is
	 */
	
	public static void serialize(Object o, String fileName)
	{
		ObjectOutputStream ouputStream = null;
		try {
			ouputStream = new ObjectOutputStream(new FileOutputStream(fileName));
			ouputStream.writeObject(o);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				ouputStream.close();
			} catch (IOException e) {
			}
		}
	}

	public static Object deserialize(String fileName)
	{	
		FileInputStream inputStream = null;
		ObjectInputStream reader = null;
		Object o = null;

		try {
			inputStream = new FileInputStream(fileName);
			reader = new ObjectInputStream(inputStream);		
			o = reader.readObject();
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());			
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());			
		} catch (IOException e) {
			System.out.println(e.getMessage());			
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
			}
			try {
				inputStream.close();
			} catch (IOException e) {
			}
		}

		return o;
	}
}
