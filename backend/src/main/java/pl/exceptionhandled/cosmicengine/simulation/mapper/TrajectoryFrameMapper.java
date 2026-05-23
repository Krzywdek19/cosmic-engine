package pl.exceptionhandled.cosmicengine.simulation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Vector2D;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.TrajectoryFrameResponse;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TrajectoryFrameMapper {

    private final SimulationBodyMapper simulationBodyMapper;

    public List<TrajectoryFrameResponse> toFrames(
            List<Vector2D> trajectory,
            double deltaTime
    ) {
        List<TrajectoryFrameResponse> frames = new ArrayList<>();

        for (int i = 0; i < trajectory.size(); i++) {
            frames.add(new TrajectoryFrameResponse(
                    i,
                    i * deltaTime,
                    simulationBodyMapper.toResponse(trajectory.get(i))
            ));
        }

        return frames;
    }
}