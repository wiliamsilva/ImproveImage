package br.com.smartimage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.imgscalr.Scalr;

import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReader;
import com.github.jaiimageio.impl.plugins.tiff.TIFFImageReaderSpi;
import com.github.jaiimageio.jpeg2000.impl.J2KImageReader;
import com.github.jaiimageio.jpeg2000.impl.J2KImageReaderSpi;

/**
 * 
 * @author wiliamps
 *
 */
public class ImproveImage {

	public static final String IMAGE_EXTENSIONS[] = new String[] {"bmp", "dib", "rle", "gif", "jp2", "j2k", "jpg", "jtf", "jff", "jpeg", "png", "tif", "tiff", "tp1"};
	public static final String JPG_EXTENSIONS[] = new String[] {"jpg", "jtf", "jff", "jpeg"};
	public static final String PNG_EXTENSIONS[] = new String[] {"png"};
	public static final String GIF_EXTENSIONS[] = new String[] {"gif"};
	public static final String BMP_EXTENSIONS[] = new String[] {"bmp", "dib", "rle"};
	public static final String JP2_EXTENSIONS[] = new String[] {"jp2", "j2k"};
	public static final String TIF_EXTENSIONS[] = new String[] {"tif", "tiff", "tp1"};

	private static final int DATA_KB_SIZE = 1024;
	private static final int DATA_MB_SIZE = DATA_KB_SIZE * 1024;
	
	private static final int HORIZONTAL_DPI_MAX = 1200;
	private static final int VERTICAL_DPI_HEIGHT = 1200;

	private static final int HORIZONTAL_DPI_MIN = 128;
	private static final int VERTICAL_DPI_MIN = 128;
	
	private static final int COMP_BY_WIDTH = 1;
	private static final int COMP_BY_HEIGHT = 2;
	
	private final long _maxFileSize; 
	
	public ImproveImage(long maxFileSize) {
		
		if (maxFileSize > DATA_MB_SIZE) {
			this._maxFileSize = maxFileSize;
		} else {
			this._maxFileSize = DATA_MB_SIZE;
		}
	}

	/**
	 * Process an raw image and returns an complete path of improved image. 
	 * If that raw image already good then its full path will be returned.
	 * 
	 * @param fileToImprove Complete file path of image to improve.
	 * @param outputFolder	Output folder to write the improved image file.  
	 * @return Complete file path of improved image file
	 * @throws IOException
	 */
	public String perform(String fileToImprove, String outputFolder) throws IOException {

		String result;
		
		File file = new File(fileToImprove);

		if (!(file.exists() && file.isFile())) {
			throw new IOException(String.format("File to improve not found: %s.", fileToImprove));
		}			

		ImageInputStream iis = null;
		
		iis = ImageIO.createImageInputStream(file);
		
		if (iis == null) {
			throw new IOException(String.format("File to improve is not image: %s.", fileToImprove));
		}

		BufferedImage originalImage = ImageIO.read(iis);
		
		if (originalImage == null) {
			throw new IOException(String.format("File image not recognized: %s.", fileToImprove));
		}

		long originalFileSize = file.length();
		int originalWidth = originalImage.getWidth();
		int originalHeight = originalImage.getHeight();

		int baseToCompare = 0;
		int targetWidth = 0;
		int targetHeight = 0;
		boolean needsAutoFit = false;

		final String trueFileType = getImageType(file.getName(), iis);

		if (originalWidth >= originalHeight) {
			baseToCompare = COMP_BY_WIDTH;
		} else {
			baseToCompare = COMP_BY_HEIGHT;
		}

		if (baseToCompare == COMP_BY_WIDTH && originalWidth > HORIZONTAL_DPI_MAX) {

			needsAutoFit = true; 
			targetWidth = HORIZONTAL_DPI_MAX;
			targetHeight = (int) (originalHeight * (HORIZONTAL_DPI_MAX / (double) originalWidth));

		}
		else if (baseToCompare == COMP_BY_HEIGHT && originalHeight > VERTICAL_DPI_HEIGHT) {

			needsAutoFit = true;
			targetWidth = (int) (originalWidth * (VERTICAL_DPI_HEIGHT / (double) originalHeight));
			targetHeight = VERTICAL_DPI_HEIGHT;

		}
		else if(isBMP(trueFileType)) {

			needsAutoFit = true;
			targetWidth = originalWidth;
			targetHeight = originalHeight;

		}
		else if (originalFileSize > _maxFileSize) {

			double reductionRate = _maxFileSize / (double) originalFileSize;
			needsAutoFit = true; 
			targetWidth = (int) (originalWidth * reductionRate);
			targetHeight = (int) (originalHeight * reductionRate);

			// Check minimum size limit
			if (baseToCompare == COMP_BY_WIDTH && targetWidth < HORIZONTAL_DPI_MIN) {
				targetWidth = HORIZONTAL_DPI_MIN;
				targetHeight = (int) (originalHeight * (HORIZONTAL_DPI_MIN / (double) originalWidth));
			}
			else if (baseToCompare == COMP_BY_HEIGHT && targetHeight < VERTICAL_DPI_MIN) {
				targetWidth = (int) (originalWidth * (VERTICAL_DPI_MIN / (double) originalHeight));
				targetHeight = VERTICAL_DPI_MIN;
			}
			
		}

		if (needsAutoFit) {

			BufferedImage scaledImg = Scalr.resize(originalImage, Scalr.Method.SPEED, Scalr.Mode.FIT_TO_WIDTH,
					targetWidth, targetHeight, Scalr.OP_ANTIALIAS);

			String outFileType = null;
			
			if (isBMP(trueFileType) || isTIF(trueFileType)) {
				outFileType = "png";
			} else {
				outFileType = trueFileType;
			}
			
			String outputFileName = createOutputFileName(file, outputFolder, outFileType);

			File outputFile = new File(outputFileName);
			
			ImageIO.write(scaledImg, outFileType, outputFile);

			result = outputFileName;

		} else {

			result = fileToImprove;

		}
			
		
		return result;
		
		
	}

