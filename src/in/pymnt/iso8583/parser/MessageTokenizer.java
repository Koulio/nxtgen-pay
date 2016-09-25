
/*
 * MessageParser.java
 * 
 * Created on Oct 1, 2003, 11:49:43 AM
 * 
 */
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


package in.pymnt.iso8583.parser;



/**
 *
 * @author ASHISH BANERJEE, Modified 17-Feb-11 (changed the names to camel case)
 */
/*
 * MessageTokenizer converts an ISO8583 bin message to an string array (bacically unpacks it) and viceversa.
 * 
 */
public class MessageTokenizer {
    public static final int PRIMARY_BITMAP_START = 4;
// bitmaps section ISO 8583: 1993 section 4.2 and 4.4.3 
// states that binary data the least bit is at left most postion 
// this 1000 000 (if the first bit is only on) corresponds to 0x80 
// So what is refered in the spec as 1st bit is actually 0x80
// so the next bit will be 0x80 >> 1 and so on 
    public static final int FIRST_BIT = 0x80;

    private  FieldInfo fldArr[] = null;
    private  ArrayToMessage arr2Msg = null;
    public MessageTokenizer() {
    }
    public void registerFieldInfo(FieldInfo finfo) {
        if(fldArr == null) {
            fldArr = fieldInfoArr.clone();
        }
        fldArr[finfo.getBitMapIndex()% fldArr.length] = finfo;
        
    }

    public String[] MessageToStringArray(byte[] msg) throws IllegalArgumentException {
        FieldInfo arr[] = fldArr;
        if(arr == null)
            arr = fieldInfoArr;
        //  based on 1st bit (ndx=0) of bitmap we  decide whether to alloc 64 or 128 array
       String ret[] = new String[(((msg[PRIMARY_BITMAP_START] & FIRST_BIT) == FIRST_BIT)?64:128)]; 
       int curOffset = 0;
        ISOBitMapIterator iter = new ISOBitMapIterator( msg);
        ret[0] = new String(msg, 0,4);
        curOffset = 4;   
        int len = ((msg[PRIMARY_BITMAP_START] & FIRST_BIT) == FIRST_BIT)? 16:8;

        ret[1] = new String(msg,PRIMARY_BITMAP_START,len);
        curOffset += len;
        FieldExtrator extr = new FieldExtrator(msg, curOffset);
        int ndx ; // 0 = MTI, 1 = bit-map
        // iterator skips the first bit (secondary bitmap indecator)
        for(ndx=2;iter.hasNext();ndx++) {
            if(iter.next()) {
                FieldInfo finfo = arr[ndx];
                if(finfo == null)
                    throw new IllegalArgumentException("E001: field def for"+ndx+" not found message["+msg+"]");
                ret[ndx] = extr.messageToString(finfo);    
            }
        } // while

        return ret;
    }
    /**
    *   index 0 = MTI, index 1 = bit array (this is ignored), if no element is present set the 
    *       element at the index to null 
    * @param arr array of Object String (ALPHA, ALNUM), Date, byte[] for binary, Number for num  
     * @return byte array containing ISO8583 message
     * @throws java.lang.IllegalArgumentException 
     */
    public byte[] arrayToMessage(Object arr[])  throws IllegalArgumentException {
        if(fldArr == null)
            arr = fieldInfoArr;
        
        if(arr2Msg == null)
            arr2Msg =  new ArrayToMessage(fldArr);
        return arr2Msg.parse(arr);
    } 


