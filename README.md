# Takamaka Core Wallet Examples
This project aims to explain the use of the Takamaka Core Wallet library.

BCQTESLAPSSC1Round2 will be used instead of BCQTESLAPSSC1Round1 since the latter is still an experimental suite

The following transactions are allowed on the takamaka blockchain:
- [PAY](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitPay.java)
> A blockchain pay refers to the process of making a payment using a blockchain network. A payment on a blockchain network is essentially a digital transaction that transfers ownership of a digital asset such as cryptocurrency. In a blockchain pay, the sender initiates the transaction by providing their digital wallet address and the recipient's address, along with the amount of digital assets to be transferred. The transaction is then broadcast to the network, where it is verified and processed by the nodes in the network. Once the transaction is confirmed, the digital assets are transferred to the recipient's digital wallet, and the transaction is recorded on the blockchain register. Blockchain payments are typically faster and cheaper than traditional banking transfers and have the potential to make financial transactions more efficient and accessible. It's worth noting that Blockchain payments are not limited to cryptocurrency, It could be used for other use cases like making micropayments for digital content, for instance, and also in other industries like Supply Chain Management, where it can be used to track and trace the origin of goods.
- [STAKE](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitStake.java)
>A blockchain stake refers to the amount ofryptocurrency a person holds and is willing to pledge asollateral in order to participate in the consensus processof a specific blockchain network. The term is most commonlyused in the context of proof-of-stake (PoS) blockchainsystems, where individuals can \"stake\" their coins tovalidate transactions and secure the network, rather thanusing computer power to solve complex mathematical puzzles(as in proof-of-work systems). The more coins a personstakes, the greater their chances of being selected tovalidate a block of transactions and earn a reward.
- [STAKE UNDO](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitStakeUndo.java)
>A blockchain stake undo refers to the process of removing astake from a blockchain network. This typically involves aperson who has previously \"staked\" their coins in aproof-of-stake (PoS) blockchain system choosing to\"unstake\" them and withdraw them from the network.The process of unstaking often involves a waiting period,during which the coins are locked and cannot be used ortransferred. This waiting period is in place to ensure thatthe network remains secure and to prevent potential attacks.Once the waiting period is over, the staked coins can bewithdrawn and used as normal.It's worth noting that unstaking process can be differentdepends on the specific blockchainnetwork and its protocols.
- [REGISTER MAIN](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitRegisterMain.java)
- [REGISTER OVERFLOW](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitRegisterOverflow.java)
- [DEREGISTER MAIN](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitDeRegisterMain.java)
- [DEREGISTER OVERFLOW](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitDeRegisterOverflow.java)
- [ASSIGN OVERFLOW](src/main/java/io/takamaka/takamaka/core/wallet/examples/SubmitAssignOverflow.java)
- unassign_overflow
- blob (image, text, rich text, hash)
