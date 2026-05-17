import { useEffect, useMemo, useRef, useState } from "react";
import type { SimulationFrame } from "./types";

type SimpleMotionCanvasProps = {
  frames: SimulationFrame[];
};

const CANVAS_WIDTH = 800;
const CANVAS_HEIGHT = 480;
const BODY_RADIUS = 7;
const PADDING = 60;
const ANIMATION_INTERVAL_MS = 120;

export function SimpleMotionCanvas({ frames }: SimpleMotionCanvasProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [currentFrameIndex, setCurrentFrameIndex] = useState(0);

  const viewport = useMemo(() => calculateViewport(frames), [frames]);

  const safeFrameIndex =
    frames.length === 0 ? 0 : Math.min(currentFrameIndex, frames.length - 1);

  const currentFrame = frames.length > 0 ? frames[safeFrameIndex] : null;

  useEffect(() => {
    setCurrentFrameIndex(0);
  }, [frames]);

  useEffect(() => {
    if (frames.length === 0) {
      return;
    }

    setCurrentFrameIndex(0);

    const intervalId = window.setInterval(() => {
      setCurrentFrameIndex((previousIndex) => {
        if (previousIndex >= frames.length - 1) {
          window.clearInterval(intervalId);
          return previousIndex;
        }

        return previousIndex + 1;
      });
    }, ANIMATION_INTERVAL_MS);

    return () => {
      window.clearInterval(intervalId);
    };
  }, [frames]);

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
    drawAxes(context, viewport);

    if (frames.length === 0 || !currentFrame) {
      drawEmptyState(context);
      return;
    }

    drawTrajectory(context, frames, safeFrameIndex, viewport);
    drawBody(context, currentFrame, viewport);
  }, [frames, safeFrameIndex, currentFrame, viewport]);

  return (
    <div>
      <canvas
        ref={canvasRef}
        width={CANVAS_WIDTH}
        height={CANVAS_HEIGHT}
        style={{
          width: "100%",
          maxWidth: `${CANVAS_WIDTH}px`,
          border: "1px solid #1e293b",
          borderRadius: "18px",
          background: "#020617",
          boxShadow: "0 24px 60px rgba(15, 23, 42, 0.45)",
        }}
      />

      {currentFrame && (
        <div
          style={{
            marginTop: "12px",
            display: "flex",
            gap: "16px",
            flexWrap: "wrap",
            color: "#cbd5e1",
            fontSize: "14px",
          }}
        >
          <span>Step: {currentFrame.step}</span>
          <span>Time: {currentFrame.time.toFixed(2)} s</span>
          <span>
            Position: ({currentFrame.position.x.toFixed(2)},{" "}
            {currentFrame.position.y.toFixed(2)})
          </span>
          <span>
            Velocity: ({currentFrame.velocity.x.toFixed(2)},{" "}
            {currentFrame.velocity.y.toFixed(2)})
          </span>
        </div>
      )}
    </div>
  );
}

type Viewport = {
  minX: number;
  maxX: number;
  minY: number;
  maxY: number;
  scale: number;
};

function calculateViewport(frames: SimulationFrame[]): Viewport {
  if (frames.length === 0) {
    return {
      minX: -10,
      maxX: 10,
      minY: -10,
      maxY: 10,
      scale: 20,
    };
  }

  const positions = frames.map((frame) => frame.position);

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

  const worldWidth = maxX - minX;
  const worldHeight = maxY - minY;

  const scaleX = (CANVAS_WIDTH - PADDING * 2) / worldWidth;
  const scaleY = (CANVAS_HEIGHT - PADDING * 2) / worldHeight;

  const scale = Math.min(scaleX, scaleY);

  return {
    minX,
    maxX,
    minY,
    maxY,
    scale,
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

function drawAxes(context: CanvasRenderingContext2D, viewport: Viewport) {
  context.strokeStyle = "#334155";
  context.lineWidth = 1;

  const origin = toCanvasPosition({ x: 0, y: 0 }, viewport);

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
  viewport: Viewport
) {
  context.strokeStyle = "#38bdf8";
  context.lineWidth = 2;

  context.beginPath();

  for (let i = 0; i <= currentFrameIndex; i++) {
    const position = toCanvasPosition(frames[i].position, viewport);

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
  frame: SimulationFrame,
  viewport: Viewport
) {
  const position = toCanvasPosition(frame.position, viewport);

  context.fillStyle = "#facc15";
  context.beginPath();
  context.arc(position.x, position.y, BODY_RADIUS, 0, Math.PI * 2);
  context.fill();

  context.strokeStyle = "#fef3c7";
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
  position: { x: number; y: number },
  viewport: Viewport
) {
  return {
    x: PADDING + (position.x - viewport.minX) * viewport.scale,
    y: CANVAS_HEIGHT - PADDING - (position.y - viewport.minY) * viewport.scale,
  };
}