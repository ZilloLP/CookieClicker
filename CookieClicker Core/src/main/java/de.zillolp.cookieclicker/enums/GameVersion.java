package de.zillolp.cookieclicker.enums;

public enum GameVersion {
    v1_20_R1(20, 1),
    v1_20_R2(20, 2),
    v1_20_R3(20, 4),
    v1_20_R4(20, 6),
    v1_21_R1(21, 1),
    v1_21_R2(21, 3),
    v1_21_R3(21, 4),
    v1_21_R4(21, 5),
    v1_21_R5(21, 8),
    v1_21_R7(21, 10),
    v1_21_R8(21, 11),
    v26_R2(26, 2);

    private final int versionNumber;
    private final int subVersionNumber;

    GameVersion(int versionNumber, int subVersionNumber) {
        this.versionNumber = versionNumber;
        this.subVersionNumber = subVersionNumber;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public int getSubVersionNumber() {
        return subVersionNumber;
    }
}
