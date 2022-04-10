package pl.confitura.jelatyna.user.parsonalagenda;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pl.confitura.jelatyna.BaseIntegrationTest;
import pl.confitura.jelatyna.agenda.AgendaEntry;
import pl.confitura.jelatyna.agenda.AgendaUtils;
import pl.confitura.jelatyna.agenda.PresentationUtils;
import pl.confitura.jelatyna.agenda.UserUtils;
import pl.confitura.jelatyna.user.User;

import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class PersonalAgendaControllerTest extends BaseIntegrationTest {

    @Autowired
    AgendaUtils agendaUtils;
    @Autowired
    UserUtils userUtils;
    @Autowired
    PresentationUtils presentationUtils;

    private User user;
    private Map<String, AgendaEntry> entriesByPresentationTitle;

    @BeforeEach
    public void setUp() {
        user = userUtils.createUser("user");

        List<AgendaEntry> agenda = agendaUtils.createAgenda(
                new String[]{"", "room1", "room2"},
                new String[]{"10-11", "presentation1", "presentation2"},
                new String[]{"11-12", "presentation3"},
                new String[]{"12-13", "presentation4", "presentation5"},
                new String[]{"13-14", "presentation6"}
        );

        entriesByPresentationTitle = agenda.stream()
                .peek(it->log.info(String.valueOf(it)))
                .collect(
                        toMap(
                                it -> it.getPresentation().getTitle(),
                                identity()
                        ));
    }

    @Test
    @Transactional
    public void personalAgendaShouldContainAllRoomsEntries() throws Exception {

        // when user checks personal agenda
        mockMvc.perform(get("/users/" + user.getId() + "/personalAgenda"))

                //then it should contain slots taking part in all rooms
                .andExpect(jsonPath("$._embedded.agendaEntries", hasSize(2)))
                .andExpect(jsonPath("$._embedded.agendaEntries[0].presentation.title").value("presentation3"))
                .andExpect(jsonPath("$._embedded.agendaEntries[0].timeSlotLabel").value("11-12"))
                .andExpect(jsonPath("$._embedded.agendaEntries[1].presentation.title").value("presentation6"))
                .andExpect(jsonPath("$._embedded.agendaEntries[1].timeSlotLabel").value("13-14"));

    }


    @Test
    @Transactional
    public void personalAgendaShouldContainChosenPresentation() throws Exception {

        // when user adds presentations to personal agenda
        addToPersonalAgenda(entriesByPresentationTitle.get("presentation1"));
        addToPersonalAgenda(entriesByPresentationTitle.get("presentation5"));

        //then personal agenda should contain added presentations
        mockMvc.perform(get("/users/" + user.getId() + "/personalAgenda"))
                .andExpect(jsonPath("$._embedded.agendaEntries", hasSize(4)))
                .andExpect(jsonPath("$._embedded.agendaEntries[0].presentation.title").value("presentation1"))
                .andExpect(jsonPath("$._embedded.agendaEntries[0].timeSlotLabel").value("10-11"))
                .andExpect(jsonPath("$._embedded.agendaEntries[1].presentation.title").value("presentation3"))
                .andExpect(jsonPath("$._embedded.agendaEntries[1].timeSlotLabel").value("11-12"))
                .andExpect(jsonPath("$._embedded.agendaEntries[2].presentation.title").value("presentation5"))
                .andExpect(jsonPath("$._embedded.agendaEntries[2].timeSlotLabel").value("12-13"))
                .andExpect(jsonPath("$._embedded.agendaEntries[3].presentation.title").value("presentation6"))
                .andExpect(jsonPath("$._embedded.agendaEntries[3].timeSlotLabel").value("13-14"));

    }

    @Test
    @Transactional()
    public void personalAgendaShouldContainOnlyOneEntryForGivenTimeSlot() throws Exception {
        //given user has presentation in first time slot
        addToPersonalAgenda(entriesByPresentationTitle.get("presentation1"));

        // when user adds presentations to personal agenda with same time slot
        addToPersonalAgenda(entriesByPresentationTitle.get("presentation2"));

        //then personal agenda should contain only latest added presentations
        mockMvc.perform(get("/users/" + user.getId() + "/personalAgenda"))
                .andExpect(jsonPath("$._embedded.agendaEntries", hasSize(3)))
                .andExpect(jsonPath("$._embedded.agendaEntries[0].presentation.title").value("presentation2"))
                .andExpect(jsonPath("$._embedded.agendaEntries[0].timeSlotLabel").value("10-11"))
                .andExpect(jsonPath("$._embedded.agendaEntries[1].presentation.title").value("presentation3"))
                .andExpect(jsonPath("$._embedded.agendaEntries[1].timeSlotLabel").value("11-12"))
                .andExpect(jsonPath("$._embedded.agendaEntries[2].presentation.title").value("presentation6"))
                .andExpect(jsonPath("$._embedded.agendaEntries[2].timeSlotLabel").value("13-14"));

    }

    private void addToPersonalAgenda(AgendaEntry agendaEntry) throws Exception {
        mockMvc.perform(post("/users/" + user.getId() + "/personalAgenda")
                .content("{\"agendaEntryId\": \"" + agendaEntry.getId() + "\"}")
                .contentType(HAL_JSON))
                .andExpect(status().is2xxSuccessful());
    }

}