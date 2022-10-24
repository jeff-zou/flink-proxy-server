package io.github.jeff_zou.proxy.common;


public enum ResultCode {
    SUCCESS(200, "ok"),

    FAILURE(400, "异常请求"),

    PARAM_MISS(400, "参数缺失"),

    PARAM_TYPE_ERROR(400, "参数类型不匹配"),

    PARAM_BIND_ERROR(400, "参数异常"),

    PARAM_VALID_ERROR(400, "参数校验失败"),

    UN_AUTHORIZED(401, "缺少权限"),

    NOT_FOUND(404, "未找到资源"),

    METHOD_NOT_SUPPORTED(405, "不支持该方法"),

    REQ_REJECT(403, "请求被拒绝"),

    INTERNAL_SERVER_ERROR(500, "服务内部错误，请联系管理员");

    final int code;

    final String msg;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
