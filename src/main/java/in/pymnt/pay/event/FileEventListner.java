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
package in.pymnt.pay.event;

import in.pymnt.event.Event;
import in.pymnt.event.EventCentral;
import in.pymnt.event.EventListner;
import in.pymnt.util.LifeCycle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class FileEventListner implements EventListner, LifeCycle {

 
    
    private String actfDir = "/tmp";
    private String balfDir = "/tmp";
    private String txnfDir = "/tmp";
    
    private String actPfix = "ACT-";
    private String balPfix = "BAL-";
    private String txnPfix = "TXN-";
    
    private String ext = ".json"; 

    private String loggerName = "PAY";

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getActfDir() {
        return actfDir;
    }

    public void setActfDir(String actfDir) {
        this.actfDir = actfDir;
    }

    public String getBalfDir() {
        return balfDir;
    }

    public void setBalfDir(String balfDir) {
        this.balfDir = balfDir;
    }

    public String getTxnfDir() {
        return txnfDir;
    }

    public void setTxnfDir(String txnfDir) {
        this.txnfDir = txnfDir;
    }

    public String getActPfix() {
        return actPfix;
    }

    public void setActPfix(String actPfix) {
        this.actPfix = actPfix;
    }

    public String getBalPfix() {
        return balPfix;
    }

    public void setBalPfix(String balPfix) {
        this.balPfix = balPfix;
    }

    public String getTxnPfix() {
        return txnPfix;
    }

    public void setTxnPfix(String txnPfix) {
        this.txnPfix = txnPfix;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
  


 
       @Override
    public boolean isEventOfInterest(Event evt) {
       // Logger.getLogger(loggerName).log(Level.FINEST,"Event of Interst: enter [FileEventListner]");
         boolean ret = (evt instanceof AccountInfoUpdatedEvent) || (evt instanceof TxnOccuredEvent) || (evt instanceof BalanceUpdatedEvent);
         //Logger.getLogger(loggerName).log(Level.FINEST,"Event of Interst: [FileEventListner]: "+ret);
         //return ret;
         return ret;
    }
    public void onEvent(Event evt) {
        Logger.getLogger(loggerName).log(Level.FINEST,"On Event: enter [FileEventListner]");
        if (evt instanceof AccountInfoUpdatedEvent) {
            store(actfDir, actPfix, evt.toString()  );
        } else if (evt instanceof TxnOccuredEvent) {
            store(txnfDir, txnPfix, evt.toString()  );
        } else if (evt instanceof BalanceUpdatedEvent) {
            store(balfDir, balPfix, evt.toString()  );
        } else 
            Logger.getLogger(loggerName).log(Level.INFO,"[FileEventListner]On Event: None Match");
    }


    protected void store(String dir, String pfix, String json) {
        try {
            File fl = File.createTempFile(dir, pfix);
            FileOutputStream fos = new FileOutputStream(fl,true);
            fos.write(json.getBytes());
            fos.close();
            File dest = new File(dir, pfix+System.nanoTime()+ext);
            fl.renameTo(dest);
        } catch (IOException ex) {
            Logger.getLogger(loggerName).log(Level.SEVERE, null, ex);
        }
        
    }


    @Override
    public void start() {
        EventCentral.subscribe(this);
        Logger.getLogger(loggerName).log(Level.FINEST,"Subscribed"+this.getClass().getName());
    }

    @Override
    public void stop() {
        EventCentral.unsubscribe(this);
    }

}
