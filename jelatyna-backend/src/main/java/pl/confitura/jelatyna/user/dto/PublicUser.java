package pl.confitura.jelatyna.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PublicUser {
    private String id;
    private String name;
    private String bio;
    private String twitter;
    private String github;
    private String www;
    private String photo;

}
