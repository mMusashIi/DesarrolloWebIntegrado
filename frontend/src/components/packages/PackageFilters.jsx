import { useState, useEffect } from 'react'

function PackageFilters({ onSearch, onCategoryChange, searchTerm, selectedCategory }) {
  const [localSearchTerm, setLocalSearchTerm] = useState(searchTerm)

  useEffect(() => {
    const timer = setTimeout(() => {
      onSearch(localSearchTerm)
    }, 300)

    return () => clearTimeout(timer)
  }, [localSearchTerm, onSearch])

  const categories = [
    { value: '', label: 'Todas las categorías' },
    { value: 'aventura', label: 'Aventura' },
    { value: 'cultura', label: 'Cultura' },
    { value: 'gastronomia', label: 'Gastronomía' },
    { value: 'naturaleza', label: 'Naturaleza' }
  ]

  return (
    <section className="py-4" style={{ backgroundColor: 'var(--background-light)' }}>
      <div className="container">
        <div className="row align-items-center">
          <div className="col-md-6">
            <div className="d-flex align-items-center gap-3">
              <label htmlFor="search-packages" className="form-label mb-0 fw-bold">
                Buscar:
              </label>
              <input 
                type="text" 
                id="search-packages"
                className="form-control form-control-custom" 
                placeholder="Buscar paquetes..."
                value={localSearchTerm}
                onChange={(e) => setLocalSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="col-md-6">
            <div className="d-flex align-items-center gap-3">
              <label htmlFor="filter-category" className="form-label mb-0 fw-bold">
                Categoría:
              </label>
              <select 
                id="filter-category"
                className="form-select form-control-custom"
                value={selectedCategory}
                onChange={(e) => onCategoryChange(e.target.value)}
              >
                {categories.map(category => (
                  <option key={category.value} value={category.value}>
                    {category.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}

export default PackageFilters