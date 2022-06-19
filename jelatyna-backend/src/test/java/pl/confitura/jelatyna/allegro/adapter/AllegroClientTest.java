package pl.confitura.jelatyna.allegro.adapter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVWriter;
import pl.confitura.jelatyna.allegro.adapter.dto.OrderEvent;
import pl.confitura.jelatyna.allegro.adapter.dto.OrderEvents;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;

class AllegroClientTest {

    private final Scanner in = new Scanner(System.in, "UTF-8");

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        new AllegroClientTest().shouldGetOrders();
    }

    void shouldGetOrders() throws IOException, ExecutionException, InterruptedException {

        AllegroProperties authorizationContext = AllegroProperties.builder()
                .clientId("TODO")
                .clientSecret("TODO")
                .api("https://api.allegro.pl")
                .uri("https://allegro.pl")
                .callback("http://localhost:3000/allegro/callback")
                .build();

        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        AllegroClient client = new AllegroClient(authorizationContext, objectMapper);

        redirectToLogin(client);
        String code = readFromInput("code >>");
        String stateSecret = readFromInput("state >>");

        client.authorize(code, stateSecret);


        Map<String, String> params = new HashMap<>();
        OrderEvents orderEvents = new OrderEvents();
        List<OrderEvent> allEvents = new ArrayList<>();
        do {
            orderEvents = client.getOrderEvents(params);
            allEvents.addAll(orderEvents.getEvents());

            if (orderEvents.hasEvents()) {
                OrderEvent lastEvent = orderEvents.getLastEvent();
                params.put("from", lastEvent.getId());
            } else {
                break;
            }
        } while (orderEvents.hasEvents());

        System.out.println("all = " + allEvents);
        exportToCsv(allEvents);
    }

    private void exportToCsv(List<OrderEvent> allEvents) throws IOException {
        long orderCount = 0;
        long totalQuantity = 0;
        File file = File.createTempFile("allegro-events", ".csv");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(file));
        for (OrderEvent event : allEvents) {
            orderCount += event.getOrderedCountChange();
            totalQuantity += event.getOrderedQuantityChange();
            String[] nextLine = {event.getId(), event.getType(), event.getOccurredAt(), String.valueOf(orderCount), String.valueOf(totalQuantity), String.valueOf(event.getOrderedQuantityChange())};
            csvWriter.writeNext(nextLine);
        }
        csvWriter.flush();
        System.out.println(file);
    }

    private void redirectToLogin(AllegroClient client) {
        String authorizationUrl = client.getAuthorizationUrl();
        System.out.println("visit to auhenticate: " + authorizationUrl);
    }

    private String readFromInput(String prompt) {
        System.out.print(prompt);
        String code = in.nextLine();
        System.out.println();
        return code;
    }

}