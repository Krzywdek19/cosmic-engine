import { type CSSProperties, type FormEvent, useState } from "react";
import { runSimpleMotionTrajectory } from "../../api/simulationApi";
import type {
  BodyConfig,
  BodySimulationResult,
  SimpleMotionRequest,
  SimulationSettings,
} from "./types";
import { SimpleMotionCanvas } from "./SimpleMotionCanvas";

const INITIAL_BODIES: BodyConfig[] = [
  {
    id: "body-1",
    name: "Body 1",
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
  },
];

const DEFAULT_SETTINGS: SimulationSettings = {
  deltaTime: 1,
  steps: 30,
};

type VectorField = "position" | "velocity" | "force";
type VectorAxis = "x" | "y";
type BodyScalarField = "mass";

export function SimpleMotionPage() {
  const [bodies, setBodies] = useState<BodyConfig[]>(INITIAL_BODIES);
  const [settings, setSettings] =
    useState<SimulationSettings>(DEFAULT_SETTINGS);
  const [selectedBodyId, setSelectedBodyId] = useState(INITIAL_BODIES[0].id);
  const [results, setResults] = useState<BodySimulationResult[]>([]);
  const [replayKey, setReplayKey] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const selectedBody = bodies.find((body) => body.id === selectedBodyId) ?? bodies[0];

  const selectedBodyIndex = selectedBody
    ? bodies.findIndex((body) => body.id === selectedBody.id)
    : -1;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setIsLoading(true);
    setError(null);

    try {
      const responses = await Promise.all(
        bodies.map(async (body) => {
          const request: SimpleMotionRequest = {
            mass: body.mass,
            position: body.position,
            velocity: body.velocity,
            force: body.force,
            deltaTime: settings.deltaTime,
            steps: settings.steps,
          };

          const response = await runSimpleMotionTrajectory(request);

          return {
            id: body.id,
            name: body.name,
            response,
            frames: response.frames,
          };
        })
      );

      setResults(responses);
      setReplayKey((previous) => previous + 1);
    } catch {
      setError("Failed to run simulation.");
    } finally {
      setIsLoading(false);
    }
  }

  function addBody() {
    const newBody = createBodyConfig(bodies.length + 1);

    setBodies((previous) => [...previous, newBody]);
    setSelectedBodyId(newBody.id);
  }

  function removeBody(bodyId: string) {
    if (bodies.length === 1) {
      return;
    }

    const nextBodies = bodies.filter((body) => body.id !== bodyId);

    setBodies(nextBodies);
    setResults((previous) => previous.filter((result) => result.id !== bodyId));

    if (selectedBodyId === bodyId) {
      setSelectedBodyId(nextBodies[0].id);
    }
  }

  function resetBodies() {
    setBodies(INITIAL_BODIES);
    setSelectedBodyId(INITIAL_BODIES[0].id);
    setResults([]);
    setReplayKey((previous) => previous + 1);
    setError(null);
  }

  function clearResults() {
    setResults([]);
    setReplayKey((previous) => previous + 1);
    setError(null);
  }

  function replaySimulation() {
    if (results.length === 0) {
      return;
    }

    setReplayKey((previous) => previous + 1);
  }

  function selectPreviousBody() {
    const currentIndex = bodies.findIndex((body) => body.id === selectedBodyId);

    if (currentIndex <= 0) {
      setSelectedBodyId(bodies[bodies.length - 1].id);
      return;
    }

    setSelectedBodyId(bodies[currentIndex - 1].id);
  }

  function selectNextBody() {
    const currentIndex = bodies.findIndex((body) => body.id === selectedBodyId);

    if (currentIndex >= bodies.length - 1) {
      setSelectedBodyId(bodies[0].id);
      return;
    }

    setSelectedBodyId(bodies[currentIndex + 1].id);
  }

  function updateBodyName(bodyId: string, name: string) {
    setBodies((previous) =>
      previous.map((body) =>
        body.id === bodyId
          ? {
              ...body,
              name,
            }
          : body
      )
    );
  }

  function updateBodyNumberField(bodyId: string, path: string, value: string) {
    const numericValue = Number(value);

    setBodies((previous) =>
      previous.map((body) => {
        if (body.id !== bodyId) {
          return body;
        }

        const keys = path.split(".");

        if (keys.length === 1) {
          const [field] = keys;

          if (!isBodyScalarField(field)) {
            return body;
          }

          return {
            ...body,
            [field]: numericValue,
          };
        }

        const [objectKey, fieldKey] = keys;

        if (!isVectorField(objectKey) || !isVectorAxis(fieldKey)) {
          return body;
        }

        return {
          ...body,
          [objectKey]: {
            ...body[objectKey],
            [fieldKey]: numericValue,
          },
        };
      })
    );
  }

  function updateSettingsField(field: keyof SimulationSettings, value: string) {
    const numericValue = Number(value);

    setSettings((previous) => ({
      ...previous,
      [field]: numericValue,
    }));
  }

  return (
    <main style={pageStyle}>
      <header style={headerStyle}>
        <p style={eyebrowStyle}>Cosmic Engine / Stage 1</p>

        <h1 style={titleStyle}>Simple Motion Simulation</h1>

        <p style={descriptionStyle}>
          Simulate independent material points using force, mass, acceleration,
          velocity and position.
        </p>
      </header>

      <section style={layoutStyle}>
        <form onSubmit={handleSubmit} style={panelStyle}>
          <div style={panelHeaderStyle}>
            <div>
              <h2 style={sectionTitleStyle}>Simulation input</h2>
              <p style={mutedTextStyle}>
                Add one or more independent bodies and run the simulation.
              </p>
            </div>

            <button type="button" onClick={addBody} style={secondaryButtonStyle}>
              Add body
            </button>
          </div>

          <div style={settingsGridStyle}>
            <NumberInput
              label="Delta time"
              value={settings.deltaTime}
              onChange={(value) => updateSettingsField("deltaTime", value)}
            />

            <NumberInput
              label="Steps"
              value={settings.steps}
              onChange={(value) => updateSettingsField("steps", value)}
            />
          </div>

          <BodySelector
            bodies={bodies}
            selectedBodyId={selectedBodyId}
            onSelect={setSelectedBodyId}
            onPrevious={selectPreviousBody}
            onNext={selectNextBody}
          />

          {selectedBody && (
            <article key={selectedBody.id} style={bodyCardStyle}>
              <div style={bodyHeaderStyle}>
                <input
                  value={selectedBody.name}
                  onChange={(event) =>
                    updateBodyName(selectedBody.id, event.target.value)
                  }
                  style={bodyNameInputStyle}
                />

                <button
                  type="button"
                  onClick={() => removeBody(selectedBody.id)}
                  disabled={bodies.length === 1}
                  style={{
                    ...dangerButtonStyle,
                    opacity: bodies.length === 1 ? 0.45 : 1,
                    cursor: bodies.length === 1 ? "not-allowed" : "pointer",
                  }}
                >
                  Remove
                </button>
              </div>

              <NumberInput
                label="Mass"
                value={selectedBody.mass}
                onChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "mass", value)
                }
              />

              <VectorInput
                title="Position"
                x={selectedBody.position.x}
                y={selectedBody.position.y}
                onXChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "position.x", value)
                }
                onYChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "position.y", value)
                }
              />

              <VectorInput
                title="Velocity"
                x={selectedBody.velocity.x}
                y={selectedBody.velocity.y}
                onXChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "velocity.x", value)
                }
                onYChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "velocity.y", value)
                }
              />

              <VectorInput
                title="Force"
                x={selectedBody.force.x}
                y={selectedBody.force.y}
                onXChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "force.x", value)
                }
                onYChange={(value) =>
                  updateBodyNumberField(selectedBody.id, "force.y", value)
                }
              />

              <p style={bodyHintStyle}>
                Body #{selectedBodyIndex + 1} is calculated independently.
              </p>
            </article>
          )}

          <div style={actionsStyle}>
            <button type="submit" disabled={isLoading} style={primaryButtonStyle}>
              {isLoading ? "Running..." : "Run simulation"}
            </button>

            <button
              type="button"
              onClick={replaySimulation}
              disabled={results.length === 0}
              style={{
                ...secondaryButtonStyle,
                opacity: results.length === 0 ? 0.45 : 1,
                cursor: results.length === 0 ? "not-allowed" : "pointer",
              }}
            >
              Replay
            </button>

            <button type="button" onClick={clearResults} style={secondaryButtonStyle}>
              Clear result
            </button>

            <button type="button" onClick={resetBodies} style={secondaryButtonStyle}>
              Reset input
            </button>
          </div>

          {error && <p style={errorTextStyle}>{error}</p>}
        </form>

        <section style={panelStyle}>
          <div style={panelHeaderStyle}>
            <div>
              <h2 style={sectionTitleStyle}>Visualization</h2>
              <p style={mutedTextStyle}>
                The canvas automatically fits all trajectories into view. You can
                also zoom and move the viewport manually.
              </p>
            </div>
          </div>

          <SimpleMotionCanvas trajectories={results} replayKey={replayKey} />

          {results.length > 0 && (
            <div style={resultsGridStyle}>
              {results.map((result) => (
                <article key={result.id} style={resultCardStyle}>
                  <h3 style={resultTitleStyle}>{result.name}</h3>

                  <Metric
                    label="Acceleration"
                    x={result.response.acceleration.x}
                    y={result.response.acceleration.y}
                  />

                  <Metric
                    label="Final velocity"
                    x={result.response.finalVelocity.x}
                    y={result.response.finalVelocity.y}
                  />

                  <Metric
                    label="Final position"
                    x={result.response.finalPosition.x}
                    y={result.response.finalPosition.y}
                  />
                </article>
              ))}
            </div>
          )}
        </section>
      </section>
    </main>
  );
}

