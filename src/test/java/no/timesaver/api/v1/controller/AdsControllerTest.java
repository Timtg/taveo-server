package no.timesaver.api.v1.controller;

import no.timesaver.domain.Advertisement;
import no.timesaver.service.AdsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@WebAppConfiguration
@AutoConfigureMockMvc
public class AdsControllerTest {
    private MockMvc mockMvc;

    @MockBean
    private AdsService adsService;

    @Autowired
    private WebApplicationContext ctx;


    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.ctx).build();
    }

    @Test
    public void getAdByIdShouldFindTheCorrectAd() throws Exception {
        Long adId = 1337L;
        Advertisement ad = new Advertisement();
        ad.setId(adId);
        ad.setProductId(1L);
        ad.setStoreName("MyStore");
        ad.setStoreId(1234L);
        ad.setPrice(BigDecimal.TEN);
        Optional<Advertisement> mockAd = Optional.of(ad);

        when(this.adsService.getAdById(adId)).thenReturn(mockAd);

        mockMvc.perform(get("/api/v1/ad/"+adId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(adId.intValue())))
                .andExpect(jsonPath("$.links", hasSize(3)))
                .andExpect(jsonPath("$.links[0].rel", is("self")))
                .andExpect(jsonPath("$.links[1].rel", is("product")))
                .andExpect(jsonPath("$.links[2].rel", is("store")))
                .andExpect(jsonPath("$.productId", is(ad.getProductId().intValue())))
                .andExpect(jsonPath("$.storeName", is(ad.getStoreName())))
                .andExpect(jsonPath("$.storeId", is(ad.getStoreId().intValue())))
                .andExpect(jsonPath("$.price", is(ad.getPrice().intValue())))
        ;

        verify(this.adsService, times(1)).getAdById(adId);
        verifyNoMoreInteractions(this.adsService);
    }
}
