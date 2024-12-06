package com.choic11.repository;

import com.choic11.model.TblState;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@Transactional
public class StateRepository {

	@Autowired
	private SessionFactory factory;

	@Autowired
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	public List<TblState> getStates(int countryId) {

//		Criteria createCriteria = getSession().createCriteria(TblState.class);
//		createCriteria.add(Restrictions.eq("isDeleted", "N"));
//		createCriteria.add(Restrictions.eq("status", "A"));
//
//		createCriteria.addOrder(Order.asc("name"));
//		return createCriteria.list();

		Query query = getSession().createQuery("SELECT ts FROM TblState ts WHERE ts.isDeleted = 'N' AND ts.status = 'A' ORDER BY ts.name ASC");
		return query.getResultList();
	}

	private Session getSession() {
		Session session = factory.getCurrentSession();
		if (session == null) {
			session = factory.openSession();
		}
		return session;
	}

}
