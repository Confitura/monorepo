package pl.confitura.jelatyna.agenda;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.ParticipationRepository;
import pl.confitura.jelatyna.registration.voucher.Voucher;
import pl.confitura.jelatyna.registration.voucher.VoucherService;
import pl.confitura.jelatyna.user.User;
import pl.confitura.jelatyna.user.UserRepository;

import static java.time.LocalDateTime.now;

@Component
@AllArgsConstructor
public class UserUtils {

    private final UserRepository userRepository;
    private final ParticipationRepository participationRepository;
    private final VoucherService voucherService;


    public User createUser(String name) {
        return userRepository.save(new User().setId(name).setName(name));
    }

    public User markArrived(User user) {
        Voucher voucher = voucherService.generateVoucher("");
        ParticipationData data = participationRepository.save(new ParticipationData().setArrivalDate(now()).setVoucher(voucher));
        return userRepository.save(user.setParticipationData(data));
    }

    public void createAdmin(String name) {
        userRepository.save(new User()
                .setId(name + "'s id")
                .setName(name)
                .setBio(name + "'s bio")
                .setTwitter(name + "'s twitter")
                .setGithub(name + "'s github")
                .setWww(name + "'s www")
                .setPhoto(name + ".png")
                .setAdmin(true));

    }
}
