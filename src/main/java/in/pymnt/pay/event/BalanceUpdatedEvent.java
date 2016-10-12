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

import com.google.gson.Gson;
import in.pymnt.Globals;
import in.pymnt.event.Event;
import in.pymnt.pay.txn.Balance;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author ashish
 */
public class BalanceUpdatedEvent implements Event{
    private Balance accountBalance;
    private String mmid = Globals.prop.getProperty(Globals.MMID_PROPNAME, "UNDEF");
    
    private LocalDateTime txnRecordTime = LocalDateTime.now();   
    transient private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH::mm:ss.SSS");
    transient private  Gson gson = new Gson();

    public BalanceUpdatedEvent(Balance account) {
        this.accountBalance = account;
    }
    
    @Override
    public Object getEventObject() {
        return accountBalance;
    }
    public Balance getAccountBalance() {
        return accountBalance;
    }
    public String toString() {
        return gson.toJson(this);
    }
        public String getTxnRecordTimestamp() {
        return txnRecordTime.format(formatter);
    } 
}
