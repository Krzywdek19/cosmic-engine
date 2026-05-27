import { type FormEvent, useState } from "react";
import { runGravityTrajectory } from "../../api/simulationApi";
import { GravityCanvas } from "./GravityCanvas";
import type {
  GravityBodyConfig,
  GravitySimulationSettings,
  GravityTrajectoryRequest,
  GravityTrajectoryResponse,
} from "./types";

const INITIAL_BODIES: GravityBodyConfig[] = [
  {
    id: "body-1",
    name: "Planet",
    mass: 1,
    position: {
      x: 100,
      y: 0,
    },
    velocity: {
      x: 0,
      y: 10,
    },
  },
  {
    id: "body-2",
    name: "Star",
    mass: 10000,
    position: {
      x: 0,
      y: 0,
    },
    velocity: {
      x: 0,
      y: 0,
    },
  },
];

const DEFAULT_SETTINGS: GravitySimulationSettings = {
  deltaTime: 0.02,
  steps: 3200,
};

type VectorField = "position" | "velocity";
type VectorAxis = "x" | "y";
type BodyScalarField = "mass";

export function GravityPage() {
  const [bodies, setBodies] = useState<GravityBodyConfig[]>(INITIAL_BODIES);
  const [settings, setSettings] =
    useState<GravitySimulationSettings>(DEFAULT_SETTINGS);
  const [selectedBodyId, setSelectedBodyId] = useState(INITIAL_BODIES[0].id);
  const [result, setResult] = useState<GravityTrajectoryResponse | null>(null);
  const [replayKey, setReplayKey] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const selectedBody =
    bodies.find((body) => body.id === selectedBodyId) ?? bodies[0];

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    setIsLoading(true);
    setError(null);

    try {
      const request: GravityTrajectoryRequest = {
        bodies: bodies.map((body) => ({
          mass: body.mass,
          position: body.position,
          velocity: body.velocity,
        })),
        deltaTime: settings.deltaTime,
        steps: settings.steps,
      };

      const response = await runGravityTrajectory(request);

      setResult(response);
      setReplayKey((previous) => previous + 1);
    } catch (error) {
      console.error(error);
      setError(
        error instanceof Error ? error.message : "Failed to run gravity simulation."
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
    if (bodies.length <= 2) {
      setError("Gravity simulation requires at least two bodies.");
      return;
    }

    const nextBodies = bodies.filter((body) => body.id !== bodyId);

    setBodies(nextBodies);
    setResult(null);
    setReplayKey((previous) => previous + 1);

    if (selectedBodyId === bodyId) {
      setSelectedBodyId(nextBodies[0].id);
    }
  }

  function resetBodies() {
    setBodies(INITIAL_BODIES);
    setSettings(DEFAULT_SETTINGS);
    setSelectedBodyId(INITIAL_BODIES[0].id);
    setResult(null);
    setReplayKey((previous) => previous + 1);
    setError(null);
  }

  function replaySimulation() {
    if (!result) {
      return;
    }

    setReplayKey((previous) => previous + 1);
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

  function updateSettingsField(field: keyof GravitySimulationSettings, value: string) {
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
          Cosmic Engine / Stage 2
        </p>

        <h1 className="text-4xl font-extrabold tracking-tight text-slate-50 lg:text-5xl">
          Newtonian Gravity Simulation
        </h1>

        <p className="mt-4 max-w-3xl text-base leading-7 text-slate-400">
          Simulate trajectories in the current static central body gravity model.
          The backend selects the most massive body as the central attractor and
          returns frame-by-frame position, velocity and acceleration.
        </p>
      </header>

      <section className="grid items-start gap-7 xl:grid-cols-[minmax(360px,460px)_minmax(0,1fr)]">
        <form
          onSubmit={handleSubmit}
          className="rounded-3xl border border-slate-800 bg-slate-900/80 p-6 shadow-2xl shadow-slate-950/40"
        >
          <div className="mb-5 flex flex-wrap items-start justify-between gap-4">
            <div>
              <h2 className="text-2xl font-bold text-slate-50">Gravity input</h2>
              <p className="mt-1 text-sm leading-6 text-slate-400">
                Edit masses, positions and velocities. The largest mass becomes
                the static central body.
              </p>
            </div>

            <button
              type="button"
              onClick={addBody}
              className="rounded-full bg-sky-500 px-4 py-2 text-sm font-bold text-slate-950 transition hover:bg-sky-400"
            >
              Add body
            </button>
          </div>

          <div className="mb-5 flex flex-wrap gap-2">
            {bodies.map((body) => (
              <button
                key={body.id}
                type="button"
                onClick={() => setSelectedBodyId(body.id)}
                className={`rounded-full border px-4 py-2 text-sm font-semibold transition ${
                  body.id === selectedBodyId
                    ? "border-sky-400 bg-sky-400 text-slate-950"
                    : "border-slate-700 bg-slate-950 text-slate-300 hover:border-slate-500"
                }`}
              >
                {body.name}
              </button>
            ))}
          </div>

          {selectedBody && (
            <div className="rounded-2xl border border-slate-800 bg-slate-950/70 p-4">
              <div className="mb-4 flex items-start justify-between gap-4">
                <div>
                  <label className="block text-xs font-bold uppercase tracking-[0.16em] text-slate-500">
                    Body name
                  </label>
                  <input
                    value={selectedBody.name}
                    onChange={(event) =>
                      updateBodyName(selectedBody.id, event.target.value)
                    }
                    className="mt-2 w-full rounded-xl border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-100 outline-none focus:border-sky-400"
                  />
                </div>

                <button
                  type="button"
                  onClick={() => removeBody(selectedBody.id)}
                  className="rounded-full border border-rose-500/40 px-3 py-2 text-xs font-bold text-rose-300 transition hover:bg-rose-500/10"
                >
                  Remove
                </button>
              </div>

              <div className="grid gap-4">
                <NumberInput
                  label="Mass"
                  value={selectedBody.mass}
                  onChange={(value) =>
                    updateBodyNumberField(selectedBody.id, "mass", value)
                  }
                />

                <VectorInput
                  label="Position"
                  x={selectedBody.position.x}
                  y={selectedBody.position.y}
                  onChange={(axis, value) =>
                    updateBodyNumberField(selectedBody.id, `position.${axis}`, value)
                  }
                />

                <VectorInput
                  label="Velocity"
                  x={selectedBody.velocity.x}
                  y={selectedBody.velocity.y}
                  onChange={(axis, value) =>
                    updateBodyNumberField(selectedBody.id, `velocity.${axis}`, value)
                  }
                />
              </div>
            </div>
          )}

          <div className="mt-5 rounded-2xl border border-slate-800 bg-slate-950/70 p-4">
            <h3 className="mb-4 text-sm font-bold uppercase tracking-[0.16em] text-slate-500">
              Simulation settings
            </h3>

            <div className="grid gap-4 sm:grid-cols-2">
              <NumberInput
                label="Delta time"
                value={settings.deltaTime}
                step="0.001"
                min="0.01"
                max="1"
                helperText="Recommended for orbits: 0.01 - 0.05. Backend limit: 1.0."
                onChange={(value) => updateSettingsField("deltaTime", value)}
              />
              <NumberInput
                label="Steps"
                value={settings.steps}
                step="1"
                min="1"
                max="10000"
                helperText="For one orbit with deltaTime 0.02 use about 3200 steps."
                onChange={(value) => updateSettingsField("steps", value)}
              />
            </div>
          </div>

          {error && (
            <div className="mt-5 rounded-2xl border border-rose-500/40 bg-rose-500/10 p-4 text-sm text-rose-200">
              {error}
            </div>
          )}

          <div className="mt-6 flex flex-wrap gap-3">
            <button
              type="submit"
              disabled={isLoading}
              className="rounded-full bg-sky-400 px-5 py-3 text-sm font-extrabold text-slate-950 transition hover:bg-sky-300 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isLoading ? "Running..." : "Run gravity"}
            </button>

            <button
              type="button"
              onClick={replaySimulation}
              disabled={!result}
              className="rounded-full border border-slate-700 px-5 py-3 text-sm font-bold text-slate-200 transition hover:border-slate-500 disabled:cursor-not-allowed disabled:opacity-50"
            >
              Replay
            </button>

            <button
              type="button"
              onClick={resetBodies}
              className="rounded-full border border-slate-700 px-5 py-3 text-sm font-bold text-slate-200 transition hover:border-slate-500"
            >
              Reset
            </button>
          </div>
        </form>

        <div className="grid gap-5">
          <GravityCanvas bodies={bodies} result={result} replayKey={replayKey} />

          {result && <GravityDetails bodies={bodies} result={result} />}
        </div>
      </section>
    </main>
  );
}

function GravityDetails({
  bodies,
  result,
}: {
  bodies: GravityBodyConfig[];
  result: GravityTrajectoryResponse;
}) {
  return (
    <section className="rounded-3xl border border-slate-800 bg-slate-900/80 p-5">
      <h2 className="text-xl font-bold text-slate-50">Current API response summary</h2>
      <div className="mt-4 grid gap-3 md:grid-cols-2">
        {result.trajectories.map((trajectory) => {
          const body = bodies[trajectory.bodyIndex];
          const lastFrame = trajectory.frames[trajectory.frames.length - 1];

          return (
            <div
              key={trajectory.bodyIndex}
              className="rounded-2xl border border-slate-800 bg-slate-950/60 p-4 text-sm"
            >
              <p className="font-bold text-slate-100">
                {body?.name ?? `Body ${trajectory.bodyIndex + 1}`}
              </p>
              <p className="mt-1 text-slate-400">Mass: {trajectory.mass}</p>
              <p className="mt-3 text-slate-300">
                Last position: x={lastFrame.position.x.toFixed(3)}, y={lastFrame.position.y.toFixed(3)}
              </p>
              <p className="text-slate-300">
                Last velocity: x={lastFrame.velocity.x.toFixed(3)}, y={lastFrame.velocity.y.toFixed(3)}
              </p>
              <p className="text-slate-300">
                Last acceleration: x={lastFrame.acceleration.x.toFixed(3)}, y={lastFrame.acceleration.y.toFixed(3)}
              </p>
            </div>
          );
        })}
      </div>
    </section>
  );
}

function NumberInput({
  label,
  value,
  step = "0.1",
  min,
  max,
  helperText,
  onChange,
}: {
  label: string;
  value: number;
  step?: string;
  min?: string;
  max?: string;
  helperText?: string;
  onChange: (value: string) => void;
}) {
  return (
    <label className="block">
      <span className="text-xs font-bold uppercase tracking-[0.16em] text-slate-500">
        {label}
      </span>
      <input
        type="number"
        value={value}
        step={step}
        min={min}
        max={max}
        onChange={(event) => onChange(event.target.value)}
        className="mt-2 w-full rounded-xl border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-100 outline-none focus:border-sky-400"
      />
      {helperText && (
        <p className="mt-1 text-xs leading-5 text-slate-500">{helperText}</p>
      )}
    </label>
  );
}

function VectorInput({
  label,
  x,
  y,
  onChange,
}: {
  label: string;
  x: number;
  y: number;
  onChange: (axis: VectorAxis, value: string) => void;
}) {
  return (
    <div>
      <p className="text-xs font-bold uppercase tracking-[0.16em] text-slate-500">
        {label}
      </p>
      <div className="mt-2 grid gap-3 sm:grid-cols-2">
        <NumberInput label="X" value={x} onChange={(value) => onChange("x", value)} />
        <NumberInput label="Y" value={y} onChange={(value) => onChange("y", value)} />
      </div>
    </div>
  );
}

function createBodyConfig(index: number): GravityBodyConfig {
  return {
    id: `body-${Date.now()}`,
    name: `Body ${index}`,
    mass: 1,
    position: {
      x: 12 + index * 2,
      y: 0,
    },
    velocity: {
      x: 0,
      y: 2.5,
    },
  };
}

function isVectorField(value: string): value is VectorField {
  return value === "position" || value === "velocity";
}

function isVectorAxis(value: string): value is VectorAxis {
  return value === "x" || value === "y";
}

function isBodyScalarField(value: string): value is BodyScalarField {
  return value === "mass";
}
