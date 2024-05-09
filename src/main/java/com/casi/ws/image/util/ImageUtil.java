package com.casi.ws.image.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class ImageUtil {

	public static byte[] compress(byte[] data) throws IOException {
//		detect image type
		ImageFormat format = ImageFormatDetector.detect(data);

//		convert to input stream
		InputStream inputImage = new ByteArrayInputStream(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		BufferedImage image = ImageIO.read(inputImage);

		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format.toString());
		

		if(writers.hasNext())
		{
			ImageWriter writer = writers.next();
		ImageWriteParam params = writer.getDefaultWriteParam();
		params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		params.setCompressionQuality(0.5f);
		
		
		writer.setOutput(new MemoryCacheImageOutputStream(outputStream)); // Set the output for the writer
		writer.write(null, new IIOImage(image, null, null), params); // Write image with compression parameters

		writer.dispose();
		}
		return outputStream.toByteArray();
	}
}
