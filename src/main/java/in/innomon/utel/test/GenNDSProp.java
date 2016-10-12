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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
/**
 *
 * @author ashish
 */

public class GenNDSProp  {

    
    public static void main(String[] args) {
        BufferedReader br = null;
        try {
            String flname = "nds-small.txt";
            if(args.length > 1) {
                flname = args[1];
            }   
            System.out.println(flname);
            br = new BufferedReader(new InputStreamReader(new FileInputStream(flname)));
            Properties p = load(br);
            p.storeToXML(System.out, ("File: ["+flname+"] <property>,<type>"));
            System.out.println("; ****************");
            Gson gson = new Gson();
            System.out.println(gson.toJson(p)); 
        } catch (IOException ex) {
            Logger.getLogger(GenNDSProp.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if(br != null)
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(GenNDSProp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public static Properties load(BufferedReader rd) throws IOException {
        Properties p = new Properties();
        String ln;
        String strClass = String.class.getCanonicalName();
        while( (ln = rd.readLine()) != null) {
            ln = ln.trim();
            if(ln.startsWith(";") || ln == "")
                continue;
            if( !p.containsKey(ln))
                p.put(ln, strClass);
        }
        
        return p;
        
    }
}
