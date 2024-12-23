package com.leshkins.currencyrateservice.client;

import com.leshkins.currencyrateservice.dto.currency.FiatCurrencyRateDTO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class CurrencyApiClientTest {

    private MockWebServer mockWebServer;
    private CurrencyApiClient currencyApiClient;

    @BeforeEach
    void setUp() throws Exception {
        // Create and start the MockWebServer
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Create the WebClient to use the mock server
        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())  // Set base URL to MockWebServer
                .build();

        // Instantiate the client with the mock WebClient
        currencyApiClient = new CurrencyApiClient(webClient, "fake-secret-key");
    }

    @Test
    void testFetchFiatRates_Success() {
        // Set up a mock response from the server
        mockWebServer.enqueue(new MockResponse()
                .setBody("[{\"currency\":\"USD\", \"rate\":1.1}]")  // Simulated API response
                .addHeader("Content-Type", "application/json")
                .setResponseCode(HttpStatus.OK.value()));

        // Call the WebClient method we want to test
        Mono<FiatCurrencyRateDTO> response = currencyApiClient.fetchFiatRates().next();

        // Verify the response
        StepVerifier.create(response)
                .expectNextMatches(dto -> dto.getCurrency().equals("USD") && dto.getRate().equals(BigDecimal.valueOf(1.1)))
                .verifyComplete();
    }

    @Test
    void testFetchFiatRates_Error() {
        // Set up a mock response with an error status
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .addHeader("Content-Type", "application/json"));

        // Call the WebClient method we want to test
        Mono<FiatCurrencyRateDTO> response = currencyApiClient.fetchFiatRates().next();

        // Verify the error handling (fallback mechanism)
        StepVerifier.create(response)
                .expectComplete()
                .verify();
    }

    @AfterEach
    void tearDown() throws Exception {
        // Shut down the mock server after the tests
        mockWebServer.shutdown();
    }
}




