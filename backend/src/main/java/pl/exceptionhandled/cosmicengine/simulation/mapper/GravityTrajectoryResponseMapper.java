package pl.exceptionhandled.cosmicengine.simulation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.BodyTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.GravityTrajectoryResponse;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.TrajectoryFrameResponse;
import pl.exceptionhandled.cosmicengine.simulation.result.BodyTrajectoryResult;
import pl.exceptionhandled.cosmicengine.simulation.result.GravityTrajectoryResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GravityTrajectoryResponseMapper {

    private final SimulationBodyMapper simulationBodyMapper;
    private final TrajectoryFrameMapper trajectoryFrameMapper;

    public GravityTrajectoryResponse toResponse(GravityTrajectoryResult result) {
        return new GravityTrajectoryResponse(
                result.model(),
                result.integrator(),
                result.centralBodyIndex(),
                simulationBodyMapper.toResponse(result.centralBodyPosition()),
                toBodyTrajectoryResponses(result.trajectories())
        );
    }

    private List<BodyTrajectoryResponse> toBodyTrajectoryResponses(
            List<BodyTrajectoryResult> trajectories
    ) {
        return trajectories.stream()
                .map(this::toBodyTrajectoryResponse)
                .toList();
    }

    private BodyTrajectoryResponse toBodyTrajectoryResponse(BodyTrajectoryResult result) {
        List<TrajectoryFrameResponse> frames = trajectoryFrameMapper.toResponses(result.frames());

        return new BodyTrajectoryResponse(
                result.bodyIndex(),
                result.mass(),
                frames
        );
    }
}