package pl.confitura.jelatyna.agenda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.infrastructure.security.SecurityHelper;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DayControllerTest extends BaseIntegrationTest {

    @Autowired
    private DayRepository dayRepository;

    private Day day1;
    private Day day2;

    @BeforeEach
    public void setUp() {
        // Clear existing days by finding and deleting them
        SecurityHelper.asAdmin();
        Iterable<Day> existingDays = dayRepository.findAll();
        for (Day day : existingDays) {
            dayRepository.deleteById(day.getId());
        }

        day1 = new Day()
                .setLabel("Day 1")
                .setDate(LocalDate.of(2025, 9, 1))
                .setDisplayOrder(1);

        day2 = new Day()
                .setLabel("Day 2")
                .setDate(LocalDate.of(2025, 9, 2))
                .setDisplayOrder(2);

        day1 = dayRepository.save(day1);
        day2 = dayRepository.save(day2);
    }

    @Test
    void shouldReturnAllDays() throws Exception {
        mockMvc.perform(get("/days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].label", is("Day 1")))
                .andExpect(jsonPath("$[1].label", is("Day 2")));
    }

    @Test
    void shouldReturnDayById() throws Exception {
        mockMvc.perform(get("/days/" + day1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.label", is("Day 1")))
                .andExpect(jsonPath("$.displayOrder", is(1)));
    }

    @Test
    void shouldReturn404WhenDayNotFound() throws Exception {
        mockMvc.perform(get("/days/non-existent-id"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateDay() throws Exception {
        String dayJson = "{\"label\":\"Day 3\",\"date\":\"2025-09-03\",\"displayOrder\":3}";

        mockMvc.perform(post("/days")
                .contentType(MediaType.APPLICATION_JSON)
                .content(dayJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.label", is("Day 3")))
                .andExpect(jsonPath("$.displayOrder", is(3)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteDay() throws Exception {
        mockMvc.perform(delete("/days/" + day1.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].label", is("Day 2")));
    }
}