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

import java.io.FileNotFoundException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author ashish
 */
public class XmlParser  {

    public static void main(String args[]) throws XMLStreamException, FileNotFoundException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader =
                inputFactory.createXMLEventReader(new java.io.FileInputStream("in1.xml"));
        EvntPrn ep = new EvntPrn();
        while (reader.hasNext()) {
            XMLEvent e = reader.nextEvent();
                // insert your processing here
            int evtNo = e.getEventType();
            System.out.println("Event:[" + e + "] Type ["+ep.toString(evtNo)+"]");
            
        }


    }


}

class EvntPrn implements XMLStreamConstants {
    public EvntPrn(){}
    String toString(int evtNo) {
        String ret = "Unknown{"+evtNo+"}";
        switch(evtNo) {
            case ATTRIBUTE:
                ret = "ATTRIBUTE";
                break;
            case CDATA:
                ret = "CDATA";
                break;
            case CHARACTERS:
                ret = "CHARACTERS";
                break;
            case COMMENT:
                ret = "COMMENT";
                break;
            case DTD:
                ret = "DTD";
                break;
            case END_DOCUMENT:
                ret = "END_DOCUMENT";
                break;
            case END_ELEMENT:
                ret = "END_ELEMENT";
                break;
            case ENTITY_DECLARATION:
                ret = "ENTITY_DECLARATION";
                break;
            case ENTITY_REFERENCE:
                ret = "ENTITY_REFERENCE";
                break;
            case NAMESPACE:
                ret = "NAMESPACE";
                break;
            case NOTATION_DECLARATION:
                ret = "NOTATION_DECLARATION";
                break;
            case PROCESSING_INSTRUCTION:
                ret = "PROCESSING_INSTRUCTION";
                break;
            case SPACE:
                ret = "SPACE";
                break;
            case START_DOCUMENT:
                ret = "START_DOCUMENT";
                break;
            case START_ELEMENT:
                ret = "START_ELEMENT";
                break;
        }
        return ret;
    }
}
