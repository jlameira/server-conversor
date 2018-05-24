package br.com.server.conversor.util;

public class Util {

	/**
	 * Removes the extension
	 * @param fileName
	 * @return fileName minus extension
	 */
	public static String removeExtension(final String fileName) {
		if (fileName.indexOf(".") > 0) {

			return fileName.substring(0, fileName.lastIndexOf("."));

		} else {

			return fileName;

		}

	}

}
