import React from 'react'

const MapSection = () => {
  return (
    <section className="section-padding bg-white">
      <div className="container">
        <div className="row justify-content-center">
          <div className="col-lg-10 text-center mb-5">
            {/* Título llamativo */}
            <h2 className="section-title mb-3">Conoce la magia de Ica con Nosotros</h2>
            <p className="section-subtitle fw-light fs-5 text-muted">
              Visita nuestra sede principal para planificar tu itinerario perfecto. Somos el punto donde la cultura, la naturaleza y la aventura se unen para crear tu experiencia inolvidable.
            </p>
            {/* Texto de llamado a la acción más fuerte */}
            <p className="lead fw-bold text-primary-custom">
              ¡Nuestro equipo de expertos te espera en el corazón de Ica para una asesoría personalizada!
            </p>
          </div>
        </div>
        
        <div className="row justify-content-center">
          <div className="col-lg-10">
            {/* Contenedor del mapa con estilo de tarjeta (SIN CAMBIOS EN EL IFRAME) */}
            <div className="card-custom overflow-hidden shadow-lg" style={{ height: '450px', borderRadius: '20px' }}>
              <iframe 
                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d241.90633260137923!2d-75.7515166059221!3d-14.04754801787607!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9110e2d347f27643%3A0x6a1c6324680980eb!2sBuganvilla%20Tours!5e0!3m2!1ses-419!2spe!4v1765087176809!5m2!1ses-419!2spe" 
                width="100%" 
                height="100%" 
                style={{ border: 0 }} 
                allowFullScreen="" 
                loading="lazy" 
                referrerPolicy="no-referrer-when-downgrade"
                title="Mapa de Buganvilla Tours"
              ></iframe>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default MapSection