package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.takamaka.core.wallet.examples.beans.WalletBean;
import io.takamaka.takamaka.core.wallet.examples.support.GlobalConstants;
import io.takamaka.takamaka.core.wallet.examples.support.ProjectHelper;
import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeyStoreBCQTESLAPSSC1Round2;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.TkmCypherProviderBCED25519;
import io.takamaka.wallet.TkmCypherProviderBCQTESLAPSSC1Round1;
import io.takamaka.wallet.TkmCypherProviderBCQTESLAPSSC1Round2;
import io.takamaka.wallet.beans.*;
import io.takamaka.wallet.utils.TkmTextUtils;
import io.takamaka.wallet.utils.FileHelper;
import io.takamaka.wallet.exceptions.HashCompositionException;
import io.takamaka.wallet.exceptions.InvalidWalletIndexException;
import io.takamaka.wallet.exceptions.NullInternalTransactionBeanException;
import io.takamaka.wallet.exceptions.PublicKeySerializzationException;
import io.takamaka.wallet.exceptions.TransactionCanNotBeCreatedException;
import io.takamaka.wallet.exceptions.TransactionCanNotBeSignedException;
import io.takamaka.wallet.exceptions.UnlockWalletException;
import io.takamaka.wallet.exceptions.WalletException;
import io.takamaka.wallet.utils.BuilderITB;
import io.takamaka.wallet.utils.DefaultInitParameters;
import io.takamaka.wallet.utils.FixedParameters;
import io.takamaka.wallet.utils.KeyContexts;
import io.takamaka.wallet.utils.KeyContexts.WalletCypher;
import io.takamaka.wallet.utils.TkmSignUtils;
import io.takamaka.wallet.utils.TkmWallet;
import io.takamaka.wallet.utils.TransactionUtils;
import io.takamaka.wallet.utils.WalletHelper;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ProtocolException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.util.encoders.UrlBase64;

/**
 *
 * @author admin
 */
@Slf4j
public class Wallet {

    public static InstanceWalletKeystoreInterface createWallet(WalletBean wallet) throws WalletException {
        System.out.println(wallet.getWalletName());
        System.out.println(wallet.getWalletCypher());

        InstanceWalletKeystoreInterface iwk = null;

        String internalName = wallet.getWalletName() + FixedParameters.USER_WALLETS_FILE_EXTENSION;//FixedParameters.USER_WALLETS_PREFIX + (new Date()).getTime() + FixedParameters.USER_WALLETS_FILE_EXTENSION;
        String password = wallet.getWalletPassword();

        WalletCypher cypher = WalletCypher.Ed25519BC;

        if (wallet.getWalletCypher().equals(KeyContexts.WalletCypher.BCQTESLA_PS_1_R2.name())) {
            cypher = WalletCypher.BCQTESLA_PS_1_R2;
        }

        try {
            switch (cypher) {
                case BCQTESLA_PS_1_R2:
                    iwk = new InstanceWalletKeyStoreBCQTESLAPSSC1Round2(internalName, password);
                    break;

                case Ed25519BC:
                    iwk = new InstanceWalletKeyStoreBCED25519(internalName, password);
                    System.out.println("public key for index 0: " + iwk.getPublicKeyAtIndexURL64(0));

                    break;
                default:
                    System.out.println("NOT IMPLEMENTED");

            }

        } catch (UnlockWalletException e) {

        }
        
        return iwk;
    }

