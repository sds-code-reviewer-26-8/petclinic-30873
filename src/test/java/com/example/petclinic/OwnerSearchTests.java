package com.example.petclinic;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OwnerSearchTests {

    @Autowired
    MockMvc mockMvc;

    @Test
    void withoutParamReturnsAllOwners() throws Exception {
        mockMvc.perform(get("/api/v1/owners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)));
    }

    @Test
    void prefixMatchIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/v1/owners").param("lastName", "dAv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].lastName").value("Davis"));
    }

    @Test
    void searchTermIsTrimmed() throws Exception {
        mockMvc.perform(get("/api/v1/owners").param("lastName", "  Davis "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void onlyPrefixMatches() throws Exception {
        mockMvc.perform(get("/api/v1/owners").param("lastName", "avis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void likeWildcardsAreTreatedAsLiterals() throws Exception {
        mockMvc.perform(get("/api/v1/owners").param("lastName", "%"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mockMvc.perform(get("/api/v1/owners").param("lastName", "_"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void blankSearchTermIsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/owners").param("lastName", ""))
                .andExpect(status().isBadRequest());
        mockMvc.perform(get("/api/v1/owners").param("lastName", "   "))
                .andExpect(status().isBadRequest());
    }
}
