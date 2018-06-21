package pl.confitura.jelatyna.dashboard;

import com.querydsl.core.Tuple;
import lombok.Data;

@Data
class RegistrationStat {
    private int withVoucher = 0;
    private int withoutVoucher = 0;
    private int notRegistered = 0;

    RegistrationStat(Tuple tuple) {
        Object[] array = tuple.toArray();
        if (array[2] != null) {
            withVoucher++;
        } else if (array[1] != null) {
            withoutVoucher++;
        } else {
            notRegistered++;
        }
    }

    public RegistrationStat(int withVoucher, int withoutVoucher, int notRegistered) {
        this.withVoucher = withVoucher;
        this.withoutVoucher = withoutVoucher;
        this.notRegistered = notRegistered;
    }

    public RegistrationStat add(RegistrationStat that) {
        return new RegistrationStat(
                this.withVoucher + that.withVoucher,
                this.withoutVoucher + that.withoutVoucher,
                this.notRegistered + that.notRegistered
        );
    }
}
