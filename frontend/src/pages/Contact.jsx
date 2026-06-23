import { useState } from 'react'

function Contact() {
  const [formData, setFormData] = useState({
    nombre: '',
    email: '',
    telefono: '',
    asunto: '',
    mensaje: ''
  })
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [submitStatus, setSubmitStatus] = useState(null)

  const handleInputChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value
    }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsSubmitting(true)
    
    // Simular envío del formulario
    try {
      await new Promise(resolve => setTimeout(resolve, 2000))
      setSubmitStatus('success')
      setFormData({
        nombre: '',
        email: '',
        telefono: '',
        asunto: '',
        mensaje: ''
      })
    } catch (error) {
      setSubmitStatus('error')
    } finally {
      setIsSubmitting(false)
    }
  }

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
            <h1 className="display-4 mb-3">Contáctanos</h1>
            <p className="lead">Estamos aquí para responder todas tus preguntas y ayudarte a planificar tu aventura</p>
          </div>
        </div>
      </section>

      {/* Contact Information */}
      <section className="section-padding">
        <div className="container">
          <div className="row">
            <div className="col-lg-4 mb-4">
              <div className="card card-custom text-center h-100">
                <div className="card-body">
                  <div className="mb-4">
                    <i className="fas fa-map-marker-alt fa-3x text-primary-custom"></i>
                  </div>
                  <h5 className="card-title">Ubicación</h5>
                  <p className="card-text">Ica, Perú</p>
                  <small className="text-muted">Ofrecemos servicios en toda la región de Ica</small>
                </div>
              </div>
            </div>
            
            <div className="col-lg-4 mb-4">
              <div className="card card-custom text-center h-100">
                <div className="card-body">
                  <div className="mb-4">
                    <i className="fas fa-phone fa-3x text-primary-custom"></i>
                  </div>
                  <h5 className="card-title">Teléfonos</h5>
                  <p className="card-text">
                    <strong>956 623 396</strong><br />
                    <strong>948 401 267</strong><br />
                    <strong>947 929 246</strong>
                  </p>
                  <small className="text-muted">Disponibles de 8:00 AM a 6:30 PM</small>
                </div>
              </div>
            </div>
            
            <div className="col-lg-4 mb-4">
              <div className="card card-custom text-center h-100">
                <div className="card-body">
                  <div className="mb-4">
                    <i className="fas fa-envelope fa-3x text-primary-custom"></i>
                  </div>
                  <h5 className="card-title">Email & WhatsApp</h5>
                  <p className="card-text">
                    <strong>reservas@buganvillatours.com.pe</strong>
                  </p>
                  <a 
                    href="https://api.whatsapp.com/send?phone=51922571344&text=Hello!%20I%20am%20interested%20in%20knowing%20more%20information%20about%20your%20programs" 
                    target="_blank" 
                    rel="noopener noreferrer"
                    className="btn btn-success btn-sm mt-2"
                  >
                    <i className="fab fa-whatsapp me-2"></i>Chat por WhatsApp
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Contact Form & Map */}
      <section className="section-padding" style={{ backgroundColor: 'var(--background-light)' }}>
        <div className="container">
          <div className="row">
            <div className="col-lg-8 mb-4">
              <div className="reservation-form">
                <h3 className="mb-4">Envíanos un Mensaje</h3>
                
                {submitStatus === 'success' && (
                  <div className="alert alert-success-custom mb-4">
                    <i className="fas fa-check-circle me-2"></i>
                    ¡Mensaje enviado exitosamente! Te contactaremos dentro de las próximas 24 horas.
                  </div>
                )}
                
                {submitStatus === 'error' && (
                  <div className="alert alert-error-custom mb-4">
                    <i className="fas fa-exclamation-triangle me-2"></i>
                    Error al enviar el mensaje. Por favor, inténtalo de nuevo o contáctanos por teléfono.
                  </div>
                )}

                <form onSubmit={handleSubmit}>
                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="nombre" className="form-label fw-bold">
                        Nombre Completo *
                      </label>
                      <input 
                        type="text" 
                        className="form-control form-control-custom" 
                        id="nombre"
                        name="nombre"
                        value={formData.nombre}
                        onChange={handleInputChange}
                        required 
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="email" className="form-label fw-bold">
                        Email *
                      </label>
                      <input 
                        type="email" 
                        className="form-control form-control-custom" 
                        id="email"
                        name="email"
                        value={formData.email}
                        onChange={handleInputChange}
                        required 
                      />
                    </div>
                  </div>

                  <div className="row">
                    <div className="col-md-6 mb-3">
                      <label htmlFor="telefono" className="form-label fw-bold">
                        Teléfono
                      </label>
                      <input 
                        type="tel" 
                        className="form-control form-control-custom" 
                        id="telefono"
                        name="telefono"
                        value={formData.telefono}
                        onChange={handleInputChange}
                      />
                    </div>
                    <div className="col-md-6 mb-3">
                      <label htmlFor="asunto" className="form-label fw-bold">
                        Asunto *
                      </label>
                      <select 
                        className="form-select form-control-custom" 
                        id="asunto"
                        name="asunto"
                        value={formData.asunto}
                        onChange={handleInputChange}
                        required
                      >
                        <option value="">Selecciona un asunto...</option>
                        <option value="consulta">Consulta General</option>
                        <option value="reserva">Reserva de Tour</option>
                        <option value="grupo">Viaje Grupal</option>
                        <option value="personalizado">Tour Personalizado</option>
                        <option value="queja">Sugerencia o Queja</option>
                        <option value="otro">Otro</option>
                      </select>
                    </div>
                  </div>

                  <div className="mb-4">
                    <label htmlFor="mensaje" className="form-label fw-bold">
                      Mensaje *
                    </label>
                    <textarea 
                      className="form-control form-control-custom" 
                      id="mensaje"
                      name="mensaje"
                      rows="6"
                      value={formData.mensaje}
                      onChange={handleInputChange}
                      placeholder="Describe tu consulta, interés en algún tour, fechas preferidas, número de personas, etc."
                      required
                    ></textarea>
                  </div>

                  <div className="text-center">
                    <button 
                      type="submit" 
                      className="btn btn-primary-custom btn-lg px-5"
                      disabled={isSubmitting}
                    >
                      {isSubmitting ? (
                        <>
                          <div className="loading-spinner me-2"></div>
                          Enviando...
                        </>
                      ) : (
                        <>
                          <i className="fas fa-paper-plane me-2"></i>
                          Enviar Mensaje
                        </>
                      )}
                    </button>
                  </div>
                </form>
              </div>
            </div>

            <div className="col-lg-4">
              <div className="sticky-top" style={{ top: '100px' }}>
                {/* Quick Contact */}
                <div className="card card-custom mb-4">
                  <div className="card-body">
                    <h5 className="card-title">
                      <i className="fas fa-clock text-primary-custom me-2"></i>
                      Horario de Atención
                    </h5>
                    <ul className="list-unstyled">
                      <li className="mb-2">
                        <strong>Lunes - Viernes:</strong><br />
                        8:00 AM - 6:30 PM
                      </li>
                      <li className="mb-2">
                        <strong>Sábados:</strong><br />
                        8:00 AM - 4:00 PM
                      </li>
                    </ul>
                  </div>
                </div>

                {/* Social Media */}
                <div className="card card-custom mb-4">
                  <div className="card-body">
                    <h5 className="card-title">
                      <i className="fas fa-share-alt text-primary-custom me-2"></i>
                      Síguenos en Redes
                    </h5>
                    <div className="d-flex justify-content-center gap-3">
                      <a 
                        href="https://www.facebook.com/Buganvillatours" 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="btn btn-outline-primary btn-sm"
                      >
                        <i className="fab fa-facebook-f"></i>
                      </a>
                      <a 
                        href="https://x.com/Buganvillatours" 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="btn btn-outline-primary btn-sm"
                      >
                        <i className="fab fa-twitter"></i>
                      </a>
                      <a 
                        href="https://api.whatsapp.com/send?phone=51922571344&text=Hello!%20I%20am%20interested%20in%20knowing%20more%20information%20about%20your%20programs" 
                        target="_blank" 
                        rel="noopener noreferrer"
                        className="btn btn-outline-success btn-sm"
                      >
                        <i className="fab fa-whatsapp"></i>
                      </a>
                    </div>
                  </div>
                </div>

                {/* Emergency Contact */}
                <div className="card card-custom border-warning">
                  <div className="card-body">
                    <h5 className="card-title text-warning">
                      <i className="fas fa-exclamation-triangle me-2"></i>
                      Contacto Urgente
                    </h5>
                    <p className="small mb-2">
                      Para emergencias durante un tour activo:
                    </p>
                    <p className="mb-2">
                      <strong>+51 956 623 396</strong>
                    </p>
                    <small className="text-muted">
                      Disponible 24/7 para clientes en tour
                    </small>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* FAQ Section */}
      <section className="section-padding">
        <div className="container">
          <div className="text-center mb-5">
            <h2 className="section-title">Preguntas Frecuentes</h2>
            <p className="section-subtitle">Respuestas rápidas a tus dudas más comunes</p>
          </div>
          <div className="row justify-content-center">
            <div className="col-lg-10">
              <div className="accordion" id="faqAccordion">
                <div className="accordion-item">
                  <h3 className="accordion-header">
                    <button 
                      className="accordion-button" 
                      type="button" 
                      data-bs-toggle="collapse" 
                      data-bs-target="#faq1"
                    >
                      ¿Con cuánta anticipación debo reservar?
                    </button>
                  </h3>
                  <div id="faq1" className="accordion-collapse collapse show" data-bs-parent="#faqAccordion">
                    <div className="accordion-body">
                      Recomendamos reservar con al menos 48 horas de anticipación. Para grupos grandes o tours personalizados, sugerimos contactarnos con 1 semana de anticipación.
                    </div>
                  </div>
                </div>
                
                <div className="accordion-item">
                  <h3 className="accordion-header">
                    <button 
                      className="accordion-button collapsed" 
                      type="button" 
                      data-bs-toggle="collapse" 
                      data-bs-target="#faq2"
                    >
                      ¿Qué métodos de pago aceptan?
                    </button>
                  </h3>
                  <div id="faq2" className="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                    <div className="accordion-body">
                      Aceptamos efectivo, transferencias bancarias, Yape, Plin y tarjetas de crédito/débito (con un pequeño recargo adicional).
                    </div>
                  </div>
                </div>
                
                <div className="accordion-item">
                  <h3 className="accordion-header">
                    <button 
                      className="accordion-button collapsed" 
                      type="button" 
                      data-bs-toggle="collapse" 
                      data-bs-target="#faq3"
                    >
                      ¿Ofrecen recogida en hotel?
                    </button>
                  </h3>
                  <div id="faq3" className="accordion-collapse collapse" data-bs-parent="#faqAccordion">
                    <div className="accordion-body">
                      Sí, ofrecemos servicio de recogida en la mayoría de hoteles de Ica sin costo adicional. Indícanos tu hotel al hacer la reserva.
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </>
  )
}

export default Contact