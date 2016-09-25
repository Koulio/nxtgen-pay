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

package in.pymnt.utel.loader;

import in.pymnt.event.EventCentral;
import in.pymnt.util.Tuple;
import in.pymnt.utel.event.InsertEvent;
import in.pymnt.utel.test.NextUUID;
import in.pymnt.utel.test.NextValueGetter;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * NDS (Netscape Directory Server) output is custom text file a hybrid between LDIF and CSV. Object Class
 * Header is optional, followed by the column heading. However, this pair
 * pattern can appear in the middle of the file. Rest CSV rows. But some rows
 * may have elements of patterns "key=val\"
 * "[a-zA-Z0-9_\-]+[\s\x20]+\=[\s\x20]+[a-zA-Z0-9_\-]+[\s\x20]*\\$"
 *
 * @author ashish
 */
public class NdsExportParser implements Runnable {

    protected String objClassKeyName = "__SYS__OBJECT_CLASS";
    private String inputKeyColumn = null;
    private NextValueGetter keyAutoGen = new NextUUID();
    private String fileName = null;
    private String defaultObjectClass = "DefaultCache";
    private long snooze = 5000; // 5 seconds

    protected Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    //transient BufferedReader in = null;
    private Properties objClassKeyNames = null;

    public long getSnooze() {
        return snooze;
    }

    public void setSnooze(long snooze) {
        this.snooze = snooze;
    }
    public String getObjClassKeyName() {
        return objClassKeyName;
    }

    public void setObjClassKeyName(String objClassKeyName) {
        this.objClassKeyName = objClassKeyName;
    }

    public String getInputKeyColumn() {
        return inputKeyColumn;
    }

    public void setInputKeyColumn(String inputKeyColumn) {
        this.inputKeyColumn = inputKeyColumn;
    }

    public NextValueGetter getKeyAutoGen() {
        return keyAutoGen;
    }

    public void setKeyAutoGen(NextValueGetter keyAutoGen) {
        this.keyAutoGen = keyAutoGen;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDefaultObjectClass() {
        return defaultObjectClass;
    }

    public void setDefaultObjectClass(String defaultObjectClass) {
        this.defaultObjectClass = defaultObjectClass;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public Properties getObjClassKeyNames() {
        return objClassKeyNames;
    }

    public void setObjClassKeyNames(Properties objClassKeyNames) {
        this.objClassKeyNames = objClassKeyNames;
    }

    public void setLoggerName(String loggerName) {
        log = Logger.getLogger(loggerName);
    }

    public void setObjectClass2KeysMapFileName(String flname) {
        try {
            if (objClassKeyNames == null) {
                objClassKeyNames = new Properties();
            }
            objClassKeyNames.load(new FileInputStream(flname));
        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(snooze); // allows the listners and lifecycles to start the coherence cluster
        } catch (InterruptedException e) {
        }
        BufferedReader in = null;
        int lineNo = 0;
        int recsProcessed = 0;
        long startTm = System.currentTimeMillis();
        log.log(Level.INFO, "Start File {0}: Start Time {1} millis ", new Object[]{fileName,startTm});
        try {
            in = new BufferedReader(new FileReader(fileName));
            String ln;
            String objClass = defaultObjectClass;

            String[] headers = null;
            String curInputKeyColumn = inputKeyColumn;
            
            while ((ln = in.readLine()) != null) {
                ln = ln.trim();
                if(ln.length() == 0)
                    continue;
                String[] toks;
                lineNo++;
                if (ln.matches("Object[\\s\\x20]+Class[\\s\\x20]+\\=.+")) {
                    toks = ln.split("[ \\s]+");
                    if (toks.length == 4) {
                        objClass = toks[3];
                        if (objClassKeyNames != null) {
                            curInputKeyColumn = objClassKeyNames.getProperty(objClass);
                            if (curInputKeyColumn == null) {
                                curInputKeyColumn = inputKeyColumn;
                            }
                        }
                    } else {
                        log.log(Level.WARNING, "Line {0}: expected 4 tokens. got {2} [{1}]", new Object[]{lineNo, ln,toks.length});
                    }

                    headers = null;
                    continue;
                }
               
                toks = ln.split(",");
                if (headers == null) {
                    headers = toks;
                    continue;
                }
                // key = val\
                Properties prop = row2prop(headers, toks);
                prop.put(objClassKeyName, objClass);
                String key = keyAutoGen.getPropertyName();
                String val = keyAutoGen.getNext();
                // insert the key
                if (curInputKeyColumn != null) {
                    if (curInputKeyColumn.matches("[0-9]+")) {
                        int ndx = Integer.parseInt(curInputKeyColumn); // TODO: make it one time
                        if (ndx < toks.length) {
                            Tuple kv = extractKeyVal(headers, toks, ndx);
                            key = kv.getKey();
                            val = kv.getVal();
                        }
                    } else {
                        String v = prop.getProperty(curInputKeyColumn);
                        if (v != null) {
                            val = v;
                        }
                    }
                }

                prop.put(key, val);
                process(val, prop);
                recsProcessed++;
            }

        } catch (IOException ex) {
            log.log(Level.SEVERE, null, ex);
        } finally {
            try { if(in != null)in.close();} catch(Exception ex) {}
            cleanup();
            long endTm = System.currentTimeMillis();
            log.log(Level.INFO, "End File {0}: END Time {1} millis ", new Object[]{fileName,endTm});
            
            log.log(Level.INFO, "Summary of File {0}: Processing Time {1} millis , Records Processed {2}, lines {3}", new Object[]{fileName,(endTm-startTm),recsProcessed, lineNo});
        }
    } // run

    private Properties row2prop(String[] headers, String[] row) {
        Properties prop = new Properties();
        for (int i = 0; i < row.length; i++) {
            Tuple kv = extractKeyVal(headers, row, i);
            if(kv.getVal() != null && kv.getVal().length() > 0 )
               prop.put(kv.getKey(), kv.getVal());
        }
        return prop;
    }

    protected void process(String txnId, Properties prop) {
        InsertEvent evt = new InsertEvent();
        evt.setTableName(prop.getProperty(objClassKeyName));
        evt.setTxnVal(prop);
        evt.setTxnId(txnId);
        EventCentral.publish(evt);
        log.log(Level.FINEST, "Published Event [{0}]", prop);
    }
    protected void cleanup() {
        
    }
    protected Tuple extractKeyVal(String[] headers, String[] row, int col) {
        Tuple kv = new Tuple();
        if (col < row.length) {
            String colVal = row[col];
            if (colVal.matches("[a-zA-Z]+[a-zA-Z0-9_\\-]*=[a-zA-Z0-9_\\-]+[ \\s]*[a-zA-Z0-9_\\-]*[\\\\$]*")) {
                String[] sub = colVal.split("[\\=\\\\]+");
                kv.setKey(sub[0]);
                kv.setVal(sub[1]);
            } else {
                String k = (col >= headers.length) ? "" + col : headers[col];
                kv.setKey(k);
                kv.setVal(row[col]);
            }
        }
        return kv;
    }

}
