package pl.exceptionhandled.cosmicengine.simulation.mapper;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.TrajectoryFrameResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrajectoryFrameMapperTest {

    private final TrajectoryFrameMapper mapper = new TrajectoryFrameMapper(
            new SimulationBodyMapper()
    );

    @Test
    void shouldMapTrajectoryToFrames() {
        List<Vector2D> trajectory = List.of(
                new Vector2D(10.0, 0.0),
                new Vector2D(9.5, 1.0),
                new Vector2D(8.0, 2.0)
        );

        List<TrajectoryFrameResponse> frames = mapper.toFrames(trajectory, 0.5);

        assertEquals(3, frames.size());

        assertEquals(0, frames.get(0).step());
        assertEquals(0.0, frames.get(0).time());
        assertEquals(10.0, frames.get(0).position().x());
        assertEquals(0.0, frames.get(0).position().y());

        assertEquals(1, frames.get(1).step());
        assertEquals(0.5, frames.get(1).time());
        assertEquals(9.5, frames.get(1).position().x());
        assertEquals(1.0, frames.get(1).position().y());

        assertEquals(2, frames.get(2).step());
        assertEquals(1.0, frames.get(2).time());
        assertEquals(8.0, frames.get(2).position().x());
        assertEquals(2.0, frames.get(2).position().y());
    }
}