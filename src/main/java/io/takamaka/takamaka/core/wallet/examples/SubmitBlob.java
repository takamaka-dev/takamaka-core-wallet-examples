/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.takamaka.takamaka.core.wallet.examples;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.takamaka.extra.beans.TkmMetadata;
import io.takamaka.extra.files.MetadataUtils;
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
import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author Giovanni Antino giovanni.antino@takamaka.io
 */
@Slf4j
public class SubmitBlob {
    
    public static final String SOURCE_WALLET_NAME = "my_example_wallet_source";
    public static final String DESTINATION_WALLET_NAME = "my_example_wallet_destination";
    public static final String SOURCE_WALLET_PASSWORD = "my_example_wallet_source_password";
    public static final String DESTINATION_WALLET_PASSWORD = "my_example_wallet_destination_password";
    
    public static void main(String[] args) throws Exception {
        
        log.info("A \"blob\" transaction in a blockchain is a type of "
                + "transaction that stores binary data, such as a document or "
                + "image, on the blockchain. This data is typically stored as "
                + "a \"hash,\" which is a unique digital fingerprint of "
                + "the data that can be used to verify its authenticity. "
                + "Blob transactions are often used to store "
                + "important documents, such as contracts, on a blockchain "
                + "for secure and tamper-proof storage.");
        
        log.info(" --- same code TransactionPayED25519 --- Begin ---");
        
        log.info("wallet creation or import");
        final InstanceWalletKeystoreInterface iwkEDSource = new InstanceWalletKeyStoreBCED25519(SOURCE_WALLET_NAME, SOURCE_WALLET_PASSWORD);
        
        final String publicKeySource = iwkEDSource.getPublicKeyAtIndexURL64(0);
        log.info("source public key " + publicKeySource);
        
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

//        try ( FileInputStream fis = new FileInputStream()) {
//            int content;
//            // reads a byte at a time, if it reached end of the file, returns -1
//            while ((content = fis.read()) != -1) {
//                System.out.println((char) content);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        File selectedFile = new File("resources/sample-image.jpg");
        FileInputStream fileIn = new FileInputStream(selectedFile);
        String[] tags = {"tag1", "tag2", "tag3"};
        
        TkmMetadata collectMetadata = MetadataUtils.collectMetadata(fileIn, tags);
        log.info(collectMetadata.toString());
        
        collectMetadata.setResourceName(selectedFile.getName());

        //Metadata extractMetadatatUsingParser = ProjectHelper.extractMetadatatUsingParser(fileIn);
        //String[] names = extractMetadatatUsingParser.names();
//        Map<String, String> mappedMetaData = new HashMap<>();
//        Map<String, String> mappedExtraMetadata = new HashMap<>();
//
//        for (String name : names) {
//            mappedMetaData.put(name, extractMetadatatUsingParser.get(name));
//        }
//
//        ObjectMapper jacksonMapper = TkmTextUtils.getJacksonMapper();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        JsonGenerator gen = jacksonMapper.createGenerator(baos);
//        gen.writeStartObject();
//
//        /*
//            Another method could be the following
//            String[] tags = "tag1,tag2,tag3".split(",");
//         */
//        gen.writeFieldName("tags");
//        gen.writeStartArray();
//        for (String tag : tags) {
//            String trimmedTag = StringUtils.trimToNull(tag);
//            if (!TkmTextUtils.isNullOrBlank(trimmedTag)) {
//                gen.writeObject(trimmedTag);
//            }
//        }
//        gen.writeEndArray();
//        mappedMetaData.entrySet().forEach((single) -> {
//            if (single.getKey().equals("Content-Type")
//                    || single.getKey().equals("X-Parsed-By")
//                    || single.getKey().equals("type")) {
//                try {
//                    gen.writeStringField(single.getKey(), single.getValue());
//                } catch (IOException ex) {
//                    Logger.getLogger(SubmitBlob.class.getName()).log(Level.SEVERE, null, ex);
//                    ex.printStackTrace();
//                }
//            } else {
//                mappedExtraMetadata.put(single.getKey(), single.getValue());
//            }
//        });
//        gen.writeStringField("resourceName", selectedFile.getName());
//        gen.writeStringField("mime", mappedMetaData.get("Content-Type"));
//        gen.writeObjectField("extraMetadata", mappedExtraMetadata);
//
//        //to optimize indexing leave data as last element
//        byte[] byteFile = FileUtils.readFileToByteArray(selectedFile);
//        String base64file = TkmSignUtils.fromByteArrayToB64URL(byteFile);
//        gen.writeStringField("data", base64file);
//
//        gen.writeEndObject();
//        gen.flush();
//
//        String generatedMap = baos.toString(StandardCharsets.UTF_8);
//
//        gen.close();
//        baos.close();
        collectMetadata.setData(MetadataUtils.fromFileToB64String(selectedFile));
        
        ObjectMapper jacksonMapper = TkmTextUtils.getJacksonMapper();
        String writeValueAsString = jacksonMapper.writeValueAsString(collectMetadata);
        //gen.writeStringField("data", base64file);
        log.info(writeValueAsString);
        InternalTransactionBean blobITB = BuilderITB.blob(
                publicKeySource,
                writeValueAsString,
                transactionInclusionTime);
        
        log.info("This forms the body of the transaction, now you need to "
                + "use a wallet to create the cryptographic envelope and sign "
                + "the transaction.");
        
        log.info("For the transaction to be valid, it is necessary that the "
                + "public key used in the from field matches the key used to "
                + "sign the tranasation.");
        
        TransactionBean myBlobObject
                = TkmWallet.createGenericTransaction(
                        blobITB,
                        iwkEDSource, // source wallet 
                        0 // same wallet and KEY INDEX of publicKeySource
                );
        
        log.info("transaction serialization");
        
        String blobTransactionJson = TkmTextUtils.toJson(myBlobObject);
        
        log.info("the serialized transaction");
        log.info(blobTransactionJson);
        
        log.info("How to perform a syntactic check of the newly created "
                + "transaction.");
        
        TransactionBox blobTbox = TkmWallet.verifyTransactionIntegrity(blobTransactionJson);
        log.info("the transaction is valid?: " + blobTbox.isValid());
        
        FeeBean blobFeeBean = TransactionFeeCalculator.getFeeBean(blobTbox);
        
        log.info("BLOB is a basic transaction");
        log.info("single inclusion transaction hash: " + blobFeeBean.getSith()
                + "\nCPU cost (nanoTK):\t" + blobFeeBean.getCpu()
                + "\nMEMORY cost (nanoTK):\t" + blobFeeBean.getMemory()
                + "\nDISK cost (nanoTK):\t" + blobFeeBean.getDisk()
        );
        log.info("readable way in TK: " + TransactionFeeCalculator.getCostInTK(blobFeeBean).toPlainString());
        
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
        
        String blobHexBody = TkmSignUtils.fromStringToHexString(blobTransactionJson);
        
        log.info("the wrapped json, can be decode using hex to text tool");
        log.info(blobHexBody);
        
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
        
        String blobTxVerifyResult = ProjectHelper.doPost(
                "https://dev.takamaka.io/api/V2/fastapi/verifytransaction", // main network verify endpoint (for verify main or test network is the same) 
                "tx", //form var
                blobHexBody); //hex transaction

        log.info("endpoint verification result");
        log.info(blobTxVerifyResult);
        
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
        String blobTxSubmitResult = ProjectHelper.doPost("https://dev.takamaka.io/api/V2/testapi/transaction", // TEST endpoint
                "tx",
                blobHexBody);
        log.info("endpoint submit result");
        log.info(blobTxSubmitResult);
        
    }
}
