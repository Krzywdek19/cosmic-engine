import { type FormEvent, useState } from "react";
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

  const selectedBody =
    bodies.find((body) => body.id === selectedBodyId) ?? bodies[0];

  const selectedBodyIndex = selectedBody
    ? bodies.findIndex((body) => body.id === selectedBody.id)
    : -1;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setIsLoading(true);
    setError(null);

    try {
      const responses = await Promise.all(
        bodies.map(async (body, index): Promise<BodySimulationResult> => {
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
            bodyIndex: index,
            name: body.name,
            mass: body.mass,
            response,
            frames: response.frames,
          };
        })
      );

      setResults(responses);
      setReplayKey((previous) => previous + 1);
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error ? error.message : "Failed to run simulation."
      );
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
    <main className="min-h-screen bg-[radial-gradient(circle_at_top_left,#1e293b_0,#020617_42%,#020617_100%)] p-6 font-sans text-slate-100 lg:p-10">
      <header className="mb-8 max-w-6xl">
        <p className="mb-3 text-xs font-bold uppercase tracking-[0.18em] text-sky-400">
          Cosmic Engine / Stage 1
        </p>

        <h1 className="text-4xl font-extrabold tracking-tight text-slate-50 lg:text-5xl">
          Simple Motion Simulation
        </h1>

        <p className="mt-4 max-w-3xl text-base leading-7 text-slate-400">
          Simulate independent material points using force, mass, acceleration,
          velocity and position.
        </p>
      </header>

      <section className="grid items-start gap-7 xl:grid-cols-[minmax(360px,440px)_minmax(0,1fr)]">
        <form
          onSubmit={handleSubmit}
          className="rounded-3xl border border-slate-800 bg-slate-900/80 p-6 shadow-2xl shadow-slate-950/40"
        >
          <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-slate-50">
                Simulation input
              </h2>
              <p className="mt-1 text-sm leading-6 text-slate-400">
                Add one or more independent bodies and run the simulation.
              </p>
            </div>

            <button
              type="button"
              onClick={addBody}
              className="rounded-full border border-slate-700 bg-slate-950 px-4 py-2.5 text-sm font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
            >
              Add body
            </button>
          </div>

          <div className="grid grid-cols-2 gap-3">
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
            <article className="rounded-3xl border border-slate-700/80 bg-slate-950/50 p-5">
              <div className="mb-4 flex items-center justify-between gap-3">
                <input
                  value={selectedBody.name}
                  onChange={(event) =>
                    updateBodyName(selectedBody.id, event.target.value)
                  }
                  className="w-full border-none bg-transparent text-lg font-extrabold text-slate-100 outline-none"
                />

                <button
                  type="button"
                  onClick={() => removeBody(selectedBody.id)}
                  disabled={bodies.length === 1}
                  className="rounded-full border border-red-900 bg-red-950 px-3 py-2 text-sm font-bold text-red-200 transition hover:border-red-700 disabled:cursor-not-allowed disabled:opacity-40"
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

              <p className="mt-3 text-xs text-slate-500">
                Body #{selectedBodyIndex + 1} is calculated independently.
              </p>
            </article>
          )}

          <div className="mt-6 flex flex-wrap gap-3">
            <button
              type="submit"
              disabled={isLoading}
              className="rounded-full bg-sky-400 px-5 py-3 font-extrabold text-slate-950 transition hover:bg-sky-300 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isLoading ? "Running..." : "Run simulation"}
            </button>

            <button
              type="button"
              onClick={replaySimulation}
              disabled={results.length === 0}
              className="rounded-full border border-slate-700 bg-slate-950 px-4 py-3 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300 disabled:cursor-not-allowed disabled:opacity-40"
            >
              Replay
            </button>

            <button
              type="button"
              onClick={clearResults}
              className="rounded-full border border-slate-700 bg-slate-950 px-4 py-3 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
            >
              Clear result
            </button>

            <button
              type="button"
              onClick={resetBodies}
              className="rounded-full border border-slate-700 bg-slate-950 px-4 py-3 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
            >
              Reset input
            </button>
          </div>

          {error && (
            <p className="mt-4 rounded-2xl border border-red-900 bg-red-950 px-4 py-3 text-sm text-red-200">
              {error}
            </p>
          )}
        </form>

        <section className="rounded-3xl border border-slate-800 bg-slate-900/80 p-6 shadow-2xl shadow-slate-950/40">
          <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-slate-50">
                Visualization
              </h2>
              <p className="mt-1 text-sm leading-6 text-slate-400">
                The canvas automatically fits all trajectories into view. You
                can also zoom and move the viewport manually.
              </p>
            </div>
          </div>

          <SimpleMotionCanvas trajectories={results} replayKey={replayKey} />

          {results.length > 0 && (
            <div className="mt-6 grid gap-4 sm:grid-cols-2 2xl:grid-cols-3">
              {results.map((result) => (
                <article
                  key={result.id}
                  className="rounded-3xl border border-slate-800 bg-slate-950 p-5"
                >
                  <h3 className="mb-4 text-lg font-bold text-slate-50">
                    {result.name}
                  </h3>

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
    <div className="my-5 grid grid-cols-[auto_minmax(0,1fr)_auto] items-center gap-3">
      <button
        type="button"
        onClick={onPrevious}
        className="rounded-full border border-slate-700 bg-slate-950 px-4 py-2.5 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
      >
        ←
      </button>

      <div className="flex gap-2 overflow-x-auto pb-1 [scrollbar-width:thin]">
        {bodies.map((body) => {
          const isSelected = body.id === selectedBodyId;

          return (
            <button
              key={body.id}
              type="button"
              onClick={() => onSelect(body.id)}
              className={cx(
                "whitespace-nowrap rounded-full px-4 py-2.5 font-extrabold transition",
                isSelected
                  ? "bg-sky-400 text-slate-950"
                  : "border border-slate-700 bg-slate-950 text-slate-200 hover:border-sky-400 hover:text-sky-300"
              )}
            >
              {body.name}
            </button>
          );
        })}
      </div>

      <button
        type="button"
        onClick={onNext}
        className="rounded-full border border-slate-700 bg-slate-950 px-4 py-2.5 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
      >
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
    <label className="mb-3 grid gap-1.5 text-sm font-semibold text-slate-300">
      <span>{label}</span>
      <input
        type="number"
        value={value}
        step="any"
        onChange={(event) => onChange(event.target.value)}
        className="w-full rounded-xl border border-slate-700 bg-slate-950 px-3 py-2.5 text-slate-50 outline-none transition focus:border-sky-400"
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
      <p className="mb-2 mt-4 text-sm font-bold text-slate-200">{title}</p>

      <div className="grid grid-cols-2 gap-3">
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
    <p className="my-3 grid gap-1">
      <span className="text-sm text-slate-400">{label}</span>
      <strong className="text-slate-100">
        ({x.toFixed(2)}, {y.toFixed(2)})
      </strong>
    </p>
  );
}

function cx(...classes: Array<string | false | null | undefined>) {
  return classes.filter(Boolean).join(" ");
}