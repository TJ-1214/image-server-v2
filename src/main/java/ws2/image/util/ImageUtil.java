package ws2.image.util;

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
	
	/*
	 * 1. **JPEG (Joint Photographic Experts Group)**: - Magic Bytes: FF D8 FF -
	 * Description: JPEG files typically start with the byte sequence FF D8 FF.
	 * 
	 * 2. **PNG (Portable Network Graphics)**: - Magic Bytes: 89 50 4E 47 0D 0A 1A
	 * 0A - Description: PNG files begin with the byte sequence 89 50 4E 47 0D 0A 1A
	 * 0A.
	 * 
	 * 3. **GIF (Graphics Interchange Format)**: - Magic Bytes: 47 49 46 38 37 61
	 * (GIF87a) or 47 49 46 38 39 61 (GIF89a) - Description: GIF files start with
	 * the byte sequence 47 49 46 38 37 61 (for GIF87a) or 47 49 46 38 39 61 (for
	 * GIF89a).
	 * 
	 * 4. **BMP (Bitmap)**: - Magic Bytes: 42 4D - Description: BMP files begin with
	 * the byte sequence 42 4D.
	 * 
	 * 5. **TIFF (Tagged Image File Format)**: - Magic Bytes: 49 49 2A 00
	 * (little-endian) or 4D 4D 00 2A (big-endian) - Description: TIFF files start
	 * with either 49 49 2A 00 (little-endian) or 4D 4D 00 2A (big-endian).
	 */

	/**
	 * 
	 * @param data byte arrays
	 * @return Format enum
	 */

	public static Format format(byte[] data) {

		if (data.length >= 2 && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8) {
			return Format.JPEG;
		}
		if (data.length >= 8 && data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E
				&& data[3] == (byte) 0x47 && data[4] == (byte) 0x0D && data[5] == (byte) 0x0A && data[6] == (byte) 0x1A
				&& data[7] == (byte) 0x0A) {
			return Format.PNG;
		}

		if (data.length >= 2 && data[0] == 0x42 && data[1] == 0x4D) {
			return Format.BMP;
		}

		if (data.length >= 6 && data[0] == 0x47 && data[1] == 0x49 && data[2] == 0x46 && data[3] == 0x38
				&& data[5] == 0x61 && (data[4] == 0x37 || data[4] == 0x39)) {
			return Format.GIF;
		}

		if (data.length >= 4 && data[0] == 0x49 && data[1] == 0x49 && data[2] == 0x2A && data[3] == 0x00
				|| data[0] == 0x4D && data[1] == 0x4D && data[2] == 0x00 && data[3] == 0x2A) {
			return Format.TIFF;
		}
		return Format.UNKNOWN;
	}

	/**
	 * 
	 * @param data bytes array
	 * @return bytes array
	 * @throws IOException
	 */

	public static byte[] compress(byte[] data) throws IOException {
//		detect image type
		Format format = ImageUtil.format(data);

//		convert to input stream
		InputStream inputImage = new ByteArrayInputStream(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		BufferedImage image = ImageIO.read(inputImage);
		
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format.toString());

		if (writers.hasNext()) {
			ImageWriter writer = writers.next();
			ImageWriteParam params = writer.getDefaultWriteParam();
			params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			params.setCompressionQuality(CompressionQuality.BALANCED.getSize());
		
			writer.setOutput(new MemoryCacheImageOutputStream(outputStream)); // Set the output for the writer
			writer.write(null, new IIOImage(image, null, null), params); // Write image with compression parameters

			writer.dispose();
		}
		return outputStream.toByteArray();
	}

	/**
	 * 
	 * @param data
	 * @param quality
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] data, CompressionQuality quality) throws IOException {
		if (quality == null) {
			throw new NullPointerException("Compression is null");
		}

//		detect image type
		Format format = ImageUtil.format(data);

//		convert to input stream
		InputStream inputImage = new ByteArrayInputStream(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		BufferedImage image = ImageIO.read(inputImage);

		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format.toString());

		if (writers.hasNext()) {
			ImageWriter writer = writers.next();
			ImageWriteParam params = writer.getDefaultWriteParam();
			params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

			params.setCompressionQuality(quality.getSize());

			writer.setOutput(new MemoryCacheImageOutputStream(outputStream)); // Set the output for the writer
			writer.write(null, new IIOImage(image, null, null), params); // Write image with compression parameters

			writer.dispose();
		}
		return outputStream.toByteArray();
	}

}
