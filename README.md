## Overview
* This project is a sample to deploy Spring Cloud function to Azure function using Serveless Framework


## Requirement
* Jdk 1.8
* Maven 3.6.0
* Node >= v6.5.0 

## Azure Settings

* Install CLI <br>
https://docs.microsoft.com/cli/azure/install-azure-cli?view=azure-cli-latest

* Create Blob Storage <br>
https://docs.microsoft.com/azure/storage/blobs/storage-quickstart-blobs-cli

## Serverless Framework Settings

* Setting Unique ID
```sh
export AZURE_STORAGE_ACCOUNT-NAME=$AZURE_STORAGE_ACCOUNT
export AZURE_STORAGE_ACCOUNT-KEY=$AZURE_STORAGE_ACCESS_KEY
```

* Setting Unique ID
```sh
export SLS_ID=$(($RANDOM % 9999))
echo $SLS_ID
```

## Build/DEPLOY
```sh
./mvnw clean package
serverless deploy \
    --id $SLS_ID \ 
    --storage-account-name "${AZURE_STORAGE_ACCOUNT-NAME}" \ 
    --storage-account-key "${AZURE_STORAGE_ACCOUNT-KEY}" 
```

## Testing
```sh
curl -w '\n' -H 'Content-Type:application/json' -d '{"blobName":"image"}' \
 https://thumbnail-azure-$SLS_ID.azurewebsites.net/api/thumbnail?code={function key}

az storage blob download \
    --container-name slsstoragecontainer \
    --name image-thumbnail \
    --file image-thumbnail.png
```

## Cleaning
```sh
serverless remove \
    --id $SLS_ID \ 
    --storage-account-name "${AZURE_STORAGE_ACCOUNT-NAME}" \ 
    --storage-account-key "${AZURE_STORAGE_ACCOUNT-KEY}" 
```
