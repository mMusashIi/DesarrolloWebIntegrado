import { useState, useEffect } from 'react'
import { packagesAPI, inventoryAPI } from '../services/api'

export function usePackages() {
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const loadPackages = async () => {
      try {
        setLoading(true)
        setError(null)
        
        // Llamadas paralelas a la API del backend
        const [packagesResponse, inventoryResponse] = await Promise.all([
          packagesAPI.getAll(),
          inventoryAPI.getAvailable().catch(() => ({ success: false, data: [] })) // Si falla, usar array vacío
        ])
        
        if (packagesResponse.success && packagesResponse.data) {
          // Crear un mapa de inventario por paquete para calcular cupos
          const inventoryMap = new Map()
          if (inventoryResponse.success && inventoryResponse.data) {
            inventoryResponse.data.forEach(inv => {
              const paqueteId = inv.idPaquete
              if (!inventoryMap.has(paqueteId)) {
                inventoryMap.set(paqueteId, { total: 0, disponible: 0 })
              }
              const stats = inventoryMap.get(paqueteId)
              stats.total += inv.cupoTotal || 0
              stats.disponible += inv.cupoDisponible || 0
            })
          }
          
          // Mapear los datos del backend al formato esperado por el frontend
          const mappedPackages = packagesResponse.data.map(pkg => {
            const inventoryStats = inventoryMap.get(pkg.idPaquete) || { total: 0, disponible: 0 }
            
            // Función para asignar imágenes reales basado en el nombre
            const getImagenPath = (nombre) => {
              if (!nombre) return '/images/placeholder.jpg'
              const nameLower = nombre.toLowerCase()
              if (nameLower.includes('dunas') || nameLower.includes('huacachina')) return '/images/huacachina.png'
              if (nameLower.includes('nazca') || nameLower.includes('nasca')) return '/images/nazcaLineas.png'
              if (nameLower.includes('ballestas') || nameLower.includes('paracas')) return '/images/islas-ballestas.png'
              return '/images/placeholder.jpg' // Imagen por defecto si no hay coincidencia
            }
            
            return {
              id: pkg.idPaquete,
              nombrePaquete: pkg.nombrePaquete,
              descripcion: pkg.descripcion || '',
              precio: pkg.precioBase ? parseFloat(pkg.precioBase) : 0,
              duracion: pkg.duracionDias ? `${pkg.duracionDias} ${pkg.duracionDias === 1 ? 'día' : 'días'}` : 'No especificado',
              categoria: 'aventura', // Por defecto, ya que no está en el DTO
              imagen: getImagenPath(pkg.nombrePaquete),
              incluye: [], // Se puede obtener de otra fuente si es necesario
              cupoDisponible: inventoryStats.disponible || 0,
              cupoTotal: inventoryStats.total || 0,
              lugar: pkg.nombreLugar || '',
              ciudad: pkg.ciudadLugar || ''
            }
          })
          
          setPackages(mappedPackages)
        } else {
          setError(packagesResponse.message || 'Error al cargar los paquetes')
        }
      } catch (err) {
        console.error('Error al cargar paquetes:', err)
        setError(err.response?.data?.message || 'Error al cargar los paquetes')
      } finally {
        setLoading(false)
      }
    }

    loadPackages()
  }, [])

  const getPackageById = (id) => {
    return packages.find(pkg => pkg.id === parseInt(id))
  }

  const filterPackages = (searchTerm = '', category = '') => {
    return packages.filter(pkg => {
      const matchesSearch = !searchTerm || 
        pkg.nombrePaquete.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (pkg.descripcion && pkg.descripcion.toLowerCase().includes(searchTerm.toLowerCase()))
      
      const matchesCategory = !category || pkg.categoria === category
      
      return matchesSearch && matchesCategory
    })
  }

  return {
    packages,
    loading,
    error,
    getPackageById,
    filterPackages
  }
}