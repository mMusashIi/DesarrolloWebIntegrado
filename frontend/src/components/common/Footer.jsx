function Footer() {
  return (
    <footer className="footer-custom">
      <div className="container">
        <div className="row">
          <div className="col-md-4 mb-4">
            <h5><i className="fas fa-mountain me-2"></i>Buganvilla Tours</h5>
            <p>Experiencias turísticas personalizadas, confiables y memorables en la región de Ica.</p>
            <div className="social-links">
              <a 
                href="https://www.facebook.com/Buganvillatours" 
                target="_blank" 
                rel="noopener noreferrer"
                className="me-3"
              >
                <i className="fab fa-facebook fa-lg"></i>
              </a>
              <a 
                href="https://x.com/Buganvillatours" 
                target="_blank" 
                rel="noopener noreferrer"
                className="me-3"
              >
                <i className="fab fa-twitter fa-lg"></i>
              </a>
              <a 
                href="https://api.whatsapp.com/send?phone=51922571344&text=Hello!%20I%20am%20interested%20in%20knowing%20more%20information%20about%20your%20programs" 
                target="_blank" 
                rel="noopener noreferrer"
              >
                <i className="fab fa-whatsapp fa-lg"></i>
              </a>
            </div>
          </div>
          <div className="col-md-2 mb-4">
            <h5>Enlaces</h5>
            <ul className="list-unstyled">
              <li><a href="/">Inicio</a></li>
              <li><a href="/paquetes">Paquetes</a></li>
              <li><a href="/nosotros">Nosotros</a></li>
              <li><a href="/contacto">Contacto</a></li>
            </ul>
          </div>
          <div className="col-md-3 mb-4">
            <h5>Destinos</h5>
            <ul className="list-unstyled">
              <li><a href="#">Líneas de Nazca</a></li>
              <li><a href="#">Islas Ballestas</a></li>
              <li><a href="#">Oasis de Huacachina</a></li>
              <li><a href="#">Bodegas de Ica</a></li>
            </ul>
          </div>
          <div className="col-md-3 mb-4">
            <h5>Contacto</h5>
            <p><i className="fas fa-map-marker-alt me-2"></i>Ica, Perú</p>
            <p>
              <i className="fas fa-phone me-2"></i>
              956 623396<br />
              <span style={{marginLeft: '24px'}}>948 401 267</span><br />
              <span style={{marginLeft: '24px'}}>947 929 246</span>
            </p>
            <p>
              <i className="fas fa-envelope me-2"></i>
              reservas@buganvillatours.com.pe
            </p>
          </div>
        </div>
        <hr />
        <div className="text-center">
          <p>&copy; 2025 Buganvilla Tours. Todos los derechos reservados.</p>
        </div>
      </div>
    </footer>
  )
}

export default Footer