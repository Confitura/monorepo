package pl.confitura.jelatyna.dashboard;

import com.querydsl.core.Tuple;
import lombok.Data;

@Data
class VoucherStat {
    private int used;
    private int notUsed;
    private int total;

    VoucherStat(Tuple tuple) {
        Object[] objects = tuple.toArray();
        if (objects[0] != null) {
            used++;
        } else {
            notUsed++;
        }
        total++;
    }

    private VoucherStat(int used, int notUsed, int total) {
        this.used = used;
        this.notUsed = notUsed;
        this.total = total;
    }

    public VoucherStat add(VoucherStat that) {
        return new VoucherStat(
                this.used + that.used,
                this.notUsed + that.notUsed,
                this.total + that.total
        );
    }
}
