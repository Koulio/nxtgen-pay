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
package in.pymnt.pay.test;


import in.pymnt.event.EventCentral;
import in.pymnt.pay.mem.MemTxnManager;
import in.pymnt.pay.mem.StartupLoader;
import in.pymnt.pay.txn.TxnFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class RandomLoader implements Runnable {
    private StartupLoader loader = new MemLoadRandom();
    private TxnFactory txnFact = new MemTxnManager() ;  //  MemTxnManager()  CohTxnFactory() JeTxnFactory()
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    
    public StartupLoader getLoader() {
        return loader;
    }

    public void setLoader(StartupLoader loader) {
        this.loader = loader;
    }
    public void setMaxEventThreads(int threads) {
        EventCentral.maxEventThreads = threads;
    }
    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public TxnFactory getTxnFact() {
        return txnFact;
    }

    public void setTxnFact(TxnFactory txnFact) {
        this.txnFact = txnFact;
    }
    
    
    public static void main(String[] args) {
        RandomLoader loader = new RandomLoader();
        loader.run();
    }

    @Override
    public void run() {
   //     CohTxnEventLogger elgr = new CohTxnEventLogger();
        try {
            
            txnFact.start();            
            loader.onStart(txnFact.createTxnManager(), txnFact.createAccountInfoManager());
            if(EventCentral.execServ != null)
                 EventCentral.execServ.awaitTermination(EventCentral.maxShutDownWaitSec+2, TimeUnit.SECONDS);
        } catch(Exception ex) {
            ex.printStackTrace();
            log.log(Level.SEVERE,ex.toString());
        } finally {
            txnFact.stop();
           // elgr.unsubscribe();
        }
    }
}
