package com.casi.ws.image.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.casi.ws.image.dao.ImageDao;
import com.casi.ws.image.model.Image;

import io.image.util.ImageUtil;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

@RunWith(Arquillian.class)
public class EndpointTest {


	@ArquillianResource
	private URL baseURL;

	
	private String key;
	@Inject
	private ImageDao imageDao;

	@Deployment(testable = true)
	public static WebArchive createDeployment() {
		WebArchive archive = ShrinkWrap
				.create(WebArchive.class).addPackages(true, "com.casi.ws.image")
				.addPackage(ImageUtil.class.getPackage())
				.addAsResource("META-INF/persistence.xml");
				
		return archive;
	}
	
	// A helper method to determine the image format
    private  String getImageFormat(File file) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
        if (!iter.hasNext()) {
            return null;
        }
        ImageReader reader = iter.next();
        iis.close();
        return reader.getFormatName();
    }
	
	@Before
	@Test
	@RunAsClient
	public void keyGenerator()
	{
		WebTarget target = ClientBuilder.newBuilder().build().target(baseURL+"/key");
		Response response = target.request().get();
	
		key = response.readEntity(String.class);
		
		
//		check if response is ok
		Assert.assertEquals(200, response.getStatus());

	}
	
	
	@Test
	@InSequence(1)
	public void uploadImage() throws IOException, URISyntaxException
	{
		File file = new File("/home/tant/Pictures/catshit.png");
		
	
		
		BufferedImage bufferedImage = ImageIO.read(file);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	
		ImageIO.write(bufferedImage, getImageFormat(file), baos);
		
		baos.flush();
		
		byte[] data = baos.toByteArray();
		
		Image image = new Image();
		
		image.setFileName("test-picture-"+LocalDate.now());
		image.setFileType(ImageUtil.format(data).toString());
		image.setOwnerClass(Image.class.getSimpleName());
		image.setOwnerKey("1");
		image.setData(data);
		
		boolean isPersist = imageDao.newImage(image);
		Assert.assertTrue( isPersist);
		
		
		
	}
	
	@Test
	@RunAsClient
	@InSequence(2)
	public void unathorizedAccess()
	{
		
		String context = "/v2/image/{id}";
		
		context  = context.replace("{id}", "7466e566-3ecc-45c3-8705-5fbca8a36dd2");
		
		WebTarget target = ClientBuilder.newBuilder().build().target(baseURL+context);
		Response response = target.request().get();
		
		Assert.assertEquals(401, response.getStatus());
		
	}
	
}
