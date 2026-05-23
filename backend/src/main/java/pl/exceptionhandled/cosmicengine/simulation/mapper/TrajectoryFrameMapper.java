package pl.exceptionhandled.cosmicengine.simulation.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.TrajectoryFrameResponse;
import pl.exceptionhandled.cosmicengine.simulation.model.BodySimulationFrame;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrajectoryFrameMapper {

    private final SimulationBodyMapper simulationBodyMapper;

    public List<TrajectoryFrameResponse> toResponses(List<BodySimulationFrame> frames) {
        return frames.stream()
                .map(this::toResponse)
                .toList();
    }

    private TrajectoryFrameResponse toResponse(BodySimulationFrame frame) {
        return new TrajectoryFrameResponse(
                frame.step(),
                frame.time(),
                simulationBodyMapper.toResponse(frame.position()),
                simulationBodyMapper.toResponse(frame.velocity()),
                simulationBodyMapper.toResponse(frame.acceleration())
        );
    }
}