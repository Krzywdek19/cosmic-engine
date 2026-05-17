import type {
    SimpleMotionRequest,
    SimpleMotionTrajectoryResponse,
} from "../features/simple-motion/types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

if (!API_BASE_URL) {
  throw new Error("Missing VITE_API_BASE_URL environment variable");
}

export async function runSimpleMotionTrajectory(
  request: SimpleMotionRequest
): Promise<SimpleMotionTrajectoryResponse> {
  const response = await fetch(`${API_BASE_URL}/simulations/simple-motion/trajectory`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    throw new Error("Failed to run simple motion simulation");
  }

  return response.json();
}