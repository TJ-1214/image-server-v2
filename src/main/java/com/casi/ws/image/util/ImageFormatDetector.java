package com.casi.ws.image.util;

public class ImageFormatDetector {

	public static ImageFormat detect(byte[] data) {
		if (data.length >= 2 && data[0] == (byte) 0xFF && data[1] == (byte) 0xD8) {
			return ImageFormat.JPG;
		} else if (data.length >= 8 && data[0] == (byte) 0x89 && data[1] == (byte) 0x50 && data[2] == (byte) 0x4E
				&& data[3] == (byte) 0x47 && data[4] == (byte) 0x0D && data[5] == (byte) 0x0A && data[6] == (byte) 0x1A
				&& data[7] == (byte) 0x0A) {
			return ImageFormat.PNG;
		} else {
			return ImageFormat.UNKNOWN;
		}
	}
}