    public static void registerGlobalFieldInfo(FieldInfo finfo) {
        fieldInfoArr[finfo.getBitMapIndex()% fieldInfoArr.length] = finfo;
    }
    // FieldDef will have start Index
    public static void registerGlobalFieldInfoRange(FieldInfo finfo, int endIndex) {
        for(int i= finfo.getBitMapIndex(); i <= endIndex; i++) {
            FieldInfo fndx = new FieldDef(finfo.getFormat(), finfo.getAttribute(), finfo.getLength(),
                    (finfo.getPropertyName() + "_"+i), i);
            fieldInfoArr[finfo.getBitMapIndex()% fieldInfoArr.length] = fndx;
        }
    }
    protected static FieldInfo fieldInfoArr[] = new FieldInfo[128];
 static {
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,4, "MTI", 0));
   // ndx 1 bitmap handled specially
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,19, "primaryAccountNumber", 2));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,12, "processingCode", 3));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,12, "transactionAmount", 4));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,12, "reconciliationAmount", 5));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,12, "cardHolderBillingAmount", 6));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.MMDDhhmmss, FieldInfo.Attribute.NUM,10, "transmissionDateTime", 7));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,8, "cardholderBillingFeeAmount", 8));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,8, "conversionRateReconciliation", 9));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,8, "conversionRateCradholerBilling", 10));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,6, "systemTraceAuditNumber", 11));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMMDDhhmmss, FieldInfo.Attribute.NUM,12, "dateTimeLocalTransaction", 12));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMM, FieldInfo.Attribute.NUM,4, "dateEffective", 13));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMM, FieldInfo.Attribute.NUM,4, "dateExpiration", 14));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMM, FieldInfo.Attribute.NUM,4, "dateSettlement", 15));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMM, FieldInfo.Attribute.NUM,4, "dateConversion", 16));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMM, FieldInfo.Attribute.NUM,4, "dateCapture", 17));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,4, "merchantType", 18)); //TODO: what the index values need validation?
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "countryCode", 19)); //TODO: validate countrycode
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "countryCodePrimaryAccountNumber", 20));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "countryCodeForwardingInstitution", 21));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUM,12, "pointOfServiceDataCode", 22));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "cardSequenceNumber", 23));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "functionCode", 24));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,4, "messageReasonCode", 25));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,4, "cardAcceptorBusinessCode", 26));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,1, "approvalCodeLength", 27));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMMDD, FieldInfo.Attribute.NUM,6, "dateReconciliation", 28));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "reconciliationIndicator", 29));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,24, "accountsOriginal", 30));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,99, "acquirerReferenceData", 31));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,11, "acquirerInstitutionIdentificationCode", 32));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,11, "forwardingInstitutionIdentificationCode", 33));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,28, "primaryAccountNumberExended", 34));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.BIN,20, "track2Data", 35));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.BIN,104, "track3Data", 36));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMPAD,12, "retrievalReferenceNumber", 37));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMPAD,6, "approvalCode", 38));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "actionCode", 39));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "serviceCode", 40));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMSPECIAL,8, "cardAcceptorTerminalIdentification", 41));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMSPECIAL,15, "cardAcceptorIdentificationCode", 42));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,99, "cardAcceptorNameLocation", 43));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,99, "additionalResponseData", 44));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,76, "track1Data", 45));
registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,204, "amountsFees", 46));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999, "nationalAdditionalData", 47));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999, "privateAdditionalData", 48));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALPHA_OR_NUM,3, "transactionCurrencyCode", 49));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALPHA_OR_NUM,3, "reconciliationCurrencyCode", 50));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALPHA_OR_NUM,3, "cardHolderBillinCurrencyCode", 51));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.BIN,8, "pinData", 52));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.BIN,48, "securityRelatedControlInformation", 53));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,120, "additionalAmounts", 54));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.BIN,255, "integratedCircuitCardSysRelatedData", 55));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,35, "originalDataElements", 56));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.NUM,3, "authLifecycleCode", 57));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,11, "authAgentInstitutionIdentificationCode", 58));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999, "transportData", 59));
// 60...63 reserved
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.BIN,8, "messageAuthCodeField", 64));
// 65 reserved
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,204, "originalFeesAmounts", 66));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,2, "extendedPaymentData", 67));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "receivingInstitutionCountryCode", 68));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "settlementInstitutionCountryCode", 69));
 registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "authAgentInstitutionCountryCode", 70));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,8, "messageNumber", 71));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999, "dataRecord", 72));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.YYMMDD, FieldInfo.Attribute.NUM,6, "actionDate", 73));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "creditsNumber", 74));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "creditsReversalNumber", 75));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "debitsNumber", 76));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "debitsReversalNumber", 77));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "transferNumber", 78));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "transferReversalNumber", 79));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "inquiriesNumber", 80));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "authsNumber", 81));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "inquiriesReversalNumber", 82));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "paymentsNumber", 83));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "paymentsReversalNumber", 84));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "feesCollectionNumber", 85));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,16, "creditsAmount", 86));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,16, "creditsReversalAmount", 87));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,16, "debitsAmount", 88));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,16, "debitsReversalAmount", 89));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "authorizationsReversalNumber", 90));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "transactionDestinationInstitutionCountryCode", 91));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,3, "transactionOriginatorInstitutionCountryCode", 92));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,11, "transactionOriginatorInstitutionIdentificationCode", 93));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,11, "transactionDestinationInstitutionIdentificationCode", 94));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,99, "cardIssuerReferenceData", 95));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.BIN,999, "KeyManagementData", 96));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.SIGN_NUM,17, "amountReconciliation", 97));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMSPECIAL,25, "payee", 98));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUM,11, "settlementInstitutionIdentificationCode", 99));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.NUM,11, "receivingInstitutionIdentificationCode", 100));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,17, "fileName", 101));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALPHA,28, "accountIdentification1", 102));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALPHA,28, "accountIdentification2", 103));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALPHA,100, "transactionDescription", 104));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,16, "creditsChargebackAmount", 105));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,16, "debitsChargebackAmount", 106));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "creditsChargebackNumber", 107));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.NUM,10, "debitsChargebackNumber", 108));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.LLVAR, FieldInfo.Attribute.ALNUMSPECIAL,84, "creditsFeeAmounts", 109));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMSPECIAL,84, "debitsFeeAmounts", 109));
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.ALNUMSPECIAL,84, "messageAuthCode", 110));
// 111-115 reserved for ISO use LLLVAR ans...999
   registerGlobalFieldInfoRange(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999,"reservedForISOUse",111),115);
