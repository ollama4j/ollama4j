GPG Signing setup

## GPG Setup

https://central.sonatype.org/publish/requirements/gpg/#listing-keys

### Steps

- Create key: `gpg --gen-key` and then list keys to verify: `gpg --list-keys`
- Distributing Your Public
  Key: `gpg --keyserver keyserver.ubuntu.com --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98`
- Now other people can import your public key from the key server to their local
  machines: `gpg --keyserver keyserver.ubuntu.com --recv-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98`

## Maven publish

https://central.sonatype.org/publish/publish-maven/

### Steps