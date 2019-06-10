package pl.confitura.jelatyna.dashboard;

import com.querydsl.core.Tuple;
import lombok.Data;

@Data
class RegistrationStat {
    private int registered = 0;
    private int notRegistered = 0;

    RegistrationStat(Tuple tuple) {
        Object[] array = tuple.toArray();
        if (array[0] != null) {
            registered++;
        } else {
            notRegistered++;
        }
    }

    public RegistrationStat(int registered, int notRegistered) {
        this.registered = registered;
        this.notRegistered = notRegistered;
    }

    public RegistrationStat add(RegistrationStat that) {
        return new RegistrationStat(
                this.registered + that.registered,
                this.notRegistered + that.notRegistered
        );
    }
}
