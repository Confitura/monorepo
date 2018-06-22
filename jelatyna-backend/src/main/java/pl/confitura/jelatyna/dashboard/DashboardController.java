package pl.confitura.jelatyna.dashboard;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.confitura.jelatyna.registration.ParticipationData;
import pl.confitura.jelatyna.registration.QParticipationData;
import pl.confitura.jelatyna.registration.voucher.QVoucher;
import pl.confitura.jelatyna.user.QUser;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@RestController
@RequestMapping("dashboard")
@PreAuthorize("@security.isAdmin()")
class DashboardController {
    private final EntityManager entityManager;

    @GetMapping("tshirts")
    public Object getTShirtSizes() {
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        QParticipationData participationData = QParticipationData.participationData;

        List<Tuple> rawData = query.select(participationData.size, participationData.gender)
                .from(participationData)
                .where(participationData.voucher.isNotNull())
                .fetch();

        Map<String, TShirtStat> map = rawData
                .stream()
                .map(it -> new TShirtStat(it.get(0, String.class), it.get(1, String.class)))
                .collect(groupingBy(
                        TShirtStat::getSize,
                        reducing(
                                new TShirtStat(),
                                TShirtStat::add
                        )));


        List<Object[]> list = map.values().stream().map(TShirtStat::toArray).collect(toList());
        list.add(0, new Object[]{"size", "male", "female"});
        return list;
    }

    @GetMapping("meal")
    public Object getMealStats() {
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        QParticipationData participationData = QParticipationData.participationData;

        List<Tuple> tuples = query.select(participationData.mealOption, participationData.mealOption.count())
                .from(participationData)
                .groupBy(participationData.mealOption)
                .where(participationData.voucher.isNotNull())
                .fetch();


        List<Object[]> list = tuples.stream().map(Tuple::toArray).collect(toList());
        list.add(0, new Object[]{"option", "count"});
        return list;
    }

    @GetMapping("vouchers")
    public Object getVoucherStats() {

        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        QParticipationData participationData = QParticipationData.participationData;
        QVoucher voucher = QVoucher.voucher;
        List<Tuple> fetch = query.select(participationData.id, voucher.id)
                .from(participationData)
                .rightJoin(participationData.voucher, voucher)
                .fetch();

        Optional<VoucherStat> stat = fetch.stream().map(VoucherStat::new).reduce(VoucherStat::add);
        if (stat.isPresent()) {
            VoucherStat voucherStat = stat.get();
            return new Object[]{
                    new Object[]{"option", "count"},
                    new Object[]{"used", voucherStat.getUsed()},
                    new Object[]{"notUsed", voucherStat.getNotUsed()}
            };
        }
        return new Object[0];

    }

    @GetMapping("registration")
    public Object getRegistrationStats() {

        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        QUser user = QUser.user;
        QParticipationData participationData = QParticipationData.participationData;
        QVoucher voucher = QVoucher.voucher;

        List<Tuple> tuples = query.select(user.id, participationData.id, voucher.id)
                .from(user)
                .leftJoin(user.participationData, participationData)
                .leftJoin(participationData.voucher, voucher)
                .fetch();

        Optional<RegistrationStat> stat = tuples.stream()
                .map(RegistrationStat::new)
                .reduce(RegistrationStat::add);

        if (stat.isPresent()) {
            RegistrationStat voucherStat = stat.get();
            return new Object[]{
                    new Object[]{"option", "count"},
                    new Object[]{"with voucher", voucherStat.getWithVoucher()},
                    new Object[]{"without voucher", voucherStat.getWithoutVoucher()},
                    new Object[]{"not registered", voucherStat.getNotRegistered()}
            };
        }
        return new Object[0];
    }


    @GetMapping("arrivals")
    Object arrivals() {
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        QParticipationData participationData = QParticipationData.participationData;

        List<LocalDateTime> fetch = query.select(participationData.arrivalDate)
                .from(participationData)
                .where(participationData.arrivalDate.isNotNull())
                .orderBy(participationData.arrivalDate.asc())
                .fetch();

        AtomicInteger atomicInteger = new AtomicInteger();
        List<Object[]> data = fetch.stream()
                .map(it -> new Object[]{it.toString(), atomicInteger.incrementAndGet()})
                .collect(toList());

        data.add(0, new Object[]{"date", "total"});
        return data;
    }

    @GetMapping("registrations")
    Object registrations() {
        JPAQuery<?> query = new JPAQuery<Void>(entityManager);
        QParticipationData participationData = QParticipationData.participationData;

        List<Instant> fetch = query.select(participationData.createdDate)
                .from(participationData)
                .where(participationData.voucher.isNotNull())
                .orderBy(participationData.createdDate.asc())
                .fetch();

        AtomicInteger atomicInteger = new AtomicInteger();
        List<Object[]> data = fetch.stream()
                .map(it -> new Object[]{it.toString(), atomicInteger.incrementAndGet()})
                .collect(toList());

        data.add(0, new Object[]{"date", "total"});
        return data;
    }
}
