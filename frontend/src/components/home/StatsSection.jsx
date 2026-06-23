import { forwardRef } from 'react'

const StatsSection = forwardRef((props, ref) => {
  return (
    <section className="stats-section" ref={ref}>
      <div className="container">
        <div className="row text-center">
          <div className="col-md-3 col-6 mb-4">
            <div className="stat-number" id="counter-clients">1500</div>
            <p className="mb-0">Clientes Satisfechos</p>
          </div>
          <div className="col-md-3 col-6 mb-4">
            <div className="stat-number" id="counter-tours">50</div>
            <p className="mb-0">Tours Realizados</p>
          </div>
          <div className="col-md-3 col-6 mb-4">
            <div className="stat-number" id="counter-destinations">+10</div>
            <p className="mb-0">Destinos</p>
          </div>
          <div className="col-md-3 col-6 mb-4">
            <div className="stat-number" id="counter-experience">+20</div>
            <p className="mb-0">Años de Experiencia</p>
          </div>
        </div>
      </div>
    </section>
  )
})

StatsSection.displayName = 'StatsSection'

export default StatsSection