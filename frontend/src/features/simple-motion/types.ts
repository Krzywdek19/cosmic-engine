export type Vector2D = {
  x: number;
  y: number;
};

export type SimpleMotionRequest = {
  mass: number;
  position: Vector2D;
  velocity: Vector2D;
  force: Vector2D;
  deltaTime: number;
  steps: number;
};

export type SimulationFrame = {
  step: number;
  time: number;
  position: Vector2D;
  velocity: Vector2D;
};

export type SimpleMotionTrajectoryResponse = {
  mass: number;
  initialPosition: Vector2D;
  appliedForce: Vector2D;
  acceleration: Vector2D;
  finalPosition: Vector2D;
  finalVelocity: Vector2D;
  steps: number;
  deltaTime: number;
  frames: SimulationFrame[];
};

export type BodyConfig = {
  id: string;
  name: string;
  mass: number;
  position: Vector2D;
  velocity: Vector2D;
  force: Vector2D;
};

export type SimulationSettings = {
  deltaTime: number;
  steps: number;
};

export type BodySimulationResult = {
  id: string;
  name: string;
  response: SimpleMotionTrajectoryResponse;
  frames: SimulationFrame[];
};