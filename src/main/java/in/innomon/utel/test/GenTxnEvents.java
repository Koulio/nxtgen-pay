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

import in.innomon.event.EventCentral;
import in.innomon.event.ExitEvent;
import in.innomon.utel.event.TxnEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class GenTxnEvents implements Runnable {
    private long iterations = 10000;
    private TxnEvent evtTmplate = null;
    private long snooze = 10000; // 10 seconds
    private boolean shutdown = true;
    
    transient ArrayList<NextValueGetter>  valSimArr = new ArrayList<NextValueGetter>();

    public void setSnooze(long snooze) {
        this.snooze = snooze;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    
    public long getIterations() {
        return iterations;
    }

    public void setIterations(long iterations) {
        this.iterations = iterations;
    }

    public TxnEvent getEvtTmplate() {
        return evtTmplate;
    }

    public void setEvtTmplate(TxnEvent evtTmplate) {
        this.evtTmplate = evtTmplate;
    }
    public void setNextValueGetter(NextValueGetter valGtr) {
        valSimArr.add(valGtr);
    }
    @Override
    public void run() {
         try {
                Thread.sleep(snooze);
            } catch (InterruptedException e) {
            }
        for(long i=0; i < iterations; i++) {
            try {
                genEvent();
            } catch (InstantiationException | IllegalAccessException ex) {
                Logger.getLogger("JIO").log(Level.SEVERE, null, ex);
            }
        }
        if(shutdown) {
                  try {
                Thread.sleep(snooze);
            } catch (InterruptedException e) {
            }
            EventCentral.publish(new ExitEvent());      
        }
    }
    private void genEvent() throws InstantiationException, IllegalAccessException {
        TxnEvent evt = evtTmplate.getClass().newInstance();
        evt.setTxnId(evtTmplate.getTxnId());
        evt.setTableName(evtTmplate.getTableName());
        Properties prop = new Properties(evtTmplate.getTxnVal());
        fillSim(prop);
        evt.setTxnVal(prop);
        EventCentral.publish(evt);
    }

    private void fillSim(Properties prop) {
        Iterator<NextValueGetter> iter = valSimArr.iterator();
        while(iter.hasNext()) {
            NextValueGetter valSim = iter.next();
            prop.setProperty(valSim.getPropertyName(), valSim.getNext());
        }
    }
}