function createBodyConfig(index: number): BodyConfig {
  return {
    id: `body-${Date.now()}-${index}`,
    name: `Body ${index}`,
    mass: 1,
    position: {
      x: 0,
      y: index * 2,
    },
    velocity: {
      x: 0,
      y: 0,
    },
    force: {
      x: 8,
      y: index % 2 === 0 ? -2 : 2,
    },
  };
}

function isVectorField(value: string): value is VectorField {
  return value === "position" || value === "velocity" || value === "force";
}

function isVectorAxis(value: string | undefined): value is VectorAxis {
  return value === "x" || value === "y";
}

function isBodyScalarField(value: string): value is BodyScalarField {
  return value === "mass";
}

type BodySelectorProps = {
  bodies: BodyConfig[];
  selectedBodyId: string;
  onSelect: (bodyId: string) => void;
  onPrevious: () => void;
  onNext: () => void;
};

function BodySelector({
  bodies,
  selectedBodyId,
  onSelect,
  onPrevious,
  onNext,
}: BodySelectorProps) {
  return (
    <div style={bodySelectorStyle}>
      <button type="button" onClick={onPrevious} style={secondaryButtonStyle}>
        ←
      </button>

      <div style={bodyTabsStyle}>
        {bodies.map((body) => {
          const isSelected = body.id === selectedBodyId;

          return (
            <button
              key={body.id}
              type="button"
              onClick={() => onSelect(body.id)}
              style={{
                border: isSelected
                  ? "1px solid #38bdf8"
                  : "1px solid #334155",
                borderRadius: "999px",
                padding: "9px 14px",
                color: isSelected ? "#020617" : "#e2e8f0",
                background: isSelected ? "#38bdf8" : "#0f172a",
                fontWeight: 800,
                whiteSpace: "nowrap",
                cursor: "pointer",
              }}
            >
              {body.name}
            </button>
          );
        })}
      </div>

      <button type="button" onClick={onNext} style={secondaryButtonStyle}>
        →
      </button>
    </div>
  );
}

