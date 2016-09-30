package com.accelerator.framework.exception;

public interface ErrorCode {

    /** 自定义错误起始值 */
    int CUSTOM_ERROR_START = 100;

    /** 请求成功执行 */
    int SUCCEED = 0;

    /** 服务器内部错误 */
    int SERVER_ERROR = 1;

    /** 非法参数 */
    int ILLEGAL_PARAM = 2;

    /** 必须参数不存在 */
    int MISS_PARAM = 3;

    /** 状态错误 */
    int ILLEGAL_STATE = 4;

    /** 不支持此操作 */
    int UN_SUPPORTED = 5;

    /** 没有权限 {0} */
    int NO_PERMISSON = 6;

    /** {0}对应的{1}对象不存在 */
    int ENTITY_NOT_FOUND = 50;

    /** {0}对应的{1}对象已存在 */
    int ENTITY_ALREADY_EXIST = 51;

    /** 参数或属性验证失败 */
    int VALIDATE_ERROR = 60;

    /** 参数不在指定范围内 */
    int RANGE_ERROR = 61;

    /** 不匹配正则表达式 */
    int PATTERN_NOT_MATCH = 62;

}
