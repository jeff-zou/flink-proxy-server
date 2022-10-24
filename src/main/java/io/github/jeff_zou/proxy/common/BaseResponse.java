package io.github.jeff_zou.proxy.common;

public class BaseResponse {
    private Object message;
    private ResultCode code;

    public Object getMessage() {
        return message;
    }

    public ResultCode getCode() {
        return code;
    }

    private BaseResponse(Object message, ResultCode code) {
        this.message = message;
        this.code = code;
    }

    public static BaseResponseBuilder builder() {
        return new BaseResponseBuilder();
    }

    /** BaseResponseBuilder. */
    public static class BaseResponseBuilder {
        private Object message;
        private ResultCode code = ResultCode.SUCCESS;

        public BaseResponseBuilder message(Object message) {
            this.message = message;
            return this;
        }

        public BaseResponseBuilder code(ResultCode code) {
            this.code = code;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(message, code);
        }
    }
}
