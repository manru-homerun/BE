package com.manruhomerun.yadan.travel.error.exception;

import com.manruhomerun.yadan.global.error.BaseException;
import com.manruhomerun.yadan.travel.error.TravelErrorCode;

public class TravelDataExportException extends BaseException {
    public TravelDataExportException(Throwable cause) {
        super(TravelErrorCode.TRAVEL_DATA_EXPORT_FAILED, "여행 데이터 파일 생성에 실패했습니다.");
        initCause(cause);
    }
}
