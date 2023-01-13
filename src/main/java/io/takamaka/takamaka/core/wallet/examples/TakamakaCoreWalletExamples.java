/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.takamaka.core.wallet.beans.WalletBean;
import io.takamaka.takamaka.core.wallet.beans.support.GlobalConstants;
import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.beans.InternalTransactionBean;
import io.takamaka.wallet.beans.KeyBean;
import io.takamaka.wallet.beans.TransactionBean;
import io.takamaka.wallet.exceptions.WalletException;
import io.takamaka.wallet.utils.FixedParameters;
import io.takamaka.wallet.utils.KeyContexts;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.util.Date;
import javax.crypto.NoSuchPaddingException;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * @author giovanni
 */
@Slf4j
public class TakamakaCoreWalletExamples {

    public static void main(String[] args) {
        try {
            InstanceWalletKeystoreInterface iwk = null;
            String walletName = "exampleWallet";
            String password = "password";
            int addressNumber = 0;
            WalletBean params = new WalletBean(walletName, password, KeyContexts.WalletCypher.Ed25519BC.name(), addressNumber);
            iwk = Wallet.createWallet(params);
            
            InstanceWalletKeystoreInterface iwk2 = new InstanceWalletKeyStoreBCED25519(walletName + FixedParameters.USER_WALLETS_FILE_EXTENSION, password);
            
            InstanceWalletKeystoreInterface iwk3 = new InstanceWalletKeyStoreBCED25519(walletName, password);
            //first and second addresses are the same but the third one differs
            System.out.println("public key for index " +addressNumber + " - " + iwk.getPublicKeyAtIndexURL64(addressNumber));
            System.out.println("private key for index " +addressNumber + " - " + Wallet.getPrivateKeyAtIndexURL64(iwk, addressNumber));
            
            System.out.println("II - public key for index " +addressNumber + " - " + iwk2.getPublicKeyAtIndexURL64(addressNumber));
            System.out.println("II - private key for index " +addressNumber + " - " + Wallet.getPrivateKeyAtIndexURL64(iwk2, addressNumber));
            
            System.out.println("III - public key for index " +addressNumber + " - " + iwk3.getPublicKeyAtIndexURL64(addressNumber));
            System.out.println("III - private key for index " +addressNumber + " - " + Wallet.getPrivateKeyAtIndexURL64(iwk3, addressNumber));
            
            KeyBean walletInfo = Wallet.getWalletInfo(walletName, password);
            System.out.println("recovery words: " + walletInfo.getWords());
            
            InternalTransactionBean itb = Wallet.BuildITBByType(
                    iwk.getPublicKeyAtIndexURL64(addressNumber), 
                    null, 
                    "generic text message", 
                    new Date().getTime() + (5 * 60 * 1000), 
                    null, 
                    null,
                    KeyContexts.TransactionType.BLOB);
            TransactionBean tb = Wallet.createGenericTransaction(itb, iwk, addressNumber);
            
            String verifyTransaction = Wallet.verifyTransaction(tb);
            System.out.println("Transaction verification result ");
            System.out.println(verifyTransaction);
            
            String sendTransaction = Wallet.sendTransaction(tb);
            System.out.println("Transaction sent response: " + sendTransaction);
            
            String searchText = "Lorem ipsum";
            boolean dumpTx = true;
            System.out.println("Search parameter: " + searchText);
            String searchTransactions = Wallet.searchTransactions(searchText, dumpTx, 5);
            System.out.println("generic search results: " + searchTransactions);
            
            
            String message = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit";
            System.out.println("exact search for string: " + message + " on field: " + GlobalConstants.ExactSearchField.message.name());
            String exactSearchTransactions = Wallet.exactSearchTransactions(message, GlobalConstants.ExactSearchField.message, dumpTx, 10);
            System.out.println("exact search results: " + exactSearchTransactions);
            
            //returns last mined block
            String blockHistory = Wallet.getBlockHistory(-1, -1, 1);
            System.out.println("Last generated block: " + blockHistory);
            
            String block = Wallet.getBlock(0, 1, "");
            System.out.println("block at epoch 0, slot 1: " + block);
            
            
            String addressBalance = Wallet.addressBalance(iwk.getPublicKeyAtIndexURL64(addressNumber));
            System.out.println("balance of address: " + iwk.getPublicKeyAtIndexURL64(addressNumber) + " : " + addressBalance);
            
            
        } catch (WalletException | IOException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchProviderException | NoSuchPaddingException ex) {
            log.error("Error executing examples", ex);
        }
    }
}
