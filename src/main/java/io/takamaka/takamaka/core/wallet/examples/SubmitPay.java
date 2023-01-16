/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.takamaka.core.wallet.examples.support.ProjectHelper;
import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.beans.FeeBean;
import io.takamaka.wallet.beans.InternalTransactionBean;
import io.takamaka.wallet.beans.TransactionBean;
import io.takamaka.wallet.beans.TransactionBox;
import io.takamaka.wallet.utils.BuilderITB;
import io.takamaka.wallet.utils.TkmSignUtils;
import io.takamaka.wallet.utils.TkmTK;
import io.takamaka.wallet.utils.TkmTextUtils;
import io.takamaka.wallet.utils.TkmWallet;
import io.takamaka.wallet.utils.TransactionFeeCalculator;
import java.math.BigInteger;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Giovanni Antino giovanni.antino@takamaka.io
 */
@Slf4j
public class SubmitPay {

    public static final String SOURCE_WALLET_NAME = "my_example_wallet_source";
    public static final String DESTINATION_WALLET_NAME = "my_example_wallet_destination";
    public static final String SOURCE_WALLET_PASSWORD = "my_example_wallet_source_password";
    public static final String DESTINATION_WALLET_PASSWORD = "my_example_wallet_destination_password";

    public static void main(String[] args) throws Exception {

        log.info(" --- same code TransactionPayED25519 --- Begin ---");

        log.info("wallet creation or import");
        final InstanceWalletKeystoreInterface iwkEDSource = new InstanceWalletKeyStoreBCED25519(SOURCE_WALLET_NAME, SOURCE_WALLET_PASSWORD);
        final InstanceWalletKeystoreInterface iwkEDDestination = new InstanceWalletKeyStoreBCED25519(DESTINATION_WALLET_NAME, DESTINATION_WALLET_PASSWORD);

        final String publicKeySource = iwkEDSource.getPublicKeyAtIndexURL64(0);
        log.info("source public key " + publicKeySource);
        final String publicKeyDestination = iwkEDDestination.getPublicKeyAtIndexURL64(0);
        log.info("destination public key " + publicKeyDestination);

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
                = TkmWallet.createGenericTransaction(
                        payITB,
                        iwkEDSource, // source wallet 
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

        FeeBean payFeeBean = TransactionFeeCalculator.getFeeBean(payTbox);

        log.info("PAY is a basic transaction");
        log.info("single inclusion transaction hash: " + payFeeBean.getSith()
                + "\nCPU cost (nanoTK):\t" + payFeeBean.getCpu()
                + "\nMEMORY cost (nanoTK):\t" + payFeeBean.getMemory()
                + "\nDISK cost (nanoTK):\t" + payFeeBean.getDisk()
        );
        log.info("readable way in TK: " + TransactionFeeCalculator.getCostInTK(payFeeBean).toPlainString());

        log.info(" --- same code TransactionPayED25519 --- End ---");

        log.info("To minimize the risk of transaction modification during "
                + "transport, Takamaka endpoints accept transactions only if "
                + "they are encoded in HEX. The content of each tansaction is "
                + "signed. In case of modification the integrity check forces "
                + "the deletion of these objects, so there is no risk of "
                + "alteration and inclusion of an altered transaction. Field "
                + "tests have shown that certain servers tend to handle json "
                + "content in a less than transparent way. This is especially "
                + "the case if there are special or Oriental language characters"
                + " in the UTF-8 messages. \n In practice, the message would be "
                + "altered and some characters could be replaced "
                + "with equivalent versions, text-wise, but not byte "
                + "encoding-wise. This obviously broke the signature.\nTo avoid "
                + "the hassle of having to wait 5 to 10 minutes to see if the "
                + "transaction did not go through and then proceed to resend it "
                + "was preferred to add a protection layer.\nHEX encoding is "
                + "used only for transport and is discarded once the message is "
                + "received by the endpoint.");

        log.info("Side note: Transactions are signed and sent over a public "
                + "network that uploads them to a public repository. For this "
                + "type of object, the ssl layer is not strictly necessary to "
                + "ensure secrecy. Takamaka endpoints are still ssl but this is "
                + "an additional layer added to ensure greater compatibility "
                + "with mobile devices that poorly digest plain http outside the"
                + " development environment.");

        String payHexBody = TkmSignUtils.fromStringToHexString(payTransactionJson);

        log.info("the wrapped json, can be decode using hex to text tool");
        log.info(payHexBody);

        log.info("You can send a transaction to a verification endpoint to "
                + "get a syntactic check on it.");
        log.info("This is especially useful when one finds oneself using "
                + "devices with reduced computing capacity or prefers to do an "
                + "offload of this work.");
        log.info("curlified version of the transaction test submit");
        log.info("curl --location --request GET 'https://dev.takamaka.io/api/V2/fastapi/verifytransaction' \\\n"
                + "--header 'Content-Type: application/x-www-form-urlencoded' \\\n"
                + "--data-urlencode 'tx=7b227...227d'");
        log.info("endpoint valid response example");
        log.info("{\n"
                + "    \"addr\": \"VQLJRNTBc9a3zc0WOuBk00yCd9enELwp_unEiBC27Bk.\",\n"
                + "    \"hexAddr\": \"5502c944d4c173d6b7cdcd163ae064d34c8277d7a710bc29fee9c48810b6ec19\",\n"
                + "    \"sith\": \"5L1UxVXZw6T8unvhjvjpm8wsRuBypewcK97aQjcxX-0.\",\n"
                + "    \"disk\": 78400000,\n"
                + "    \"memory\": 0,\n"
                + "    \"cpu\": 0\n"
                + "}");

        String payTxVerifyResult = ProjectHelper.doPost(
                "https://dev.takamaka.io/api/V2/fastapi/verifytransaction", // main network verify endpoint (for verify main or test network is the same) 
                "tx", //form var
                payHexBody); //hex transaction

        log.info("endpoint verification result");
        log.info(payTxVerifyResult);

        log.info("curlified version of the transaction submit");
        log.info("curl --location --request GET 'https://dev.takamaka.io/api/V2/testapi/transaction' \\\n"
                + "--header 'Content-Type: application/x-www-form-urlencoded' \\\n"
                + "--data-urlencode 'tx=7b227...3227d'");
        log.info("response example");
        log.info("{\n"
                + "    \"TxIsVerified\": \"true\"\n"
                + "}");
        log.info("When the endpoint replies this way, the transaction has been "
                + "successfully received by the server, the syntax is correct, "
                + "and it has been added to the queue for inclusion in a block.\n"
                + "At this point the SEMANTIC checks have not yet been "
                + "performed, for example if the sending account cannot pay for "
                + "the inclusion the transaction will be discarded.");
        log.info("transaction submit to test endpoint");
        String payTxSubmitResult = ProjectHelper.doPost("https://dev.takamaka.io/api/V2/testapi/transaction", // TEST endpoint
                "tx", 
                payHexBody);
        log.info("endpoint submit result");
        log.info(payTxSubmitResult);
        
    }
}