// 116-122 reserved for national use LLLVAR ans...999
   registerGlobalFieldInfoRange(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999,"reservedForNationalUse",116),122);
// 123-127 reserved for private use LLLVAR ans...999
   registerGlobalFieldInfoRange(new FieldDef(FieldInfo.Format.LLLVAR, FieldInfo.Attribute.ALNUMSPECIAL,999,"reservedForPrivateUse",123),127);
   registerGlobalFieldInfo(new FieldDef(FieldInfo.Format.FIXED, FieldInfo.Attribute.BIN,8, "messageAuthCodeField", 128));
   
 }
}

class FieldExtrator {
    byte[] msg; int curOff;
    FieldExtrator(byte[] msg, int curOff)  {
        this.msg = msg;
        this.curOff = curOff;
    }
    public int getCurOffset() {
        return curOff;
    }
    public String messageToString( FieldInfo finfo) throws IllegalArgumentException{
        String ret = null;
// switch/case is really not OO design, delegating to field info was a better option. But we 
// chose this for performance, as finer grain means more overheads.
        switch(finfo.getFormat()) {
            case LLVAR:
                ret = getVarLen(finfo,2);
            break;    
            case LLLVAR:
                ret = getVarLen(finfo,3);
            break;    
            case FIXED:
            case DDMMYY:
            case MMDDhhmmss:
            case YYMMDDhhmmss:
                ret = getFixedLen(finfo);
            break;    
        }
        return ret;
    }
    private String getVarLen(FieldInfo finfo, int pfix) throws IllegalArgumentException{
        String slen = new String(msg, curOff, pfix);
        int len = Short.parseShort(slen);
        if(len < 0 || len > finfo.getLength() || ((len+curOff) >= msg.length))
             throw new IllegalArgumentException("E002: Field len error in message["+msg+"], offset ="+curOff+" got len="+len);
        curOff += pfix;
        String ret = new String(msg, curOff,len);
        curOff += len;

        return ret;
    }
 
    private String getFixedLen(FieldInfo finfo) throws IllegalArgumentException{
        int len = finfo.getLength();
          if(len < 0 || ((len+curOff) >= msg.length))
             throw new IllegalArgumentException("E003: Field len error in message["+msg+"], offset ="+curOff+" got len="+len);
 
        String ret = new String(msg, curOff,len);
        curOff += len;

        return ret;
            
    }
}

class ISOBitMapIterator {
    byte[] prim = new byte[8];
    byte[] sec  = null;
    int curByteNdx = 0;
    int curBitNdx = 1; // left most bit indicates if secondry bitmap is present, so we skip it
    public static final int PRIMARY_START = 4;
    public static final int SECONDARY_START = 12; // 4+8
// bitmaps section ISO 8583: 1993 section 4.2 and 4.4.3 
// states that binary data the least bit is at left most postion 
// this 1000 000 (if the first bit is only on) corresponds to 0x80 
// So what is refered in the spec as 1st bit is actually 0x80
// so the next bit will be 0x80 >> 1 and so on 
    public static final int FIRST_BIT = 0x80;

    ISOBitMapIterator(byte[] msg) {
        if((msg[PRIMARY_START] & FIRST_BIT) == FIRST_BIT) {
            sec = new byte[8];
            System.arraycopy(msg, SECONDARY_START, sec, 0, 8);
        }
        System.arraycopy(msg, PRIMARY_START, prim, 0, 8);

    }    
    public boolean hasNext() {
         return (!((sec == null)? (curByteNdx >= 8) : (curByteNdx >= 16)));
    }
    public boolean next() {
        if(curBitNdx >= 8) {
            curBitNdx = 0;
            curByteNdx ++;
        }
        boolean ret = false;
        int mask = FIRST_BIT >> curBitNdx;;
        if(curByteNdx < 8) {
            ret = ((prim[curByteNdx] & mask) == mask);
            if((++curBitNdx) >= 8) {
                curBitNdx = 0;
                curByteNdx++;
            }
            
        }else if ((sec != null) && (curByteNdx < 16)) {
             ret = ((sec[curByteNdx%8] & mask) == mask);
            if((++curBitNdx) >= 16) {
                curBitNdx = 0;
                curByteNdx++;
            }
              
        }
        return ret;
    }
    public void reset() {
        curByteNdx =0; curBitNdx = 1;
    }
}



