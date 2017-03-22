package com.codinghazard.oraclize;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Contract;

import com.codinghazard.oraclize.contracts.CallbackContract;
import com.codinghazard.oraclize.contracts.OraclizeContract;
import com.mashape.unirest.http.Unirest;

public class OracleService {
    
    private static final Logger LOG = LoggerFactory.getLogger(OracleService.class);

    public static void main(String[] args) throws Exception {
        
        String rpcUrl = args[0];
        String callbackPrivateKey = args[1];
        String contractAddress = args[2];

        Credentials credentials = Credentials.create(callbackPrivateKey);
        
        LOG.info("RPC URL: {}", rpcUrl);
        LOG.info("CALLBACK ADDRESS: {}", credentials.getAddress());
        LOG.info("ORACLIZE ADDRESS: {}", contractAddress);

        Web3j web3 = Web3j.build(new HttpService(rpcUrl));

        OraclizeContract oracle = OraclizeContract.load(contractAddress, web3, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        oracle.logEventObservable().subscribe(event -> {
            
            String rid = Hex.encodeHexString(event.cid.getValue());
            LOG.info("New request {} from {}", rid, event.sender.toString());

            CallbackContract cb = CallbackContract.load(event.sender.toString(), web3, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
            try {
                // perfrom a GET request for the specified URL
                String result = Unirest.get(event.arg.getValue()).asString().getBody();
                
                // call the __callback function of the sending contract with the result
                TransactionReceipt receipt = cb.__callback(event.cid, new Utf8String(result.trim())).get();
                LOG.info("Request {} Tx {}", rid, receipt.getTransactionHash());
            } catch (Exception e) {
                LOG.error("Request {} error {}", rid, e.getMessage());
            }
        });
    }
}
