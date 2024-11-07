package com.example.backendCICD.ControllerTest;

import com.example.backendCICD.Controller.HelloWorldController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = HelloWorldController.class)
public class HelloControllerTest {
    @Autowired
    private MockMvc mockMVC;


    @Test
    public void testWelcome() throws Exception {
        mockMVC.perform(get("/welcome")) // Perform GET request on "/"
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().string("Hello World!")); // Expect content to be "Hello World!"
    }

    @Test
    public void testContactUs() throws Exception {
        List<String> contacts = Arrays.asList("contact1@example.com", "contact2@example.com");
        // Perform GET request on "/contacts" with contacts as request parameter
        mockMVC.perform(get("/welcome/contacts")
                        .param("contacts", contacts.toArray(new String[0])))
                .andExpect(status().isOk()) // Expect HTTP 200 status
                .andExpect(content().json("[\"contact1@example.com\",\"contact2@example.com\"]")); // Expect JSON array response
    }
}
