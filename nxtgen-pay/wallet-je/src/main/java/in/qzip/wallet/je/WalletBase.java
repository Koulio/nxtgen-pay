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

import in.qzip.wallet.je.Wallet;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *  *TODO:* Delete it
 * @author ashish
 */
public class WalletBase implements Wallet {

    private static final long serialVersionUID = 1L;
    private String accountId;
    private String currency;
    private BigDecimal balance;
    private Timestamp lastTnxTimestamp;
    private String lastTxnId;
    private boolean active;

    public WalletBase() {
    }

    public WalletBase(String accountId, String currency) {
        this.accountId = accountId;
        this.currency = currency;
    }

    @Override
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    @Override
    public Timestamp getLastTnxTimestamp() {
        return lastTnxTimestamp;
    }

    @Override
    public void setLastTnxTimestamp(Timestamp lastTnxTimestamp) {
        this.lastTnxTimestamp = lastTnxTimestamp;
    }

    @Override
    public String getLastTxnId() {
        return lastTxnId;
    }

    @Override
    public void setLastTxnId(String lastTxnId) {
        this.lastTxnId = lastTxnId;
    }

    @Override
    public String getAccountId() {
        return accountId;
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountId != null ? accountId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the accountId fields are not set
        if (!(object instanceof WalletBase)) {
            return false;
        }
        WalletBase other = (WalletBase) object;
        return !((this.accountId == null && other.accountId != null) || (this.accountId != null && !this.accountId.equals(other.accountId)));
    }

    @Override
    public String toString() {
        return "in.qzip.ledger.Balance[ id=" + accountId + "; balance =" + balance + "; ]";
    }

}
