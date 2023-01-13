/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package io.takamaka.takamaka.core.wallet.examples;

import io.takamaka.wallet.InstanceWalletKeyStoreBCED25519;
import io.takamaka.wallet.InstanceWalletKeystoreInterface;
import io.takamaka.wallet.exceptions.UnlockWalletException;
import io.takamaka.wallet.exceptions.WalletException;
import io.takamaka.wallet.utils.BuilderITB;

/**
 *
 * @author giovanni
 */
public class TakamakaCoreWalletExamples {

    public static void main(String[] args) throws UnlockWalletException, WalletException {
        System.out.println("Hello World!");
        InstanceWalletKeystoreInterface myWalletOne = null;
        myWalletOne = new InstanceWalletKeyStoreBCED25519("myWalletOne");
        String myWalletOnePubKeyZero = myWalletOne.getPublicKeyAtIndexURL64(0);
        System.out.println("My public key at index 0");
        System.out.println(myWalletOnePubKeyZero);
    }
}
