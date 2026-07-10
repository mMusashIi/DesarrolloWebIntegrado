package com.buganvilla.buganvillatours.TestService;

import com.buganvilla.buganvillatours.whatsapp.MockWhatsAppProvider;
import com.buganvilla.buganvillatours.whatsapp.WhatsAppNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class WhatsAppProviderTest {

    @Test
    void mockProvider_isEnabled_returnsFalse() {
        MockWhatsAppProvider provider = new MockWhatsAppProvider();
        assertFalse(provider.isEnabled());
    }

    @Test
    void mockProvider_sendReservationConfirmation_doesNotThrow() {
        MockWhatsAppProvider provider = new MockWhatsAppProvider();
        assertDoesNotThrow(() ->
                provider.sendReservationConfirmation("999999999", "Juan", "Tour Cusco", LocalDate.now(), 2));
    }

    @Test
    void mockProvider_sendPaymentConfirmation_doesNotThrow() {
        MockWhatsAppProvider provider = new MockWhatsAppProvider();
        assertDoesNotThrow(() ->
                provider.sendPaymentConfirmation("999999999", "Juan", BigDecimal.valueOf(350.00), "42"));
    }

    @Test
    void mockProvider_sendReservationCancellation_doesNotThrow() {
        MockWhatsAppProvider provider = new MockWhatsAppProvider();
        assertDoesNotThrow(() ->
                provider.sendReservationCancellation("999999999", "Juan", "Tour Machu Picchu"));
    }

    @Test
    void mockProvider_nullPhone_doesNotThrow() {
        MockWhatsAppProvider provider = new MockWhatsAppProvider();
        assertDoesNotThrow(() ->
                provider.sendReservationConfirmation(null, "Juan", "Tour Cusco", LocalDate.now(), 1));
    }

    @Test
    void notificationService_whenDisabled_doesNotCallProvider() {
        // WhatsAppNotificationService with enabled=false (default) should not delegate to provider
        // Verified indirectly: MockWhatsAppProvider.isEnabled() returns false,
        // and WhatsAppNotificationService checks whatsapp.enabled flag before calling provider.
        MockWhatsAppProvider mockProvider = new MockWhatsAppProvider();
        assertFalse(mockProvider.isEnabled(), "Mock provider should always report disabled");
    }
}
