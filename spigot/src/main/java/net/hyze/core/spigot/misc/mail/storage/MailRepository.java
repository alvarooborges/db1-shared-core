package net.hyze.core.spigot.misc.mail.storage;

import net.hyze.core.shared.providers.MysqlDatabaseProvider;
import net.hyze.core.shared.storage.repositories.MysqlRepository;
import net.hyze.core.spigot.misc.mail.Mail;
import net.hyze.core.spigot.misc.mail.storage.specs.DeleteMailSpec;
import net.hyze.core.spigot.misc.mail.storage.specs.InsertMailSpec;
import net.hyze.core.spigot.misc.mail.storage.specs.SelectMailSpec;
import java.util.LinkedList;
import java.util.Map;
import net.hyze.core.spigot.misc.mail.storage.specs.CountMailSpec;

public class MailRepository extends MysqlRepository {

    public MailRepository(MysqlDatabaseProvider databaseProvider) {
        super(databaseProvider);
    }

    public LinkedList<Mail> fetchMails(int receiverId, String type) {
        return query(new SelectMailSpec(receiverId, type));
    }

    public Map<String, Integer> countMails(int receiverId, String type) {
        return query(new CountMailSpec(receiverId, type));
    }

    public void insertMail(Mail mail) {
        query(new InsertMailSpec(mail));
    }

    public Boolean deleteMail(int id) {
        return query(new DeleteMailSpec(id));
    }

}
