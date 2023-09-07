package main

import (
	"fmt"
	"os"

	"github.com/spf13/viper"
)

type AppConfig struct {
	ServerAddress string
	ServerPort    int
}

func main() {
	if len(os.Args) < 4 {
		fmt.Println("Usage: program <config-name> <server-address> <server-port>")
		return
	}

	configName := os.Args[1]
	serverAddress := os.Args[2]
	serverPort := os.Args[3]

	viper.SetConfigType("yaml") // 配置文件类型为 YAML

	// 创建新的 Viper 实例
	config := viper.New()

	// 设置配置文件名称
	config.SetConfigName(configName)

	// 设置配置文件的内容
	config.Set("ServerAddress", serverAddress)
	config.Set("ServerPort", serverPort)

	// 设置配置文件保存路径
	config.AddConfigPath(".") // 可以根据需要修改路径

	// 保存配置文件
	err := config.WriteConfigAs(fmt.Sprintf("%s.yaml", configName))
	if err != nil {
		panic(fmt.Errorf("Error writing %s.yaml: %s", configName, err))
	}

	fmt.Printf("Generated %s.yaml\n", configName)

	// 读取配置文件
	loadedConfig := loadConfig(fmt.Sprintf("%s.yaml", configName))
	fmt.Printf("Config %s:\n", configName)
	fmt.Printf("Server Address: %s\n", loadedConfig.ServerAddress)
	fmt.Printf("Server Port: %s\n", loadedConfig.ServerPort)
}

func loadConfig(configFile string) AppConfig {
	viper.SetConfigFile(configFile)
	err := viper.ReadInConfig()
	if err != nil {
		panic(fmt.Errorf("Fatal error config file: %s", err))
	}

	var config AppConfig
	err = viper.Unmarshal(&config)
	if err != nil {
		panic(fmt.Errorf("Fatal error unmarshal config: %s", err))
	}

	return config
}