type NumberInputProps = {
  label: string;
  value: number;
  onChange: (value: string) => void;
};

function NumberInput({ label, value, onChange }: NumberInputProps) {
  return (
    <label style={inputLabelStyle}>
      <span>{label}</span>
      <input
        type="number"
        value={value}
        step="any"
        onChange={(event) => onChange(event.target.value)}
        style={inputStyle}
      />
    </label>
  );
}

type VectorInputProps = {
  title: string;
  x: number;
  y: number;
  onXChange: (value: string) => void;
  onYChange: (value: string) => void;
};

function VectorInput({
  title,
  x,
  y,
  onXChange,
  onYChange,
}: VectorInputProps) {
  return (
    <div>
      <p style={smallTitleStyle}>{title}</p>

      <div style={settingsGridStyle}>
        <NumberInput label="X" value={x} onChange={onXChange} />
        <NumberInput label="Y" value={y} onChange={onYChange} />
      </div>
    </div>
  );
}

type MetricProps = {
  label: string;
  x: number;
  y: number;
};

function Metric({ label, x, y }: MetricProps) {
  return (
    <p style={metricStyle}>
      <span style={{ color: "#94a3b8" }}>{label}</span>
      <strong>
        ({x.toFixed(2)}, {y.toFixed(2)})
      </strong>
    </p>
  );
}

const pageStyle = {
  minHeight: "100vh",
  padding: "40px",
  fontFamily: "Inter, system-ui, sans-serif",
  color: "#e5e7eb",
  background:
    "radial-gradient(circle at top left, #1e293b 0, #020617 42%, #020617 100%)",
} satisfies CSSProperties;

const headerStyle = {
  marginBottom: "32px",
  maxWidth: "1100px",
} satisfies CSSProperties;

const eyebrowStyle = {
  margin: "0 0 10px",
  color: "#38bdf8",
  fontWeight: 700,
  letterSpacing: "0.08em",
  textTransform: "uppercase",
  fontSize: "13px",
} satisfies CSSProperties;

const titleStyle = {
  margin: 0,
  fontSize: "42px",
  lineHeight: 1.1,
} satisfies CSSProperties;

const descriptionStyle = {
  marginTop: "14px",
  maxWidth: "760px",
  color: "#94a3b8",
  fontSize: "16px",
  lineHeight: 1.7,
} satisfies CSSProperties;

