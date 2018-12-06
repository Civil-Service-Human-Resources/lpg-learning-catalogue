package uk.gov.cslearning.catalogue.service.record;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import uk.gov.cslearning.catalogue.service.record.model.Booking;

import java.util.ArrayList;
import java.util.List;

public class LearnerRecordServiceTest {

    private RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private RequestEntityFactory requestEntityFactory = Mockito.mock(RequestEntityFactory.class);
    private String eventUrlFormat = "testhost:9000/event/%s";

    private LearnerRecordService learnerRecordService = new LearnerRecordService(restTemplate, requestEntityFactory, eventUrlFormat);

    @Test
    public void shouldReturnEventBookings(){
        String eventId = "eventId";

        List<Booking> bookings = new ArrayList<>();

        Booking booking1 = new Booking();
        Booking booking2 = new Booking();

        booking1.setId(1);
        booking2.setId(2);

        bookings.add(booking1);
        bookings.add(booking2);

        RequestEntity requestEntity = Mockito.mock(RequestEntity.class);
        ResponseEntity<List<Booking>> responseEntity = Mockito.mock(ResponseEntity.class);

        Mockito.when(requestEntityFactory.createGetRequest(String.format(eventUrlFormat, eventId))).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Booking>>(){})).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(bookings);

        Assert.assertEquals(learnerRecordService.getEventBookings(eventId), bookings);
    }
}
