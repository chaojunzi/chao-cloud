package main

import (
	"encoding/json"
	"encoding/base64"
	"encoding/pem"
    "net"
    "os"
    "os/exec"
    "strings"
    "crypto/rand"
    "crypto/rsa"
    "crypto/x509"
    "bytes"
    "errors"
)
//定义一个结构体 首字母大写
type OsInfo struct {
    Ip []string `json:"ipAddress"`
    Mac []string `json:"macAddress"`
    CpuSerial string `json:"cpuSerial"`
    MainBoardSerial string `json:"mainBoardSerial"`
}
//获取mac地址
func getMacAddrs() (macAddrs []string) {
    netInterfaces, err := net.Interfaces()
    if err != nil {
        panic(err)
    }

    for _, netInterface := range netInterfaces {
        macAddr := netInterface.HardwareAddr.String()
        if len(macAddr) == 0 {
            continue
        }

        macAddrs = append(macAddrs, macAddr)
    }
    return macAddrs
}
//获取ip
func getIPs() (ips []string) {

    interfaceAddr, err := net.InterfaceAddrs()
    if err != nil {
       panic(err)
    }

    for _, address := range interfaceAddr {
        ipNet, isValidIpNet := address.(*net.IPNet)
        if isValidIpNet && !ipNet.IP.IsLoopback() {
            if ipNet.IP.To4() != nil {
                ips = append(ips, ipNet.IP.String())
            }
        }
    }
    return ips
}
//运行shell脚本
func runShell(cmd string) string{
    result, err := exec.Command("/bin/bash", "-c", cmd).Output()
    if err != nil {
       // panic(err)
       return "";
    }
    return strings.TrimSpace(string(result))
}

//RSA加密- 公钥加密
var pubKey = []byte{45, 45, 45, 45, 45, 66, 69, 71, 73, 78, 32, 80, 85, 66, 76, 73, 67, 32, 75, 69, 89, 45, 45, 45, 45, 45, 13, 10, 77, 73, 71, 102, 77, 65, 48, 71, 67, 83, 113, 71, 83, 73, 98, 51, 68, 81, 69, 66, 65, 81, 85, 65, 65, 52, 71, 78, 65, 68, 67, 66, 105, 81, 75, 66, 103, 81, 67, 85, 97, 52, 57, 105, 111, 112, 118, 90, 117, 115, 109, 72, 113, 104, 112, 89, 68, 49, 113, 101, 68, 48, 117, 84, 116, 43, 50, 81, 116, 55, 43, 110, 108, 100, 57, 119, 50, 106, 49, 84, 103, 108, 115, 56, 54, 114, 81, 75, 116, 65, 107, 71, 117, 122, 72, 57, 88, 100, 99, 88, 51, 53, 98, 111, 85, 113, 102, 105, 122, 121, 76, 97, 90, 104, 111, 82, 80, 114, 83, 51, 67, 55, 86, 74, 54, 116, 49, 98, 53, 114, 73, 43, 48, 69, 120, 47, 77, 86, 69, 66, 77, 54, 85, 73, 112, 74, 68, 97, 56, 83, 112, 79, 65, 49, 81, 48, 119, 119, 88, 104, 106, 52, 108, 75, 120, 105, 47, 110, 76, 116, 43, 117, 78, 106, 109, 56, 100, 68, 112, 47, 47, 82, 90, 118, 70, 110, 79, 77, 121, 57, 100, 116, 118, 116, 51, 83, 49, 81, 106, 78, 114, 118, 97, 103, 112, 65, 83, 117, 105, 81, 73, 68, 65, 81, 65, 66, 13, 10, 45, 45, 45, 45, 45, 69, 78, 68, 32, 80, 85, 66, 76, 73, 67, 32, 75, 69, 89, 45, 45, 45, 45, 45}
//rsa 分段加密
func rsaEncryptBlock(data []byte) string {
	block, _ := pem.Decode(pubKey)
    if block == nil {
    	panic(errors.New("public key error"))
    }
    pubInterface, err := x509.ParsePKIXPublicKey(block.Bytes)
    if err != nil {
         panic(err);
    }
    publicKey := pubInterface.(*rsa.PublicKey)
    
    partLen := publicKey.N.BitLen() / 8 - 11
    chunks := split(data, partLen)
    buffer := bytes.NewBufferString("")
    for _, chunk := range chunks {
        bytes, err := rsa.EncryptPKCS1v15(rand.Reader, publicKey, chunk)
        if err != nil {
            panic(err);
        }
        buffer.Write(bytes)
    }
    return base64.StdEncoding.EncodeToString(buffer.Bytes())
}
//截取字节
func split(buf []byte, lim int) [][]byte {
    var chunk []byte
    chunks := make([][]byte, 0, len(buf)/lim+1)
    for len(buf) >= lim {
        chunk, buf = buf[:lim], buf[lim:]
        chunks = append(chunks, chunk)
    }
    if len(buf) > 0 {
        chunks = append(chunks, buf[:len(buf)])
    }
    return chunks
}
//执行
func main() {
	ips :=  getIPs();
	macAddrs :=  getMacAddrs();
	cpuSerial := runShell("dmidecode -t processor | grep 'ID' | awk -F ':' '{print $2}' | head -n 1");
	mainBoardSerial := runShell("dmidecode | grep 'Serial Number' | awk -F ':' '{print $2}' | head -n 1");
    
	jsonByte, _ := json.Marshal(OsInfo{ ips , macAddrs, cpuSerial , mainBoardSerial})
	
    //rsa 加密
    result := rsaEncryptBlock(jsonByte)
    
    //输出到文件
	f,err := os.Create("key-license");
    if err != nil {
     	panic(err)
    }
    f.WriteString(result) 
    f.Close()
}