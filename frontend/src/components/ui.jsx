import { useState } from 'react'
import { parseApiError } from '../api/client'

const variants = {
  primary: 'bg-gradient-to-br from-red-600 to-red-700 text-white hover:from-red-700 hover:to-red-800 shadow-lg hover:shadow-xl hover:shadow-red-500/25 active:scale-95',
  secondary: 'bg-white text-red-700 border border-red-200 hover:border-red-400 hover:bg-red-50 shadow-sm hover:shadow-md',
  danger: 'bg-white text-red-700 border border-red-300 hover:bg-red-700 hover:text-white hover:shadow-lg hover:shadow-red-500/25 active:scale-95',
  ghost: 'text-slate-600 hover:text-slate-900 hover:bg-slate-100/50',
}
const sizes = { sm: 'px-3 py-1.5 text-xs', md: 'px-5 py-2.5 text-sm' }

export function Button({ variant = 'primary', size = 'md', className = '', type = 'button', ...props }) {
  return (
    <button
      type={type}
      className={`inline-flex items-center justify-center gap-2 rounded-lg font-semibold transition-all duration-200 disabled:cursor-not-allowed disabled:opacity-50 disabled:active:scale-100 ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    />
  )
}

export function Card({ className = '', children }) {
  return <div className={`rounded-xl border border-slate-200 bg-white/95 backdrop-blur shadow-sm hover:shadow-lg hover:border-red-200 transition-all duration-300 ${className}`}>{children}</div>
}

export function PageHeader({ title, subtitle, actions }) {
  return (
    <div className="mb-8 animate-fade-in">
      <div className="flex flex-wrap items-end justify-between gap-4 pb-4 border-b-2 border-gradient-to-r from-red-700 to-red-500">
        <div>
          <h1 className="text-3xl font-bold text-slate-900 drop-shadow-sm">{title}</h1>
          {subtitle && <p className="mt-2 text-sm text-slate-500 font-medium">{subtitle}</p>}
        </div>
        {actions && <div className="flex flex-wrap gap-2">{actions}</div>}
      </div>
    </div>
  )
}

const badgeColors = {
  red: 'bg-red-100 text-red-800',
  green: 'bg-emerald-100 text-emerald-800',
  amber: 'bg-amber-100 text-amber-800',
  slate: 'bg-slate-100 text-slate-700',
  orange: 'bg-orange-100 text-orange-800',
}

export function Badge({ color = 'slate', children }) {
  return <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-medium ${badgeColors[color]}`}>{children}</span>
}

export function Spinner() {
  return (
    <div className="flex flex-col items-center justify-center py-10 gap-4" role="status" aria-label="Carregando">
      <div className="relative h-12 w-12">
        <div className="absolute inset-0 rounded-full border-4 border-red-200/30" />
        <div className="absolute inset-0 animate-spin rounded-full border-4 border-transparent border-t-red-600 border-r-red-600" />
      </div>
      <p className="text-sm text-slate-500 font-medium">Carregando...</p>
    </div>
  )
}

export function EmptyState({ children }) {
  return (
    <div className="rounded-xl border border-dashed border-slate-300 bg-white py-10 text-center text-sm text-slate-500">
      {children}
    </div>
  )
}

export function ErrorBox({ message, onRetry }) {
  return (
    <div className="flex items-center justify-between gap-3 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
      <span>{message}</span>
      {onRetry && (
        <Button variant="secondary" size="sm" onClick={onRetry}>
          Tentar de novo
        </Button>
      )}
    </div>
  )
}

export function Async({ query, empty, children }) {
  if (query.isLoading) return <Spinner />
  if (query.isError) {
    const e = parseApiError(query.error)
    const message = e.status === 404 ? 'Registro não encontrado.' : e.message
    return <ErrorBox message={message} onRetry={() => query.refetch()} />
  }
  if (empty && Array.isArray(query.data) && query.data.length === 0) return <EmptyState>{empty}</EmptyState>
  return children(query.data)
}

export function Tabs({ tabs, value, onChange }) {
  return (
    <div className="mb-6 flex gap-1 border-b-2 border-slate-200">
      {tabs.map((t) => (
        <button
          key={t.value}
          type="button"
          onClick={() => onChange(t.value)}
          className={`-mb-0.5 border-b-2 px-5 py-3 text-sm font-semibold transition-all duration-300 ${
            value === t.value
              ? 'border-red-700 text-red-700 shadow-sm'
              : 'border-transparent text-slate-500 hover:text-slate-700 hover:border-slate-300'
          }`}
        >
          {t.label}
        </button>
      ))}
    </div>
  )
}

export function Modal({ title, onClose, children }) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4 animate-fade-in" onMouseDown={onClose}>
      <div
        className="max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl bg-white shadow-2xl transform transition-all duration-300"
        onMouseDown={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
      >
        <div className="flex items-center justify-between rounded-t-2xl bg-gradient-to-r from-red-600 to-red-700 px-6 py-4 text-white shadow-lg">
          <h2 className="font-bold text-lg">{title}</h2>
          <button type="button" onClick={onClose} className="text-2xl leading-none hover:opacity-70 transition-opacity duration-200" aria-label="Fechar">
            ×
          </button>
        </div>
        <div className="p-6">{children}</div>
      </div>
    </div>
  )
}

