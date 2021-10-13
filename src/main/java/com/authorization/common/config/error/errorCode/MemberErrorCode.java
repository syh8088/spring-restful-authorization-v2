package com.authorization.common.config.error.errorCode;

public enum MemberErrorCode implements ErrorCode {

    NOT_EXIST_USERNAME_OR_PASSWORD("MEC0001"),
    AUTHENTICATION_FAILED("MEC0002"),
    INVALID_TOKEN("MEC0003"),
    NOT_FOUND_USERNAME("MEC0004"),
    NOT_FOUND_PROVIDER("MEC0005"),
    NOT_EXIST_REDIRECT_SOCIAL_AUTHORIZATION_PAGE_PARAMETER("MEC0006"),
    SOCIAL_GET_ME_ERROR("MEC0007"),
    NOT_EXIST_AUTHENTICATE_SOCIAL_LOGIN_PARAMETER("MEC0008");

    private final String code;

    MemberErrorCode(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return "error.member." + code;
    }
}
