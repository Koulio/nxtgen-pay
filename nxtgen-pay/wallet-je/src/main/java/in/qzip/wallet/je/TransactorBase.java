/*
* Copyright (c) 2016, BON BIZ IT Services Pvt LTD.
*
* The Universal Permissive License (UPL), Version 1.0
* 
* Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data (collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each licensor hereunder covering either (i) the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both
*
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
package in.qzip.wallet.je;

import in.qzip.wallet.Transactor;
import in.qzip.wallet.je.TxnManager;
import in.qzip.wallet.TxnReq;
import in.qzip.wallet.TxnRes;
import in.qzip.wallet.base.TxnReqBase;
import in.qzip.wallet.base.TxnResBase;
import in.qzip.wallet.exceptions.TxnException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *  *TODO:* Delete it
 * @author ashish
 */
public class TransactorBase implements Transactor{

    private TxnManager txnMgr;
    private String walletCurrency;

    public TransactorBase(TxnManager txnMgr, String currency) {
        this.txnMgr = txnMgr;
        walletCurrency = currency;
    }
    
    @Override
    public TxnRes transact(TxnReq req)  {
        txnMgr.beginTxn();
        
        try {
            validateTxnReq(req);
        } catch (TxnException ex) {
           txnMgr.getTxnRecorder().recordTxn(ex.toString().getBytes(), TxnException.URN);
           txnMgr.rollback();
           return new TxnResBase(req,false, ex.getMessage());
        }
        
        txnMgr.commit();
        return null;
    }

    @Override
    public TxnReq createTxnReq() {
        return new TxnReqBase();
    }

    
    private void validateTxnReq(TxnReq req) throws TxnException{
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    @Override
    public TxnReq createTxnReq(byte[] raw, String urn)  throws TxnException {
        return  TxnReqBase.createTxnReq(raw, urn);
    }

}
