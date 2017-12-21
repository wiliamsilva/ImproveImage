package br.com.smartimage.test;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import br.com.smartimage.ImproveImage;

public class Test {

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

					File newFile = new File(resultFile);
					
					System.out.println("-----------------------------------------------------------------------------");
					System.out.println("Original file name: " + file.getAbsolutePath());
					System.out.println("Original file size: " + file.length() + " bytes");
					System.out.println("");
					System.out.println("Result file name: " + newFile.getAbsolutePath());
					System.out.println("Result file size: " + newFile.length() + " bytes");
					System.out.println("");
					System.out.println(String.format("Reduction rate: %.02f", 1 - (newFile.length() / (double) file.length())));
					System.out.println("");

				}
				
			}
			
			
		}
		
		
		
		
		
	}
	
	
}
