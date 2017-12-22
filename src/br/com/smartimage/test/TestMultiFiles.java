package br.com.smartimage.test;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.com.smartimage.ImproveImage;

public class TestMultiFiles {

	public static void main(String[] args) {
		
		final String outputFolder = System.getProperty("java.io.tmpdir");
		
		JFileChooser chooser = new JFileChooser();

		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Imagens", ImproveImage.IMAGE_EXTENSIONS));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("JPG - Bitmap JPEG (*.jpg;*.jtf;*.jff;*.jpeg)", ImproveImage.JPG_EXTENSIONS));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("PNG - Portable Network Graphics (*.png)", ImproveImage.PNG_EXTENSIONS));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("GIF - Bitmap CompuServe (*.gif)", ImproveImage.GIF_EXTENSIONS));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("BMP - Bitmap do Windows (*.bmp;*.dib;*.rle)", ImproveImage.BMP_EXTENSIONS));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("JP2 - Bitmap JPEG 2000 (*.jp2;*.j2k)", ImproveImage.JP2_EXTENSIONS));
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("TIF - Bitmap TIFF (*.tif;*.tiff;*.tp1)", ImproveImage.TIF_EXTENSIONS));
		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);

		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

			final int MEGABYTE_UNITY = 1024 * 1024;   
			
			ImproveImage iImage = new ImproveImage(MEGABYTE_UNITY);
			
			File[] files = chooser.getSelectedFiles();
			
			for (File file: files) {

				String resultFile = null;
				
				try {
					resultFile = iImage.perform(file.getAbsolutePath(), outputFolder);
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (resultFile != null) {

					long originalSize = file.length();
					long resultSize = getFileSize(resultFile);
					double reductionRate = ((1 - (resultSize / (double) originalSize)) * 100.0d); 					
					
					System.out.println("-----------------------------------------------------------------------------");
					System.out.println("Original file name: " + file.getAbsolutePath());
					System.out.println("Original file size: " + file.length() + " bytes");
					System.out.println("");
					System.out.println("Result file name: " + resultFile);
					System.out.println("Result file size: " + resultSize + " bytes");
					System.out.println("");
					System.out.println(String.format("Reduction rate: %.02f %%", reductionRate));
					System.out.println("");

				}
				
			}
			
			
		}
		
	}

	public static long getFileSize(String filePath) {
		
		File file = new File(filePath);
		
		if (file.exists() && file.isFile()) {
			return file.length();
		}
		
		return 0L;

	}
	
	
}
