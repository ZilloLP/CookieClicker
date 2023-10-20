package de.zillolp.cookieclicker.enums;

public enum Setup {
    CLICKER,
    TIME_WALL,
    ALLTIME_WALL;

    private boolean sign;
    private int setupNumber;

    public boolean hasSign() {
        return sign;
    }

    public int getSetupNumber() {
        return setupNumber;
    }

    public void setSetupNumber(int setupNumber) {
        this.setupNumber = setupNumber;
    }

    public void setSign(boolean sign) {
        this.sign = sign;
    }
}
