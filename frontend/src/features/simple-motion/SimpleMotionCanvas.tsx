import { useEffect, useMemo, useRef, useState } from "react";
import type { Dispatch, PointerEvent, SetStateAction } from "react";
import type { BodySimulationResult, SimulationFrame, Vector2D } from "./types";

type SimpleMotionCanvasProps = {
  trajectories: BodySimulationResult[];
  replayKey: number;
};

type ViewTransform = {
  zoom: number;
  panX: number;
  panY: number;
};

type Viewport = {
  minX: number;
  maxX: number;
  minY: number;
  maxY: number;
  scale: number;
};

type SimpleMotionCanvasContentProps = {
  trajectories: BodySimulationResult[];
  viewport: Viewport;
  maxFrameCount: number;
  viewTransform: ViewTransform;
  setViewTransform: Dispatch<SetStateAction<ViewTransform>>;
};

const CANVAS_WIDTH = 900;
const CANVAS_HEIGHT = 520;
const BODY_RADIUS = 7;
const PADDING = 64;
const ANIMATION_INTERVAL_MS = 100;
const MIN_ZOOM = 0.25;
const MAX_ZOOM = 8;

const DEFAULT_VIEW_TRANSFORM: ViewTransform = {
  zoom: 1,
  panX: 0,
  panY: 0,
};

const COLORS = ["#38bdf8", "#facc15", "#fb7185", "#34d399", "#a78bfa"];

export function SimpleMotionCanvas({
  trajectories,
  replayKey,
}: SimpleMotionCanvasProps) {
  const [viewTransform, setViewTransform] = useState<ViewTransform>(
    DEFAULT_VIEW_TRANSFORM
  );

  const viewport = useMemo(
    () => calculateViewport(trajectories),
    [trajectories]
  );

  const maxFrameCount = useMemo(() => {
    if (trajectories.length === 0) {
      return 0;
    }

    return Math.max(
      ...trajectories.map((trajectory) => trajectory.frames.length)
    );
  }, [trajectories]);

  return (
    <SimpleMotionCanvasContent
      key={`${replayKey}-${maxFrameCount}`}
      trajectories={trajectories}
      viewport={viewport}
      maxFrameCount={maxFrameCount}
      viewTransform={viewTransform}
      setViewTransform={setViewTransform}
    />
  );
}

