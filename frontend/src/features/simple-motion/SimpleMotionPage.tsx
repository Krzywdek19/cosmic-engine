import { useState } from "react";
import type { FormEvent } from "react";
import { runSimpleMotionTrajectory } from "../../api/simulationApi";
import type {
    SimpleMotionRequest,
    SimpleMotionTrajectoryResponse,
} from "./types";
import { SimpleMotionCanvas } from "./SimpleMotionCanvas";

const DEFAULT_REQUEST: SimpleMotionRequest = {
  mass: 2,
  position: {
    x: 0,
    y: 0,
  },
  velocity: {
    x: 0,
    y: 0,
  },
  force: {
    x: 10,
    y: 0,
  },
  deltaTime: 1,
  steps: 20,
};

export function SimpleMotionPage() {
  const [request, setRequest] = useState<SimpleMotionRequest>(DEFAULT_REQUEST);
  const [result, setResult] = useState<SimpleMotionTrajectoryResponse | null>(
    null
  );
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setIsLoading(true);
    setError(null);

    try {
      const response = await runSimpleMotionTrajectory(request);
      setResult(response);
    } catch {
      setError("Failed to run simulation.");
    } finally {
      setIsLoading(false);
    }
  }

  function updateNumberField(path: string, value: string) {
    const numericValue = Number(value);

    setRequest((previous) => {
        const keys = path.split(".");

        if (keys.length === 1) {
        return {
            ...previous,
            [keys[0]]: numericValue,
        };
        }

        const [objectKey, fieldKey] = keys;

        if (
        objectKey !== "position" &&
        objectKey !== "velocity" &&
        objectKey !== "force"
        ) {
        return previous;
        }

        return {
        ...previous,
        [objectKey]: {
            ...previous[objectKey],
            [fieldKey]: numericValue,
        },
        };
    });
    }

  return (
    <main style={{ padding: "32px", fontFamily: "system-ui, sans-serif" }}>
      <h1>Simple Motion Simulation</h1>

      <p>
        This simulation uses force, mass, acceleration, velocity and position to
        calculate the motion of a material point.
      </p>

      <section
        style={{
          display: "grid",
          gridTemplateColumns: "320px 1fr",
          gap: "32px",
          alignItems: "start",
        }}
      >
        <form onSubmit={handleSubmit}>
          <h2>Input</h2>

          <NumberInput
            label="Mass"
            value={request.mass}
            onChange={(value) => updateNumberField("mass", value)}
          />

          <h3>Position</h3>
          <NumberInput
            label="X"
            value={request.position.x}
            onChange={(value) => updateNumberField("position.x", value)}
          />
          <NumberInput
            label="Y"
            value={request.position.y}
            onChange={(value) => updateNumberField("position.y", value)}
          />

          <h3>Velocity</h3>
          <NumberInput
            label="X"
            value={request.velocity.x}
            onChange={(value) => updateNumberField("velocity.x", value)}
          />
          <NumberInput
            label="Y"
            value={request.velocity.y}
            onChange={(value) => updateNumberField("velocity.y", value)}
          />

          <h3>Force</h3>
          <NumberInput
            label="X"
            value={request.force.x}
            onChange={(value) => updateNumberField("force.x", value)}
          />
          <NumberInput
            label="Y"
            value={request.force.y}
            onChange={(value) => updateNumberField("force.y", value)}
          />

          <h3>Simulation</h3>
          <NumberInput
            label="Delta time"
            value={request.deltaTime}
            onChange={(value) => updateNumberField("deltaTime", value)}
          />
          <NumberInput
            label="Steps"
            value={request.steps}
            onChange={(value) => updateNumberField("steps", value)}
          />

          <button type="submit" disabled={isLoading}>
            {isLoading ? "Running..." : "Run simulation"}
          </button>

          {error && <p style={{ color: "crimson" }}>{error}</p>}
        </form>

        <section>
          <h2>Visualization</h2>

          <SimpleMotionCanvas frames={result?.frames ?? []} />

          {result && (
            <div>
              <h2>Result</h2>

              <p>
                Acceleration: ({result.acceleration.x}, {result.acceleration.y})
              </p>
              <p>
                Final velocity: ({result.finalVelocity.x},{" "}
                {result.finalVelocity.y})
              </p>
              <p>
                Final position: ({result.finalPosition.x},{" "}
                {result.finalPosition.y})
              </p>
            </div>
          )}
        </section>
      </section>
    </main>
  );
}

type NumberInputProps = {
  label: string;
  value: number;
  onChange: (value: string) => void;
};

function NumberInput({ label, value, onChange }: NumberInputProps) {
  return (
    <label
      style={{
        display: "grid",
        gap: "4px",
        marginBottom: "12px",
      }}
    >
      {label}
      <input
        type="number"
        value={value}
        step="any"
        onChange={(event) => onChange(event.target.value)}
      />
    </label>
  );
}