import { Link } from 'react-router-dom'
function About() {

  return (
    <>
      {/* Page Header */}
      <section 
        className="py-5 mt-5" 
        style={{ 
          background: 'linear-gradient(135deg, var(--primary-color), var(--secondary-color))', 
          color: 'white' 
        }}
      >
        <div className="container">
          <div className="text-center">
            <h1 className="display-4 mb-3">Sobre Buganvilla Tours</h1>
            <p className="lead">Conoce más sobre nuestra historia, misión y compromiso con el turismo en Ica</p>
          </div>
        </div>
      </section>

      {/* Historia Section */}
      <section className="section-padding">
        <div className="container">
          <div className="row align-items-center">
            <div className="col-lg-6 mb-4">
              <h2 className="section-title">Nuestra Historia</h2>
              <p className="mb-4">
                Buganvilla Tours nació del amor por nuestra tierra y el deseo de compartir 
                las maravillas de Ica con el mundo. Desde nuestros inicios, nos hemos dedicado 
                a crear experiencias turísticas auténticas que conecten a los visitantes con 
                la riqueza cultural, natural e histórica de nuestra región.
              </p>
              <p className="mb-4">
                Con años de experiencia en el sector turístico, hemos perfeccionado cada 
                detalle de nuestros servicios para ofrecer no solo tours, sino experiencias 
                memorables que perduran en el corazón de nuestros viajeros.
              </p>
              <div className="d-flex flex-wrap gap-3">
                <Link to="/paquetes" className="btn btn-primary-custom">
                  <i className="fas fa-map-marked-alt me-2"></i>Ver Nuestros Tours
                </Link>
              </div>
            </div>
            <div className="col-lg-6">
              <img 
                src="/images/about-us.png" 
                alt="Historia de Buganvilla Tours" 
                className="img-fluid rounded shadow"
                onError={(e) => {
                  e.target.src = '/images/placeholder.jpg'
                }}
              />
            </div>
          </div>
        </div>
      </section>

      {/* Misión y Visión */}
      <section className="section-padding" style={{ backgroundColor: 'var(--background-light)' }}>
        <div className="container">
          <div className="row">
            <div className="col-md-6 mb-5">
              <div className="text-center">
                <div className="mb-4">
                  <i className="fas fa-bullseye fa-3x text-primary-custom"></i>
                </div>
                <h3 className="mb-3">Nuestra Misión</h3>
                <p className="mb-0">
                  Ofrecer experiencias turísticas de calidad que superen las expectativas 
                  de nuestros clientes, promoviendo el desarrollo sostenible de nuestra 
                  región y valorando nuestro patrimonio cultural y natural.
                </p>
              </div>
            </div>
            <div className="col-md-6 mb-5">
              <div className="text-center">
                <div className="mb-4">
                  <i className="fas fa-eye fa-3x text-primary-custom"></i>
                </div>
                <h3 className="mb-3">Nuestra Visión</h3>
                <p className="mb-0">
                  Ser la agencia de turismo líder en Ica, reconocida por nuestra excelencia 
                  en servicio, innovación constante y contribución al desarrollo turístico 
                  sostenible de la región.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Valores */}
      <section className="section-padding">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="section-title">Nuestros Valores</h2>
            <p className="section-subtitle">Principios que guían cada una de nuestras acciones</p>
          </div>
          <div className="row">
            <div className="col-md-4 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-heart fa-2x text-primary-custom"></i>
                </div>
                <h5>Pasión por el Servicio</h5>
                <p className="small">
                  Amamos lo que hacemos y nos esforzamos por brindar una atención 
                  cálida y personalizada a cada cliente.
                </p>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-shield-alt fa-2x text-primary-custom"></i>
                </div>
                <h5>Compromiso con la Calidad</h5>
                <p className="small">
                  Garantizamos servicios de alta calidad, desde el transporte hasta 
                  las experiencias guiadas, pensando siempre en tu seguridad y comodidad.
                </p>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-leaf fa-2x text-primary-custom"></i>
                </div>
                <h5>Turismo Sostenible</h5>
                <p className="small">
                  Promovemos prácticas responsables que respetan el medio ambiente 
                  y contribuyen al desarrollo de las comunidades locales.
                </p>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-handshake fa-2x text-primary-custom"></i>
                </div>
                <h5>Honestidad y Transparencia</h5>
                <p className="small">
                  Operamos con integridad, ofreciendo información clara y precios 
                  justos sin cargos ocultos.
                </p>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-users fa-2x text-primary-custom"></i>
                </div>
                <h5>Trabajo en Equipo</h5>
                <p className="small">
                  Valoramos la colaboración entre nuestro equipo y partners para 
                  ofrecer experiencias integrales y memorables.
                </p>
              </div>
            </div>
            <div className="col-md-4 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-lightbulb fa-2x text-primary-custom"></i>
                </div>
                <h5>Innovación Constante</h5>
                <p className="small">
                  Buscamos siempre mejorar y actualizar nuestros servicios para 
                  ofrecer experiencias únicas y novedosas.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Por qué elegirnos */}
      <section className="section-padding" style={{ backgroundColor: 'var(--background-light)' }}>
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="section-title">¿Por Qué Elegir Buganvilla Tours?</h2>
            <p className="section-subtitle">Razones que nos hacen diferentes</p>
          </div>
          <div className="row">
            <div className="col-lg-3 col-md-6 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-map-marked-alt fa-2x text-primary-custom"></i>
                </div>
                <h6>Conocimiento Local</h6>
                <p className="small">
                  Somos de Ica y conocemos cada rincón de nuestra región como la palma de nuestra mano.
                </p>
              </div>
            </div>
            <div className="col-lg-3 col-md-6 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-user-tie fa-2x text-primary-custom"></i>
                </div>
                <h6>Guías Especializados</h6>
                <p className="small">
                  Nuestros guías son profesionales certificados con amplia experiencia y conocimiento.
                </p>
              </div>
            </div>
            <div className="col-lg-3 col-md-6 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-shield-alt fa-2x text-primary-custom"></i>
                </div>
                <h6>Seguridad Garantizada</h6>
                <p className="small">
                  Todos nuestros servicios cuentan con seguros y protocolos de seguridad establecidos.
                </p>
              </div>
            </div>
            <div className="col-lg-3 col-md-6 mb-4">
              <div className="text-center">
                <div className="mb-3">
                  <i className="fas fa-hand-holding-heart fa-2x text-primary-custom"></i>
                </div>
                <h6>Atención Personalizada</h6>
                <p className="small">
                  Nos adaptamos a tus necesidades y preferencias para crear la experiencia perfecta.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Team Section */}
      <section className="section-padding">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="section-title">Nuestro Compromiso</h2>
            <p className="section-subtitle">Trabajamos cada día para superar tus expectativas</p>
          </div>
          <div className="row justify-content-center">
            <div className="col-lg-8">
              <div className="text-center">
                <p className="lead mb-4">
                  En Buganvilla Tours no solo organizamos viajes; creamos recuerdos 
                  inolvidables. Cada tour, cada experiencia, cada sonrisa de nuestros 
                  clientes nos impulsa a seguir mejorando y creciendo.
                </p>
                <p>
                  Creemos firmemente que el turismo bien gestionado puede ser una 
                  fuerza positiva para el desarrollo de nuestra comunidad y la 
                  conservación de nuestro patrimonio. Por eso, trabajamos en estrecha 
                  colaboración con actores locales y seguimos prácticas sostenibles 
                  en todas nuestras operaciones.
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

    </>
  )
}

export default About