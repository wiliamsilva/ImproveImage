package br.com.improveimage.test;

import java.io.File;
import java.io.IOException;

import br.com.improveimage.ImproveImage;

public class TestSimple {

	public static void main(String[] args) throws IOException {
		
		final int MEGABYTE_UNITY = 1024 * 1024;   		
		
		final String outputFolder = System.getProperty("java.io.tmpdir");
		
		ImproveImage iImage = new ImproveImage(MEGABYTE_UNITY);

		String inputFilePath = "C:\\Temp\\DisplayTest.bmp";
		
		String outputFilePath = iImage.perform(inputFilePath, outputFolder); 
		
		// Print report
		
		long originalSize = getFileSize(inputFilePath);
		long resultSize = getFileSize(outputFilePath);
		double reductionRate = ((1 - (resultSize / (double) originalSize)) * 100.0d); 
		
		System.out.println("-----------------------------------------------------------------------------");
		System.out.println("Original file name: " + inputFilePath);
		System.out.println("Original file size: " + originalSize + " bytes");
		System.out.println("");
		System.out.println("Result file name: " + outputFilePath);
		System.out.println("Result file size: " + resultSize + " bytes");
		System.out.println("");
		System.out.println(String.format("Reduction rate: %.02f %%", reductionRate));
		System.out.println("");		
		
	}
	
	public static long getFileSize(String filePath) {
		
		File file = new File(filePath);
		
		if (file.exists() && file.isFile()) {
			return file.length();
		}
		
		return 0L;

	}
	
}
