package pl.exceptionhandled.cosmicengine.simulation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.exceptionhandled.cosmicengine.simulation.SimpleMotionSimulationService;
import pl.exceptionhandled.cosmicengine.simulation.SimulationService;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.*;
import pl.exceptionhandled.cosmicengine.simulation.command.GravityTrajectoryCommand;
import pl.exceptionhandled.cosmicengine.simulation.mapper.GravityTrajectoryCommandMapper;
import pl.exceptionhandled.cosmicengine.simulation.mapper.GravityTrajectoryResponseMapper;
import pl.exceptionhandled.cosmicengine.simulation.result.GravityTrajectoryResult;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/simulations")
public class SimulationController {

    private final SimpleMotionSimulationService simpleMotionSimulationService;
    private final SimulationService simulationService;
    private final GravityTrajectoryCommandMapper gravityTrajectoryCommandMapper;
    private final GravityTrajectoryResponseMapper gravityTrajectoryResponseMapper;


    @GetMapping("/simple-motion")
    public SimpleMotionSimulationResponse runSimpleMotionSimulation() {
        return simpleMotionSimulationService.runSimpleMotionSimulation();
    }

    @PostMapping("/simple-motion")
    public ConfigurableSimpleMotionSimulationResponse runConfigurableSimpleMotionSimulation(
            @Valid @RequestBody SimpleMotionSimulationRequest request
    ) {
        return simpleMotionSimulationService.runConfigurableSimpleMotionSimulation(request);
    }

    @PostMapping("/simple-motion/trajectory")
    public SimpleMotionTrajectoryResponse runSimpleMotionTrajectory(
            @Valid @RequestBody SimpleMotionSimulationRequest request
    ) {
        return simpleMotionSimulationService.runSimpleMotionTrajectory(request);
    }

    @PostMapping("/gravity/trajectory")
    public GravityTrajectoryResponse simulateStaticCentralGravityTrajectory(
            @Valid @RequestBody GravityTrajectoryRequest request
    ) {
        GravityTrajectoryCommand command = gravityTrajectoryCommandMapper.toCommand(request);

        GravityTrajectoryResult result = simulationService.simulateStaticCentralGravityTrajectory(command);

        return gravityTrajectoryResponseMapper.toResponse(result);
    }
}