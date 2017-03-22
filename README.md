## Oracle Service
An example of an Oracle Service mechanism for the Ethereum network

### What is an Oracle Service
An Oracle Service can be defined as a **data carrier for decentralized apps** (definition from [oraclize.it](http://www.oraclize.it/ "oraclize.it"))

### How it works
As Smart Contracts cannot fetch data from the outside world on their own, Oracle Services come into play and do it for them.

This project is meant to describe the approach a developer would take in implementing their own Oracle Service for the Ethereum Network.

#### Mechanism
1. Emit HTTP request descriptors **RD** (and the requesting contract address **CA**) as Ethereum events
2. Listen for these events using a filter with a separate program (on a physical machine)
3. Perform the HTTP request **RD** and send the result back to **CA** by calling an agreed-upon function (`__callback` in our case)

### Project Contents
1. **Smart Contracts** in the ```contracts``` directory
	- ```PSOraclize.sol``` - responsible for emitting/logging the user request
	- ```PSOraclizeAPI.sol``` - API describing the ```usingPSOraclize``` contract
	- ```TestContract.sol``` - An example contract using our Oracle Service
2. **Java (Maven) project** - *The service that actually makes the HTTP requests*

### Build
The Java project can be compiled by running
```
mvn clean install
```
(given that you have maven installed and it is on your **PATH**)

The Solidity contracts can be compiled using your favorite Solidity compiler and should be deployed to the Ethereum network.

Note that the ```TestContract``` requires the deployed address of the ```PSOraclize``` contract in its constructor.

I personally use [Truffle](http://truffleframework.com/ "Truffle") for managing and deploying the contracts. The following deployment script comes in handy when deploying contracts that depend on another contracts address:

```js
deployer.deploy(PSOraclize).then(function() {
  var oraclize = PSOraclize.deployed().then(function (instance) {
    console.log("Oraclize is deployed at: " + instance.address);
    deployer.deploy(TestContract, instance.address);
  });
});
```

### Usage
```
mvn exec:java -Dexec.args "[RPC-URL] [CALLBACK-PRIVATE-KEY] [ORACLZE-CONTRACT-ADDRESS]"

mvn exec:java -Dexec.args "http://localhost:8545/ 80c2e663c2e55f795b0054c636042257277409a66bf043d9817cc1fa0639694d 0xf74cd4f8fea7c9cd6224747950c75f2a2cd36761"
```
1. ```RPC-URL``` - URL of the RPC endpoint
2. ```CALLBACK-PRIVATE-KEY``` - Private key of the callback account
3. ```ORACLZE-CONTRACT-ADDRESS``` - PSOraclize contract address
