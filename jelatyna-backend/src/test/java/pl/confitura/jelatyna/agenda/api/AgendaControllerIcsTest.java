package pl.confitura.jelatyna.agenda.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.confitura.jelatyna.agenda.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class AgendaControllerIcsTest {


    @Mock
    IcalExportService icalExportService;

    @InjectMocks
    AgendaController controller;

    MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void returnsCalendar() throws Exception {
        when(icalExportService.generateIcs()).thenReturn("BEGIN:VCALENDAR\nEND:VCALENDAR".getBytes());

        mvc.perform(get("/agenda/ical"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/calendar"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("attachment")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("BEGIN:VCALENDAR")));
    }

    @Test
    void returnsCalendarForSubscriptionWithoutAttachment() throws Exception {
        when(icalExportService.generateIcs()).thenReturn("BEGIN:VCALENDAR\nEND:VCALENDAR".getBytes());

        mvc.perform(get("/agenda/ical/subscribe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/calendar"))
                .andExpect(header().doesNotExist("Content-Disposition"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("BEGIN:VCALENDAR")));
    }
}
