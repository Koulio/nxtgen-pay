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

package in.pymnt.iso8583;

import in.pymnt.iso8583.parser.MessageTokenizer;

/**
 *
 * @author ashish
 */
public class GenXmlDef extends MessageTokenizer {

    public static java.io.PrintStream prn = System.out;

    public static void main(String[] args) {
        GenEmpty.gen();
    }

    public static void geDef() {
        header();
        body();
        footer();

    }

    public static void header() {
        prn.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        prn.println("<ab:ParseIso8583   xmlns:ab='http://xml.pymnt.in/parse/iso8583' >");
    }

    public static void body() {
        for (int i = 0; i < fieldInfoArr.length; i++) {
            if (fieldInfoArr[i] == null) {
                continue;
            }
            String fmt = fieldInfoArr[i].getFormat().name();
            String attr = fieldInfoArr[i].getAttribute().name();
            int len = fieldInfoArr[i].getLength();
            prn.println("    <ab:field name=\"" + fieldInfoArr[i].getPropertyName() + "\" offset=\"" + i + "\" format=\"" + fmt + "\" attr=\"" + attr + "\" len=\"" + len + "\" >");
        }

    }

    public static void footer() {
        prn.println("</ab:ParseIso8583>");
    }
}

class GenEmpty extends MessageTokenizer {

    public static java.io.PrintStream prn = System.out;

    public static void gen() {
        header();
        body();
        footer();
    }

    public static void header() {
        prn.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        prn.println("<ab:InIso8583   xmlns:ab='http://xml.pymnt.in/in/iso8583' >");
    }
    public static void body() {
        for (int i = 0; i < fieldInfoArr.length; i++) {
            if (fieldInfoArr[i] == null) {
                continue;
            }
            String fmt = fieldInfoArr[i].getFormat().name();
            String attr = fieldInfoArr[i].getAttribute().name();
            int len = fieldInfoArr[i].getLength();
            prn.println("    <ab:"+fieldInfoArr[i].getPropertyName() + "> </ab:" +fieldInfoArr[i].getPropertyName() + ">");
        }

    }
    public static void footer() {
        prn.println("</ab:InIso8583>");
    }
}