    public static TransactionBean createTransaction(
            InstanceWalletKeystoreInterface iwk,
            int addressNumber,
            String from,
            String to,
            String message,
            long notBefore,
            BigInteger greenValue,
            BigInteger redValue,
            KeyContexts.TransactionType transactionType,
            Integer epoch,
            Integer slot) throws WalletException, NullInternalTransactionBeanException, HashCompositionException {

        TransactionBean tb = new TransactionBean();
        InternalTransactionBean itb = new InternalTransactionBean();

        itb.setFrom(iwk.getPublicKeyAtIndexURL64(addressNumber));
        itb.setTo(to);
        itb.setMessage(message);
        itb.setNotBefore(new Date(notBefore));
        itb.setGreenValue(greenValue);
        itb.setRedValue(redValue);
        itb.setTransactionType(transactionType);
        itb.setEpoch(epoch);
        itb.setSlot(slot);

        itb.setTransactionHash(TkmTextUtils.internalTransactionBeanHash(itb));

        tb.setRandomSeed(TkmTextUtils.generateWalletRandomString());
        tb.setPublicKey(iwk.getPublicKeyAtIndexURL64(addressNumber));
        tb.setMessage(TkmTextUtils.toJson(itb));
        tb.setWalletCypher(iwk.getWalletCypher());

        TkmCypherBean signatureBean = new TkmCypherBean();

        switch (tb.getWalletCypher()) {
            case Ed25519BC:
                signatureBean = TkmCypherProviderBCED25519.sign(iwk.getKeyPairAtIndex(addressNumber), tb.getMessage() + tb.getRandomSeed() + tb.getWalletCypher().name());
                if (!signatureBean.isValid()) {
                    throw new TransactionCanNotBeSignedException(signatureBean.getEx());
                }
                break;
            case BCQTESLA_PS_1:
                signatureBean = TkmCypherProviderBCQTESLAPSSC1Round1.sign(iwk.getKeyPairAtIndex(addressNumber), tb.getMessage() + tb.getRandomSeed() + tb.getWalletCypher().name());
                if (!signatureBean.isValid()) {
                    throw new TransactionCanNotBeSignedException(signatureBean.getEx());
                }
                break;
            case BCQTESLA_PS_1_R2:
                signatureBean = TkmCypherProviderBCQTESLAPSSC1Round2.sign(iwk.getKeyPairAtIndex(addressNumber), tb.getMessage() + tb.getRandomSeed() + tb.getWalletCypher().name());
                if (!signatureBean.isValid()) {
                    throw new TransactionCanNotBeSignedException(signatureBean.getEx());
                }
                break;
            default:
                //signatureBean.setValid(false);
                //signatureBean.setEx("UNKNOWN CYPHER");
                log.error("UNKNOWN CYPHER");
        }

        tb.setSignature(signatureBean.getSignature());
        TransactionSyntaxBean transactionBeanValid = TransactionUtils.isTransactionBeanValid(tb);
        if (!transactionBeanValid.isValidSyntax()) {
            throw new TransactionCanNotBeCreatedException("invalid internal parameter " + transactionBeanValid.getExtendedMessage());
        }

        return tb;
    }

    public static KeyBean getWalletInfo(String walletName, String password) throws FileNotFoundException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, UnlockWalletException {
        KeyBean wallet = new KeyBean();

        Path walletPath = Paths.get(FileHelper.getDefaultWalletDirectoryPath().toString(), walletName + DefaultInitParameters.WALLET_EXTENSION);
        wallet = WalletHelper.readKeyFile(walletPath, password);

        return wallet;
    }

    public static void recoverWallet(List<String> words, String newName, String newPassword, WalletCypher cypher) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException {
        String internalName = newName + FixedParameters.USER_WALLETS_FILE_EXTENSION;
        WalletHelper.importKeyFromWords(words, FileHelper.getDefaultWalletDirectoryPath(), internalName, cypher, newPassword);
    }

    public static String getWalletCrc(String walletAddress) {

        if (TkmTextUtils.isNullOrBlank(walletAddress) || (walletAddress.length() != 44 && walletAddress.length() != 19840)) {
            String msg = "Not a valid address";
            System.err.println(msg);
            return null;
        } else {

            String crc = TkmSignUtils.getHexCRC(walletAddress);
            return crc.toUpperCase();
        }
    }

    public static final String getWalletIdenticon(String walletAddress) throws IOException {
        String result = "";
        //String testEndpoint = GlobalConstants.TEST_NET_ENDPOINT;
        String identiconApi = GlobalConstants.TEST_NET_ENDPOINT + GlobalConstants.WALLET_IDENTICON_API;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("address", "" + walletAddress + "");

        result = ProjectHelper.doPost(identiconApi, params);

        return result;
    }

    public static String getPrivateKeyAtIndexURL64(InstanceWalletKeystoreInterface iwk, int addressNumber) throws InvalidWalletIndexException, PublicKeySerializzationException {
        String result = null;
        try {
            AsymmetricCipherKeyPair keyPairAtIndex = iwk.getKeyPairAtIndex(addressNumber);
            UrlBase64 b64e = new UrlBase64();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AsymmetricKeyParameter aPrivate = keyPairAtIndex.getPrivate();
            Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) aPrivate;
            b64e.encode(privateKey.getEncoded(), baos);
            result = baos.toString();
            baos.close();
        } catch (WalletException | IOException ex) {
            log.error("Error obtaining keys", ex);
            //System.err.println("Wallet can not serialize public key");
            throw new PublicKeySerializzationException(ex);
        }

