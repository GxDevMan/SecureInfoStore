package com.secinfostore.secureinfostore.util;

import com.secinfostore.secureinfostore.exception.ValidationExistsException;
import com.secinfostore.secureinfostore.model.Validation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import javax.crypto.SecretKey;
import java.util.Optional;

public class DatabaseHandler {
    private static Session getSession() {
        HibernateUtil util = HibernateUtil.getInstance();
        SessionFactory sessionFactory = util.getSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    public static boolean createValidation(SecretKey key){
        Validation validation = new Validation(1,"sampleText");
        try {
            validation.setTestText(EncryptionDecryption.encryptAESGCM(validation.getTestText(),key));
            return saveValidation(validation);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean saveValidation(Validation validation) throws ValidationExistsException {
       Optional<Validation> validationOptional = getValidation();
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        if (!validationOptional.isPresent()) {
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

    public static Optional<Validation> getValidation() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Validation validation = null;
        try {
            validation = session.get(Validation.class, 1L);
            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
        return Optional.ofNullable(validation);
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
