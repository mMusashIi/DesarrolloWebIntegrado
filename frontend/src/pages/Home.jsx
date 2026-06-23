import { useRef, useEffect } from 'react'
import HeroSection from '../components/home/HeroSection'
import StatsSection from '../components/home/StatsSection'
import FeaturedPackages from '../components/home/FeaturedPackages'
import Testimonials from '../components/home/Testimonials'
import MapSection from '../components/home/MapSection' // <--- 1. Importamos el mapa

function Home({ onShowAuthRequired }) { // Recibimos la prop por si los paquetes la necesitan
  const counterRef = useRef(null)

  useEffect(() => {
    const animateCounters = () => {
      const counters = document.querySelectorAll('[id^="counter-"]')
      counters.forEach(counter => {
        const target = parseInt(counter.textContent)
        let current = 0
        const increment = target / 100
        
        const timer = setInterval(() => {
          current += increment
          if (current >= target) {
            counter.textContent = target
            clearInterval(timer)
          } else {
            counter.textContent = Math.floor(current)
          }
        }, 20)
      })
    }

    const observer = new IntersectionObserver((entries) => {
      entries.forEach(entry => {
        if (entry.isIntersecting && entry.target.id === 'counter-clients') {
          animateCounters()
          observer.disconnect()
        }
      })
    })

    if (counterRef.current) {
      observer.observe(counterRef.current)
    }

    return () => observer.disconnect()
  }, [])

  return (
    <>
      <HeroSection />
      <StatsSection ref={counterRef} />
      <FeaturedPackages onShowAuthRequired={onShowAuthRequired} />
      <Testimonials />
      
      {/* Call to Action Section (Banner Morado de Contacto) */}
      <section className="section-padding" style={{ backgroundColor: 'var(--primary-color)', color: 'white' }}>
        <div className="container">
          <div className="row align-items-center">
            <div className="col-lg-8">
              <h3 className="mb-3 text-white">¿Listo para vivir tu aventura en Ica?</h3>
              <p className="mb-0 text-white-50">Contáctanos y planifiquemos juntos tu experiencia perfecta.</p>
            </div>
            <div className="col-lg-4 text-lg-end">
              <a href="/contacto" className="btn btn-light btn-lg text-primary fw-bold">
                <i className="fas fa-envelope me-2"></i>Contáctanos
              </a>
            </div>
          </div>
        </div>
      </section>

      {/* 2. AQUÍ ESTÁ EL MAPA (Justo abajo del banner) */}
      <MapSection />
    </>
  )
}

export default Home