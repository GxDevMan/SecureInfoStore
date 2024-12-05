package com.secinfostore.secureinfostore.util;

import com.secinfostore.secureinfostore.exception.ValidationExistsException;
import com.secinfostore.secureinfostore.model.Validation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DatabaseHandler {
    private static Session getSession() {
        HibernateUtil util = HibernateUtil.getInstance();
        SessionFactory sessionFactory = util.getSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    public static boolean saveValidation(Validation validation) throws ValidationExistsException {
        Validation validationCheck = getValidation();
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        if (validationCheck == null) {
            try {
                session.save(validation);
                transaction.commit();
                return true;
            } catch (Exception e) {
                transaction.rollback();
                return false;
            }
        }
        transaction.rollback();
        throw new ValidationExistsException("Validation already exists");
    }

    public static Validation getValidation() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Validation validation = session.get(Validation.class, 1L);
            transaction.commit();
            return validation;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }

    public static boolean deleteValidation(Validation validation) {
        Session session = getSession();
        Transaction transaction = session.getTransaction();
        try {
            session.delete(validation);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }
}
