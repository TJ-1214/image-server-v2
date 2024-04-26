package com.casi.ws.image.dao;

import java.util.ArrayList;
import java.util.List;

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
			em.remove(obj);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		} catch (TransactionRequiredException e) {
			return false;
		}

	}

	public Image find(String id) {
		try {
			return em.createQuery("select u from " + Image.NAME + " u where u.ownerKey = :id", Image.class)
					.setParameter("id", id).getSingleResult();

		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalStateException e) {
			return null;
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<Image> findAll() {
		try {
			return em.createQuery("select u from " + Image.NAME + " u ", Image.class).getResultList();
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
	}

	public List<Image> findAll(String ownerClass) {
		try {
			return em.createQuery("select u from " + Image.NAME + " u where u.ownerClass = :ownerClass", Image.class)
					.setParameter("ownerClass", ownerClass).getResultList();
		} catch (IllegalArgumentException e) {
			return new ArrayList<>();
		}
	}

}
