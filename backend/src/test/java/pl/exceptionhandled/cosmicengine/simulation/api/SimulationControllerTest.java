package pl.exceptionhandled.cosmicengine.simulation.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
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
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.fieldErrors.mass").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnBadRequestWhenGravityBodiesHaveSamePosition() throws Exception {
        String requestBody = """
                {
                  "bodies": [
                    {
                      "mass": 100.0,
                      "position": {
                        "x": 0.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 0.0
                      }
                    },
                    {
                      "mass": 1.0,
                      "position": {
                        "x": 0.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 1.0
                      }
                    }
                  ],
                  "deltaTime": 1.0,
                  "steps": 1
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/gravity/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
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

    @Test
    void shouldReturnBadRequestWhenSimpleMotionStepsExceedLimit() throws Exception {
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
                  "steps": 10001
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/simple-motion/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.steps").exists());
    }

    @Test
    void shouldReturnBadRequestWhenGravityTrajectoryStepsExceedLimit() throws Exception {
        String requestBody = """
                {
                  "bodies": [
                    {
                      "mass": 100.0,
                      "position": {
                        "x": 0.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 0.0
                      }
                    },
                    {
                      "mass": 1.0,
                      "position": {
                        "x": 10.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 1.0
                      }
                    }
                  ],
                  "deltaTime": 1.0,
                  "steps": 10001
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/gravity/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.steps").exists());
    }

    @Test
    void shouldReturnBadRequestWhenGravityTrajectoryBodiesExceedLimit() throws Exception {
        String requestBody = gravityTrajectoryRequestWithBodiesCount(51);

        mockMvc.perform(post("/api/v1/simulations/gravity/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.bodies").exists());
    }

    @Test
    void shouldRunGravityTrajectorySimulation() throws Exception {
        String requestBody = """
                {
                  "bodies": [
                    {
                      "mass": 1.0,
                      "position": {
                        "x": 10.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 1.0
                      }
                    },
                    {
                      "mass": 100.0,
                      "position": {
                        "x": 0.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 0.0
                      }
                    }
                  ],
                  "deltaTime": 1.0,
                  "steps": 3
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/gravity/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("STATIC_CENTRAL_BODY"))
                .andExpect(jsonPath("$.integrator").value("CONSTANT_ACCELERATION_STEP"))
                .andExpect(jsonPath("$.centralBodyIndex").value(1))
                .andExpect(jsonPath("$.centralBodyPosition.x").value(0.0))
                .andExpect(jsonPath("$.centralBodyPosition.y").value(0.0))
                .andExpect(jsonPath("$.trajectories.length()").value(1))
                .andExpect(jsonPath("$.trajectories[0].bodyIndex").value(0))
                .andExpect(jsonPath("$.trajectories[0].mass").value(1.0))
                .andExpect(jsonPath("$.trajectories[0].frames.length()").value(4))
                .andExpect(jsonPath("$.trajectories[0].frames[0].step").value(0))
                .andExpect(jsonPath("$.trajectories[0].frames[0].time").value(0.0))
                .andExpect(jsonPath("$.trajectories[0].frames[0].position.x").value(10.0))
                .andExpect(jsonPath("$.trajectories[0].frames[0].position.y").value(0.0))
                .andExpect(jsonPath("$.trajectories[0].frames[1].step").value(1))
                .andExpect(jsonPath("$.trajectories[0].frames[1].time").value(1.0))
                .andExpect(jsonPath("$.trajectories[0].frames[1].position.x").isNumber())
                .andExpect(jsonPath("$.trajectories[0].frames[1].position.y").isNumber());
    }

    @Test
    void shouldCurveGravityTrajectoryTowardsAttractingBody() throws Exception {
        String requestBody = """
                {
                  "bodies": [
                    {
                      "mass": 1.0,
                      "position": {
                        "x": 10.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 1.0
                      }
                    },
                    {
                      "mass": 100.0,
                      "position": {
                        "x": 0.0,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 0.0
                      }
                    }
                  ],
                  "deltaTime": 1.0,
                  "steps": 1
                }
                """;

        mockMvc.perform(post("/api/v1/simulations/gravity/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trajectories[0].frames.length()").value(2))
                .andExpect(jsonPath("$.trajectories[0].frames[0].position.x").value(10.0))
                .andExpect(jsonPath("$.trajectories[0].frames[0].position.y").value(0.0))
                .andExpect(jsonPath("$.trajectories[0].frames[1].position.x").value(lessThan(10.0)))
                .andExpect(jsonPath("$.trajectories[0].frames[1].position.y").value(greaterThan(0.0)));
    }

    private String gravityTrajectoryRequestWithBodiesCount(int bodiesCount) {
        StringBuilder bodiesJson = new StringBuilder();

        for (int i = 0; i < bodiesCount; i++) {
            if (i > 0) {
                bodiesJson.append(",");
            }

            bodiesJson.append("""
                    {
                      "mass": %s,
                      "position": {
                        "x": %s,
                        "y": 0.0
                      },
                      "velocity": {
                        "x": 0.0,
                        "y": 1.0
                      }
                    }
                    """.formatted(100.0 + i, 10.0 + i));
        }

        return """
                {
                  "bodies": [
                    %s
                  ],
                  "deltaTime": 1.0,
                  "steps": 1
                }
                """.formatted(bodiesJson);
    }

    @Test
    void shouldReturnBadRequestWhenGravityTrajectoryDeltaTimeExceedsLimit() throws Exception {
        String requestBody = """
            {
              "bodies": [
                {
                  "mass": 10000.0,
                  "position": {
                    "x": 0.0,
                    "y": 0.0
                  },
                  "velocity": {
                    "x": 0.0,
                    "y": 0.0
                  }
                },
                {
                  "mass": 1.0,
                  "position": {
                    "x": 100.0,
                    "y": 0.0
                  },
                  "velocity": {
                    "x": 0.0,
                    "y": 10.0
                  }
                }
              ],
              "deltaTime": 55.0,
              "steps": 3200
            }
            """;

        mockMvc.perform(post("/api/v1/simulations/gravity/trajectory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.fieldErrors.deltaTime").exists());
    }
}