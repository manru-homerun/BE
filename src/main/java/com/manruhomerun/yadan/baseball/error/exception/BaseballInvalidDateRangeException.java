package com.manruhomerun.yadan.baseball.error.exception;

import com.manruhomerun.yadan.baseball.error.BaseballErrorCode;
import com.manruhomerun.yadan.global.error.BaseException;

public class BaseballInvalidDateRangeException extends BaseException {
    public BaseballInvalidDateRangeException() {
        super(BaseballErrorCode.BASEBALL_INVALID_DATE_RANGE,
                BaseballErrorCode.BASEBALL_INVALID_DATE_RANGE.getDefaultMessage());
    }
}
