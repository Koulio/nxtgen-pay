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
package in.qzip.pay.test;

import in.innomon.event.EventCentral;
import in.qzip.pay.mem.MemTxnManager;
import in.qzip.pay.txn.Balance;
import in.qzip.pay.txn.TxnFactory;
import in.qzip.pay.txn.TxnManager;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FIRST RUN THE RandomLoader, to populate the db once.
 * This program transacts on the populated db
 * @author ashish
 */
public class RandomLoadTest implements Runnable {

    private int recsInDb = 100;
    private String firstMobileNo = "9810000000";
    private int numThreads = 4;
    private long iterationPerThread = 1000;
    private int maxTxnAmount = 100;
    private ExecutorService execServ = null;
    private TxnFactory txnFact = new MemTxnManager();  //   JeTxnFactory(), ,  CohTxnFactory()
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void setMaxEventThreads(int threads) {
        EventCentral.maxEventThreads = threads;
    }

    public static void main(String[] args) {
        RandomLoadTest test = new RandomLoadTest();
        test.run();
    }

    @Override
    public void run() {
        recsInDb -= 1;
        if (execServ == null) {
            execServ = Executors.newFixedThreadPool(numThreads);
        }
        try {
            txnFact.start();
            ArrayList<LoadStrand> arr = new ArrayList<LoadStrand>();
            for(int i=0; i < numThreads; i++)
                arr.add(new LoadStrand(i));
            List<Future<Perf>> lst = execServ.invokeAll(arr);
            Iterator<Future<Perf>> iter = lst.iterator();
            long totalNano = 0;
            while(iter.hasNext()) {
                Perf prf = iter.next().get();
                totalNano += prf.deltaNanosec;
                
                log.log(Level.INFO, "Thread {0}: {1} nano sec, total txns {2}. failed {3} ", new Object[]{prf.ndx, prf.deltaNanosec, prf.totalTxn, prf.failedTxn});
            }
            log.log(Level.INFO, "Total Perf {0} nano sec, for {1} Threads {2}, iterations per thread {3}", new Object[]{totalNano, numThreads, iterationPerThread});
        } catch(Exception ex) {
            log.log(Level.SEVERE, ex.toString());
        } finally {
            txnFact.stop();
        }
    }
    
    class LoadStrand implements Callable<Perf> {
       // private Transactor txtor = new Transactor();
        private Perf perf = new Perf();
        private Random rand = new Random();
        private BigInteger bint = new BigInteger(firstMobileNo);
        
        LoadStrand(int ndx) {
            perf.ndx = ndx;
        }

        @Override
        public Perf call() throws Exception {
            
            for(int i=0; i < iterationPerThread; i++)
                transact();
            
            
            return perf;
        }
// TODO: should call Balance.deductAmount() and addAmount()
        private void transact() {
            perf.totalTxn++;
            TxnManager txnMgr = txnFact.createTxnManager();
            long start = System.nanoTime();;
            try {
                int dr = rand.nextInt(recsInDb);
                int cr = rand.nextInt(recsInDb);
                int txnamt = rand.nextInt(maxTxnAmount);
                
                String debit = bint.add(BigInteger.valueOf(dr)).toString();
                String credit = bint.add(BigInteger.valueOf(cr)).toString();
                
                start = System.nanoTime();  
                txnMgr.beginTxn();
                Balance drBal = txnMgr.getBalance(debit);
                Balance crBal = txnMgr.getBalance(credit);
                
                /*
                double drAmt = drBal.getBalance();
                double crAmt = crBal.getBalance();
                drBal.setBalance(drAmt - txnamt);
                crBal.setBalance(crAmt + txnamt);
                */
                String txnId = java.util.UUID.randomUUID().toString();
                drBal.deductAmount(txnamt, ""+txnId);
                crBal.addAmount(txnamt, ""+txnId);
                txnMgr.updateBalance(crBal);
                txnMgr.updateBalance(drBal);
                txnMgr.commit();
                
            } catch(Exception e) {
                perf.failedTxn++;
                perf.deltaNanosec = start - System.nanoTime();
            } finally {
                txnMgr.close();
            }
        }
        
    }
}

class Perf {
    public int ndx = 0;
    public long deltaNanosec = 0;
    public long   totalTxn = 0;
    public long   failedTxn = 0;
}