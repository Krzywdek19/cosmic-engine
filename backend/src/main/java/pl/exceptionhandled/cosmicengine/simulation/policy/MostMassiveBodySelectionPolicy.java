package pl.exceptionhandled.cosmicengine.simulation.policy;

import org.springframework.stereotype.Component;
import pl.exceptionhandled.cosmicengine.physics.model.Body;

import java.util.List;

@Component
public class MostMassiveBodySelectionPolicy implements CentralBodySelectionPolicy {

    @Override
    public int selectCentralBodyIndex(List<Body> bodies) {
        if (bodies == null || bodies.isEmpty()) {
            throw new IllegalArgumentException("Bodies list cannot be empty");
        }

        int mostMassiveBodyIndex = 0;
        double highestMass = bodies.getFirst().getMass();

        for (int i = 1; i < bodies.size(); i++) {
            double currentMass = bodies.get(i).getMass();

            if (currentMass > highestMass) {
                highestMass = currentMass;
                mostMassiveBodyIndex = i;
            }
        }

        return mostMassiveBodyIndex;
    }
}