function SimpleMotionCanvasContent({
  trajectories,
  viewport,
  maxFrameCount,
  viewTransform,
  setViewTransform,
}: SimpleMotionCanvasContentProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const lastPointerPosition = useRef<Vector2D | null>(null);

  const [currentFrameIndex, setCurrentFrameIndex] = useState(0);
  const [isDragging, setIsDragging] = useState(false);

  const safeFrameIndex =
    maxFrameCount === 0
      ? 0
      : Math.min(currentFrameIndex, maxFrameCount - 1);

  useEffect(() => {
    const canvas = canvasRef.current;

    if (!canvas) {
      return;
    }

    function handleNativeWheel(event: WheelEvent) {
      event.preventDefault();

      const zoomFactor = event.deltaY < 0 ? 1.12 : 1 / 1.12;

      setViewTransform((previous) => ({
        ...previous,
        zoom: clamp(previous.zoom * zoomFactor, MIN_ZOOM, MAX_ZOOM),
      }));
    }

    canvas.addEventListener("wheel", handleNativeWheel, {
      passive: false,
    });

    return () => {
      canvas.removeEventListener("wheel", handleNativeWheel);
    };
  }, [setViewTransform]);

  useEffect(() => {
    if (maxFrameCount === 0) {
      return;
    }

    const intervalId = window.setInterval(() => {
      setCurrentFrameIndex((previousIndex) => {
        if (previousIndex >= maxFrameCount - 1) {
          window.clearInterval(intervalId);
          return previousIndex;
        }

        return previousIndex + 1;
      });
    }, ANIMATION_INTERVAL_MS);

    return () => {
      window.clearInterval(intervalId);
    };
  }, [maxFrameCount]);

  useEffect(() => {
    const canvas = canvasRef.current;

    if (!canvas) {
      return;
    }

    const context = canvas.getContext("2d");

    if (!context) {
      return;
    }

    context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

    drawBackground(context);
    drawGrid(context);
    drawAxes(context, viewport, viewTransform);

    if (trajectories.length === 0 || maxFrameCount === 0) {
      drawEmptyState(context);
      return;
    }

    trajectories.forEach((trajectory, index) => {
      const color = getColor(index);

      drawTrajectory(
        context,
        trajectory.frames,
        safeFrameIndex,
        viewport,
        viewTransform,
        color
      );

      drawBody(
        context,
        trajectory.frames,
        safeFrameIndex,
        viewport,
        viewTransform,
        color
      );
    });
  }, [trajectories, safeFrameIndex, viewport, maxFrameCount, viewTransform]);

  function zoomIn() {
    setViewTransform((previous) => ({
      ...previous,
      zoom: clamp(previous.zoom * 1.2, MIN_ZOOM, MAX_ZOOM),
    }));
  }

  function zoomOut() {
    setViewTransform((previous) => ({
      ...previous,
      zoom: clamp(previous.zoom / 1.2, MIN_ZOOM, MAX_ZOOM),
    }));
  }

  function resetView() {
    setViewTransform(DEFAULT_VIEW_TRANSFORM);
  }

  function handlePointerDown(event: PointerEvent<HTMLCanvasElement>) {
    const canvas = canvasRef.current;

    if (!canvas) {
      return;
    }

    canvas.setPointerCapture(event.pointerId);
    lastPointerPosition.current = getCanvasPointerPosition(event, canvas);
    setIsDragging(true);
  }

  function handlePointerMove(event: PointerEvent<HTMLCanvasElement>) {
    const canvas = canvasRef.current;

    if (!canvas || !isDragging || !lastPointerPosition.current) {
      return;
    }

    const currentPointerPosition = getCanvasPointerPosition(event, canvas);

    const deltaX = currentPointerPosition.x - lastPointerPosition.current.x;
    const deltaY = currentPointerPosition.y - lastPointerPosition.current.y;

    setViewTransform((previous) => ({
      ...previous,
      panX: previous.panX + deltaX,
      panY: previous.panY + deltaY,
    }));

    lastPointerPosition.current = currentPointerPosition;
  }

  function handlePointerUp(event: PointerEvent<HTMLCanvasElement>) {
    const canvas = canvasRef.current;

    if (canvas && canvas.hasPointerCapture(event.pointerId)) {
      canvas.releasePointerCapture(event.pointerId);
    }

    lastPointerPosition.current = null;
    setIsDragging(false);
  }

  return (
    <div>
      <div className="mb-3 flex flex-wrap items-center justify-between gap-3">
        <div className="flex flex-wrap items-center gap-2">
          <button
            type="button"
            onClick={zoomOut}
            className="rounded-full border border-slate-700 bg-slate-950 px-3 py-2 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
          >
            -
          </button>

          <span className="min-w-16 text-center text-sm text-slate-400">
            {(viewTransform.zoom * 100).toFixed(0)}%
          </span>

          <button
            type="button"
            onClick={zoomIn}
            className="rounded-full border border-slate-700 bg-slate-950 px-3 py-2 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
          >
            +
          </button>

          <button
            type="button"
            onClick={resetView}
            className="rounded-full border border-slate-700 bg-slate-950 px-4 py-2 font-bold text-slate-200 transition hover:border-sky-400 hover:text-sky-300"
          >
            Reset view
          </button>
        </div>

        <p className="m-0 text-sm text-slate-500">
          Scroll to zoom, drag to move
        </p>
      </div>

      <canvas
        ref={canvasRef}
        width={CANVAS_WIDTH}
        height={CANVAS_HEIGHT}
        onPointerDown={handlePointerDown}
        onPointerMove={handlePointerMove}
        onPointerUp={handlePointerUp}
        onPointerLeave={handlePointerUp}
        className={cx(
          "w-full touch-none rounded-3xl border border-slate-800 bg-slate-950 shadow-2xl shadow-slate-950/50",
          isDragging ? "cursor-grabbing" : "cursor-grab"
        )}
      />

      {trajectories.length > 0 && (
        <div className="mt-4 flex flex-wrap gap-2.5">
          {trajectories.map((trajectory, index) => {
            const frame =
              trajectory.frames[
                Math.min(safeFrameIndex, trajectory.frames.length - 1)
              ];

            return (
              <div
                key={trajectory.id}
                className="rounded-full border border-slate-800 bg-slate-950 px-3 py-2 text-sm text-slate-300"
              >
                <span
                  className="mr-2 inline-block h-2.5 w-2.5 rounded-full"
                  style={{
                    background: getColor(index),
                  }}
                />
                {trajectory.name}: step {frame.step}, position (
                {frame.position.x.toFixed(2)}, {frame.position.y.toFixed(2)})
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

function calculateViewport(trajectories: BodySimulationResult[]): Viewport {
  const positions = trajectories.flatMap((trajectory) =>
    trajectory.frames.map((frame) => frame.position)
  );

  if (positions.length === 0) {
    return {
      minX: -10,
      maxX: 10,
      minY: -10,
      maxY: 10,
      scale: 20,
    };
  }

  let minX = Math.min(...positions.map((position) => position.x));
  let maxX = Math.max(...positions.map((position) => position.x));
  let minY = Math.min(...positions.map((position) => position.y));
  let maxY = Math.max(...positions.map((position) => position.y));

  if (minX === maxX) {
    minX -= 10;
    maxX += 10;
  }

  if (minY === maxY) {
    minY -= 10;
    maxY += 10;
  }

  const marginX = Math.max((maxX - minX) * 0.12, 5);
  const marginY = Math.max((maxY - minY) * 0.12, 5);

  minX -= marginX;
  maxX += marginX;
  minY -= marginY;
  maxY += marginY;

  const worldWidth = maxX - minX;
  const worldHeight = maxY - minY;

  const scaleX = (CANVAS_WIDTH - PADDING * 2) / worldWidth;
  const scaleY = (CANVAS_HEIGHT - PADDING * 2) / worldHeight;

  return {
    minX,
    maxX,
    minY,
    maxY,
    scale: Math.min(scaleX, scaleY),
  };
}

function drawBackground(context: CanvasRenderingContext2D) {
  context.fillStyle = "#020617";
  context.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
}

function drawGrid(context: CanvasRenderingContext2D) {
  context.strokeStyle = "#0f172a";
  context.lineWidth = 1;

  for (let x = 0; x <= CANVAS_WIDTH; x += 40) {
    context.beginPath();
    context.moveTo(x, 0);
    context.lineTo(x, CANVAS_HEIGHT);
    context.stroke();
  }

  for (let y = 0; y <= CANVAS_HEIGHT; y += 40) {
    context.beginPath();
    context.moveTo(0, y);
    context.lineTo(CANVAS_WIDTH, y);
    context.stroke();
  }
}

function drawAxes(
  context: CanvasRenderingContext2D,
  viewport: Viewport,
  viewTransform: ViewTransform
) {
  const origin = toCanvasPosition({ x: 0, y: 0 }, viewport, viewTransform);

  context.strokeStyle = "#334155";
  context.lineWidth = 1;

  context.beginPath();
  context.moveTo(0, origin.y);
  context.lineTo(CANVAS_WIDTH, origin.y);
  context.stroke();

  context.beginPath();
  context.moveTo(origin.x, 0);
  context.lineTo(origin.x, CANVAS_HEIGHT);
  context.stroke();
}

function drawTrajectory(
  context: CanvasRenderingContext2D,
  frames: SimulationFrame[],
  currentFrameIndex: number,
  viewport: Viewport,
  viewTransform: ViewTransform,
  color: string
) {
  if (frames.length === 0) {
    return;
  }

  const lastFrameIndex = Math.min(currentFrameIndex, frames.length - 1);

  context.strokeStyle = color;
  context.lineWidth = 2;

  context.beginPath();

  for (let i = 0; i <= lastFrameIndex; i++) {
    const position = toCanvasPosition(
      frames[i].position,
      viewport,
      viewTransform
    );

    if (i === 0) {
      context.moveTo(position.x, position.y);
    } else {
      context.lineTo(position.x, position.y);
    }
  }

  context.stroke();
}

function drawBody(
  context: CanvasRenderingContext2D,
  frames: SimulationFrame[],
  currentFrameIndex: number,
  viewport: Viewport,
  viewTransform: ViewTransform,
  color: string
) {
  if (frames.length === 0) {
    return;
  }

  const frame = frames[Math.min(currentFrameIndex, frames.length - 1)];
  const position = toCanvasPosition(frame.position, viewport, viewTransform);

  context.fillStyle = color;
  context.beginPath();
  context.arc(position.x, position.y, BODY_RADIUS, 0, Math.PI * 2);
  context.fill();

  context.strokeStyle = "#f8fafc";
  context.lineWidth = 2;
  context.stroke();
}

function drawEmptyState(context: CanvasRenderingContext2D) {
  context.fillStyle = "#64748b";
  context.font = "16px system-ui, sans-serif";
  context.textAlign = "center";
  context.fillText(
    "Run a simulation to see the trajectory",
    CANVAS_WIDTH / 2,
    CANVAS_HEIGHT / 2
  );
}

function toCanvasPosition(
  position: Vector2D,
  viewport: Viewport,
  viewTransform: ViewTransform
) {
  const baseX = PADDING + (position.x - viewport.minX) * viewport.scale;
  const baseY =
    CANVAS_HEIGHT - PADDING - (position.y - viewport.minY) * viewport.scale;

  const centerX = CANVAS_WIDTH / 2;
  const centerY = CANVAS_HEIGHT / 2;

  return {
    x: centerX + (baseX - centerX) * viewTransform.zoom + viewTransform.panX,
    y: centerY + (baseY - centerY) * viewTransform.zoom + viewTransform.panY,
  };
}

function getCanvasPointerPosition(
  event: PointerEvent<HTMLCanvasElement>,
  canvas: HTMLCanvasElement
): Vector2D {
  const rect = canvas.getBoundingClientRect();

  return {
    x: ((event.clientX - rect.left) / rect.width) * CANVAS_WIDTH,
    y: ((event.clientY - rect.top) / rect.height) * CANVAS_HEIGHT,
  };
}

function getColor(index: number) {
  return COLORS[index % COLORS.length];
}

function clamp(value: number, min: number, max: number) {
  return Math.min(Math.max(value, min), max);
}

function cx(...classes: Array<string | false | null | undefined>) {
  return classes.filter(Boolean).join(" ");
}