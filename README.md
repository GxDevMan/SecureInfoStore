## Secure Information Store Project

This project is a complete rewrite of my previous project named "Account Logging",
it expands on the previous project by including various additional features.

Logo used is made by Maniprasanth


### Design Patterns Used

- Factory
- Mediator
- Singleton


### Features


- Platform Logos can be saved


- Encryption of the Following

  - Encrypted Fields
    - Username (AES - 256 GCM)
    - Password (AES - 256 GCM)
    - email (AES - 256 GCM)
    - Text Entry (AES - 256 GCM)
    - Text Title (AES - 256 GCM)
    - Text Tags (AES - 256 ECB)
  

  - Unencrypted Fields
    - Platform
    - Platform Logo (byte Array)


- Text Encryptor Window
  - Encryption/Decryption of Text via a loaded key


- Exporting of Accounts
  - Json Encrypted or Unencrypted
  - Txt Encrypted or Unencrypted

### Requirements

-   Minimum JDK 14