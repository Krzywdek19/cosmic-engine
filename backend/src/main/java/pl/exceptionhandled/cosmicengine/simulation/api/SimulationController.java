package pl.exceptionhandled.cosmicengine.simulation.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.exceptionhandled.cosmicengine.simulation.SimulationDemoService;
import pl.exceptionhandled.cosmicengine.simulation.SimulationService;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/simulations")
public class SimulationController {

    private final SimulationDemoService simulationDemoService;
    private final SimulationService simulationService;


    @GetMapping("/simple-motion")
    public SimpleMotionSimulationResponse runSimpleMotionSimulation() {
        return simulationDemoService.runSimpleMotionSimulation();
    }

    @PostMapping("/simple-motion")
    public ConfigurableSimpleMotionSimulationResponse runConfigurableSimpleMotionSimulation(
            @Valid @RequestBody SimpleMotionSimulationRequest request
    ) {
        return simulationDemoService.runConfigurableSimpleMotionSimulation(request);
    }

    @PostMapping("/simple-motion/trajectory")
    public SimpleMotionTrajectoryResponse runSimpleMotionTrajectory(
            @Valid @RequestBody SimpleMotionSimulationRequest request
    ) {
        return simulationDemoService.runSimpleMotionTrajectory(request);
    }

    @PostMapping("/gravity/trajectory")
    public GravityTrajectoryResponse simulateGravityTrajectory(
            @Valid @RequestBody GravityTrajectoryRequest request
    ) {
        return simulationService.simulateGravityTrajectory(request);
    }
}