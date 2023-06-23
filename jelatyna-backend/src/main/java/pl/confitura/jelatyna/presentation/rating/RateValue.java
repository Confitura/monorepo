package pl.confitura.jelatyna.presentation.rating;

public enum RateValue {
    AWESOME(4),
    GREAT(3),
    IT_WAS_FINE(2),
    BAD(1),
    TERRIBLE(0);

    int numericValue;

    RateValue(int numericValue) {
        this.numericValue = numericValue;
    }

    public static RateValue from(int value) {
        for (RateValue rateValue : values()) {
            if (rateValue.numericValue == value) {
                return rateValue;
            }
        }
        return null;
    }

    public int getNumericValue() {
        return numericValue;
    }
}