	private static String getImageType(String fileName, ImageInputStream imageInputStream) throws IOException {

		if (fileName == null || imageInputStream == null) {
			return null;
		}

		final String fileType = extractFileType(fileName);

		String trueFileType = null;

		Iterator<ImageReader> readers = null;

		readers = ImageIO.getImageReaders(imageInputStream);

		if (readers == null || !readers.hasNext()) {

			if (isJPG(fileType)) {
				readers = ImageIO.getImageReadersByFormatName("JPEG");
			}
			else if (isJP2(fileType)) {
				ImageReader ir = new J2KImageReader(new J2KImageReaderSpi());
				ir.setInput(imageInputStream);
				IterableVetor<ImageReader> iv = new IterableVetor<ImageReader>(new ImageReader[]{ir});
				readers = iv.iterator();    			
			}
			else if (isTIF(fileType)) {
				ImageReader ir = new TIFFImageReader(new TIFFImageReaderSpi());
				ir.setInput(imageInputStream);
				IterableVetor<ImageReader> iv = new IterableVetor<ImageReader>(new ImageReader[]{ir});
				readers = iv.iterator();    			
			}

		}

		if (readers != null && readers.hasNext()) {

			// pick the first available ImageReader
			ImageReader reader = readers.next();

			// attach source to the reader
			reader.setInput(imageInputStream, true);

			trueFileType = reader.getFormatName();

		} else {

			trueFileType = fileType;

		}

		if (trueFileType != null) {
			trueFileType = trueFileType.toLowerCase();
		} else {
			trueFileType = fileType;
		}

		return trueFileType;
	}


	private static String extractFileType(String fileName) {

		if (fileName == null || fileName.trim().length() == 0) {
			return null;
		}

		String [] fileNameFragments = fileName.split("\\.");
		String fileExtension = null;

		if (fileNameFragments != null && fileNameFragments.length >= 1) {
			fileExtension = fileNameFragments[fileNameFragments.length -1].trim().toLowerCase();
		}

		return fileExtension;

	}

	public static boolean isJPG(String extension) {

		for (String type: JPG_EXTENSIONS) {
			if (type.equals(extension)) {
				return true;
			}
		}

		return false;
	}	

	public static boolean isPNG(String extension) {

		for (String type: PNG_EXTENSIONS) {
			if (type.equals(extension)) {
				return true;
			}
		}

		return false;
	}	

	public static boolean isGIF(String extension) {

		for (String type: GIF_EXTENSIONS) {
			if (type.equals(extension)) {
				return true;
			}
		}

		return false;
	}	

	public static boolean isBMP(String extension) {

		for (String type: BMP_EXTENSIONS) {
			if (type.equals(extension)) {
				return true;
			}
		}

		return false;
	}	

	public static boolean isJP2(String extension) {

		for (String type: JP2_EXTENSIONS) {
			if (type.equals(extension)) {
				return true;
			}
		}

		return false;
	}	

	public static boolean isTIF(String extension) {

		for (String type: TIF_EXTENSIONS) {
			if (type.equals(extension)) {
				return true;
			}
		}

		return false;
	}	

	private static int randomBetween(int minimum, int maximum) {

		int randomNum = minimum + (int)(Math.random() * maximum); 
		
		return randomNum;
		
	}	

	private static String createOutputFileName(File originalFile, String outputFolder, String outFileType) {

		String result = null;

		if (!(originalFile != null && originalFile.exists() && originalFile.isFile())) {
			return null;
		}

		String indicatedFolder = null;
		
		if (outputFolder != null) {
			File f = new File(outputFolder);
			if (f.exists() && f.isDirectory()) {
				indicatedFolder = outputFolder;
			}
		}
		
		final String path = indicatedFolder != null? indicatedFolder: originalFile.getParent();
		final String name = originalFile.getName().trim().toLowerCase();

		final String[] nameFragments = name.split("\\.");

		String newName = null;

		int rnd = randomBetween(1000, 99999);
		
		if (nameFragments != null && nameFragments.length >= 1) {
			newName = String.format("%s_%d_improved.%s", nameFragments[0], rnd, outFileType);
		} else {
			newName = String.format("nonamed_%d_improved.%s", rnd, outFileType);
		}

		result = String.format("%s/%s", path, newName);

		return result;

	}

}
