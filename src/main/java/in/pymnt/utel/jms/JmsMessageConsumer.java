/*
* Copyright (c) 2016, BON BIZ IT Services Pvt LTD.
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both

* (a) the Software, and

* (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a “Larger Work” to which the Software is contributed by such licensors),
* 
* without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell, offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms.
* 
* This license is subject to the following condition:
* 
* The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* Author: Ashish Banerjee, ashish@bonbiz.in
*/

package in.pymnt.utel.jms;

import in.pymnt.util.LifeCycle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;


/**
 *
 * @author ashish
 */
public class JmsMessageConsumer implements LifeCycle,  ExceptionListener{
   
    JmsConnectFactory fact = null;
    String queueName = "JIO";
    MessageListener router = null;
    transient Connection connection = null;
    transient Session session = null;
    transient MessageConsumer consumer = null;

    private String loggerName = "JIO";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
    
    
    @Override
    public void start() {
        try {
            connection = fact.createConnection();
            connection.start();
            connection.setExceptionListener(this);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create the destination (Topic or Queue)
           Destination destination = session.createQueue(queueName);
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(router);
           
        } catch (JMSException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
        }
    }

    public JmsConnectFactory getFact() {
        return fact;
    }

    public void setFact(JmsConnectFactory fact) {
        this.fact = fact;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public MessageListener getRouter() {
        return router;
    }

    public void setRouter(MessageListener router) {
        this.router = router;
    }

    @Override
    public void stop() {
        try {
            if(consumer != null)
                consumer.close();
            if(session != null)
                session.close();
            if(connection != null)
                connection.close();
        } catch (JMSException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public void onException(JMSException ex) {
        Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
    }
    
    
}
