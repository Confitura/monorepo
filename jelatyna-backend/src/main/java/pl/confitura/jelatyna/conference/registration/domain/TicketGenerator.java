package pl.confitura.jelatyna.conference.registration.domain;

import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class TicketGenerator {

    public byte[] generateFor(String token) {
        return QRCode
                .from(token)
                .to(ImageType.PNG)
                .withSize(250, 250)
                .stream().toByteArray();
    }
}
