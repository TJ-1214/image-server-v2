package com.casi.ws.image.model;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

/**
 * Entity implementation class for Entity: Image
 *
 */

@Entity(name = Image.NAME)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "ownerKey", "ownerClass" }))
public class Image implements Serializable {

	public static final String NAME = "wsImage";
	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;

	@Lob
	@NotNull
	private byte[] data;

	private String fileName;
	private String ownerKey;
	private String fileType;

	@NotNull
	private String ownerClass;

	public Image() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOwnerKey() {
		return ownerKey;
	}

	public void setOwnerKey(String ownerKey) {
		this.ownerKey = ownerKey;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getOwnerClass() {
		return ownerClass;
	}

	public void setOwnerClass(String ownerClass) {
		this.ownerClass = ownerClass;
	}

}
