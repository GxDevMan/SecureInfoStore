package com.secinfostore.secureinfostore.util;

import com.secinfostore.secureinfostore.exception.ValidationExistsException;
import com.secinfostore.secureinfostore.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.crypto.SecretKey;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.LinkedList;
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
        session.close();
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
        } finally {
            session.close();
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
        } finally {
            session.close();
        }
    }

    public static boolean deleteTextEntry(TextObj textEntry) {
        Session session = getSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.delete(textEntry);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public static boolean saveTextEntry(TextObj textEntry) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        long textEntryId = textEntry.getTextId();
        try {
            if (textEntryId == 0) {
                session.saveOrUpdate(textEntry);
            } else {
                textEntry = InformationFactory.updateTextEntry(textEntry);
                session.saveOrUpdate(textEntry);
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        } finally {
            session.close();
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
        } finally {
            session.close();
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
                ChangeLogObj changeLogObj = InformationFactory.newChangeLog(newAccount);
                session.save(changeLogObj);
                if (i % 50 == 0) {
                    session.flush();
                    session.clear();
                }
                i++;
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            return false;
        } finally {
            session.close();
        }
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

    public static Optional<List<ChangeLogObj>> getChangeLogs() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<ChangeLogObj> decChangeLogList = null;
        try {
            CriteriaQuery<ChangeLogObj> criteriaQuery = session.getCriteriaBuilder().createQuery(ChangeLogObj.class);
            criteriaQuery.from(ChangeLogObj.class);

            Query query = session.createQuery(criteriaQuery);
            List<ChangeLogObj> objList = query.getResultList();
            transaction.commit();

            if (objList != null && !(objList.isEmpty())) {
                decChangeLogList = new LinkedList<>();
                for (ChangeLogObj eachChangeLog : objList) {
                    decChangeLogList.add(InformationFactory.decChangeLog(eachChangeLog));
                }
            }
            return Optional.ofNullable(decChangeLogList);
        } catch (Exception e) {
            transaction.rollback();
            return Optional.empty();
        }
    }

    public static Optional<List<AccountObj>> getAccounts(String searchQuery) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<AccountObj> encAccountList = null;
        List<AccountObj> decAccountList = null;

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<AccountObj> cq = cb.createQuery(AccountObj.class);
            Root<AccountObj> root = cq.from(AccountObj.class);

            Predicate filter = cb.or(
                    cb.like(cb.lower(root.get("platformName")), "%" + searchQuery + "%")
            );
            cq.where(filter);

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
            decAccountList = new LinkedList<>();
            for (AccountObj encAccount : encAccountList) {
                decAccountList.add(InformationFactory.decAccount(encAccount));
            }
        }
        return Optional.ofNullable(decAccountList);
    }

    public static Optional<TextObj> getTextEntryById(long id) {
        Session session = getSession();
        Transaction transaction = null;
        TextObj textObj = null;

        try {
            transaction = session.beginTransaction();
            TextObj textObjdb = session.get(TextObj.class, id);
            textObj = new TextObj(textObjdb);
            transaction.commit();

            textObj = InformationFactory.decTextEntry(textObj);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }


        return Optional.ofNullable(textObj);
    }

    public static Optional<List<TextObjDTO>> getTextEntries() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<TextObjDTO> encTextEntryList = null;
        List<TextObjDTO> decTextEntryList = null;

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TextObjDTO> cq = cb.createQuery(TextObjDTO.class);
            Root<TextObj> root = cq.from(TextObj.class);

            cq.select(cb.construct(
                    TextObjDTO.class,
                    root.get("textId"),
                    root.get("timeModified"),
                    root.get("textTitle"),
                    root.get("tags")
            ));

            TypedQuery<TextObjDTO> query = session.createQuery(cq);
            encTextEntryList = query.getResultList();

            if (transaction != null)
                transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }

        if ((encTextEntryList != null) && !encTextEntryList.isEmpty()) {
            decTextEntryList = new LinkedList<>();
            for (TextObjDTO encTextEntry : encTextEntryList) {
                decTextEntryList.add(InformationFactory.decPreviewTextEntry(encTextEntry));
            }
        }
        return Optional.ofNullable(decTextEntryList);
    }

    public static Optional<List<TextObjDTO>> getTextEntries(String tagSearchKey) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<TextObjDTO> encTextEntryList = null;
        List<TextObjDTO> decTextEntryList = null;

        DataStore dataStore = DataStore.getInstance();
        SecretKey key = (SecretKey) dataStore.getObject("default_key");

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TextObjDTO> cq = cb.createQuery(TextObjDTO.class);
            Root<TextObj> root = cq.from(TextObj.class);

            String enctagSearchKey = EncryptionDecryption.encryptAESECB(tagSearchKey, key);

            Predicate filter = cb.or(
                    cb.like(cb.lower(root.get("tags")), "%" + enctagSearchKey + "%")
            );
            cq.where(filter);

            cq.select(cb.construct(
                    TextObjDTO.class,
                    root.get("textId"),
                    root.get("timeModified"),
                    root.get("textTitle"),
                    root.get("tags")
            ));

            TypedQuery<TextObjDTO> query = session.createQuery(cq);
            encTextEntryList = query.getResultList();

            if (transaction != null)
                transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }

        if ((encTextEntryList != null) && !encTextEntryList.isEmpty()) {
            decTextEntryList = new ArrayList<>();
            for (TextObjDTO encTextEntry : encTextEntryList) {
                decTextEntryList.add(InformationFactory.decPreviewTextEntry(encTextEntry));
            }
        }
        return Optional.ofNullable(decTextEntryList);
    }

    public static boolean deleteAccount(AccountObj accountObj) {
        deleteChangeLogsByAccountId(accountObj.getAccountId());
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(accountObj);
            transaction.commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            return false;
        } finally {
            session.close();
        }

    }

    public static boolean deleteChangeLog(ChangeLogObj changeLogObj) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.delete(changeLogObj);
            transaction.commit();
            return true;
        } catch (Exception e) {
            transaction.rollback();
            return false;
        } finally {
            session.close();
        }

    }

    public static boolean deleteAllChangeLog() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery("DELETE FROM ChangeLogObj");
            query.executeUpdate();

            transaction.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
            return false;
        } finally {
            session.close();
        }

    }

    private static boolean deleteChangeLogsByAccountId(long accountId) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
            String hql = "DELETE FROM ChangeLogObj c WHERE c.account.accountId = :accountId";
            int result = session.createQuery(hql)
                    .setParameter("accountId", accountId)
                    .executeUpdate();
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }
}
