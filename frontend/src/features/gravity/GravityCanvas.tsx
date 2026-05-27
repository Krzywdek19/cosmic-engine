import { useEffect, useMemo, useRef, useState } from "react";
import type {
  GravityBodyConfig,
  GravityTrajectoryFrame,
  GravityTrajectoryResponse,
  Vector2D,
} from "./types";

type GravityCanvasProps = {
  bodies: GravityBodyConfig[];
  result: GravityTrajectoryResponse | null;
  replayKey: number;
};

type GravityCanvasContentProps = Omit<GravityCanvasProps, "replayKey">;

type Viewport = {
  centerX: number;
  centerY: number;
  scale: number;
};

const WIDTH = 920;
const HEIGHT = 560;
const PADDING = 70;
const BODY_RADIUS = 6;
const CENTRAL_BODY_RADIUS = 12;
const FRAME_INTERVAL_MS = 80;
const COLORS = ["#38bdf8", "#facc15", "#fb7185", "#34d399", "#a78bfa"];

export function GravityCanvas({ replayKey, ...props }: GravityCanvasProps) {
  return <GravityCanvasContent key={replayKey} {...props} />;
}

function GravityCanvasContent({ bodies, result }: GravityCanvasContentProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [frameIndex, setFrameIndex] = useState(0);

  const maxFrameCount = useMemo(() => {
    if (!result || result.trajectories.length === 0) {
      return 0;
    }

    return Math.max(
      ...result.trajectories.map((trajectory) => trajectory.frames.length)
    );
  }, [result]);

  const safeFrameIndex =
    maxFrameCount === 0 ? 0 : Math.min(frameIndex, maxFrameCount - 1);

  const viewport = useMemo(() => calculateViewport(result), [result]);
  const currentFrame = getFirstVisibleFrame(result, safeFrameIndex);
  const centralBody = result ? bodies[result.centralBodyIndex] : null;

  useEffect(() => {
    if (!result || maxFrameCount === 0 || frameIndex >= maxFrameCount - 1) {
      return;
    }

    const timeoutId = window.setTimeout(() => {
      setFrameIndex(frameIndex + 1);
    }, FRAME_INTERVAL_MS);

    return () => window.clearTimeout(timeoutId);
  }, [result, maxFrameCount, frameIndex]);

  useEffect(() => {
    const canvas = canvasRef.current;
    const context = canvas?.getContext("2d");

    if (!canvas || !context) {
      return;
    }

    drawScene(context, result, viewport, safeFrameIndex);
  }, [result, viewport, safeFrameIndex]);

  return (
    <section className="rounded-3xl border border-slate-800 bg-slate-900/80 p-5 shadow-2xl shadow-slate-950/40">
      <div className="mb-4 flex flex-wrap items-start justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-50">Gravity preview</h2>
          <p className="mt-1 text-sm leading-6 text-slate-400">
            Backend response uses frames with position, velocity and acceleration.
          </p>
        </div>

        <div className="rounded-2xl border border-slate-800 bg-slate-950/70 px-4 py-3 text-sm text-slate-300">
          <div>
            Frame:{" "}
            <span className="font-semibold text-slate-100">
              {safeFrameIndex}
            </span>
          </div>
          <div>
            Time:{" "}
            <span className="font-semibold text-slate-100">
              {currentFrame?.time.toFixed(2) ?? "0.00"}
            </span>
          </div>
        </div>
      </div>

      <canvas
        ref={canvasRef}
        width={WIDTH}
        height={HEIGHT}
        className="h-auto w-full rounded-2xl border border-slate-800 bg-slate-950"
      />

      {result && (
        <div className="mt-4 grid gap-3 text-sm text-slate-300 md:grid-cols-3">
          <InfoCard label="Model" value={result.model} />
          <InfoCard label="Integrator" value={result.integrator} />
          <InfoCard
            label="Central body"
            value={
              centralBody
                ? `${centralBody.name} / mass ${centralBody.mass}`
                : `Index ${result.centralBodyIndex}`
            }
          />
        </div>
      )}
    </section>
  );
}

function InfoCard({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-2xl border border-slate-800 bg-slate-950/60 p-4">
      <p className="text-xs font-bold uppercase tracking-[0.16em] text-slate-500">
        {label}
      </p>
      <p className="mt-2 break-words font-semibold text-slate-100">{value}</p>
    </div>
  );
}

function drawScene(
  context: CanvasRenderingContext2D,
  result: GravityTrajectoryResponse | null,
  viewport: Viewport,
  frameIndex: number
) {
  drawBackground(context);
  drawGrid(context);

  if (!result) {
    drawEmptyState(context);
    return;
  }

  drawCentralBody(context, result.centralBodyPosition, viewport);

  result.trajectories.forEach((trajectory, index) => {
    const color = COLORS[index % COLORS.length];
    const visibleFrames = trajectory.frames.slice(0, frameIndex + 1);

    drawTrajectory(context, visibleFrames, viewport, color);
    drawBody(context, trajectory.frames, frameIndex, viewport, color);
  });
}

