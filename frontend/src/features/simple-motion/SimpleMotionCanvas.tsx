import { useEffect, useRef, useState } from "react";
import type { SimulationFrame } from "./types";

type SimpleMotionCanvasProps = {
  frames: SimulationFrame[];
};

const CANVAS_WIDTH = 700;
const CANVAS_HEIGHT = 400;
const SCALE = 10;
const ANIMATION_INTERVAL_MS = 250;

export function SimpleMotionCanvas({ frames }: SimpleMotionCanvasProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const [currentFrameIndex, setCurrentFrameIndex] = useState(0);

  useEffect(() => {
    setCurrentFrameIndex(0);
  }, [frames]);

  useEffect(() => {
    if (frames.length === 0) {
      return;
    }

    const intervalId = window.setInterval(() => {
      setCurrentFrameIndex((previousIndex) => {
        if (previousIndex >= frames.length - 1) {
          return previousIndex;
        }

        return previousIndex + 1;
      });
    }, ANIMATION_INTERVAL_MS);

    return () => window.clearInterval(intervalId);
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

    drawGrid(context);
    drawAxes(context);

    if (frames.length === 0) {
      return;
    }

    drawTrajectory(context, frames, currentFrameIndex);
    drawBody(context, frames[currentFrameIndex]);
  }, [frames, currentFrameIndex]);

  return (
    <div>
      <canvas
        ref={canvasRef}
        width={CANVAS_WIDTH}
        height={CANVAS_HEIGHT}
        style={{
          border: "1px solid #333",
          borderRadius: "12px",
          background: "#080b12",
        }}
      />

      {frames.length > 0 && (
        <p>
          Step: {frames[currentFrameIndex].step} | Time:{" "}
          {frames[currentFrameIndex].time.toFixed(2)} s
        </p>
      )}
    </div>
  );
}

function drawGrid(context: CanvasRenderingContext2D) {
  context.strokeStyle = "#1f2937";
  context.lineWidth = 1;

  for (let x = 0; x <= CANVAS_WIDTH; x += 50) {
    context.beginPath();
    context.moveTo(x, 0);
    context.lineTo(x, CANVAS_HEIGHT);
    context.stroke();
  }

  for (let y = 0; y <= CANVAS_HEIGHT; y += 50) {
    context.beginPath();
    context.moveTo(0, y);
    context.lineTo(CANVAS_WIDTH, y);
    context.stroke();
  }
}

function drawAxes(context: CanvasRenderingContext2D) {
  const centerX = CANVAS_WIDTH / 2;
  const centerY = CANVAS_HEIGHT / 2;

  context.strokeStyle = "#64748b";
  context.lineWidth = 2;

  context.beginPath();
  context.moveTo(0, centerY);
  context.lineTo(CANVAS_WIDTH, centerY);
  context.stroke();

  context.beginPath();
  context.moveTo(centerX, 0);
  context.lineTo(centerX, CANVAS_HEIGHT);
  context.stroke();
}

function drawTrajectory(
  context: CanvasRenderingContext2D,
  frames: SimulationFrame[],
  currentFrameIndex: number
) {
  context.strokeStyle = "#38bdf8";
  context.lineWidth = 2;

  context.beginPath();

  for (let i = 0; i <= currentFrameIndex; i++) {
    const { x, y } = toCanvasPosition(frames[i]);

    if (i === 0) {
      context.moveTo(x, y);
    } else {
      context.lineTo(x, y);
    }
  }

  context.stroke();
}

function drawBody(context: CanvasRenderingContext2D, frame: SimulationFrame) {
  const { x, y } = toCanvasPosition(frame);

  context.fillStyle = "#facc15";
  context.beginPath();
  context.arc(x, y, 8, 0, Math.PI * 2);
  context.fill();
}

function toCanvasPosition(frame: SimulationFrame) {
  return {
    x: CANVAS_WIDTH / 2 + frame.position.x * SCALE,
    y: CANVAS_HEIGHT / 2 - frame.position.y * SCALE,
  };
}