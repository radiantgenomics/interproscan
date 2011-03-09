package uk.ac.ebi.interpro.scan.jms.activemq;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.interpro.scan.management.dao.StepExecutionDAO;
import uk.ac.ebi.interpro.scan.management.model.StepExecution;
import uk.ac.ebi.interpro.scan.management.model.StepInstance;

import javax.jms.*;
import java.lang.IllegalStateException;

/**
 * TODO: Description
 *
 * @author Phil Jones
 * @version $Id$
 * @since 1.0-SNAPSHOT
 */
public class MasterMessageSenderImpl implements MasterMessageSender {

    private static final Logger LOGGER = Logger.getLogger(MasterMessageSenderImpl.class.getName());

    public static final String HIGH_MEMORY_PROPERTY = "highmem";

    private JmsTemplate jmsTemplate;

    private final Object jmsTemplateLock = new Object();

    private StepExecutionDAO stepExecutionDAO;

    private Destination workerJobRequestQueue;


    @Required
    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @Required
    public void setStepExecutionDAO(StepExecutionDAO stepExecutionDAO) {
        this.stepExecutionDAO = stepExecutionDAO;
    }

    @Required
    public void setWorkerJobRequestQueue(Destination workerJobRequestQueue) {
        this.workerJobRequestQueue = workerJobRequestQueue;
    }

    /**
     * Creates messages to be sent to Worker nodes.
     * Does all of this in a transaction, hence in this separate interface.
     *
     * @param stepInstance to send as a message
     * @throws javax.jms.JMSException in the event of a failure sending the message to the JMS Broker.
     */
    @Transactional
    public void sendMessage(StepInstance stepInstance, final boolean highMemory, final int priority) throws JMSException {
        LOGGER.debug("Attempting to send message to queue.");
        final StepExecution stepExecution = stepInstance.createStepExecution();
        stepExecutionDAO.insert(stepExecution);
        stepExecution.submit(stepExecutionDAO);

        if (!jmsTemplate.isExplicitQosEnabled()) {
            throw new IllegalStateException("It is not possible to set the priority of the JMS message, as the JMSTemplate does not have explicitQosEnabled.");
        }

        synchronized (jmsTemplateLock) {
            jmsTemplate.setPriority(priority);
            jmsTemplate.send(workerJobRequestQueue, new MessageCreator() {
                public Message createMessage(Session session) throws JMSException {
                    final ObjectMessage message = session.createObjectMessage(stepExecution);
                    message.setBooleanProperty(HIGH_MEMORY_PROPERTY, highMemory);
                    return message;
                }
            });
        }
    }
}