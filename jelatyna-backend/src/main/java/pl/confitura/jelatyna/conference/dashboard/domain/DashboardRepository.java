package pl.confitura.jelatyna.conference.dashboard.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pl.confitura.jelatyna.user.User;

public interface DashboardRepository extends Repository<User, String> {


    @Query("SELECT new pl.confitura.jelatyna.conference.dashboard.domain.UsersStats(" +
           "COUNT(u), " +
           "SUM(CASE WHEN u.isAdmin = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.isVolunteer = true THEN 1 ELSE 0 END) )" +
           "FROM User u")
    UsersStats getUserStats();

    // Admin pages show only the Current Edition (ADR-0004); Users are not per Edition.
    @Query("SELECT new pl.confitura.jelatyna.conference.dashboard.domain.SubmittedStats(" +
           "COUNT(p), " +
           "SUM(CASE WHEN p.workshop = false THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.workshop = true THEN 1 ELSE 0 END) )" +
           "FROM Presentation p WHERE p.edition.current = true")
    SubmittedStats getSubmittedStats();


}
