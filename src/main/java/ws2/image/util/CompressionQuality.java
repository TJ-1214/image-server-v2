package ws2.image.util;

public enum CompressionQuality {

	HIGH_QUALITY(1.0f, "High Image Quality Priority"), MEDIUM_QUALITY(0.8f, "Very Good Image Quality"),
	BALANCED(0.5f, "Balanced quality"), MEDIUM_COMPRESSED(0.2f, "Low Image Quality"),
	HIGH_COMPRESSED(0.0f, "High Compression Priority");

	private float size;
	private String description;

	CompressionQuality(float f, String description) {
		this.size = f;
		this.description = description;
	}

	public float getSize() {
		return size;
	}

	public String getDescription() {
		return description;
	}
}
