package pl.exceptionhandled.cosmicengine.simulation.mapper;

import org.junit.jupiter.api.Test;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimulationBodyRequest;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.Vector2DRequest;
import pl.exceptionhandled.cosmicengine.simulation.command.GravityTrajectoryCommand;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GravityTrajectoryCommandMapperTest {

    private final GravityTrajectoryCommandMapper mapper = new GravityTrajectoryCommandMapper(
            new SimulationBodyMapper()
    );

    @Test
    void shouldMapGravityTrajectoryRequestToCommand() {
        GravityTrajectoryRequest request = new GravityTrajectoryRequest(
                List.of(
                        new SimulationBodyRequest(
                                10.0,
                                new Vector2DRequest(1.0, 2.0),
                                new Vector2DRequest(3.0, 4.0)
                        )
                ),
                0.5,
                10
        );

        GravityTrajectoryCommand command = mapper.toCommand(request);

        assertEquals(1, command.bodies().size());
        assertEquals(10.0, command.bodies().getFirst().getMass());
        assertEquals(1.0, command.bodies().getFirst().getPosition().x());
        assertEquals(2.0, command.bodies().getFirst().getPosition().y());
        assertEquals(3.0, command.bodies().getFirst().getVelocity().x());
        assertEquals(4.0, command.bodies().getFirst().getVelocity().y());
        assertEquals(0.5, command.deltaTime());
        assertEquals(10, command.steps());
    }
}