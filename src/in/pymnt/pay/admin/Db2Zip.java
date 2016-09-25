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

package in.pymnt.pay.admin;

import java.io.File;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
BigInteger bint = new BigInteger("9826543210");
        for (int i = 0; i < ITER; i++, bint = bint.add(BigInteger.ONE)) {
 */
/**
 *
 * @author ashish
 */
public class Db2Zip implements Runnable {

    private Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private String zipFileName = "pay.zip";
    private String prefix = "pay";
    private String nameSpace = "http://xml.pymnt.in/pay/v01";
    private String balRootElement = "Balances";
    private String txnRootElement = "Transactions";
    private String actInfoRootElement = "AccountInfo";

    public String getActInfoRootElement() {
        return actInfoRootElement;
    }

    public void setActInfoRootElement(String actInfoRootElement) {
        this.actInfoRootElement = actInfoRootElement;
    }
    
    public String getBalRootElement() {
        return balRootElement;
    }

    public void setBalRootElement(String balRootElement) {
        this.balRootElement = balRootElement;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTxnRootElement() {
        return txnRootElement;
    }

    public void setTxnRootElement(String txnRootElement) {
        this.txnRootElement = txnRootElement;
    }

    public String getZipFileName() {
        return zipFileName;
    }

    public void setZipFileName(String zipFileName) {
        this.zipFileName = zipFileName;
    }

    @Override
    public void run() {
        try {
            File zfl = new File(zipFileName);
            if(zfl.exists()) {
              String msg = "File ["+zipFileName+"] already exits";
              System.out.println(msg);
              log.log(Level.SEVERE,msg);
            }
            else {
                
            }
        } catch(Exception ex) {
            log.log(Level.SEVERE, ex.toString());
        }
    }
}

class Balance2Xml {

    private String ns, pfix, rootElement;
    private PrintStream out;

    public Balance2Xml(String ns, String pfix, String rootElement, PrintStream out) {
        this.ns = ns;
        this.pfix = pfix;
        this.rootElement = rootElement;
        this.out = out;
    }

 
}
