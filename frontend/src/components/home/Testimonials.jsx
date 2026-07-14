function Testimonials() {
  const testimonials = [
    {
      name: "María González",
      rating: 5,
      comment: "Excelente experiencia en las Líneas de Nazca. El servicio fue impecable y el guía muy conocedor.",
      avatar: "/images/image.png"
    },
    {
      name: "Carlos Mendoza",
      rating: 5,
      comment: "El tour por las bodegas de Ica fue increíble. Altamente recomendado para los amantes del pisco.",
      avatar: "/images/image.png"
    },
    {
      name: "Ana López",
      rating: 5,
      comment: "Paracas y las Islas Ballestas fueron espectaculares. Una experiencia que no olvidaré.",
      avatar: "/images/image.png"
    }
  ]

  return (
    <section className="section-padding" style={{ backgroundColor: 'var(--background-light)' }}>
      <div className="container">
        <div className="text-center mb-5">
          <h2 className="section-title">Lo que Dicen Nuestros Clientes</h2>
          <p className="section-subtitle">Testimonios reales de experiencias inolvidables</p>
        </div>
        <div className="row">
          {testimonials.map((testimonial, index) => (
            <div key={index} className="col-md-4 mb-4">
              <div className="testimonial-card">
                <img 
                  src={testimonial.avatar} 
                  alt={testimonial.name}
                  className="testimonial-avatar"
                  onError={(e) => {
                    e.target.src = '/images/placeholder.jpg'
                  }}
                />
                <h5>{testimonial.name}</h5>
                <div className="mb-3">
                  {[...Array(testimonial.rating)].map((_, i) => (
                    <i key={i} className="fas fa-star text-warning"></i>
                  ))}
                </div>
                <p>"{testimonial.comment}"</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

export default Testimonials