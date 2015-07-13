package benninger.lucas.mogrifygui.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

	public void createZip(String imageFolder, String zipName) {
		try {
			File folder = new File(imageFolder);
			File[] listOfFiles = folder.listFiles();

			FileOutputStream fos = new FileOutputStream(imageFolder + zipName);
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && !listOfFiles[i].toString().endsWith(".zip")) {
					addToZipFile(listOfFiles[i].getAbsolutePath(), zos);
				}
			}
			zos.close();
			fos.close();

		} catch (Exception e) {

		}
	}

	public static void addToZipFile(String fileName, ZipOutputStream zos) {
		try {

			//Remove path from file name
			String fileNameFix = fileName;
			if (!fileName.endsWith("/") || !fileName.endsWith("\\")) {
				for (int i = fileName.length() - 1; i >= 0; i--) {
					if (String.valueOf(fileName.charAt(i)).equals("/") || String.valueOf(fileName.charAt(i)).equals("\\")) {
						fileNameFix = fileName.substring(i + 1, fileName.length());
						i = -1; //Exit the loop.
					}
				}
			}

			System.out.println("Writing '" + fileName + "' to zip file");

			File file = new File(fileName);
			FileInputStream fis = new FileInputStream(file);

			ZipEntry zipEntry = new ZipEntry(fileNameFix);
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}

			zos.closeEntry();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
