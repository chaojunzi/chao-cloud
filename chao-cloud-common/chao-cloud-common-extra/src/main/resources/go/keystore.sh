#!/bin/bash

#1.生成密钥对
keytool -genkeypair -storetype JKS -keystore "privateKeys.keystore" -storepass "public_password1234" -alias privateKey -keyalg RSA -sigalg SHA1withRSA -keysize 1024 -keypass "private_abcdef123456" -startdate 2020/12/11 -validity 3650 -dname "CN=GroupClient, OU=GoldTax,O=Aisino, L=Beijing, S=Beijing, C=CH"

#2.导出包含公钥的证书
keytool -exportcert -alias "privateKey" -storetype JKS -keystore "privateKeys.keystore" -storepass public_password1234 -file "certfile.cer"

#3.导入包含公钥的证书到公钥store
keytool -import -alias publicCert -file "certfile.cer" -storetype JKS -keystore "publicCerts.keystore" -storepass "public_password1234"