export const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

export const validatePhone = (phone) => {
  const phoneRegex = /^(\+51|51)?[0-9]{9}$/
  return phoneRegex.test(phone.replace(/\s/g, ''))
}