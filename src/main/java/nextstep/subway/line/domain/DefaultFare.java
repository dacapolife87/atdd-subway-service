package nextstep.subway.line.domain;

import java.util.Arrays;

public enum DefaultFare {
    ADULT(0, 0, 0, 0),
    TEENAGER(13, 19, 20, 350),
    CHILD(6, 13, 50, 350);

    private final int minAge;
    private final int maxAge;
    private final int discountRate;
    private final int discountBaseFare;

    DefaultFare(int minAge, int maxAge, int discountRate, int discountBaseFare) {
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.discountRate = discountRate;
        this.discountBaseFare = discountBaseFare;
    }

    public int fare() {
        return ((1250 - discountBaseFare) * (100 - discountRate)) / 100;
    }

    public static DefaultFare of(Integer age) {
        if (age == null) {
            return ADULT;
        }

        return Arrays.stream(values())
                .filter(defaultFare -> defaultFare.minAge <= age && defaultFare.maxAge > age)
                .findFirst()
                .orElse(ADULT);
    }
}