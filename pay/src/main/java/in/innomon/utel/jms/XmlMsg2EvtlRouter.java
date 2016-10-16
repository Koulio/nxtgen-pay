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

package in.innomon.utel.jms;

import in.innomon.event.EventCentral;
import in.innomon.utel.event.InsertEvent;
import in.innomon.utel.event.TxnEvent;
import in.innomon.xml.XmlToProp;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author ashish
 */
public class XmlMsg2EvtlRouter extends XmlToProp implements MessageListener {
    private String txnKeyProName = TxnEvent.TXN_ID_TAG;
    private String tableName = "";

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    public String getTxnKeyProName() {
        return txnKeyProName;
    }

    public void setTxnKeyProName(String txnKeyProName) {
        this.txnKeyProName = txnKeyProName;
    }
    
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            try {
                TextMessage textMessage = (TextMessage) message;
                String text = textMessage.getText();
                Properties prop = parse(text.getBytes());
                InsertEvent evt = new InsertEvent();
                evt.setTableName(tableName);
                String txnid = prop.getProperty(txnKeyProName);
                if(txnid == null) {
                    txnid = ""+ System.nanoTime();
                    prop.setProperty(txnKeyProName, txnid);
                }    
                evt.setTxnVal(prop);
                EventCentral.publish(evt);
                
            } catch (JMSException | XMLStreamException ex) {
                Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(loggerName).log(Level.INFO, "XmMsglRouter Received: {0}", message);
        }
    }

}
