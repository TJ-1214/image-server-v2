package com.casi.ws.image.test;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
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
		WebArchive archive = ShrinkWrap.create(WebArchive.class).addPackages(true, "com.casi.ws.image")
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

		
		List<File> files = new ArrayList<>();
		
//		example data
		files.add(new File("/home/tant/Pictures/gv-example-1.png"));
		files.add(new File("/home/tant/Pictures/gv-example-2.png"));
		

		files.add(new File("/home/tant/Pictures/gv-example-3.png"));
		files.add(new File("/home/tant/Pictures/gv-example-4.png"));
		
//		for random pick file
		Random rand = new Random();
		
		
		
	
		List<Image> images = new ArrayList<>();
		
//		populating image objects
		for (int index = 0; index < rand.nextInt(1,10); index++) {
			File file = files.get(rand.nextInt(1, files.size())      );
			BufferedImage bufferedImage = ImageIO.read(file);
		
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			ImageIO.write(bufferedImage, getImageFormat(file), baos);

			baos.flush();

			byte[] data = baos.toByteArray();

			Image image = new Image();

			image.setFileName("test-picture-" + LocalDate.now());
			image.setFileType(ImageUtil.format(data).toString());
			image.setOwnerClass(Image.class.getName());
			image.setOwnerKey("1");
			image.setData(data);

			images.add(image);

		}
		
		
		
		Assert.assertTrue(imageDao.bulkUpload(images));
		
	}
	
	@Test
	@RunAsClient
	@InSequence(2)
	public void unathorizedAccess() {

		String context = "v2/images/keys/{ownerClass}/{ownerKey}";

		context = context.replace("{ownerClass}", "com.casi.ws.image.model.Image").replace("{ownerKey}", "1");

		WebTarget target = ClientBuilder.newBuilder().build().target(baseURL + context);
		Response response = target.request().get();

		Assert.assertEquals(401, response.getStatus());

	}
	
	
	
	
	@Test
	@RunAsClient
	@InSequence(3)
	public void fetchImages() throws IOException
	{
		String epImage = "v2/images/{ownerClass}/{ownerKey}";
		
		epImage = epImage.replace("{ownerClass}", Image.class.getName()).replace("{ownerKey}", "1");
		
		
		
		WebTarget target = ClientBuilder.newBuilder().build().target(baseURL + epImage);
		Response response = target.request().header("Authorization", "Bearer" + key).get();
		
		
		MultipartInput input = response.readEntity(MultipartInput.class);
		
		
		
		for(InputPart part : input.getParts())
		{
			System.out.println( "Size : "+String.valueOf(part.getBody().readAllBytes().length)  );
		}
		

		
		
	
	}
	
	
	
}
