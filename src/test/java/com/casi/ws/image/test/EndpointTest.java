package com.casi.ws.image.test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.junit.jupiter.api.Test;

import com.casi.ws.image.constant.Constant;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import jakarta.ws.rs.core.MediaType;

public class EndpointTest {

	/* upload image */
	@Test
	public void uploadImageResponse() throws URISyntaxException, IOException, InterruptedException,
			NoSuchAlgorithmException, KeyManagementException {

		var body = new MultipartFormDataBodyPublisher().add("ownerClass", "x-act").add("ownerKey", "1").addFile("data",
				Path.of("/home/tant/Downloads/cat.jpg"));
		
//		for trusting ssl self signed certificate
		TrustManager dummyTrust = new X509ExtendedTrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		};

		
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { dummyTrust }, new SecureRandom());

		HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();

		HttpRequest request = HttpRequest.newBuilder(new URI("https://localhost:9443/upload"))
				.header("Content-Type", body.contentType()).header("Authorization", "X-Act " + Constant.API_KEY)
				.POST(body).build();

		HttpResponse<?> response = client.send(request, BodyHandlers.discarding());
		assertTrue(response.statusCode() == 201);
		
		
	
	}
	
	
	@Test
	public void fetchImageBytes() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, InterruptedException
	{
		TrustManager dummyTrust = new X509ExtendedTrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		};
		
		String endPoint = "/verify/{ownerClass}/image/data/{ownerKey}";
		
		endPoint = endPoint.replace("{ownerClass}", "x-act").replace("{ownerKey}", "2");
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { dummyTrust }, new SecureRandom());

		HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();
		
		
		HttpRequest request = HttpRequest.newBuilder(new URI("https://localhost:9443"+endPoint))
				.header("Content-Type",MediaType.APPLICATION_JSON).header("Authorization", "X-Act " + Constant.API_KEY).GET().build();
		
		
		HttpResponse<?> response = client.send(request,BodyHandlers.ofByteArray());
		
		String bytes = response.body().toString();
		
		
		System.out.println(bytes);
		assertTrue(response.statusCode() == 200);


		
	}
	
	@Test
	public void fetchImageKeys() throws NoSuchAlgorithmException, KeyManagementException, URISyntaxException, IOException, InterruptedException
	{
		TrustManager dummyTrust = new X509ExtendedTrustManager() {

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
					throws CertificateException {
				// TODO Auto-generated method stub

			}
		};
		
	String endPoint =	"/verify/{ownerClass}/images";
		
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[] { dummyTrust }, new SecureRandom());

		endPoint =endPoint.replace("{ownerClass}", "x-act");
		
		
		HttpClient client = HttpClient.newBuilder().sslContext(sslContext).build();
		
		
		HttpRequest request = HttpRequest.newBuilder(new URI("https://localhost:9443"+endPoint))
				.header("Content-Type",MediaType.APPLICATION_JSON).header("Authorization", "X-Act " + Constant.API_KEY).GET().build();
		
		
		HttpResponse<?> response = client.send(request,BodyHandlers.ofString());
		
		String json = response.body().toString();
		
		
		System.out.println(json);
		assertTrue(response.statusCode() == 200);
		
		

	}

}
