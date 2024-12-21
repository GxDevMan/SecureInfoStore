# Secure Information Store Project


This JavaFX project is a complete rewrite of my previous repository, 
accountLogging. 
It builds upon the 
original by incorporating numerous new features, with a focus on
improving maintainability and readability through the use of design patterns.

Protect your account information with AES-256 encryption, 
where the encryption key is securely stored in a file. 
This key also safeguards sensitive data through the text 
entries feature. Titles and textual information are encrypted 
using AES-GCM for robust security, while tags employ AES-ECB to 
enable efficient searchability. The app’s encryptor window allows you to 
generate multiple keys for securing different messages. 
These keys can encrypt text, which can then be saved to text files or other storage media.
Additionally, the project offers file encryption, providing a reliable way to securely store your files.


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
    - Text Tags (AES - 256 ECB) <- exact searches only
  

- Unencrypted Fields
  - Platform Name
  - Platform Logo (byte Array)


- Encryptor/Decryptor Window 

  - Text Encryptor/Decryptor Tab
      - Encryption/Decryption of Text via a loaded key
      - Single Block or Multi Block Treatment of Text
      - QR Code generation from input and output
      - Key Creation

  - File Encryptor/Decryptor Tab
    - Encryption/Decryption of Files via a loaded key
    - Key Creation



- Exporting of Accounts
  - Json Encrypted or Unencrypted
  - Txt Encrypted or Unencrypted

- Importing of Accounts via json

## Requirements

-   Minimum JDK 14

## Feature Ideas

-  Password Derived key via hash function
