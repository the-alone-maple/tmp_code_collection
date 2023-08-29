package main

import (
	"bytes"
	"encoding/binary"
	"fmt"
	"net"
)

type ISCSIPDU struct {
	OpCode    byte
	Length    uint32
	// 其他字段根据需要添加
}

func main() {
	// 创建TCP连接
	conn, err := net.Dial("tcp", "iscsi_server_ip:port")
	if err != nil {
		fmt.Println("Error connecting:", err)
		return
	}
	defer conn.Close()

	var buffer bytes.Buffer

	for {
		// 读取数据流
		data := make([]byte, 4096)
		n, err := conn.Read(data)
		if err != nil {
			fmt.Println("Error reading data:", err)
			return
		}

		// 将读取的数据添加到缓冲区
		buffer.Write(data[:n])

		// 解析报文
		for buffer.Len() >= 48 { // iSCSI header 长度为 48 字节
			// 读取 iSCSI 报文长度
			var length uint32
			if err := binary.Read(&buffer, binary.BigEndian, &length); err != nil {
				fmt.Println("Error reading length:", err)
				return
			}

			// 检查是否完整的报文
			if buffer.Len() < int(length) {
				break
			}

			// 读取完整报文
			payload := make([]byte, length)
			if _, err := buffer.Read(payload); err != nil {
				fmt.Println("Error reading payload:", err)
				return
			}

			// 解析报文
			iscsiPDU := ISCSIPDU{
				OpCode: payload[0],
				Length: length,
			}

			// 在这里可以根据 OpCode 进一步处理不同类型的报文
			fmt.Printf("Received iSCSI PDU: OpCode=%d, Length=%d\n", iscsiPDU.OpCode, iscsiPDU.Length)
		}
	}
}
