package com.secinfostore.secureinfostore.util;

import com.secinfostore.secureinfostore.exception.ValidationExistsException;
import com.secinfostore.secureinfostore.model.AccountObj;
import com.secinfostore.secureinfostore.model.ChangeLogObj;
import com.secinfostore.secureinfostore.model.InformationFactory;
import com.secinfostore.secureinfostore.model.Validation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.crypto.SecretKey;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseHandler {
    private static Session getSession() {
        HibernateUtil util = HibernateUtil.getInstance();
        SessionFactory sessionFactory = util.getSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        return session;
    }

    public static boolean createValidation(SecretKey key) {
        Validation validation = new Validation(1, "sampleText");
        try {
            validation.setTestText(EncryptionDecryption.encryptAESGCM(validation.getTestText(), key));
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

    public static boolean saveAccount(AccountObj accountObj) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        long accountId = accountObj.getAccountId();
        try {
            if (accountId == 0) {
                session.saveOrUpdate(accountObj);
            } else {
                accountObj = InformationFactory.updateAccount(accountObj);
                session.saveOrUpdate(accountObj);
            }

            ChangeLogObj changeLogObj = InformationFactory.newChangeLog(accountObj);
            session.save(changeLogObj);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        }
    }

    public static boolean saveAccount(List<AccountObj> accountList) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        int i = 0;
        try {
            for (AccountObj account : accountList) {
                AccountObj newAccount = InformationFactory.newAccount(account.getPlatformName(), account.getUserName(), account.getEmail(), account.getPassword(), null);
                session.save(newAccount);
                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        transaction.commit();
        return true;
    }

    public static Optional<List<AccountObj>> getAccounts() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<AccountObj> encAccountList = null;
        List<AccountObj> decAccountList = null;

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<AccountObj> cq = cb.createQuery(AccountObj.class);
            Root<AccountObj> root = cq.from(AccountObj.class);

            cq.select(cb.construct(
                    AccountObj.class,
                    root.get("accountId"),
                    root.get("platformName"),
                    root.get("userName"),
                    root.get("email"),
                    root.get("password"),
                    root.get("platformThumbnail")
            ));

            TypedQuery<AccountObj> query = session.createQuery(cq);
            encAccountList = query.getResultList();

            if (transaction != null)
                transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }

        if ((encAccountList != null) && !encAccountList.isEmpty()) {
            decAccountList = new ArrayList<>();
            for (AccountObj encAccount : encAccountList) {
                decAccountList.add(InformationFactory.decAccount(encAccount));
            }
        }
        return Optional.ofNullable(decAccountList);
    }

    public static boolean deleteAccount(AccountObj accountObj) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(accountObj);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        }

    }
}
