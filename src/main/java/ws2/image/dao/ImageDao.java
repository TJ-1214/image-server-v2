package ws2.image.dao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TransactionRequiredException;
import ws2.image.model.Image;

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

	/**
	 * 
	 * @return Image objects
	 */
	@SuppressWarnings("rawtypes")
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
}
