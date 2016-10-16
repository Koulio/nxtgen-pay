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

package in.innomon.utel.test;

import in.innomon.event.Event;
import in.innomon.event.EventCentral;
import in.innomon.event.EventListner;
import in.innomon.util.LifeCycle;
import in.innomon.util.PropTemplar;
import in.innomon.utel.event.InsertEvent;
import in.innomon.utel.jms.TxtMsgSender;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class GenJmsMsg implements EventListner , LifeCycle {

    TxtMsgSender sender = null;
    String tmplFile = null;
    String template = null;
    transient PropTemplar tmplr = new PropTemplar();
    private String loggerName = "JIO";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }
    
    @Override
    public boolean isEventOfInterest(Event evt) {
        return (evt instanceof InsertEvent);
    }

    @Override
    public void onEvent(Event evt) {
        if (evt instanceof InsertEvent) {
            if (template == null) {
                readFile();
            }
            Properties prop = ((InsertEvent) evt).getTxnVal();
            String msg;
            msg = tmplr.replace(template, prop);
            try {
                sender.send(msg);
            } catch (IOException ex) {
                Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            }

        }

    }

    private void readFile() {
        try {
            if (tmplFile == null) {
                template = "ERROR Template file name not specified";
                return;
            }
            try (RandomAccessFile raf = new RandomAccessFile(tmplFile, "r")) {
                byte[] arr = new byte[(int) raf.length()];
                raf.readFully(arr);
                template = new String(arr);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            template = ex.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
            template = ex.getMessage();
        }
    }

   public void start() {
        EventCentral.subscribe(this);
        //System.out.println("Subscrined"+this.getClass().getName());
    }

    public void stop() {
        EventCentral.unsubscribe(this);
    }

    public TxtMsgSender getSender() {
        return sender;
    }

    public void setSender(TxtMsgSender sender) {
        this.sender = sender;
    }

    public String getTmplFile() {
        return tmplFile;
    }

    public void setTmplFile(String tmplFile) {
        this.tmplFile = tmplFile;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

}
