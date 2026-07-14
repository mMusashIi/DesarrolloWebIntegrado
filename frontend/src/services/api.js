import axios from 'axios'

// Configuración base de la API
// Usa la variable de entorno o el localhost:8080 por defecto
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// Crear instancia de axios con configuración base
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Variable para evitar múltiples redirecciones en caso de error 401
let isRedirecting = false

// ============ INTERCEPTORS ============

// 1. Interceptor de SOLICITUD: Agrega el token JWT si existe
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('buganvilla_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 2. Interceptor de RESPUESTA: Maneja errores globales (como token vencido)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expirado o inválido - solo redirigir una vez
      if (!isRedirecting) {
        isRedirecting = true
        localStorage.removeItem('buganvilla_token')
        localStorage.removeItem('buganvilla_user')
        
        // Usar setTimeout para evitar bucles de redirección
        setTimeout(() => {
          window.location.href = '/'
          isRedirecting = false
        }, 100)
      }
    }
    return Promise.reject(error)
  }
)

// ============ AUTENTICACIÓN ============

export const authAPI = {
  // Login: Envía email y password
  login: async (email, password) => {
    const response = await api.post('/auth/login', { email, password })
    return response.data
  },

  // Registro: Recibe el objeto completo (nombre, apellido, email, password, etc.)
  register: async (userData) => {
    const response = await api.post('/auth/register', userData)
    return response.data
  },

  // Obtener perfil del usuario actual (útil para validar sesión)
  getProfile: async () => {
    const response = await api.get('/auth/profile')
    return response.data
  }
}

// ============ PAQUETES ============

export const packagesAPI = {
  getAll: async () => {
    const response = await api.get('/paquetes/activos')
    return response.data
  },

  getById: async (id) => {
    const response = await api.get(`/paquetes/${id}`)
    return response.data
  },

  search: async (filters = {}) => {
    const params = new URLSearchParams()
    if (filters.nombre) params.append('nombre', filters.nombre)
    if (filters.precioMin) params.append('precioMin', filters.precioMin)
    if (filters.precioMax) params.append('precioMax', filters.precioMax)
    if (filters.estado) params.append('estado', filters.estado)
    
    const response = await api.get(`/paquetes/public/search?${params.toString()}`)
    return response.data
  },

  // --- ADMIN ---
  getAllForAdmin: async () => {
    const response = await api.get('/paquetes')
    return response.data
  },

  getByIdForAdmin: async (id) => {
    const response = await api.get(`/paquetes/${id}`)
    return response.data
  },

  create: async (paqueteData) => {
    const response = await api.post('/paquetes', paqueteData)
    return response.data
  },

  update: async (id, paqueteData) => {
    const response = await api.put(`/paquetes/${id}`, paqueteData)
    return response.data
  },

  delete: async (id) => {
    const response = await api.delete(`/paquetes/${id}`)
    return response.data
  }
}

// ============ LUGARES ============

export const lugaresAPI = {
  getAll: async () => {
    const response = await api.get('/lugares')
    return response.data
  },

  getById: async (id) => {
    const response = await api.get(`/lugares/${id}`)
    return response.data
  }
}

// ============ INVENTARIO ============

export const inventoryAPI = {
  getAvailable: async () => {
    const response = await api.get('/inventario/disponible')
    return response.data
  },

  getAvailableByPackage: async (packageId) => {
    const response = await api.get(`/inventario/paquete/${packageId}/disponible`)
    return response.data
  },

  getUpcoming: async () => {
    const response = await api.get('/inventario/proximas-salidas')
    return response.data
  }
}

// ============ RESERVAS ============

export const reservationsAPI = {
  create: async (reservationData) => {
    const response = await api.post('/reservas', reservationData)
    return response.data
  },

  getMyReservations: async () => {
    const response = await api.get('/reservas/my')
    return response.data
  },

  getById: async (id) => {
    const response = await api.get(`/reservas/${id}`)
    return response.data
  }
}

// ============ REPORTES ============

export const reportsAPI = {
  getExcel: async () => {
    const response = await api.get('/reportes/excel', {
      responseType: 'blob'
    })
    return response.data
  },

  getPDF: async () => {
    const response = await api.get('/reportes/pdf', {
      responseType: 'blob'
    })
    return response.data
  }
}

// ============ APIS.NET ============

export const apisNetAPI = {
  getPersonaByDni: async (dni) => {
    const response = await api.get(`/apis-net/dni/${dni}`)
    return response.data
  }
}

// ============ MERCADO PAGO ============

export const mercadoPagoAPI = {
  createPreference: async (preferenceData) => {
    const response = await api.post('/mercadopago/crear-preferencia', preferenceData)
    // Backend wraps in ResponseDTO: { success, message, data: { initPoint, preferenceId, ... } }
    return response.data?.data || response.data
  }
}

export default api