package pl.confitura.jelatyna.dashboard;

import lombok.Data;

import java.util.Objects;

@Data
class TShirtStat {
    String size;
    Integer male = 0;
    Integer female = 0;

    TShirtStat(String size, String gender) {
        this.size = size;
        if (Objects.equals(gender, "M")) {
            male++;
        } else {
            female++;
        }
    }

    private TShirtStat(String size, Integer male, Integer female) {
        this.size = size;
        this.male = male;
        this.female = female;
    }

    TShirtStat() {
        System.out.println();
    }

    public TShirtStat add(TShirtStat that) {
        return new TShirtStat(that.size,
                this.male + that.male,
                this.female + that.female);
    }

    public Object[] toArray() {
        return new Object[]{size, male, female};
    }
}
