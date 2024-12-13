package com.secinfostore.util;
import com.secinfostore.exception.ValidationExistsException;
import com.secinfostore.controller.interfaces.ProgressObserver;
import com.secinfostore.model.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.crypto.SecretKey;
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
//            e.printStackTrace();
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
//            e.printStackTrace();
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

    public static Optional<List<AccountObj>> getAllAccounts() {
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
        } finally {
            session.close();
        }

        if ((encAccountList != null) && !encAccountList.isEmpty()) {
            decAccountList = new ArrayList<>();
            for (AccountObj encAccount : encAccountList) {
                decAccountList.add(InformationFactory.decAccount(encAccount));
            }
        }
        return Optional.ofNullable(decAccountList);
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
        } finally {
            session.close();
        }

        if ((encAccountList != null) && !encAccountList.isEmpty()) {
            decAccountList = new LinkedList<>();
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
        } finally {
            session.close();
        }
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
//            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            session.close();
        }


        return Optional.ofNullable(textObj);
    }

    public static Optional<List<TextObjDTO>> getTextEntries(int pageNumber, int pageSize, boolean ascending) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<TextObjDTO> encTextEntryList = null;
        List<TextObjDTO> decTextEntryList = null;

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TextObjDTO> cq = cb.createQuery(TextObjDTO.class);
            Root<TextObj> root = cq.from(TextObj.class);

            if (ascending) {
                cq.orderBy(cb.desc(root.get("timeModified")));
            }

            cq.select(cb.construct(
                    TextObjDTO.class,
                    root.get("textId"),
                    root.get("timeModified"),
                    root.get("textTitle"),
                    root.get("tags")
            ));

            TypedQuery<TextObjDTO> query = session.createQuery(cq);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);

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

    public static Optional<List<TextObjDTO>> getTextEntries(int pageNumber, int pageSize, boolean ascending, String tagSearchKey) {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<TextObjDTO> encTextEntryList = null;
        List<TextObjDTO> decTextEntryList = null;

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<TextObjDTO> cq = cb.createQuery(TextObjDTO.class);
            Root<TextObj> root = cq.from(TextObj.class);

            String enctagSearchKey = InformationFactory.tagEcbBlockENC(tagSearchKey);

            if (ascending) {
                cq.orderBy(cb.desc(root.get("timeModified")));
            }

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
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
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

    public static Optional<List<TextObj>> getTextEntriesActual() {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        List<TextObj> encTextEntryList = null;
        List<TextObj> decTextEntryList = null;

        try {
            String hql = "FROM TextObj";
            encTextEntryList = session.createQuery(hql, TextObj.class).getResultList();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
//            e.printStackTrace();
        } finally {
            session.close();
        }

        if ((encTextEntryList != null) && !encTextEntryList.isEmpty()) {
            decTextEntryList = new LinkedList<>();
            for (TextObj encTextEntry : encTextEntryList) {
                decTextEntryList.add(InformationFactory.decTextEntry(encTextEntry));
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
//            e.printStackTrace();
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
//            e.printStackTrace();
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
            //e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
    }

    public static long totalAccountRecords() {
        long totalRecords = 0;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<AccountObj> root = cq.from(AccountObj.class);
        cq.select(cb.count(root));

        Query<Long> query = session.createQuery(cq);
        totalRecords = query.getSingleResult();
        transaction.commit();

        return totalRecords;
    }

    public static long totalTextEntryRecords() {
        long totalRecords = 0;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<TextObj> root = cq.from(TextObj.class);
        cq.select(cb.count(root));

        Query<Long> query = session.createQuery(cq);
        totalRecords = query.getSingleResult();
        transaction.commit();

        return totalRecords;
    }

    public static long totalChangeLogRecords() {
        long totalRecords = 0;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<ChangeLogObj> root = cq.from(ChangeLogObj.class);
        cq.select(cb.count(root));

        Query<Long> query = session.createQuery(cq);
        totalRecords = query.getSingleResult();
        transaction.commit();

        return totalRecords;
    }

    public static int calculateTotalPagesTextEntry(long pageSize) {
        long totalRecords = totalTextEntryRecords();
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    public static int calculateTotalPagesTextEntry(long pageSize, String searchKey) {
        long totalRecords = 0;
        Session session = getSession();
        Transaction transaction = session.beginTransaction();

        DataStore dataStore = DataStore.getInstance();
        SecretKey key = (SecretKey) dataStore.getObject("default_key");

        try {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<TextObj> root = countQuery.from(TextObj.class);

            String enctagSearchKey = EncryptionDecryption.encryptAESECB(searchKey, key);

            Predicate filter = cb.or(
                    cb.like(cb.lower(root.get("tags")), "%" + enctagSearchKey + "%")
            );
            countQuery.select(cb.count(root)).where(filter);
            totalRecords = session.createQuery(countQuery).getSingleResult();

            if (transaction != null)
                transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {
            session.close();
        }

        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    public static boolean reEncryption(ProgressObserver observer, SecretKey newKey) {
        long totalRecords = 0;
        long totalRecordsProcessed = 0;
        observer.updateProgress(0.0);
        observer.updateStatus("Getting total Records");

        totalRecords += totalAccountRecords();
        totalRecords += totalTextEntryRecords();
        totalRecords += totalChangeLogRecords();
        totalRecords += 1;

        DataStore dataStore = DataStore.getInstance();
        SecretKey oldKey = (SecretKey) dataStore.getObject("default_key");

        // Debugging: Check if oldKey and newKey are correctly retrieved
        // System.out.println("Old Key: " + oldKey);
        // System.out.println("New Key: " + newKey);

        if (oldKey == null || newKey == null) {
//            System.err.println("Error: Old key or new key is null.");
            return false;
        }

        Optional<Validation> validationOptional = getValidation();
        if (validationOptional.isPresent()) {
            Validation validation = validationOptional.get();
            try {
//                System.out.println("Decrypting Validation with old key...");
                validation.setTestText(EncryptionDecryption.decryptAESGCM(validation.getTestText(), oldKey));

//                System.out.println("Encrypting Validation with new key...");
                validation.setTestText(EncryptionDecryption.encryptAESGCM(validation.getTestText(), newKey));
            } catch (Exception e) {
             //   System.err.println("Error re-encrypting Validation:");
              //  e.printStackTrace();
                return false;
            }

            Session session = getSession();
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(validation);
            transaction.commit();
            session.close();

            totalRecordsProcessed++;
            observer.updateStatus("Validation Re Encryption Finished");
            if (totalRecords > 0) {
                observer.updateProgress((double) totalRecordsProcessed / totalRecords);
            }
        } else {
           // System.err.println("Validation data not found.");
        }

        observer.updateStatus("Re Encrypting Accounts");
        Optional<List<AccountObj>> accountObjListOptional = getAllAccounts();
        if (accountObjListOptional.isPresent()) {
            Session session = getSession();
            Transaction transaction = session.beginTransaction();

            for (AccountObj account : accountObjListOptional.get()) {
                try {
                  //  System.out.println("Re-encrypting account: " + account.getAccountId());
                    account = InformationFactory.reEncryptAccountObj(account, newKey);
                    session.saveOrUpdate(account);
                    totalRecordsProcessed++;
                    if (totalRecords > 0) {
                        observer.updateProgress((double) totalRecordsProcessed / totalRecords);
                    }
                } catch (Exception e) {
                  //  System.err.println("Error re-encrypting account: " + account.getAccountId());
                   // e.printStackTrace();
                }
            }

            transaction.commit();
            session.close();
        } else {
           // System.err.println("Accounts data not found.");
        }

        observer.updateStatus("Re Encrypting Change Log");
        Optional<List<ChangeLogObj>> changeLogObjListOptional = getChangeLogs();
        if (changeLogObjListOptional.isPresent()) {
            Session session = getSession();
            Transaction transaction = session.beginTransaction();

            for (ChangeLogObj changeLogObj : changeLogObjListOptional.get()) {
                try {
                   // System.out.println("Re-encrypting Change Log: " + changeLogObj.getChangeId());
                    changeLogObj = InformationFactory.reEncryptChangeLog(changeLogObj, newKey);
                    session.saveOrUpdate(changeLogObj);
                    totalRecordsProcessed++;
                    if (totalRecords > 0) {
                        observer.updateProgress((double) totalRecordsProcessed / totalRecords);
                    }
                } catch (Exception e) {
                   // System.err.println("Error re-encrypting Change Log: " + changeLogObj.getChangeId());
                    //e.printStackTrace();
                }
            }

            transaction.commit();
            session.close();
        } else {
            System.err.println("Change Log data not found.");
        }

        observer.updateStatus("Re Encrypting Text Entries");
        Optional<List<TextObj>> textObjListOptional = getTextEntriesActual();
        if (textObjListOptional.isPresent()) {
            Session session = getSession();
            Transaction transaction = session.beginTransaction();

            for (TextObj textObj : textObjListOptional.get()) {
                try {
//                    System.out.println("Re-encrypting Text Entry: " + textObj.getTextId());
                    textObj = InformationFactory.reEncryptTextEntry(textObj, newKey);
                    session.saveOrUpdate(textObj);
                    totalRecordsProcessed++;
                    if (totalRecords > 0) {
                        observer.updateProgress((double) totalRecordsProcessed / totalRecords);
                    }
                } catch (Exception e) {
//                    System.err.println("Error re-encrypting Text Entry: " + textObj.getTextId());
//                    e.printStackTrace();
                }
            }

            transaction.commit();
            session.close();
        } else {
//            System.err.println("Text Entries data not found.");
        }

        // Store the new key in DataStore
        try {
            //System.out.println("Saving new key to DataStore...");
            dataStore.insertObject("default_key", newKey);
        } catch (Exception e) {
            //System.err.println("Error saving new key to DataStore:");
            //e.printStackTrace();
        }

        observer.updateStatus("Re Encryption Complete");
        observer.updateProgress(1.0);
        return true;
    }
}
