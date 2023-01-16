/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeyStoreBCQTESLAPSSC1Round2;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.beans.FeeBean;
import io.takamaka.wallet.beans.InternalTransactionBean;
import io.takamaka.wallet.beans.TransactionBean;
import io.takamaka.wallet.beans.TransactionBox;
import io.takamaka.wallet.utils.BuilderITB;
import io.takamaka.wallet.utils.TkmTK;
import io.takamaka.wallet.utils.TkmTextUtils;
import io.takamaka.wallet.utils.TkmWallet;
import io.takamaka.wallet.utils.TransactionFeeCalculator;
import java.math.BigInteger;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author giovanni
 */
@Slf4j
public class TransactionPayBCQTESLAPSSC1Round2 {

    public static final String SOURCE_WALLET_NAME = "my_example_wallet_source";
    public static final String DESTINATION_WALLET_NAME = "my_example_wallet_destination";
    public static final String SOURCE_WALLET_PASSWORD = "my_example_wallet_source_password";
    public static final String DESTINATION_WALLET_PASSWORD = "my_example_wallet_destination_password";

    public static void main(String[] args) throws Exception {

        log.info("wallet creation or import");
        final InstanceWalletKeystoreInterface iwkBCQTR2Source = new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(SOURCE_WALLET_NAME, SOURCE_WALLET_PASSWORD);
        final InstanceWalletKeystoreInterface iwkBCQTR2Destination = new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(DESTINATION_WALLET_NAME, DESTINATION_WALLET_PASSWORD);

        final String publicKeySource = iwkBCQTR2Source.getPublicKeyAtIndexURL64(0);
        log.info("source public key " + publicKeySource);
        final String publicKeyDestination = iwkBCQTR2Destination.getPublicKeyAtIndexURL64(0);
        log.info("destination public key " + publicKeyDestination);

        log.info("In the takamaka blockchain each transaction has a time limit "
                + "beyond which it cannot be included in a block. \nThe "
                + "transaction timer must be formatted as unix timestamp "
                + "(with milliseconds) and set to between \"current time\" and \""
                + "current time + 10 minutes\". This enables the transaction to "
                + "be included in the first available block for the next "
                + "10 minutes. Creating transactions with timer set to current"
                + " time might work well for local testing but is unlikely to"
                + " function when interacting with remote servers.\nTo give"
                + " a \"sensible\" inclusion window to the transaction and to "
                + "account for possible clock errors of the nodes, the device "
                + "doing the sending, and network transport times, it is "
                + "recommended to place the transaction in the middle of the "
                + "interval. To do this simply add 60000L * 5 (5 minutes) "
                + "to the NOW(), current time.");

        final Date transactionInclusionTime = TkmTK.getTransactionTime();

        log.info("In the takamaka blockchain, values are represented in "
                + "nanoTK, which means that to transfer 1 TK (a red token or a "
                + "green token) you need to multiply this value by 10^9.");

        final BigInteger oneTKGValue = TkmTK.unitTK(1);

        log.info("1 TKG value in nano TKG " + oneTKGValue.toString());

        final BigInteger fiveTKRValue = TkmTK.unitTK(5);

        log.info("5 TKR value in nano TKR " + fiveTKRValue.toString());

        log.info("BuilderITB is a class that allows you to create the stub "
                + "for sending any transaction.");

        InternalTransactionBean payITB = BuilderITB.pay(
                publicKeySource, publicKeyDestination,
                oneTKGValue, fiveTKRValue,
                "UTF8 message, free annotation",
                transactionInclusionTime);

        log.info("This forms the body of the transaction, now you need to "
                + "use a wallet to create the cryptographic envelope and sign "
                + "the transaction.");

        log.info("For the transaction to be valid, it is necessary that the "
                + "public key used in the from field matches the key used to "
                + "sign the tranasation.");

        TransactionBean myPayObject
                = TkmWallet.createGenericTransaction(payITB,
                        iwkBCQTR2Source, // source wallet 
                        0 // same wallet and KEY INDEX of publicKeySource
                );

        log.info("transaction serialization");

        String payTransactionJson = TkmTextUtils.toJson(myPayObject);

        log.info("the serialized transaction");
        log.info(payTransactionJson);

        log.info("How to perform a syntactic check of the newly created "
                + "transaction.");

        TransactionBox payTbox = TkmWallet.verifyTransactionIntegrity(payTransactionJson);
        log.info("the transaction is valid?: " + payTbox.isValid());

        log.info("If the transaction is valid, the inclusion cost can be "
                + "calculated locally. For all basic transactions (those with "
                + "CPU and MEMORY equal to 0) the cost is determinable a priori "
                + "and determined only by the number of bytes in the json.");

        FeeBean payFeeBean = TransactionFeeCalculator.getFeeBean(payTbox);

        log.info("PAY is a basic transaction");
        log.info("single inclusion transaction hash: " + payFeeBean.getSith()
                + "\nCPU cost (nanoTK):\t" + payFeeBean.getCpu()
                + "\nMEMORY cost (nanoTK):\t" + payFeeBean.getMemory()
                + "\nDISK cost (nanoTK):\t" + payFeeBean.getDisk()
        );
        log.info("readable way in TK: " + TransactionFeeCalculator.getCostInTK(payFeeBean).toPlainString());

    }
}
