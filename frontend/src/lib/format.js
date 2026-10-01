export function formatDate(value) {
  if (!value) return '-'
  const [y, m, d] = value.split('-')
  return `${d}/${m}/${y}`
}

export function formatDateTime(value) {
  if (!value) return 'A definir'
  return new Date(value).toLocaleString('pt-BR', { dateStyle: 'short', timeStyle: 'short' })
}

export function calcAge(value) {
  if (!value) return '-'
  const born = new Date(value)
  const now = new Date()
  let age = now.getFullYear() - born.getFullYear()
  const m = now.getMonth() - born.getMonth()
  if (m < 0 || (m === 0 && now.getDate() < born.getDate())) age--
  return age
}
