package io.github.jeff_zou.proxy.proxy;

import java.time.Duration;

public class CallbackCacheConfig {

    private int cacheMaxSize;

    private Duration cacheExpire;

    public int getCacheMaxSize() {
        return cacheMaxSize;
    }

    public Duration getCacheExpire() {
        return cacheExpire;
    }

    private CallbackCacheConfig(int cacheMaxSize, Duration cacheExpire) {
        this.cacheMaxSize = cacheMaxSize;
        this.cacheExpire = cacheExpire;
    }

    public static class Builder {
        private int cacheMaxSize = 20000;

        private int cacheExpire = 2000;

        public Builder setCacheMaxSize(int cacheMaxSize) {
            this.cacheMaxSize = cacheMaxSize;
            return this;
        }

        public Builder setCacheExpire(int cacheExpire) {
            this.cacheExpire = cacheExpire;
            return this;
        }

        public CallbackCacheConfig build() {
            return new CallbackCacheConfig(cacheMaxSize, Duration.ofMillis(cacheExpire));
        }
    }
}
