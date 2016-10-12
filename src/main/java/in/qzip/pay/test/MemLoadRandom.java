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
import in.innomon.event.ExitEvent;
import in.qzip.pay.mem.StartupLoader;
import in.qzip.pay.txn.TxnException;
import in.qzip.pay.txn.TxnManager;
import java.io.FileReader;
import in.qzip.pay.txn.AccountInfo;
import in.qzip.pay.txn.AccountInfoManager;
import in.qzip.pay.txn.Transactor;
import in.qzip.pay.txn.Balance;
import in.qzip.pay.txn.TxnPayload;
import in.qzip.pay.txn.TxnReq;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ashish
 */
public class MemLoadRandom implements StartupLoader {

    private String fnameFileName = "hindu-fnames.txt";
    private String lnameFileName = "hindu-lnames.txt";
    private long recordsToCreate = 100;
    private String firstMobileNo = "9810000000";
    private String mailDomain = "test.com";
    private double initialAmount = 200;
    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private ArrayList<String> fNameArr = new ArrayList<String>();
    private ArrayList<String> lNameArr = new ArrayList<String>();
    private Random rnd = new Random();
    private boolean sendShutDownEvent = true;

    public boolean isSendShutDownEvent() {
        return sendShutDownEvent;
    }

    public void setSendShutDownEvent(boolean sendShutDownEvent) {
        this.sendShutDownEvent = sendShutDownEvent;
    }
    
    
    public String getFirstMobileNo() {
        return firstMobileNo;
    }

    public void setFirstMobileNo(String firstMobileNo) {
        this.firstMobileNo = firstMobileNo;
    }


    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getMailDomain() {
        return mailDomain;
    }

    public void setMailDomain(String mailDomain) {
        this.mailDomain = mailDomain;
    }

    public long getRecordsToCreate() {
        return recordsToCreate;
    }

    public void setRecordsToCreate(long recordsToCreate) {
        this.recordsToCreate = recordsToCreate;
    }

    public String getFnameFileName() {
        return fnameFileName;
    }

    public void setFnameFileName(String fnameFileName) {
        this.fnameFileName = fnameFileName;
    }

    public String getLnameFileName() {
        return lnameFileName;
    }

    public void setLnameFileName(String lnameFileName) {
        this.lnameFileName = lnameFileName;
    }

 
    private void file2Array(String fname, ArrayList lst) throws IOException {
        File fnameFl = new File(fname);
        if (!fnameFl.isFile()) {
            throw new IOException("file [" + fname + "] does not exists");
        }
        BufferedReader rdr = new BufferedReader(new FileReader(fnameFl));
        String ln;
        while ((ln = rdr.readLine()) != null) {
            lst.add(ln.trim());
        }
        rdr.close();
    }
    
    public String genRandomName() {
        StringBuilder buf = new StringBuilder();
        buf.append(fNameArr.get(rnd.nextInt(fNameArr.size())));
        buf.append(" ");
        buf.append(lNameArr.get(rnd.nextInt(lNameArr.size())));
        
        return buf.toString();
    }
   // TODO: Check Why Balance after txn not updated ?
    @Override
    public void onStart(TxnManager txnMgr, AccountInfoManager actMgr) {
      try {
            file2Array(fnameFileName, fNameArr);
            file2Array(lnameFileName, lNameArr);
            long start = System.nanoTime();
            long delta;
            // Load Random Accounts
            AccountInfoIterator actIter = new AccountInfoIterator();
            while(actIter.hasNext()) {
                actMgr.updateAccountInfo(actIter.next());
            }
            actMgr.close();
            delta = System.nanoTime() - start;
            log.log(Level.INFO, "PERF: Loaded {0} AccountInfo records in {1} nano Sec", new Object[]{recordsToCreate, delta});
            // Load Balances sequentially
            Transactor txtor = new Transactor();
            String floaterAccountName = txnMgr.getFloaterAccountName();
            Balance bal = txnMgr.createBalance(floaterAccountName);
            txnMgr.beginTxn();
            txnMgr.updateBalance(bal); // FLOAT is Zero but will end up -ve after all txn
            
            TxnInterceptor txnIntercept = new TxnInterceptor(txnMgr);
            
            BigInteger bint = new BigInteger(firstMobileNo);
            start =  System.nanoTime();
            for(long cur=0; cur < recordsToCreate; cur++, bint = bint.add(BigInteger.ONE)) {
                String curAct = bint.toString();
                bal = txnMgr.createBalance(curAct);
                
                txnMgr.updateBalance(bal);
                
                
                TxnPayload payload = new TxnPayload();
                TxnReq req = payload.getReq();
                req.setTxnAmount(initialAmount);
                req.setAddToAccount(curAct);
                req.setDeductFromAccount(floaterAccountName);
                req.setTxnRefID(java.util.UUID.randomUUID().toString());
                
                txtor.transact(txnIntercept, payload);
                
            }// for
            txnMgr.commit();
            delta = System.nanoTime() - start;
            log.log(Level.INFO, "PERF: Loaded {0} Balances & Transacted {0} records in {1} nano Sec", new Object[]{recordsToCreate, delta});
            //System.out.format("PERF: Loaded {0} Balances & Transacted {0} records in {1} nano Sec", new Object[]{recordsToCreate, delta});
            
        } catch (Exception ex) {
            ex.printStackTrace();
            log.log(Level.SEVERE, ex.toString());
        } finally {
          if(sendShutDownEvent) {
             EventCentral.publish(new ExitEvent());
          }
      }
    }
    class AccountInfoIterator implements Iterator<AccountInfo>{
        private long cur = 0;
        private BigInteger bint = new BigInteger(firstMobileNo);
        
        @Override
        public boolean hasNext() {
            return (cur < recordsToCreate);
        }

        @Override
        public AccountInfo next() {
            bint = bint.add(BigInteger.ONE);
            cur += 1;
            AccountInfo ret = new AccountInfo();
            ret.setAccountName(bint.toString());
            ret.setEmail(bint.toString()+"@"+mailDomain);
            ret.setPersonName(genRandomName());
          
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }

    
}

class TxnInterceptor implements TxnManager {
    private TxnManager txnOrig;
    public TxnInterceptor(TxnManager txnOrig) {
        this.txnOrig = txnOrig;
    }
    @Override
    public void beginTxn() {
        // dummy
    }

    @Override
    public void commit() {
       //dummy
    }

    @Override
    public void rollback() {
        //dummy
    }

    @Override
    public Balance getBalance(String key) throws TxnException {
       return txnOrig.getBalance(key);
    }

    @Override
    public Balance createBalance(String accountName) throws TxnException {
        return txnOrig.createBalance(accountName);
    }

    @Override
    public void updateBalance(Balance account) throws TxnException {
         txnOrig.updateBalance(account);
    }

    @Override
    public void recordTxn(TxnPayload payload) throws TxnException {
         txnOrig.recordTxn(payload);
    }

    @Override
    public boolean isFloaterAccount(String account) {
       return txnOrig.isFloaterAccount(account);
    }

    @Override
    public String getFloaterAccountName() {
       return txnOrig.getFloaterAccountName();
    }

    @Override
    public void close() {
        // dummy
    }
    
}