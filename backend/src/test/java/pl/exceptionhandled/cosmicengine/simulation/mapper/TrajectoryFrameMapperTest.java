package pl.exceptionhandled.cosmicengine.simulation.mapper;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.TrajectoryFrameResponse;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrajectoryFrameMapperTest {

    private final TrajectoryFrameMapper mapper = new TrajectoryFrameMapper(
            new SimulationBodyMapper()
    );

    @Test
    void shouldMapSimulationFramesToTrajectoryFrameResponses() {
        List<BodySimulationFrame> simulationFrames = List.of(
                new BodySimulationFrame(
                        0,
                        0.0,
                        new Vector2D(10.0, 0.0),
                        new Vector2D(0.0, 1.0),
                        new Vector2D(0.0, 0.0)
                ),
                new BodySimulationFrame(
                        1,
                        0.5,
                        new Vector2D(9.5, 1.0),
                        new Vector2D(-1.0, 1.0),
                        new Vector2D(-2.0, 0.0)
                )
        );

        List<TrajectoryFrameResponse> frames = mapper.toResponses(simulationFrames);

        assertEquals(2, frames.size());

        TrajectoryFrameResponse firstFrame = frames.getFirst();

        assertEquals(0, firstFrame.step());
        assertEquals(0.0, firstFrame.time());
        assertEquals(10.0, firstFrame.position().x());
        assertEquals(0.0, firstFrame.position().y());
        assertEquals(0.0, firstFrame.velocity().x());
        assertEquals(1.0, firstFrame.velocity().y());
        assertEquals(0.0, firstFrame.acceleration().x());
        assertEquals(0.0, firstFrame.acceleration().y());

        TrajectoryFrameResponse secondFrame = frames.getLast();

        assertEquals(1, secondFrame.step());
        assertEquals(0.5, secondFrame.time());
        assertEquals(9.5, secondFrame.position().x());
        assertEquals(1.0, secondFrame.position().y());
        assertEquals(-1.0, secondFrame.velocity().x());
        assertEquals(1.0, secondFrame.velocity().y());
        assertEquals(-2.0, secondFrame.acceleration().x());
        assertEquals(0.0, secondFrame.acceleration().y());
    }
}