        return result;
    }

    public static String sendTransaction(TransactionBean tb) throws ProtocolException, IOException {
        String txJson = TkmTextUtils.toJson(tb);
        TransactionBox tbox = TkmWallet.verifyTransactionIntegrity(txJson);

        if (!tbox.isValid()) {
            throw new RuntimeException("Invalid Transaction");
        }

        String hexBody = TkmSignUtils.fromStringToHexString(txJson);
        String testEndpoint = GlobalConstants.TEST_NET_ENDPOINT;
        //String prodEndpoint = GlobalConstants.MAIN_NET_ENDPOINT;
        String sendTransactionAPI = testEndpoint + GlobalConstants.SEND_TRANSACTION_API;
        String result = ProjectHelper.doPost(sendTransactionAPI, "tx", hexBody);
        return result;
    }

    public static String verifyTransaction(TransactionBean tb) throws ProtocolException, IOException {
        String txJson = TkmTextUtils.toJson(tb);
        TransactionBox tbox = TkmWallet.verifyTransactionIntegrity(txJson);

        if (!tbox.isValid()) {
            throw new RuntimeException("Invalid Transaction");
        }

        String hexBody = TkmSignUtils.fromStringToHexString(txJson);
        String testEndpoint = GlobalConstants.TEST_NET_ENDPOINT;
        //String prodEndpoint = GlobalConstants.MAIN_NET_ENDPOINT;
        String verifyTransactionAPI = testEndpoint + GlobalConstants.VERIFY_TRANSACTION_API;
        String result = ProjectHelper.doPost(verifyTransactionAPI, "tx", hexBody);
        
        return result;
    }

    public static String searchTransactions(String data, boolean dumpTx, int limit) throws ProtocolException, IOException {
        String result = "";
        //String testEndpoint = GlobalConstants.TEST_NET_ENDPOINT;
        String searchTransactionAPI = GlobalConstants.TEST_NET_ENDPOINT + GlobalConstants.SEARCH_TRANSACTION_API;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("address", "" + data + "");
        params.put("dumptx", "" + dumpTx + "");
        params.put("limit", "" + limit + "");

        result = ProjectHelper.doPost(searchTransactionAPI, params);

        return result;
    }

    public static String exactSearchTransactions(String data, GlobalConstants.ExactSearchField field, boolean dumpTx, int limit) throws ProtocolException, IOException {
        String result = "";
        //String testEndpoint = GlobalConstants.TEST_NET_ENDPOINT;
        String searchTransactionAPI = GlobalConstants.TEST_NET_ENDPOINT + GlobalConstants.EXACT_SEARCH_TRANSACTION_API;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("data", "" + data + "");
        params.put("field", "" + field.name() + "");
        params.put("dumptx", "" + dumpTx + "");
        params.put("limit", "" + limit + "");

        result = ProjectHelper.doPost(searchTransactionAPI, params);

        return result;
    }

    public static String addressBalance(String address) throws ProtocolException, IOException {
        String result = "";
        //String testEndpoint = GlobalConstants.TEST_NET_ENDPOINT;
        String searchTransactionAPI = GlobalConstants.TEST_NET_ENDPOINT + GlobalConstants.ADDRESS_BALANCE_API;
        Map<String, String> params = new LinkedHashMap<>();
        params.put("address", "" + address + "");

        result = ProjectHelper.doPost(searchTransactionAPI, params);

        return result;
    }

    public static String getBlockHistory(int epoch, int slot, int limit) throws IOException {
        String result = "";

        String blockHistoryAPI = GlobalConstants.TEST_NET_ENDPOINT + GlobalConstants.BLOCK_HISTORY_API;
        Map<String, String> params = new LinkedHashMap<>();

        if (epoch > 0) {
            params.put("epoch", "" + epoch + "");
        }

        if (slot > 0) {
            params.put("slot", "" + slot + "");
        }

        if (limit <= 0) {
            params.put("limit", "" + 1 + "");
        } else {
            params.put("limit", "" + limit + "");
        }

        //passing only limit, returns the latest <limit> number of block data(epoch, slot, blockhash)
        result = ProjectHelper.doPost(blockHistoryAPI, params);

        return result;
    }

    public static String getBlock(int epoch, int slot, String blockHash) throws IOException {
        String result = "";
        String getBlockAPI = GlobalConstants.TEST_NET_ENDPOINT + GlobalConstants.GET_BLOCK_API;
        Map<String, String> params = new LinkedHashMap<>();
        
        params.put("epoch", "" + epoch + "");
        params.put("slot", "" + slot + "");
        params.put("blockhash", blockHash);

        result = ProjectHelper.doPost(getBlockAPI, params);

        return result;
    }

    public static InternalTransactionBean BuildITBByType(String from,
            String to,
            String message,
            long notBefore,
            BigInteger greenValue,
            BigInteger redValue,
            KeyContexts.TransactionType transactionType) {
        InternalTransactionBean itb = null;

        Date maxIncludeTime = new Date(notBefore);

        switch (transactionType) {
            case ASSIGN_OVERFLOW:
                itb = BuilderITB.assignOverflow(from, to, message, maxIncludeTime);
                break;
            case BLOB:
                itb = BuilderITB.blob(from, message, maxIncludeTime);
                break;
            case DEREGISTER_MAIN:
                itb = BuilderITB.deregisterMain(from, message, maxIncludeTime);
                break;
            case DEREGISTER_OVERFLOW:
                itb = BuilderITB.deregisterOverflow(from, message, maxIncludeTime);
                break;
            case PAY:
                itb = BuilderITB.pay(from, to, greenValue, redValue, message, maxIncludeTime);
                break;
            case STAKE:
                itb = BuilderITB.stake(from, to, greenValue, message, maxIncludeTime);
                break;
            case STAKE_UNDO:
                itb = BuilderITB.stakeUndo(from, message, maxIncludeTime);
                break;
            case REGISTER_MAIN:
                itb = BuilderITB.registerMain(from, message, maxIncludeTime);
                break;
            case REGISTER_OVERFLOW:
                itb = BuilderITB.registerOverflow(from, message, maxIncludeTime);
                break;
            case UNASSIGN_OVERFLOW:
                itb = BuilderITB.unassignOverflow(from, to, message, maxIncludeTime);
                break;
            default:
                break;
        }

        return itb;
    }

    public static TransactionBean createGenericTransaction(InternalTransactionBean itb, InstanceWalletKeystoreInterface iwk,
            int addressNumber) throws TransactionCanNotBeCreatedException, TransactionCanNotBeSignedException, WalletException {

        TransactionBean tb = new TransactionBean();

        tb.setRandomSeed(TkmTextUtils.generateWalletRandomString());
        tb.setPublicKey(iwk.getPublicKeyAtIndexURL64(addressNumber));
        tb.setMessage(TkmTextUtils.toJson(itb));
        tb.setWalletCypher(iwk.getWalletCypher());

        TkmCypherBean signatureBean = new TkmCypherBean();

        switch (tb.getWalletCypher()) {
            case Ed25519BC:
                signatureBean = TkmCypherProviderBCED25519.sign(iwk.getKeyPairAtIndex(addressNumber), tb.getMessage() + tb.getRandomSeed() + tb.getWalletCypher().name());
                if (!signatureBean.isValid()) {
                    throw new TransactionCanNotBeSignedException(signatureBean.getEx());
                }
                break;
            case BCQTESLA_PS_1:
                signatureBean = TkmCypherProviderBCQTESLAPSSC1Round1.sign(iwk.getKeyPairAtIndex(addressNumber), tb.getMessage() + tb.getRandomSeed() + tb.getWalletCypher().name());
                if (!signatureBean.isValid()) {
                    throw new TransactionCanNotBeSignedException(signatureBean.getEx());
                }
                break;
            case BCQTESLA_PS_1_R2:
                signatureBean = TkmCypherProviderBCQTESLAPSSC1Round2.sign(iwk.getKeyPairAtIndex(addressNumber), tb.getMessage() + tb.getRandomSeed() + tb.getWalletCypher().name());
                if (!signatureBean.isValid()) {
                    throw new TransactionCanNotBeSignedException(signatureBean.getEx());
                }
                break;
            default:
                //signatureBean.setValid(false);
                //signatureBean.setEx("UNKNOWN CYPHER");
                log.error("UNKNOWN CYPHER");
        }

        tb.setSignature(signatureBean.getSignature());
        TransactionSyntaxBean transactionBeanValid = TransactionUtils.isTransactionBeanValid(tb);
        if (!transactionBeanValid.isValidSyntax()) {
            throw new TransactionCanNotBeCreatedException("invalid internal parameter " + transactionBeanValid.getExtendedMessage());
        }

        return tb;
    }

//    public byte[] getPrivateKeyAtIndexByte(InstanceWalletKeystoreInterface iwk, int addressNumber) throws InvalidWalletIndexException, PublicKeySerializzationException, WalletException {
//        byte[] result;
//        try {
//            AsymmetricCipherKeyPair keyPairAtIndex = iwk.getKeyPairAtIndex(addressNumber);
//            UrlBase64 b64e = new UrlBase64();
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            AsymmetricKeyParameter aPrivate = keyPairAtIndex.getPrivate();
//            Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) aPrivate;
//            b64e.encode(privateKey.getEncoded(), baos);
//            result = baos.toByteArray();
//            baos.close();
//        } catch (IOException ex) {
//            Log.logStacktrace(Level.SEVERE, ex);
//            System.err.println("Wallet can not serialize public key");
//            throw new PublicKeySerializzationException(ex);
//        }
//        return result;
//    }
}
