
package io.takamaka.takamaka.core.wallet.beans;

import java.io.Serializable;

/**
 *
 * @author admin
 */
public class WalletBean implements Serializable {

    private String walletName;
    private String walletPassword;
    private String walletCypher;
    private int addressNumber;

    public WalletBean() {
        
    }
    
    public WalletBean(String walletName, String walletPassword, String walletCypher, int addressNumber) {
        this.walletName = walletName;
        this.walletPassword = walletPassword;
        this.walletCypher = walletCypher;
        this.addressNumber = addressNumber;
    }

    public int getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(int addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getWalletPassword() {
        return walletPassword;
    }

    public void setWalletPassword(String walletPassword) {
        this.walletPassword = walletPassword;
    }

    public String getWalletCypher() {
        return walletCypher;
    }

    public void setWalletCypher(String walletCypher) {
        this.walletCypher = walletCypher;
    }

}
