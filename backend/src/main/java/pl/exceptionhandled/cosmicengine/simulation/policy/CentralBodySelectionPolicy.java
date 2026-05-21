package pl.exceptionhandled.cosmicengine.simulation.policy;

import pl.exceptionhandled.cosmicengine.physics.model.Body;

import java.util.List;

public interface CentralBodySelectionPolicy {

    int selectCentralBodyIndex(List<Body> bodies);
}