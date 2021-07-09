package com.infoworks.lab.services;

import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerRepository;
import com.infoworks.lab.services.impl.PassengerService;
import com.infoworks.lab.webapp.config.TestJPAConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = {TestJPAConfig.class})
public class PassengerServiceUnitTest {

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this);
    }

    @Mock
    PassengerRepository repository;

    @InjectMocks
    PassengerService service;

    @Test
    public void happyPathTest(){
        //Defining Mock Object:
        Passenger aPassenger = new Passenger("Towhid", Gender.MALE, 36);
        when(repository.save(any(Passenger.class))).thenReturn(aPassenger);

        //Call controller to make the save:
        Passenger nPassenger = service.add(aPassenger);

        //Verify:
        assertNotNull(nPassenger);
        assertNotNull(nPassenger.getId());
        assertEquals("Towhid", nPassenger.getName());
    }
}
