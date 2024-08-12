package ru.andreymerkulov.cbrcurrency.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.BindMode;
import ru.andreymerkulov.cbrcurrency.dto.CbrResponse;
import ru.andreymerkulov.cbrcurrency.dto.CbrValute;
import ru.andreymerkulov.cbrcurrency.model.Currency;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRate;
import ru.andreymerkulov.cbrcurrency.model.CurrencyRateId;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRateRepository;
import ru.andreymerkulov.cbrcurrency.repository.CurrencyRepository;
import ru.andreymerkulov.cbrcurrency.scheduler.CurrencyScheduler;

import java.io.File;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@RequiredArgsConstructor
@MockBean(CurrencyScheduler.class)
public class CurrencyServiceIntegrationTest {

    private static final PostgresTestContainer POSTGRES_CONTAINER = PostgresTestContainer.getInstance();

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        POSTGRES_CONTAINER.start();
        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    }

    @AfterAll
    static void stopContainer() {
        POSTGRES_CONTAINER.stop();
    }

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CurrencyRateRepository currencyRateRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSaveCurrencyDataIntegration() throws Exception {

        File testDataFile = new File("src/test/resources/cbr-response.json");
        CbrResponse cbrResponse = objectMapper.readValue(testDataFile, CbrResponse.class);

        currencyService.saveCurrencyData(cbrResponse);

        List<Currency> currencies = currencyRepository.findAll();
        List<CurrencyRate> currencyRates = currencyRateRepository.findAll();

        assertEquals(2, currencies.size());
        assertEquals(2, currencyRates.size());

        assertTrue(currencies.stream().anyMatch(
                c -> c.getCharCode().equals("AUD")));

        CbrValute valute = cbrResponse.getValute().values().stream().findFirst().orElse(null);
        CurrencyRateId audId = new CurrencyRateId();
        audId.setNumCode(valute.getNumCode());
        audId.setRateDate(cbrResponse.getDate());
        CurrencyRate audRate = currencyRateRepository
                                    .findById(audId)
                                    .orElse(null);
        assertNotNull(audRate);
        assertEquals( BigDecimal.valueOf(valute.getValue())
                    , audRate.getValue()
        );
    }

}