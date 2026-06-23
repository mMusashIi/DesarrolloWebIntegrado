export const formatCurrency = (amount) => {
  return new Intl.NumberFormat('es-PE', {
    style: 'currency',
    currency: 'PEN'
  }).format(amount)
}

export const formatDate = (date) => {
  return new Intl.DateTimeFormat('es-PE', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(new Date(date))
}