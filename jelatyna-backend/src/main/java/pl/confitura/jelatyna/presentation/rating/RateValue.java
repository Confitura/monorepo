package pl.confitura.jelatyna.presentation.rating;

public enum RateValue {
    AWESOME(5),
    GREAT(4),
    IT_WAS_FINE(3),
    BAD(2),
    TERRIBLE(1);

    double numericValue;

    RateValue(double numericValue) {
        this.numericValue = numericValue;
    }
}
