# Secure Information Store Project


This JavaFX project is a complete rewrite of my previous project named "accountLogging" repository,
it expands on the previous project by including various additional features. The aim of this
project is to make it easier to maintain and read by using design patterns. 


Secure your accounts Information with an AES 256 key saved to a file. In addition to this, 
secure sensitive information using the same key with the text entries feature. 
Titles and Text Information is secured using GCM mode while tags are secured using ECB mode for
searchability. The encryptor window of the app allows you to create different
keys to secure different messages. Use the key to encrypt text which you can then save to
text files or any other medium.

Logo used is made by Maniprasanth

QR Code Generation by Nayuki -> https://github.com/nayuki

## Design Patterns Used

- Factory
- Mediator
- Singleton
- Observer


## Features


- Platform Logos can be saved
- All Fields can generate QR Code for other devices to just copy.
- Encryption of the Following

    - Username (AES - 256 GCM)
    - Password (AES - 256 GCM)
    - Email (AES - 256 GCM)
    - Text Entry (AES - 256 GCM)
    - Text Title (AES - 256 GCM)
    - Text Tags (AES - 256 ECB)
  

- Unencrypted Fields
  - Platform Name
  - Platform Logo (byte Array)


- Text Encryptor Window
  - Encryption/Decryption of Text via a loaded key
  - Single Block or Multi Block Treatment of Text
  - QR Code generation from input and output
  - Key Creation


- Exporting of Accounts
  - Json Encrypted or Unencrypted
  - Txt Encrypted or Unencrypted

## Requirements

-   Minimum JDK 14

## Feature Ideas

-   File Encryptor