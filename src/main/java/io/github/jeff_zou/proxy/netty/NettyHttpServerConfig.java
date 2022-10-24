package io.github.jeff_zou.proxy.netty;

/**
 * @Author: Jeff Zou @Date: 2022/10/19 15:27
 */
public class NettyHttpServerConfig {

    private int port;

    private int bossThreadNum;

    private int workerThreadNum;

    public int getBossThreadNum() {
        return bossThreadNum;
    }

    public int getWorkerThreadNum() {
        return workerThreadNum;
    }

    public int getPort() {
        return port;
    }

    private NettyHttpServerConfig(int bossThreadNum, int workerThreadNum, int port) {
        this.bossThreadNum = bossThreadNum;
        this.workerThreadNum = workerThreadNum;
        this.port = port;
    }

    public static class Builder {
        private int bossThreadNum = 2;

        private int workerThreadNum = 8;

        private int port = 8080;

        public Builder setBossThreadNum(int bossThreadNum) {
            this.bossThreadNum = bossThreadNum;
            return this;
        }

        public Builder setWorkerThreadNum(int workerThreadNum) {
            this.workerThreadNum = workerThreadNum;
            return this;
        }

        public Builder setPort(int port) {
            this.port = port;
            return this;
        }

        public NettyHttpServerConfig build() {
            return new NettyHttpServerConfig(bossThreadNum, workerThreadNum, port);
        }
    }
}