class ArrayToMessage {
    private  FieldInfo fldArr[] = null; 
    int curOff = 0;
    StringBuilder sb = new StringBuilder();
    byte[] bitmap = null;
    
    public static final int FIRST_BIT = 0x80;

    ArrayToMessage(FieldInfo fldArr[]){
        this.fldArr = fldArr;
    }
    public byte[] parse(Object oar[]) throws IllegalArgumentException {
        int i,cnt=0;
        // pass 1
        for(i=2; i < oar.length; i++) 
            if(oar[i] != null)
                cnt++;
        //
        if(cnt > 64)
            bitmap = new byte[16];
        else
            bitmap = new byte[8];
        int ndx = 2;
        for(i=0; i < bitmap.length; i++) {
            for(int j=0; j < 8 ; j++,ndx++) {
                int mask =  FIRST_BIT >> j;   
                if((ndx >= oar.length )|| (oar[ndx] == null))
                    bitmap[i] &= (~mask); // reset bit
                else {
                     bitmap[i] |= mask; // set the bit
                     if(ndx >= fldArr.length) 
                        throw new IllegalArgumentException("E003: FieldInfo index exceeded ndx="+ndx);
                    FieldInfo finfo = fldArr[ndx];
                    if(finfo == null)
                        throw new IllegalArgumentException("E004: FieldInfo null at ndx="+ndx);
                    parseField(oar[ndx], finfo);    
                } //else    
            }// for j
        } // for i
        byte body[] = sb.toString().getBytes();
        int msgLen = 4 + bitmap.length + body.length;
        byte msg[] = new byte[msgLen];
        byte mti[];
        if(oar[0] instanceof byte[]) {
            mti = (byte[])oar[0];
        }
        else {
            mti = oar[0].toString().getBytes();
        }
        if(mti.length != 4)
             throw new IllegalArgumentException("E014: MTI lenght should be 4 but got"+mti.length);
        System.arraycopy(mti, 0, msg, 0, 4);   
        System.arraycopy(bitmap, 0, msg, 4, bitmap.length);
        System.arraycopy(body, 0, msg, 4+ bitmap.length, body.length);

        return msg;
    }
    private void parseField(Object obj, FieldInfo finfo) {
        String ele =obj.toString();
        String pad = "                ";
        String zero = "000000000000000";

        // TODO: Validate the strings for ALPHA, ALNUM, NUM, Date etc.
        int len = ele.length();
        String slen;
        switch(finfo.getFormat()) {
            case FIXED:
                if(len > finfo.getLength() )
                    throw new IllegalArgumentException("E005: field len ="+len+" expected ="+ finfo.getLength() + " at ["+finfo.getPropertyName());
                if((finfo.getAttribute() == FieldInfo.Attribute.NUM) && len < finfo.getLength() )
                        sb.append(zero.substring(0, finfo.getLength() - len));
                 sb.append(ele);
                if((finfo.getAttribute() != FieldInfo.Attribute.NUM) && len < finfo.getLength() )
                        sb.append(pad.substring(0, finfo.getLength() - len));
 
                break;    
            case LLLVAR:
                if(len > finfo.getLength() || len > 999)
                    throw new IllegalArgumentException("E006: field len ="+len+" expected ="+ finfo.getLength() + " at ["+finfo.getPropertyName());
                slen = String.valueOf(len);
                if(slen.length() == 1)
                    sb.append("00");
                if(slen.length() == 2)
                    sb.append("0");
                sb.append(slen);
                sb.append(ele);
            break;
            case LLVAR:
                if(len > finfo.getLength() || len > 99)
                    throw new IllegalArgumentException("E007: field len ="+len+" expected ="+ finfo.getLength() + " at ["+finfo.getPropertyName());
                slen = String.valueOf(len);
                if(slen.length() == 1)
                    sb.append("0");
                sb.append(slen);
                sb.append(ele);
            break;
            case YYMMDDhhmmss:
                if(obj instanceof java.util.Date) {
                    java.util.Date dt = (java.util.Date)obj;
                    java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyMMddHHmmss");
                    ele = fmt.format(dt);
                }
            break;
            case MMDDhhmmss:
                if(obj instanceof java.util.Date) {
                    java.util.Date dt = (java.util.Date)obj;
                    java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("MMddHHmmss");
                    ele = fmt.format(dt);
                }
            break;
            case DDMMYY:
                if(obj instanceof java.util.Date) {
                    java.util.Date dt = (java.util.Date)obj;
                    java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyMMdd");
                    ele = fmt.format(dt);
                }
             break;   
         } //switch
        }
    }


