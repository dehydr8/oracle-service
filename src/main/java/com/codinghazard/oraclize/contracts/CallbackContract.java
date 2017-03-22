package com.codinghazard.oraclize.contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Future;

import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import java.lang.String;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;

public class CallbackContract extends Contract {

    protected CallbackContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static CallbackContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CallbackContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @SuppressWarnings("rawtypes")
    public Future<TransactionReceipt> __callback(Bytes32 myid, Utf8String result) {
        Function function = new Function("__callback", Arrays.<Type>asList(myid, result), Collections.<TypeReference<?>>emptyList());
        return executeTransactionAsync(function);
    }

}
