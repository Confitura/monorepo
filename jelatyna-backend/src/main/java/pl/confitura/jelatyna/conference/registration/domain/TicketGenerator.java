package pl.confitura.jelatyna.conference.registration.domain;

import org.springframework.stereotype.Service;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

@Service
public class TicketGenerator {

    public byte[] generateFor(String token) {
        return QRCode
                .from(token)
                .to(ImageType.PNG)
                .withSize(250, 250)
                .stream().toByteArray();
    }
}
