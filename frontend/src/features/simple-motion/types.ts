export type Vector2D = {
  x: number;
  y: number;
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

export type SimpleMotionRequest = {
  mass: number;
  position: Vector2D;
  velocity: Vector2D;
  force: Vector2D;
  deltaTime: number;
  steps: number;
};

export type SimpleMotionFrame = {
  step: number;
  time: number;
  position: Vector2D;
  velocity: Vector2D;
};

export type SimpleMotionTrajectoryResponse = {
  mass: number;
  initialPosition: Vector2D;
  initialVelocity: Vector2D;
  appliedForce: Vector2D;
  acceleration: Vector2D;
  frames: SimpleMotionFrame[];
  finalPosition: Vector2D;
  finalVelocity: Vector2D;
  steps: number;
  deltaTime: number;
};

export type SimulationFrame = SimpleMotionFrame;

export type BodySimulationResult = {
  id: string;
  bodyIndex: number;
  name: string;
  mass: number;
  response: SimpleMotionTrajectoryResponse;
  frames: SimulationFrame[];
};