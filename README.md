# ImproveImage

This tool will improve yours images automatically to make it ligther without loss quality then you can use that in your website, blog, social media, email and so one.

## Supported image formats

JPG, PNG, GIF, BMP, JPEG 2000 and TIFF

## Coding sample

```
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
```

## Result log

```
-----------------------------------------------------------------------------
Original file name: C:\Temp\DisplayTest.bmp
Original file size: 6739270 bytes

Result file name: C:\Users\Root\AppData\Local\Temp\displaytest_53433_improved.png
Result file size: 191239 bytes

Reduction rate: 97,16 %
```

## Maven dependencies

```
<dependency>
   <groupId>com.github.jai-imageio</groupId>
   <artifactId>jai-imageio-jpeg2000</artifactId>
   <version>1.3.0</version>
</dependency>
<dependency>
   <groupId>org.imgscalr</groupId>
   <artifactId>imgscalr-lib</artifactId>
   <version>4.2</version>
</dependency>
```
