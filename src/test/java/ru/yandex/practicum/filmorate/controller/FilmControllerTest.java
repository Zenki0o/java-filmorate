package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class FilmControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        FilmController filmController = new FilmController(new FilmValidator());
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    void createEmptyJsonBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().is(400));
    }

    @Test
    void createEmptyRequestBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().is(400));
    }

    @Test
    void createInvalidFilmReturnsBadRequest() throws Exception {
        Film film = new Film();
        film.setName("");
        film.setDuration(0);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(400));
    }

    @Test
    void createValidFilmReturnsOk() throws Exception {
        Film film = new Film();
        film.setName("Film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(200));
    }

    @Test
    void updateNonExistingFilmReturnsNotFound() throws Exception {
        Film film = new Film();
        film.setId(999);
        film.setName("Film");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(90);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andExpect(status().is(404));
    }
}