export function ConfirmDialog({ title = 'Confirmar', message, confirmLabel = 'Confirmar', conflictHint, onConfirm, onClose }) {
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState(null)

  async function run() {
    setBusy(true)
    setError(null)
    try {
      await onConfirm()
      onClose()
    } catch (err) {
      const e = parseApiError(err)
      setError(e.status === 409 && conflictHint ? conflictHint : e.message)
      setBusy(false)
    }
  }

  return (
    <Modal title={title} onClose={onClose}>
      <p className="text-sm text-slate-700">{message}</p>
      {error && <div className="mt-3"><ErrorBox message={error} /></div>}
      <div className="mt-5 flex justify-end gap-2">
        <Button variant="ghost" onClick={onClose}>Cancelar</Button>
        <Button onClick={run} disabled={busy}>{busy ? 'Aguarde...' : confirmLabel}</Button>
      </div>
    </Modal>
  )
}

const inputClass =
  'w-full rounded-lg border border-slate-300 bg-white px-4 py-2.5 text-sm transition-all duration-200 focus:border-red-600 focus:outline-none focus:ring-2 focus:ring-red-200/50 hover:border-slate-400 disabled:bg-slate-100 disabled:text-slate-500 disabled:cursor-not-allowed'

function toPayload(fields, values) {
  const out = {}
  for (const f of fields) {
    let v = values[f.name]
    if (v === '' || v == null) v = null
    else if (f.type === 'number' || f.numeric) v = Number(v)
    out[f.name] = v
  }
  return out
}

function initialValue(f, initial) {
  let v = initial?.[f.name]
  if (v == null) v = f.defaultValue ?? ''
  if (f.type === 'datetime-local' && typeof v === 'string') v = v.slice(0, 16)
  return String(v)
}

export function FormModal({ title, fields, initial, onSubmit, onClose, validate, submitLabel = 'Salvar' }) {
  const [values, setValues] = useState(() => Object.fromEntries(fields.map((f) => [f.name, initialValue(f, initial)])))
  const [error, setError] = useState(null)
  const [fieldErrors, setFieldErrors] = useState({})
  const [busy, setBusy] = useState(false)

  async function handleSubmit(e) {
    e.preventDefault()
    setError(null)
    setFieldErrors({})
    const payload = toPayload(fields, values)
    const invalid = validate?.(payload)
    if (invalid) return setError(invalid)
    setBusy(true)
    try {
      await onSubmit(payload)
      onClose()
    } catch (err) {
      const apiErr = parseApiError(err)
      setFieldErrors(apiErr.fieldErrors)
      setError(apiErr.status === 409 ? 'Já existe um registro com esses dados (nome duplicado ou conflito com outro registro).' : apiErr.message)
      setBusy(false)
    }
  }

  return (
    <Modal title={title} onClose={onClose}>
      <form onSubmit={handleSubmit} className="space-y-4">
        {fields.map((f) => (
          <div key={f.name}>
            <label className="mb-1 block text-sm font-medium text-slate-700" htmlFor={f.name}>
              {f.label}
              {f.required && <span className="text-red-600"> *</span>}
            </label>
            {f.type === 'select' ? (
              <select
                id={f.name}
                className={inputClass}
                value={values[f.name]}
                required={f.required}
                disabled={f.disabled}
                onChange={(e) => setValues({ ...values, [f.name]: e.target.value })}
              >
                {!f.required && <option value="">Não informado</option>}
                {f.required && values[f.name] === '' && <option value="">Selecione...</option>}
                {f.options.map((o) => (
                  <option key={o.value} value={o.value}>{o.label}</option>
                ))}
              </select>
            ) : f.type === 'textarea' ? (
              <textarea
                id={f.name}
                rows={3}
                className={inputClass}
                value={values[f.name]}
                onChange={(e) => setValues({ ...values, [f.name]: e.target.value })}
              />
            ) : (
              <input
                id={f.name}
                type={f.type ?? 'text'}
                className={inputClass}
                value={values[f.name]}
                required={f.required}
                min={f.min}
                disabled={f.disabled}
                placeholder={f.placeholder}
                onChange={(e) => setValues({ ...values, [f.name]: e.target.value })}
              />
            )}
            {fieldErrors[f.name] && <p className="mt-1 text-xs text-red-600">{fieldErrors[f.name]}</p>}
          </div>
        ))}
        {error && <ErrorBox message={error} />}
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="ghost" onClick={onClose}>Cancelar</Button>
          <Button type="submit" disabled={busy}>{busy ? 'Salvando...' : submitLabel}</Button>
        </div>
      </form>
    </Modal>
  )
}

export function Escudo({ time, size = 'md' }) {
  const [failed, setFailed] = useState(false)
  const dim = size === 'lg' ? 'h-20 w-20 text-xl' : size === 'sm' ? 'h-8 w-8 text-xs' : 'h-12 w-12 text-sm'
  if (time.escudoUrl && !failed) {
    return <img src={time.escudoUrl} alt={time.nome} onError={() => setFailed(true)} className={`${dim} object-contain`} />
  }
  const initials = (time.sigla || time.nome).slice(0, 3).toUpperCase()
  return (
    <div className={`${dim} flex items-center justify-center rounded-full bg-red-700 font-bold text-white`}>{initials}</div>
  )
}
