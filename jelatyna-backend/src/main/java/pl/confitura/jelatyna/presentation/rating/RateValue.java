package pl.confitura.jelatyna.presentation.rating;

public enum RateValue {
    AWESOME(5),
    GREAT(4),
    IT_WAS_FINE(3),
    BAD(2),
    TERRIBLE(1);

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
