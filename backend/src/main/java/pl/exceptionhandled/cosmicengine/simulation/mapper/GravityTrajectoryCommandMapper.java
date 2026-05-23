package pl.exceptionhandled.cosmicengine.simulation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Body;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryRequest;
import pl.exceptionhandled.cosmicengine.simulation.command.GravityTrajectoryCommand;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GravityTrajectoryCommandMapper {

    private final SimulationBodyMapper simulationBodyMapper;

    public GravityTrajectoryCommand toCommand(GravityTrajectoryRequest request) {
        List<Body> bodies = request.bodies()
                .stream()
                .map(simulationBodyMapper::toBody)
                .toList();

        return new GravityTrajectoryCommand(
                bodies,
                request.deltaTime(),
                request.steps()
        );
    }
}