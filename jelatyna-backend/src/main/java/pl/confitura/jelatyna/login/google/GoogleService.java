package pl.confitura.jelatyna.login.google;

import pl.confitura.jelatyna.user.User;

interface GoogleService {
    String getAuthorizationUrl();

    User getUserFor(String code);
}
