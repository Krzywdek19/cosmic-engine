package pl.exceptionhandled.cosmicengine.simulation.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.exceptionhandled.cosmicengine.simulation.SimulationDemoService;
import pl.exceptionhandled.cosmicengine.simulation.api.dto.SimpleMotionSimulationResponse;

@RestController
@RequestMapping("/api/v1/simulations")
public class SimulationController {

    private final SimulationDemoService simulationDemoService;

    public SimulationController(SimulationDemoService simulationDemoService) {
        this.simulationDemoService = simulationDemoService;
    }

    @GetMapping("/simple-motion")
    public SimpleMotionSimulationResponse runSimpleMotionSimulation() {
        return simulationDemoService.runSimpleMotionSimulation();
    }
}