
package model.util;

import java.io.*;

/**
 * SerializeObject: Load or save an object using Java serialization.
 *
 */
public class SerializeObject {
	
	/**
	 * Load an object.
	 * 
	 * @param filename
	 *            The filename.
	 * @return The loaded object.
	 * @throws java.io.IOException
	 *             An IO error occurred.
	 * @throws ClassNotFoundException
	 *             The specified class can't be found.
	 */
	public static Serializable load(final String filename) throws IOException,
			ClassNotFoundException {
		Serializable object;
		FileInputStream fis = null;
		ObjectInputStream in = null;
		fis = new FileInputStream(filename);
		in = new ObjectInputStream(fis);
		object = (Serializable) in.readObject();
		in.close();
		return object;
	}

	/**
	 * Save the specified object.
	 * @param filename The filename to save.
	 * @param object The object to save.
	 * @throws java.io.IOException An IO error occurred.
	 */
	public static void save(final String filename, final Serializable object)
			throws IOException {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		fos = new FileOutputStream(filename);
		out = new ObjectOutputStream(fos);
		out.writeObject(object);
		out.close();
	}

}
