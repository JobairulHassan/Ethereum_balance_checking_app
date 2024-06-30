package com.example.assesmentproject;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.math.BigInteger;

public class EthereumUtils {
    private static final String INFURA_URL = "https://mainnet.infura.io/v3/0b36e7b254994c478a675e818953d49c";

    public static BigInteger getBalance(String address) throws Exception {
        try {
            Web3j web3 = Web3j.build(new HttpService(INFURA_URL));
            EthGetBalance balance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return balance.getBalance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BigInteger getNonce(String address) throws Exception {
        try {
            Web3j web3 = Web3j.build(new HttpService(INFURA_URL));
            EthGetTransactionCount transactionCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
            return transactionCount.getTransactionCount();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
