/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.takamaka.takamaka.core.wallet.examples.support;

/**
 *
 * @author iris.dimni
 */
public final class GlobalConstants {
    
    public static final String MAIN_NET_ENDPOINT = "https://dev.takamaka.io/api/V2/nodeapi";
    public static final String TEST_NET_ENDPOINT = "https://dev.takamaka.io/api/V2/testapi";
    public static final String FAST_NET_ENDPOINT = "https://dev.takamaka.io/api/V2/fastapi";
    public static final String SEND_TRANSACTION_API = "/transaction";
    public static final String VERIFY_TRANSACTION_API = "/verifytransaction";
    public static final String ADDRESS_BALANCE_API = "/balanceof";
    public static final String SEARCH_TRANSACTION_API = "/listtransactions";
    public static final String EXACT_SEARCH_TRANSACTION_API = "/exactsearch";
    public static final String WALLET_IDENTICON_API = "/avatar";
    public static final String BLOCK_HISTORY_API = "/blockhistory";
    public static final String GET_BLOCK_API = "/getblock";
    
    public static enum ExactSearchField {

        from,
        to,
        message,
        transactionhash,
        sith,
        UNDEFINED
    }
    
}
