import "./App.css";

function App() {
  return (
    <main className="app">
      <section className="hero">
        <p className="eyebrow">Cosmic Engine</p>

        <h1>Astrophysics Simulation Engine</h1>

        <p className="description">
          A long-term project for learning physics through implementation:
          from Newtonian orbits to N-body simulations, black holes and
          relativistic effects.
        </p>

        <div className="status-card">
          <span className="status-dot" />
          <span>Stage 0: Project foundation</span>
        </div>
      </section>
    </main>
  );
}

export default App;