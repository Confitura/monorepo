package pl.confitura.jelatyna.dashboard;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import pl.confitura.jelatyna.dashboard.model.SubmittedStats;
import pl.confitura.jelatyna.dashboard.model.UsersStats;
import pl.confitura.jelatyna.user.User;

public interface DashboardRepository extends Repository<User, String> {


    @Query("SELECT new pl.confitura.jelatyna.dashboard.model.UsersStats(" +
           "COUNT(u), " +
           "SUM(CASE WHEN u.isAdmin = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.isVolunteer = true THEN 1 ELSE 0 END) )" +
           "FROM User u")
    UsersStats getUserStats();

    @Query("SELECT new pl.confitura.jelatyna.dashboard.model.SubmittedStats(" +
           "COUNT(p), " +
           "SUM(CASE WHEN p.workshop = false THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN p.workshop = true THEN 1 ELSE 0 END) )" +
           "FROM Presentation p")
    SubmittedStats getSubmittedStats();


}
