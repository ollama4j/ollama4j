Understanding publishing:
https://dzone.com/articles/how-to-publish-artifacts-to-maven-central

GPG Signing setup

## GPG Setup

https://central.sonatype.org/publish/requirements/gpg/#listing-keys

### Steps

- Create key: `gpg --gen-key` and then list keys to verify: `gpg --list-keys`
- Distributing Your Public Key:

```
gpg --keyserver pool.sks-keyservers.net --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98
gpg --keyserver pgp.key-server.io --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98
gpg --keyserver keyserver.ubuntu.com --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98
gpg --keyserver pgp.mit.edu --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98
gpg --keyserver keys.gnupg.net --send-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98
```

- Now other people can import your public key from the key server to their local
  machines: `gpg --keyserver keyserver.ubuntu.com --recv-keys CA925CD6C9E8D064FF05B4728190C4130ABA0F98`

Export for later use:

```shell
gpg --armor --export-secret-keys 88AA0C903A513340A0F3094326257A6F6F5F24A9 > ~/ollama4j/mvn-publish/private.gpg
```

## Maven publish

https://central.sonatype.org/publish/publish-maven/

### Steps