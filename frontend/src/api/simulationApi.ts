import type {
  GravityTrajectoryRequest,
  GravityTrajectoryResponse,
} from "../features/gravity/types";
import type {
  SimpleMotionRequest,
  SimpleMotionTrajectoryResponse,
} from "../features/simple-motion/types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api/v1";

export async function runSimpleMotionTrajectory(
  request: SimpleMotionRequest
): Promise<SimpleMotionTrajectoryResponse> {
  const response = await fetch(
    `${API_BASE_URL}/simulations/simple-motion/trajectory`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    throw new Error(await resolveApiErrorMessage(response));
  }

  return response.json();
}

export async function runGravityTrajectory(
  request: GravityTrajectoryRequest
): Promise<GravityTrajectoryResponse> {
  const response = await fetch(
    `${API_BASE_URL}/simulations/gravity/trajectory`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(request),
    }
  );

  if (!response.ok) {
    throw new Error(await resolveApiErrorMessage(response));
  }

  return response.json();
}

async function resolveApiErrorMessage(response: Response): Promise<string> {
  try {
    const body = (await response.json()) as {
      message?: string;
      fieldErrors?: Record<string, string>;
    };

    const firstFieldError = body.fieldErrors
      ? Object.entries(body.fieldErrors)[0]
      : undefined;

    if (firstFieldError) {
      const [field, message] = firstFieldError;

      return `${field}: ${message}`;
    }

    return body.message ?? "Simulation request failed";
  } catch {
    return "Simulation request failed";
  }
}