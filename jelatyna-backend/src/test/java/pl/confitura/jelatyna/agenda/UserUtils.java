package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;
import pl.confitura.jelatyna.user.dto.User;
import pl.confitura.jelatyna.user.UserFacade;

import static java.time.LocalDateTime.now;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserFacade userRepository;
    private final ParticipationRepository participationRepository;
    private final VoucherService voucherService;


    public User createUser(String title) {
        return userRepository.createUser(new User().setId(title).setName(title));
    }

}
