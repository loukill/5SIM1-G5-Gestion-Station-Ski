package tn.esprit.spring.SubscriptionManagement;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.GestionStationSkiApplication;
import tn.esprit.spring.entities.Skier;
import tn.esprit.spring.entities.Subscription;
import tn.esprit.spring.entities.TypeSubscription;
import tn.esprit.spring.repositories.ISkierRepository;
import tn.esprit.spring.repositories.ISubscriptionRepository;
import tn.esprit.spring.services.ISubscriptionServices;
import tn.esprit.spring.services.SubscriptionServicesImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // For unit tests with mocks
public class SubscriptionManagementTest {

    @Mock
    ISubscriptionRepository subscriptionRepository;

    @Mock
    ISkierRepository skierRepository;

    @InjectMocks
    SubscriptionServicesImpl subscriptionServices;

    Subscription subscription1 = new Subscription(1L, LocalDate.of(2023, 1, 1), null, 1000f, TypeSubscription.ANNUAL);

    @Test
    void addSubscription() {
        // Arrange
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.of(2023, 1, 1));
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(1000f);

        Subscription savedSubscription = new Subscription();
        savedSubscription.setNumSub(1L);
        savedSubscription.setStartDate(LocalDate.of(2023, 1, 1));
        savedSubscription.setEndDate(LocalDate.of(2024, 1, 1)); // One year later
        savedSubscription.setTypeSub(TypeSubscription.ANNUAL);
        savedSubscription.setPrice(1000f);

        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(savedSubscription);

        // Act
        Subscription result = subscriptionServices.addSubscription(subscription);

        // Assert
        assertNotNull(result);
        assertEquals(savedSubscription.getEndDate(), result.getEndDate());
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
    }

    @Test
    void updateSubscription() {
        // Arrange
        Subscription subscription = new Subscription();
        subscription.setNumSub(1L);
        subscription.setStartDate(LocalDate.of(2023, 1, 1));
        subscription.setEndDate(LocalDate.of(2023, 12, 31));
        subscription.setTypeSub(TypeSubscription.MONTHLY);
        subscription.setPrice(100f);

        when(subscriptionRepository.save(subscription)).thenReturn(subscription);

        // Act
        Subscription result = subscriptionServices.updateSubscription(subscription);

        // Assert
        assertNotNull(result);
        assertEquals(subscription.getNumSub(), result.getNumSub());
        verify(subscriptionRepository, times(1)).save(subscription);
    }

    @Test
    void retrieveSubscription() {
        // Arrange
        Long numSubscription = 1L;
        when(subscriptionRepository.findById(numSubscription)).thenReturn(Optional.of(subscription1));

        // Act
        Subscription retrievedSubscription = subscriptionServices.retrieveSubscriptionById(numSubscription);

        // Assert
        assertNotNull(retrievedSubscription);
        assertEquals(subscription1.getNumSub(), retrievedSubscription.getNumSub());
        verify(subscriptionRepository, times(1)).findById(numSubscription);
    }

    @Test
    void getSubscriptionByType() {
        // Arrange
        TypeSubscription typeSubscription = TypeSubscription.MONTHLY;
        Set<Subscription> subscriptions = new HashSet<>(Arrays.asList(
                new Subscription(2L, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 3, 1), 100f, typeSubscription),
                new Subscription(3L, LocalDate.of(2023, 3, 1), LocalDate.of(2023, 4, 1), 100f, typeSubscription)
        ));

        when(subscriptionRepository.findByTypeSubOrderByStartDateAsc(typeSubscription)).thenReturn(subscriptions);

        // Act
        Set<Subscription> result = subscriptionServices.getSubscriptionByType(typeSubscription);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subscriptionRepository, times(1)).findByTypeSubOrderByStartDateAsc(typeSubscription);
    }

    @Test
    void retrieveSubscriptionsByDates() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 3, 1);

        List<Subscription> subscriptions = Arrays.asList(
                new Subscription(4L, LocalDate.of(2023, 1, 15), LocalDate.of(2023, 2, 15), 100f, TypeSubscription.MONTHLY),
                new Subscription(5L, LocalDate.of(2023, 2, 15), LocalDate.of(2023, 3, 15), 100f, TypeSubscription.MONTHLY)
        );

        when(subscriptionRepository.getSubscriptionsByStartDateBetween(startDate, endDate)).thenReturn(subscriptions);

        // Act
        List<Subscription> result = subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(subscriptionRepository, times(1)).getSubscriptionsByStartDateBetween(startDate, endDate);
    }
}

@SpringBootTest(classes = {GestionStationSkiApplication.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(SpringExtension.class)
class SubscriptionServicesImplJUnitTest {

    @Autowired
    ISubscriptionServices subscriptionServices;

    static Long subscriptionId;

    @Test
    @Order(1)
    void testAddSubscription() {
        // Arrange
        Subscription subscription = new Subscription();
        subscription.setStartDate(LocalDate.of(2023, 1, 1));
        subscription.setTypeSub(TypeSubscription.ANNUAL);
        subscription.setPrice(1000f);

        // Act
        Subscription savedSubscription = subscriptionServices.addSubscription(subscription);
        subscriptionId = savedSubscription.getNumSub();

        // Assert
        assertNotNull(savedSubscription);
        assertEquals(LocalDate.of(2024, 1, 1), savedSubscription.getEndDate()); // End date should be one year later
    }

    @Test
    @Order(2)
    void testRetrieveSubscriptionById() {
        // Act
        Subscription subscription = subscriptionServices.retrieveSubscriptionById(subscriptionId);

        // Assert
        assertNotNull(subscription);
        assertEquals(subscriptionId, subscription.getNumSub());
    }

    @Test
    @Order(3)
    void testGetSubscriptionByType() {
        // Act
        Set<Subscription> subscriptions = subscriptionServices.getSubscriptionByType(TypeSubscription.ANNUAL);

        // Assert
        assertNotNull(subscriptions);
        assertFalse(subscriptions.isEmpty());
    }

    @Test
    @Order(4)
    void testRetrieveSubscriptionsByDates() {
        // Arrange
        LocalDate startDate = LocalDate.of(2022, 12, 31);
        LocalDate endDate = LocalDate.of(2023, 1, 2);

        // Act
        List<Subscription> subscriptions = subscriptionServices.retrieveSubscriptionsByDates(startDate, endDate);

        // Assert
        assertNotNull(subscriptions);
        assertFalse(subscriptions.isEmpty());
    }

    @Test
    @Order(5)
    void testUpdateSubscription() {
        // Arrange
        Subscription subscription = subscriptionServices.retrieveSubscriptionById(subscriptionId);
        subscription.setPrice(1100f);

        // Act
        Subscription updatedSubscription = subscriptionServices.updateSubscription(subscription);

        // Assert
        assertNotNull(updatedSubscription);
        assertEquals(1100f, updatedSubscription.getPrice());
    }
}
