import { useState, useEffect } from 'react'
import { useSearchParams } from 'react-router-dom'
import PackageFilters from '../components/packages/PackageFilters'
import PackageGrid from '../components/packages/PackageGrid'
import PackageModal from '../components/packages/PackageModal'
import AuthRequiredModal from '../components/auth/AuthRequiredModal'
import { usePackages } from '../hooks/usePackages'

function Packages() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('')
  const [selectedPackage, setSelectedPackage] = useState(null)
  const [showPackageModal, setShowPackageModal] = useState(false)
  const [showAuthModal, setShowAuthModal] = useState(false)
  
  const { packages, loading, error, filterPackages } = usePackages()
  const filteredPackages = filterPackages(searchTerm, selectedCategory)

  useEffect(() => {
    const packageId = searchParams.get('package')
    if (packageId) {
      const pkg = packages.find(p => p.id === parseInt(packageId))
      if (pkg) {
        setSelectedPackage(pkg)
        setShowPackageModal(true)
      }
    }
  }, [searchParams, packages])

  const handleSearch = (term) => {
    setSearchTerm(term)
  }

  const handleCategoryChange = (category) => {
    setSelectedCategory(category)
  }

  const handlePackageSelect = (pkg) => {
    setSelectedPackage(pkg)
    setShowPackageModal(true)
  }

  const handleClosePackageModal = () => {
    setShowPackageModal(false)
    setSelectedPackage(null)
    setSearchParams({})
  }

  const handleAuthRequired = () => {
    setShowAuthModal(true)
  }

  return (
    <>
      {/* Page Header */}
      <section className="py-5 mt-5" style={{ 
        background: 'linear-gradient(135deg, var(--primary-color), var(--secondary-color))', 
        color: 'white' 
      }}>
        <div className="container">
          <div className="text-center">
            <h1 className="display-4 mb-3">Nuestros Paquetes Turísticos</h1>
            <p className="lead">Descubre experiencias únicas en Ica, Paracas y Nazca</p>
          </div>
        </div>
      </section>

      {/* Filters */}
      <PackageFilters 
        onSearch={handleSearch}
        onCategoryChange={handleCategoryChange}
        searchTerm={searchTerm}
        selectedCategory={selectedCategory}
      />

      {/* Packages Grid */}
      <section className="section-padding">
        <div className="container">
          <PackageGrid 
            packages={filteredPackages}
            loading={loading}
            error={error}
            onPackageSelect={handlePackageSelect}
            onAuthRequired={handleAuthRequired}
          />
        </div>
      </section>

      {/* Package Modal */}
      {selectedPackage && (
        <PackageModal 
          package={selectedPackage}
          show={showPackageModal}
          onHide={handleClosePackageModal}
          onAuthRequired={handleAuthRequired}
        />
      )}

      {/* Auth Required Modal */}
      <AuthRequiredModal 
        show={showAuthModal}
        onHide={() => setShowAuthModal(false)}
      />
    </>
  )
}

export default Packages