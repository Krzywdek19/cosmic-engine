export type Vector2D = {
  x: number;
  y: number;
};

export type GravitySimulationModel = "STATIC_CENTRAL_BODY";

export type IntegratorType = "CONSTANT_ACCELERATION_STEP";

export type GravityBodyRequest = {
  mass: number;
  position: Vector2D;
  velocity: Vector2D;
};

export type GravityTrajectoryRequest = {
  bodies: GravityBodyRequest[];
  deltaTime: number;
  steps: number;
};

export type GravityTrajectoryFrame = {
  step: number;
  time: number;
  position: Vector2D;
  velocity: Vector2D;
  acceleration: Vector2D;
};

export type GravityBodyTrajectory = {
  bodyIndex: number;
  mass: number;
  frames: GravityTrajectoryFrame[];
};

export type GravityTrajectoryResponse = {
  model: GravitySimulationModel;
  integrator: IntegratorType;
  centralBodyIndex: number;
  centralBodyPosition: Vector2D;
  trajectories: GravityBodyTrajectory[];
};

export type GravityBodyConfig = {
  id: string;
  name: string;
  mass: number;
  position: Vector2D;
  velocity: Vector2D;
};

export type GravitySimulationSettings = {
  deltaTime: number;
  steps: number;
};