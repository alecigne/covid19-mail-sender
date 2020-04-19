package net.lecigne.coronamailsender.service;

import net.lecigne.coronamailsender.client.CoronaClient;
import net.lecigne.coronamailsender.dao.CoronaInfoDao;
import net.lecigne.coronamailsender.exception.DataAccessException;
import net.lecigne.coronamailsender.model.CoronaInfo;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoronaInfoServiceTest {

    @InjectMocks
    CoronaInfoService coronaInfoService;

    @Mock
    CoronaInfoDao coronaInfoDao;

    @Mock
    CoronaClient coronaClient;

    @Test
    void syncCoronaInfo_whenCountryMatches_ShouldStoreAndReturnInfo() {
        // Given
        String country = "france";
        CoronaInfo expectedCoronaInfo = CoronaInfo.builder().country(StringUtils.capitalize(country)).build();
        when(coronaClient.getCoronaInfo(country)).thenReturn(expectedCoronaInfo);

        // When
        Optional<CoronaInfo> actualCoronaInfo = coronaInfoService.syncCoronaInfo(country);

        // Then
        assertThat(actualCoronaInfo).isNotEmpty();
        assertThat(actualCoronaInfo.get()).isEqualTo(expectedCoronaInfo);
        verify(coronaInfoDao, times(1)).updateCoronaInfo(expectedCoronaInfo);
    }

    @Test
    void syncCoronaInfo_whenCountryDoesNotMatch_ShouldStoreNullAndReturnEmpty() {
        // Given
        String country = "france";
        when(coronaClient.getCoronaInfo(country)).thenReturn(CoronaInfo.builder().country("Germany").build());

        // When
        Optional<CoronaInfo> coronaInfo = coronaInfoService.syncCoronaInfo(country);

        // Then
        assertThat(coronaInfo).isEmpty();
        verify(coronaInfoDao, times(1)).updateCoronaInfo(null);
    }

    @Test
    void getCoronaInfo_whenDataAccesException_shouldReturnEmptyOptional() {
        // Given
        when(coronaInfoDao.getCoronaInfo()).thenThrow(new DataAccessException("error"));

        // When
        Optional<CoronaInfo> coronaInfo = coronaInfoService.getCoronaInfo();

        // Then
        assertThat(coronaInfo).isEmpty();
    }

}