const layoutStyle = {
  display: "grid",
  gridTemplateColumns: "minmax(360px, 440px) minmax(0, 1fr)",
  gap: "28px",
  alignItems: "start",
} satisfies CSSProperties;

const panelStyle = {
  border: "1px solid #1e293b",
  borderRadius: "28px",
  padding: "24px",
  background: "rgba(15, 23, 42, 0.78)",
  boxShadow: "0 24px 80px rgba(2, 6, 23, 0.4)",
} satisfies CSSProperties;

const panelHeaderStyle = {
  display: "flex",
  justifyContent: "space-between",
  alignItems: "flex-start",
  gap: "16px",
  marginBottom: "20px",
} satisfies CSSProperties;

const sectionTitleStyle = {
  margin: 0,
  fontSize: "22px",
} satisfies CSSProperties;

const mutedTextStyle = {
  margin: "6px 0 0",
  color: "#94a3b8",
  fontSize: "14px",
  lineHeight: 1.5,
} satisfies CSSProperties;

const settingsGridStyle = {
  display: "grid",
  gridTemplateColumns: "1fr 1fr",
  gap: "12px",
} satisfies CSSProperties;

const bodySelectorStyle = {
  display: "grid",
  gridTemplateColumns: "auto minmax(0, 1fr) auto",
  gap: "10px",
  alignItems: "center",
  marginTop: "18px",
  marginBottom: "18px",
} satisfies CSSProperties;

const bodyTabsStyle = {
  display: "flex",
  gap: "8px",
  overflowX: "auto",
  paddingBottom: "4px",
  scrollbarWidth: "thin",
} satisfies CSSProperties;

const bodyCardStyle = {
  border: "1px solid #243244",
  borderRadius: "22px",
  padding: "18px",
  background: "rgba(2, 6, 23, 0.42)",
} satisfies CSSProperties;

const bodyHeaderStyle = {
  display: "flex",
  justifyContent: "space-between",
  gap: "12px",
  alignItems: "center",
  marginBottom: "16px",
} satisfies CSSProperties;

const bodyNameInputStyle = {
  width: "100%",
  border: "none",
  outline: "none",
  color: "#e5e7eb",
  background: "transparent",
  fontSize: "18px",
  fontWeight: 700,
} satisfies CSSProperties;

const inputLabelStyle = {
  display: "grid",
  gap: "6px",
  marginBottom: "12px",
  color: "#cbd5e1",
  fontSize: "13px",
  fontWeight: 600,
} satisfies CSSProperties;

const inputStyle = {
  width: "100%",
  boxSizing: "border-box",
  border: "1px solid #334155",
  borderRadius: "12px",
  padding: "10px 12px",
  color: "#f8fafc",
  background: "#020617",
  outline: "none",
} satisfies CSSProperties;

const smallTitleStyle = {
  margin: "14px 0 10px",
  color: "#e2e8f0",
  fontWeight: 700,
  fontSize: "14px",
} satisfies CSSProperties;

const bodyHintStyle = {
  margin: "10px 0 0",
  color: "#64748b",
  fontSize: "12px",
} satisfies CSSProperties;

const actionsStyle = {
  display: "flex",
  gap: "10px",
  flexWrap: "wrap",
  marginTop: "24px",
} satisfies CSSProperties;

const primaryButtonStyle = {
  border: "none",
  borderRadius: "999px",
  padding: "11px 18px",
  color: "#020617",
  background: "#38bdf8",
  fontWeight: 800,
  cursor: "pointer",
} satisfies CSSProperties;

const secondaryButtonStyle = {
  border: "1px solid #334155",
  borderRadius: "999px",
  padding: "10px 16px",
  color: "#e2e8f0",
  background: "#0f172a",
  fontWeight: 700,
  cursor: "pointer",
} satisfies CSSProperties;

const dangerButtonStyle = {
  border: "1px solid #7f1d1d",
  borderRadius: "999px",
  padding: "8px 12px",
  color: "#fecaca",
  background: "#450a0a",
  fontWeight: 700,
} satisfies CSSProperties;

const errorTextStyle = {
  marginTop: "14px",
  color: "#fecaca",
  background: "#450a0a",
  border: "1px solid #7f1d1d",
  borderRadius: "14px",
  padding: "10px 12px",
} satisfies CSSProperties;

const resultsGridStyle = {
  display: "grid",
  gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
  gap: "14px",
  marginTop: "22px",
} satisfies CSSProperties;

const resultCardStyle = {
  border: "1px solid #1e293b",
  borderRadius: "20px",
  padding: "16px",
  background: "#020617",
} satisfies CSSProperties;

const resultTitleStyle = {
  margin: "0 0 12px",
  fontSize: "17px",
} satisfies CSSProperties;

const metricStyle = {
  display: "grid",
  gap: "4px",
  margin: "10px 0",
} satisfies CSSProperties;