function drawBackground(context: CanvasRenderingContext2D) {
  const gradient = context.createRadialGradient(250, 80, 0, 250, 80, 620);

  gradient.addColorStop(0, "#172554");
  gradient.addColorStop(0.5, "#020617");
  gradient.addColorStop(1, "#020617");

  context.fillStyle = gradient;
  context.fillRect(0, 0, WIDTH, HEIGHT);
}

function drawGrid(context: CanvasRenderingContext2D) {
  context.save();
  context.strokeStyle = "rgba(148, 163, 184, 0.08)";
  context.lineWidth = 1;

  for (let x = 0; x <= WIDTH; x += 40) {
    context.beginPath();
    context.moveTo(x, 0);
    context.lineTo(x, HEIGHT);
    context.stroke();
  }

  for (let y = 0; y <= HEIGHT; y += 40) {
    context.beginPath();
    context.moveTo(0, y);
    context.lineTo(WIDTH, y);
    context.stroke();
  }

  context.restore();
}

function drawEmptyState(context: CanvasRenderingContext2D) {
  context.save();
  context.fillStyle = "rgba(226, 232, 240, 0.75)";
  context.font = "600 20px Inter, system-ui, sans-serif";
  context.textAlign = "center";
  context.fillText(
    "Run gravity simulation to see trajectories",
    WIDTH / 2,
    HEIGHT / 2
  );
  context.restore();
}

function drawCentralBody(
  context: CanvasRenderingContext2D,
  position: Vector2D,
  viewport: Viewport
) {
  const point = worldToCanvas(position, viewport);

  context.save();
  context.shadowColor = "rgba(250, 204, 21, 0.85)";
  context.shadowBlur = 22;
  context.fillStyle = "#facc15";
  context.beginPath();
  context.arc(point.x, point.y, CENTRAL_BODY_RADIUS, 0, Math.PI * 2);
  context.fill();
  context.restore();
}

function drawTrajectory(
  context: CanvasRenderingContext2D,
  frames: GravityTrajectoryFrame[],
  viewport: Viewport,
  color: string
) {
  if (frames.length < 2) {
    return;
  }

  context.save();
  context.strokeStyle = color;
  context.globalAlpha = 0.8;
  context.lineWidth = 2;
  context.beginPath();

  frames.forEach((frame, index) => {
    const point = worldToCanvas(frame.position, viewport);

    if (index === 0) {
      context.moveTo(point.x, point.y);
      return;
    }

    context.lineTo(point.x, point.y);
  });

  context.stroke();
  context.restore();
}

function drawBody(
  context: CanvasRenderingContext2D,
  frames: GravityTrajectoryFrame[],
  frameIndex: number,
  viewport: Viewport,
  color: string
) {
  const frame = frames[Math.min(frameIndex, frames.length - 1)];

  if (!frame) {
    return;
  }

  const point = worldToCanvas(frame.position, viewport);

  context.save();
  context.fillStyle = color;
  context.beginPath();
  context.arc(point.x, point.y, BODY_RADIUS, 0, Math.PI * 2);
  context.fill();
  context.restore();
}

function calculateViewport(result: GravityTrajectoryResponse | null): Viewport {
  const points: Vector2D[] = [];

  if (result) {
    points.push(result.centralBodyPosition);

    result.trajectories.forEach((trajectory) => {
      trajectory.frames.forEach((frame) => points.push(frame.position));
    });
  }

  if (points.length === 0) {
    return {
      centerX: 0,
      centerY: 0,
      scale: 20,
    };
  }

  const xs = points.map((point) => point.x);
  const ys = points.map((point) => point.y);

  const minX = Math.min(...xs);
  const maxX = Math.max(...xs);
  const minY = Math.min(...ys);
  const maxY = Math.max(...ys);

  const width = Math.max(maxX - minX, 1);
  const height = Math.max(maxY - minY, 1);

  const scaleX = (WIDTH - PADDING * 2) / width;
  const scaleY = (HEIGHT - PADDING * 2) / height;

  return {
    centerX: (minX + maxX) / 2,
    centerY: (minY + maxY) / 2,
    scale: Math.min(scaleX, scaleY),
  };
}

function worldToCanvas(point: Vector2D, viewport: Viewport): Vector2D {
  return {
    x: WIDTH / 2 + (point.x - viewport.centerX) * viewport.scale,
    y: HEIGHT / 2 - (point.y - viewport.centerY) * viewport.scale,
  };
}

function getFirstVisibleFrame(
  result: GravityTrajectoryResponse | null,
  frameIndex: number
): GravityTrajectoryFrame | null {
  const firstTrajectory = result?.trajectories[0];

  if (!firstTrajectory) {
    return null;
  }

  return (
    firstTrajectory.frames[
      Math.min(frameIndex, firstTrajectory.frames.length - 1)
    ] ?? null
  );
}