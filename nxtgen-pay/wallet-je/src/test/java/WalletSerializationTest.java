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


import in.qzip.wallet.je.Wallet;
import in.qzip.wallet.je.WalletBase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import java.math.BigDecimal;
import java.util.HashMap;

/**
 *
 * @author ashish
 */
public class WalletSerializationTest {

    Gson gson;
    ObjectMapper mapper;

    public WalletSerializationTest() {
        gson = new Gson();
        mapper = new ObjectMapper();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testJackson() throws JsonProcessingException {
        System.out.println("testJackson");
        WalletExt instance = new WalletExt("jackson","919810472329", "INR");
        instance.setBalance(new BigDecimal("200.03"));
        instance.setLastTnxTimestamp(new Timestamp(System.nanoTime()));
        instance.setLastTxnId(java.util.UUID.randomUUID().toString());
        
        System.out.println("testJackson -- WalletExt");
        System.out.println(mapper.writeValueAsString(instance));
        System.out.println("testJackson -- Wallet Interface");
        System.out.println(mapper.writeValueAsString((Wallet) instance));
        System.out.println("testJackson -- WalletExt XML");
        System.out.println(instance.toXMLString());
    }

    @Test
    public void testGsonSerialize() {
        System.out.println("testGsonSerialize");
        WalletBase instance = new WalletBase("919810472329", "INR");
        instance.setBalance(new BigDecimal("200.03"));
        instance.setLastTnxTimestamp(new Timestamp(System.nanoTime()));
        instance.setLastTxnId(java.util.UUID.randomUUID().toString());
        //System.out.println( gson.toJson(instance));
        System.out.println(gsonInterface(instance));
    }

    @Test
    public void testGsonExt() {
        System.out.println("testGsonExt");
        WalletExt instance = new WalletExt("7654321","919810472330", "INR" );
        instance.setBalance(new BigDecimal("200.03"));
        instance.setLastTnxTimestamp(new Timestamp(System.nanoTime()));
        instance.setLastTxnId(java.util.UUID.randomUUID().toString());
       
        System.out.println(gson.toJson(instance));
        //System.out.println(gsonInterface(instance));
    }
    @Test
    public void testHashedGsonExt() {
        // delegator test
         System.out.println("testHashedGsonExt");
        WalletExt instance = new WalletExt("icici","919910372321", "INR" );
        instance.setBalance(new BigDecimal("200.03"));
        instance.setLastTnxTimestamp(new Timestamp(System.nanoTime()));
        instance.setLastTxnId(java.util.UUID.randomUUID().toString());

        HashMap tab = new HashMap();
        tab.put("mmid", instance.getMmid());
        tab.put("account", instance.getAccountId());
        tab.put("balance", instance.getBalance());
        tab.put("currency", instance.getCurrency());
        tab.put("active", instance.isActive());
        tab.put("lastTxnTimestamp", instance.getLastTnxTimestamp());
        tab.put("lastTxnId", instance.getLastTxnId());
       
        System.out.println(gson.toJson(tab));
        System.out.println();
         
    }
    protected String gsonInterface(Wallet instance) {
        return gson.toJson(instance);
    }

}
