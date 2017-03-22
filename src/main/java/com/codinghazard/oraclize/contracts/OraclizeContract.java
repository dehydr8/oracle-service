package com.codinghazard.oraclize.contracts;

import java.math.BigInteger;
import java.util.Arrays;

import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.Contract;

import rx.Observable;
import rx.functions.Func1;

public class OraclizeContract extends Contract {

    public static class LogEventResponse {
        public Address sender;
        public Bytes32 cid;
        public Utf8String arg;
    }

    protected OraclizeContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static OraclizeContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new OraclizeContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public Observable<LogEventResponse> logEventObservable() {
        
        final Event event = new Event(
                    "Log",
                    Arrays.<TypeReference<?>>asList(),
                    Arrays.<TypeReference<?>>asList(
                        new TypeReference<Address>() {},
                        new TypeReference<Bytes32>() {},
                        new TypeReference<Utf8String>() {}
                    )
                );
        
        EthFilter filter = new EthFilter(
                DefaultBlockParameterName.EARLIEST,
                DefaultBlockParameterName.LATEST,
                getContractAddress().substring(2)
            );
        
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, LogEventResponse>() {
            @Override
            public LogEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                LogEventResponse typedResponse = new LogEventResponse();
                typedResponse.sender = (Address) eventValues.getNonIndexedValues().get(0);
                typedResponse.cid = (Bytes32) eventValues.getNonIndexedValues().get(1);
                typedResponse.arg = (Utf8String) eventValues.getNonIndexedValues().get(2);
                return typedResponse;
            }
        });
    }

}
