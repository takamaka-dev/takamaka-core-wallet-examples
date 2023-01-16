/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.beans.KeyBean;
import io.takamaka.wallet.utils.DefaultInitParameters;
import io.takamaka.wallet.utils.FileHelper;
import io.takamaka.wallet.utils.FixedParameters;
import io.takamaka.wallet.utils.WalletHelper;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author giovanni
 */
@Slf4j
public class CreateNewWalletED25519 {

    public static void main(String[] args) throws Exception {
        //generic interface for all takamaka wallet
        InstanceWalletKeystoreInterface iwkED;
        //ed wallet creation with recovery based on 25 words from dictionary.
        String walletName = "my_ed_wallet_example";
        String recoveredWalletName = "my_recovered_ed_wallet_example";
        String walletPassword = "my_super_safe_password";
        String recoveredWalletPassword = "my_super_safe_password_for_recovered_wallet";
        iwkED = new InstanceWalletKeyStoreBCED25519(walletName, walletPassword);
        //If a wallet with the same name exists the instance will try to open it 
        //using the specified password.
        //From the single seed of iwk, 2^31 key pairs can be obtained.
        //Key generation is deterministic and seed-based.
        //To an external observer, a public key pair generated from the same
        //wallet is indistinguishable from a key pair generated from two 
        //different wallets.
        String publicKeyAtIndexZero = iwkED.getPublicKeyAtIndexURL64(0);
        String publicKeyAtIndexOne = iwkED.getPublicKeyAtIndexURL64(1);
        log.info("Publick key zero " + publicKeyAtIndexZero);
        log.info("Publick key one " + publicKeyAtIndexOne);
        Path walletPath = Paths.get(
                FileHelper.getDefaultWalletDirectoryPath().toString(), 
                walletName + DefaultInitParameters.WALLET_EXTENSION);
        KeyBean walletKeyBean = WalletHelper.readKeyFile(walletPath, walletPassword);
        //Within a KeyBean is stored all the information, in plain text, 
        //to be able to reconstruct a wallet.
        walletKeyBean.getAlgorithm();

        String internalName = recoveredWalletName + FixedParameters.USER_WALLETS_FILE_EXTENSION;
        log.info("the 25 words");
        log.info(walletKeyBean.getWords());
        log.info("Contains reference to the configuration to be used to "
                + "determine the algorithm to be applied to the seed and the "
                + "parameters required for key generation.");
        log.info(walletKeyBean.getCypher().name());
        log.info("wallet revision");
        log.info(walletKeyBean.getVersion());
        log.info("The 25 words are transformed to generate a string of "
                + "random characters used as an entropy source for key creation. "
                + "This string is not required for wallet retrieval. "
                + "It is saved to optimize the time to open an existing wallet. "
                + "This string can be used to reconstruct private keys. "
                + "This string does not allow recovery of the 25 words.");
        log.info(walletKeyBean.getSeed());
        log.info("The password is used to generate the AES256 seed with which to"
                + " encrypt the wallet. When you want to change the "
                + "password to the wallet, you do so by extracting the KeyBean,"
                + " which effectively removes the old AES encryption, and "
                + "proceeding to a new encryption with the "
                + "WalletHelper.importKeyFromWords(...) function. ");
        String[] splittedWordsList = walletKeyBean.getWords().split(" ");
        List<String> splittedWordsAsList = Arrays.asList(splittedWordsList);
        Path importKeyFromWords = WalletHelper.importKeyFromWords(
                splittedWordsAsList,
                FileHelper.getDefaultWalletDirectoryPath(),
                internalName,
                walletKeyBean.getCypher(),
                recoveredWalletPassword);
        log.info("the path where the restore wallet has been saved");
        log.info(importKeyFromWords.toString());
        log.info("The wallet is retrieved with an extension DIFFERENT from "
                + "that of a wallet created from scratch. To be able to open it, "
                + "it is necessary to change the extension from \"userWallet\" "
                + "to \"wallet.\"");
        Path reamedRecoveredWallet = Paths.get(
                importKeyFromWords.getParent().toString(),
                importKeyFromWords.getFileName().toString().split(".userWallet")[0] + ".wallet");
        FileHelper.rename(
                importKeyFromWords.toString(),
                reamedRecoveredWallet.toString(),
                Boolean.TRUE);
        log.info("open the restored wallet");
        InstanceWalletKeystoreInterface restoredIwkED = new InstanceWalletKeyStoreBCED25519(
                recoveredWalletName, 
                recoveredWalletPassword);
        log.info("orignal wallet key  " + iwkED.getPublicKeyAtIndexURL64(0));
        log.info("restored wallet key " + restoredIwkED.getPublicKeyAtIndexURL64(0));
        
        if(publicKeyAtIndexZero.equals(restoredIwkED.getPublicKeyAtIndexURL64(0))){
            log.info("the original public key match the restored");
            log.info("the restoring procedure has been successful");
        }

    }
}
