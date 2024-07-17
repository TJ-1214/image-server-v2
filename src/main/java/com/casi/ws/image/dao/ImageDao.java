package com.casi.ws.image.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import com.casi.ws.image.model.Image;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TransactionRequiredException;

/*Wrapper class*/
@Stateless
public class ImageDao {

	@PersistenceContext
	private EntityManager em;

	
	public boolean newImage(Object obj) {
		try {
			em.persist(obj);
			return true;
		} catch (EntityExistsException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (TransactionRequiredException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
	}
	

	public boolean bulkUpload(List<Image> images) {
		try {
			
			images.forEach(m -> em.persist(m));
			return true;
		} catch (EntityExistsException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (TransactionRequiredException e) {
			return false;
		} catch (RuntimeException e) {
			return false;
		}
	}
	
	public boolean updateImage(Object image) {
		Object obj = update(image);
		if (obj != null) {
			return true;
		}
		return false;
	}

	public Object update(Object obj) {
		try {

			return em.merge(obj);

		} catch (IllegalArgumentException e) {
			return null;
		} catch (TransactionRequiredException e) {
			return null;
		}
	}
	



	
	
	public boolean delete(Object obj) {
		try {

			em.remove(em.merge(obj));
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (TransactionRequiredException e) {
			return false;
		}

	}
	@Deprecated
	public boolean delete(String oKey, String oClass) {
		try {
			
			em.remove(find(oKey, oClass));
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (TransactionRequiredException e) {
			return false;
		}

	}
	
	


	/**
	 * 
	 * @param oKey   - String
	 * @param oClass - String
	 * @return Image object
	 */
	@Deprecated
	public Image find(String oKey, String oClass) {
		try {
			return em.createQuery(
					"select u from " + Image.NAME + " u where u.ownerKey = :oKey and u.ownerClass = :oClass",
					Image.class).setParameter("oKey", oKey).setParameter("oClass", oClass).getResultList().get(0);

		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	/**
	 * 
	 * @param oKey   - String
	 * @param oClass - String
	 * @return Image object
	 */
	public Image find(String uniqueId) {
		try {
			return em.find(Image.class, UUID.fromString(uniqueId));

		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		} catch (NoResultException e) {
			return null;
		}
	}
	
	
	@Deprecated
	public List<Image> findAll() {
		try {
			return em.createQuery("select u from " + Image.NAME + " u ", Image.class).getResultList();
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
	}
	
	/**
	 * 
	 * @return Image objects
	 */
	public Stream findImages(String oKey, String oClass) {
		try {
			return em
					.createQuery(
							"select u from " + Image.NAME + " u where u.ownerKey = :oKey and u.ownerClass = :oClass",
							Image.class)
					.setParameter("oKey", oKey).setParameter("oClass", oClass).getResultList().stream();

		} catch (IllegalArgumentException e) {
			return Stream.empty();
		} catch (IllegalStateException e) {
			return Stream.empty();
		} catch (NoResultException e) {
			return Stream.empty();
		}
	}
	


	/**
	 * 
	 * @param ownerClass - String
	 * @return List of image objects
	 */
	public List<Image> findAll(String ownerClass) {
		try {
			return em.createQuery("select u from " + Image.NAME + " u where u.ownerClass = :ownerClass", Image.class)
					.setParameter("ownerClass", ownerClass).getResultList();
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
	}
	
	/**
	 * 
	 * @param oClass
	 * @param oKey
	 * @return List Image Objects
	 */
	public List<Image> findAll(String oClass, String oKey) {
		try {
			return em.createQuery(
					"select u from " + Image.NAME + " u where u.ownerKey = :oKey and u.ownerClass = :oClass",
					Image.class).setParameter("oKey", oKey).setParameter("oClass", oClass).getResultList();
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
	}
	

}
