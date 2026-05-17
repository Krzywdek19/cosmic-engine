package pl.exceptionhandled.cosmicengine.simulation.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SimulationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRunConfigurableSimpleMotionSimulation() throws Exception {
        String requestBody = """
                {
                  "mass": 2.0,
                  "position": {
                    "x": 0.0,
                    "y": 0.0
                  },
                  "velocity": {
                    "x": 0.0,
                    "y": 0.0
                  },
                  "force": {
                    "x": 10.0,
                    "y": 0.0
                  },
                  "deltaTime": 2.0,
                  "steps": 1
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/simple-motion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mass").value(2.0))
                .andExpect(jsonPath("$.initialPosition.x").value(0.0))
                .andExpect(jsonPath("$.initialPosition.y").value(0.0))
                .andExpect(jsonPath("$.appliedForce.x").value(10.0))
                .andExpect(jsonPath("$.appliedForce.y").value(0.0))
                .andExpect(jsonPath("$.acceleration.x").value(5.0))
                .andExpect(jsonPath("$.acceleration.y").value(0.0))
                .andExpect(jsonPath("$.finalVelocity.x").value(10.0))
                .andExpect(jsonPath("$.finalVelocity.y").value(0.0))
                .andExpect(jsonPath("$.finalPosition.x").value(10.0))
                .andExpect(jsonPath("$.finalPosition.y").value(0.0))
                .andExpect(jsonPath("$.steps").value(1))
                .andExpect(jsonPath("$.deltaTime").value(2.0));
    }

    @Test
    void shouldReturnBadRequestWhenMassIsNegative() throws Exception {
        String requestBody = """
            {
              "mass": -2.0,
              "position": {
                "x": 0.0,
                "y": 0.0
              },
              "velocity": {
                "x": 0.0,
                "y": 0.0
              },
              "force": {
                "x": 10.0,
                "y": 0.0
              },
              "deltaTime": 2.0,
              "steps": 1
            }
            """;

        mockMvc.perform(post("/api/v1/simulations/simple-motion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRunSimpleMotionTrajectorySimulation() throws Exception {
        String requestBody = """
            {
              "mass": 2.0,
              "position": {
                "x": 0.0,
                "y": 0.0
              },
              "velocity": {
                "x": 0.0,
                "y": 0.0
              },
              "force": {
                "x": 10.0,
                "y": 0.0
              },
              "deltaTime": 1.0,
              "steps": 3
            }
            """;

        mockMvc.perform(post("/api/v1/simulations/simple-motion/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acceleration.x").value(5.0))
                .andExpect(jsonPath("$.frames.length()").value(4))
                .andExpect(jsonPath("$.frames[0].step").value(0))
                .andExpect(jsonPath("$.frames[0].position.x").value(0.0))
                .andExpect(jsonPath("$.frames[0].velocity.x").value(0.0))
                .andExpect(jsonPath("$.frames[1].position.x").value(2.5))
                .andExpect(jsonPath("$.frames[1].velocity.x").value(5.0))
                .andExpect(jsonPath("$.frames[2].position.x").value(10.0))
                .andExpect(jsonPath("$.frames[2].velocity.x").value(10.0))
                .andExpect(jsonPath("$.frames[3].position.x").value(22.5))
                .andExpect(jsonPath("$.frames[3].velocity.x").value(15.0))
                .andExpect(jsonPath("$.finalPosition.x").value(22.5))
                .andExpect(jsonPath("$.finalVelocity.x").value(15.0));
    }
}