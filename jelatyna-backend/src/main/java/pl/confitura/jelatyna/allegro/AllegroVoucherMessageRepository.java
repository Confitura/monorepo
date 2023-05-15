package pl.confitura.jelatyna.allegro;

import org.springframework.data.repository.Repository;

import java.util.List;

public interface AllegroVoucherMessageRepository extends Repository<AllegroVoucherMessage, String> {

    List<AllegroVoucherMessage> findAllBySendDateIsNull();

    void save(AllegroVoucherMessage messageToSend);
}
