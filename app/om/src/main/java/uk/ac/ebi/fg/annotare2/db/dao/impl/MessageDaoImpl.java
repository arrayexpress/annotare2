/*
 * Copyright 2009-2016 European Molecular Biology Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.ebi.fg.annotare2.db.dao.impl;

import com.google.inject.Inject;
import org.hibernate.criterion.Restrictions;
import uk.ac.ebi.fg.annotare2.db.dao.MessageDao;
import uk.ac.ebi.fg.annotare2.db.model.Message;
import uk.ac.ebi.fg.annotare2.db.model.enums.MessageStatus;
import uk.ac.ebi.fg.annotare2.db.util.HibernateSessionFactory;

import java.util.Collection;
import java.util.Date;

public class MessageDaoImpl extends AbstractDaoImpl<Message> implements MessageDao {

    @Inject
    public  MessageDaoImpl(HibernateSessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Message create(String from, String to, String subject, String body) {
        Message message = new Message(from, to, subject, body);
        save(message);
        return message;
    }

    @Override
    public Message markSent(Message message) {
        message.setSent(new Date());
        message.setStatus(MessageStatus.SENT);
        save(message);
        return message;
    }

    @Override
    public Message markFailed(Message message) {
        message.setSent(null);
        message.setStatus(MessageStatus.ERROR);
        save(message);
        return message;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Message> getMessagesByStatus(MessageStatus... statuses) {
        return getCurrentSession()
                .createCriteria(Message.class)
                .add(Restrictions.in("status", statuses))
                .list();
    }
}
