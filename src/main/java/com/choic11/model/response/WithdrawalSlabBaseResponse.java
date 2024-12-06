package com.choic11.model.response;

public class WithdrawalSlabBaseResponse extends BaseResponse {
    String INSTANT_WITHDRAWAL_IS_AVAILABLE;
    String INSTANT_WITHDRAWAL_ADMIN_MESSAGE;

    public WithdrawalSlabBaseResponse(int code, boolean error, String message, Object data, String INSTANT_WITHDRAWAL_IS_AVAILABLE, String INSTANT_WITHDRAWAL_ADMIN_MESSAGE) {
        super(code, error, message, data);
        this.INSTANT_WITHDRAWAL_IS_AVAILABLE = INSTANT_WITHDRAWAL_IS_AVAILABLE;
        this.INSTANT_WITHDRAWAL_ADMIN_MESSAGE = INSTANT_WITHDRAWAL_ADMIN_MESSAGE;
    }

    public String getINSTANT_WITHDRAWAL_IS_AVAILABLE() {
        return INSTANT_WITHDRAWAL_IS_AVAILABLE;
    }

    public void setINSTANT_WITHDRAWAL_IS_AVAILABLE(String INSTANT_WITHDRAWAL_IS_AVAILABLE) {
        this.INSTANT_WITHDRAWAL_IS_AVAILABLE = INSTANT_WITHDRAWAL_IS_AVAILABLE;
    }

    public String getINSTANT_WITHDRAWAL_ADMIN_MESSAGE() {
        return INSTANT_WITHDRAWAL_ADMIN_MESSAGE;
    }

    public void setINSTANT_WITHDRAWAL_ADMIN_MESSAGE(String INSTANT_WITHDRAWAL_ADMIN_MESSAGE) {
        this.INSTANT_WITHDRAWAL_ADMIN_MESSAGE = INSTANT_WITHDRAWAL_ADMIN_MESSAGE;
    }
}