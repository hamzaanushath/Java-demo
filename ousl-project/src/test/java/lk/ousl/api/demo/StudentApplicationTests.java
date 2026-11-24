package lk.ousl.api.demo;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentApplication.class)
class StudentApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private StudentRepository repository;

	@Test
	void getStudentsReturnsAllStudents() throws Exception {
		when(repository.findAll()).thenReturn(List.of(new Student("Anushath", "Cloud Computing")));

		mockMvc.perform(get("/api/students"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value("Anushath"))
				.andExpect(jsonPath("$[0].course").value("Cloud Computing"));
	}

	@Test
	void postStudentSavesAndReturnsStudent() throws Exception {
		when(repository.save(any(Student.class))).thenReturn(new Student("Anushath", "Cloud Computing"));

		mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Anushath\",\"course\":\"Cloud Computing\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Anushath"))
				.andExpect(jsonPath("$.course").value("Cloud Computing"));
	}

	@Test
	void postStudentRejectsEmptyName() throws Exception {
		mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"\",\"course\":\"Cloud Computing\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertFieldError(result.getResolvedException(), "name"));

		verify(repository, never()).save(any());
	}

	@Test
	void postStudentRejectsEmptyCourse() throws Exception {
		mockMvc.perform(post("/api/students")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"name\":\"Anushath\",\"course\":\"\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(result -> assertFieldError(result.getResolvedException(), "course"));

		verify(repository, never()).save(any());
	}

	private static void assertFieldError(Exception exception, String field) {
		MethodArgumentNotValidException validation = assertInstanceOf(MethodArgumentNotValidException.class, exception);
		assertTrue(validation.getBindingResult().getFieldErrors().stream().anyMatch(error -> field.equals(error.getField())));
	}

}
