package com.emdata.messagewarningscore.common.exception;

/**
 * METAR报文异常
 *
 * @author pupengfei
 * @version 1.0
 * @date 2020/9/10 14:07
 */
public class MetarParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MetarParseException() {
        super();
    }

    public MetarParseException(String msg) {
        super(msg);
    }

    public MetarParseException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

}