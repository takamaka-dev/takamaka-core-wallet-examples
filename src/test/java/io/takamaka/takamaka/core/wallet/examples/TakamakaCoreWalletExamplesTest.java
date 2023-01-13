/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.takamaka.core.wallet.examples.beans.WalletBean;
import io.takamaka.takamaka.core.wallet.examples.support.GlobalConstants;
import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.beans.InternalTransactionBean;
import io.takamaka.wallet.beans.KeyBean;
import io.takamaka.wallet.beans.TransactionBean;
import io.takamaka.wallet.beans.TransactionBox;
import io.takamaka.wallet.exceptions.TransactionNotYetImplementedException;
import io.takamaka.wallet.exceptions.WalletException;
import io.takamaka.wallet.utils.BuilderITB;
import io.takamaka.wallet.utils.FixedParameters;
import io.takamaka.wallet.utils.KeyContexts;
import io.takamaka.wallet.utils.TkmTextUtils;
import io.takamaka.wallet.utils.TkmWallet;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.ConcurrentSkipListMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author giovanni
 */
@Slf4j
public class TakamakaCoreWalletExamplesTest {

    public TakamakaCoreWalletExamplesTest() {
        BasicConfigurator.configure();
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

    /**
     * Test of main method, of class TakamakaCoreWalletExamples.
     */
    @Test
    public void testMain() throws Exception {
        InstanceWalletKeystoreInterface iwk = null;
            String walletName = "exampleWallet";
            String password = "password";
            int addressNumber = 0;
            WalletBean params = new WalletBean(walletName, password, KeyContexts.WalletCypher.Ed25519BC.name(), addressNumber);
            iwk = Wallet.createWallet(params);
            
            InstanceWalletKeystoreInterface iwk2 = new InstanceWalletKeyStoreBCED25519(walletName + FixedParameters.USER_WALLETS_FILE_EXTENSION, password);
            
            InstanceWalletKeystoreInterface iwk3 = new InstanceWalletKeyStoreBCED25519(walletName, password);
            //first and second addresses are the same but the third one differs
            log.info("public key for index " +addressNumber + " - " + iwk.getPublicKeyAtIndexURL64(addressNumber));
            log.info("private key for index " +addressNumber + " - " + Wallet.getPrivateKeyAtIndexURL64(iwk, addressNumber));
            
            log.info("II - public key for index " +addressNumber + " - " + iwk2.getPublicKeyAtIndexURL64(addressNumber));
            log.info("II - private key for index " +addressNumber + " - " + Wallet.getPrivateKeyAtIndexURL64(iwk2, addressNumber));
            
            log.info("III - public key for index " +addressNumber + " - " + iwk3.getPublicKeyAtIndexURL64(addressNumber));
            log.info("III - private key for index " +addressNumber + " - " + Wallet.getPrivateKeyAtIndexURL64(iwk3, addressNumber));
            
            KeyBean walletInfo = Wallet.getWalletInfo(walletName, password);
            log.info("recovery words: " + walletInfo.getWords());
            
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
            log.info("Transaction verification result ");
            log.info(verifyTransaction);
            
            String sendTransaction = Wallet.sendTransaction(tb);
            log.info("Transaction sent response: " + sendTransaction);
            
            String searchText = "Lorem ipsum";
            boolean dumpTx = true;
            log.info("Search parameter: " + searchText);
            String searchTransactions = Wallet.searchTransactions(searchText, dumpTx, 5);
            log.info("generic search results: " + searchTransactions);
            
            
            String message = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit";
            log.info("exact search for string: " + message + " on field: " + GlobalConstants.ExactSearchField.message.name());
            String exactSearchTransactions = Wallet.exactSearchTransactions(message, GlobalConstants.ExactSearchField.message, dumpTx, 10);
            log.info("exact search results: " + exactSearchTransactions);
            
            //returns last mined block
            String blockHistory = Wallet.getBlockHistory(-1, -1, 1);
            log.info("Last generated block: " + blockHistory);
            
            String block = Wallet.getBlock(0, 1, "");
            log.info("block at epoch 0, slot 1: " + block);
            
            
            String addressBalance = Wallet.addressBalance(iwk.getPublicKeyAtIndexURL64(addressNumber));
            log.info("balance of address: " + iwk.getPublicKeyAtIndexURL64(addressNumber) + " : " + addressBalance);
            assertTrue(true);
    }

}
