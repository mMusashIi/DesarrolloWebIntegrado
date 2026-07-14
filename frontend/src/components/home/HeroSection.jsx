import React from 'react';
import { Link } from 'react-router-dom';

function HeroSection() {
  // ⚠️ IMPORTANTE: Coloca la URL pública de tu video aquí (ej: de Cloudinary o tu servidor)
  const videoUrl = "/videos/ica-loop.mp4"; 
  
  return (
    // 1. Contenedor principal: Ocupa todo el viewport y permite posicionamiento absoluto
    <section id="home" className="hero-section position-relative vh-100 overflow-hidden">
      
      {/* === Elementos del Video de Fondo (Background) === */}
      <div className="video-background">
        <video autoPlay loop muted playsInline className="video-element">
          <source src={videoUrl} type="video/mp4" />
          Tu navegador no soporta el tag de video.
        </video>
        {/* Capa de Sombreado (Overlay) para asegurar legibilidad */}
        {/* Usamos el color de tu marca (Morado) con opacidad reducida */}
        <div className="video-overlay"></div>
      </div>
      
      {/* === Contenido Principal (Texto y Botones) === */}
      <div className="container h-100 position-relative z-1 d-flex align-items-center">
        <div className="row w-100"> 
          {/* 2. Eliminamos la columna de la imagen y extendemos el contenido a un ancho amplio */}
          <div className="col-lg-10 col-xl-8 text-white">
            <div className="hero-content">
              <h1 className="mb-4 display-4 fw-bolder">Descubre la Magia de Ica</h1>
              <p className="lead fw-light mb-5">
                Experiencias turísticas personalizadas, confiables y memorables en Ica, 
                Paracas y Nazca. Combina cultura, naturaleza y aventura con nosotros.
              </p>
              <div className="d-flex flex-wrap gap-3">
                {/* Nota: Usé btn-light para que resalte sobre el fondo oscuro */}
                <Link to="/paquetes" className="btn btn-light btn-lg text-primary-custom fw-bold">
                  <i className="fas fa-map-marked-alt me-2"></i>Ver Paquetes
                </Link>
                <Link to="/reservas" className="btn btn-outline-light btn-lg fw-bold">
                  <i className="fas fa-calendar-plus me-2"></i>Reservar Ahora
                </Link>
              </div>
            </div>
          </div>
          
          {/* 🔴 Eliminamos: <div className="col-lg-6"> que contenía la imagen */}
          
        </div>
      </div>
      
    </section>
  )
}

export default HeroSection