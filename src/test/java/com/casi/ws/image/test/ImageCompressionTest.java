package com.casi.ws.image.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.junit.jupiter.api.Test;

public class ImageCompressionTest {

	 @Test
	    public void test_1() throws IOException {
	        File file = new File("/home/tant/Downloads/gv_test_1.jpg");
	        FileInputStream inputImage = new FileInputStream(file);
	        BufferedImage image = ImageIO.read(file);

	        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
	        ImageWriter writer = writers.next();

	        ImageWriteParam params = writer.getDefaultWriteParam();
	        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	        params.setCompressionQuality(0.3f);

	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	        
	        
	        writer.setOutput(new MemoryCacheImageOutputStream(outputStream)); // Set the output for the writer
	        writer.write(null, new IIOImage(image, null, null), params); // Write image with compression parameters

	        byte[] originalSize = new byte[(int) file.length()];
	        inputImage.read(originalSize);

	        byte[] compressSize = outputStream.toByteArray();

	        System.out.println("Original size: " + originalSize.length);
	        System.out.println("Compressed size: " + compressSize.length +"\n");

	        assertTrue(originalSize.length != compressSize.length);
	    }
	 
	 
	
}
