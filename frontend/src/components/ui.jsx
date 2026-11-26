import { useState } from 'react'
import { parseApiError } from '../api/client'
import { IconArrowRight, IconBall, IconBolt } from './icons'

const variants = {
  primary: 'brand-gradient animate-gradient text-white shadow-lg shadow-primary/25 hover:-translate-y-0.5 hover:shadow-xl hover:shadow-primary/30 active:translate-y-0 active:scale-95',
  secondary: 'border border-primary/25 bg-white text-primary-dark shadow-sm hover:border-primary hover:bg-primary-light hover:-translate-y-0.5 hover:shadow-md active:translate-y-0 active:scale-95',
  danger: 'border border-red-300 bg-white text-red-700 shadow-sm hover:bg-red-600 hover:text-white hover:shadow-lg hover:shadow-red-500/25 active:scale-95',
  ghost: 'text-slate-600 hover:bg-primary-light hover:text-primary-dark',
}
const sizes = { sm: 'px-3.5 py-2 text-xs', md: 'px-5 py-2.5 text-sm' }

export function Button({ variant = 'primary', size = 'md', className = '', type = 'button', ...props }) {
  return (
    <button
      type={type}
      className={`inline-flex items-center justify-center gap-2 rounded-xl font-semibold transition-all duration-300 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary/40 focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:translate-y-0 disabled:active:scale-100 ${variants[variant]} ${sizes[size]} ${className}`}
      {...props}
    />
  )
}

export function Card({ className = '', children }) {
  return (
    <div className={`animate-fade-in rounded-2xl border border-slate-200/80 bg-white shadow-sm transition-all duration-300 hover:-translate-y-1 hover:border-primary/30 hover:shadow-xl hover:shadow-primary/10 ${className}`}>
      {children}
    </div>
  )
}

export function PageHeader({ title, subtitle, actions }) {
  return (
    <div className="mb-8 animate-fade-in">
      <div className="flex flex-wrap items-end justify-between gap-4 pb-5">
        <div>
          <div className="mb-3 h-1.5 w-16 animate-gradient rounded-full brand-gradient" />
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 drop-shadow-sm sm:text-4xl">{title}</h1>
          {subtitle && <p className="mt-2 text-sm font-medium text-slate-500">{subtitle}</p>}
        </div>
        {actions && <div className="flex flex-wrap gap-2">{actions}</div>}
      </div>
    </div>
  )
}

export function HeroPanel({ eyebrow, title, description, stats = [], action }) {
  return (
    <section className="animate-slide-in relative mb-8 overflow-hidden rounded-3xl border border-primary/10 bg-white p-6 shadow-xl shadow-primary/5 sm:p-8">
      <div className="pointer-events-none absolute -right-16 -top-24 h-72 w-72 rounded-full bg-primary/10 blur-3xl" />
      <div className="pointer-events-none absolute -bottom-28 left-1/3 h-72 w-72 rounded-full bg-secondary/10 blur-3xl" />
      <div className="pointer-events-none absolute right-10 top-6 h-40 w-40 rounded-full border border-dashed border-primary/20" />
      <div className="relative z-10 grid gap-6 lg:grid-cols-[1fr_auto] lg:items-center">
        <div>
          {eyebrow && (
            <div className="inline-flex items-center gap-2 rounded-full bg-primary-light px-3 py-1.5 text-xs font-bold uppercase tracking-wider text-primary-dark ring-1 ring-primary/10">
              <IconBolt className="h-3.5 w-3.5" />
              {eyebrow}
            </div>
          )}
          <h2 className="mt-4 text-3xl font-extrabold leading-tight tracking-tight text-slate-900 sm:text-4xl">{title}</h2>
          {description && <p className="mt-3 max-w-xl text-sm leading-relaxed text-slate-600 sm:text-base">{description}</p>}
          {stats.length > 0 && (
            <div className="mt-6 flex flex-wrap gap-3">
              {stats.map((stat) => (
                <div key={stat.label} className="animate-pop rounded-2xl border border-primary/10 bg-white/80 px-4 py-3 shadow-sm backdrop-blur">
                  <div className="text-xl font-extrabold text-primary">{stat.value}</div>
                  <div className="text-[11px] font-semibold uppercase tracking-wide text-slate-500">{stat.label}</div>
                </div>
              ))}
            </div>
          )}
          {action && <div className="mt-6 flex flex-wrap gap-3">{action}</div>}
        </div>
        <div className="relative hidden h-44 w-44 items-center justify-center lg:flex">
          <div className="absolute inset-0 animate-pulse-slow rounded-full bg-primary-light blur-xl" />
          <div className="animate-float-slow relative flex h-32 w-32 items-center justify-center rounded-full border-4 border-white bg-gradient-to-br from-primary to-secondary shadow-2xl shadow-primary/25">
            <IconBall className="h-16 w-16 text-white" />
          </div>
          <div className="absolute inset-0 animate-spin rounded-full border border-dashed border-primary/20 [animation-duration:18s]" />
          <IconArrowRight className="absolute -left-2 bottom-4 h-5 w-5 text-secondary" />
        </div>
      </div>
    </section>
  )
}

const badgeColors = {
  red: 'bg-red-100 text-red-800 ring-1 ring-inset ring-red-200',
  green: 'bg-emerald-100 text-emerald-800 ring-1 ring-inset ring-emerald-200',
  amber: 'bg-amber-100 text-amber-800 ring-1 ring-inset ring-amber-200',
  yellow: 'bg-yellow-100 text-yellow-800 ring-1 ring-inset ring-yellow-200',
  blue: 'bg-primary-light text-primary-dark ring-1 ring-inset ring-primary/20',
  primary: 'bg-primary-light text-primary-dark ring-1 ring-inset ring-primary/20',
  purple: 'bg-purple-100 text-purple-800 ring-1 ring-inset ring-purple-200',
  slate: 'bg-slate-100 text-slate-700 ring-1 ring-inset ring-slate-200',
  orange: 'bg-orange-100 text-orange-800 ring-1 ring-inset ring-orange-200',
}

export function Badge({ color = 'slate', children }) {
  return <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-semibold shadow-sm ${badgeColors[color]}`}>{children}</span>
}

export function Spinner() {
  return (
    <div className="flex flex-col items-center justify-center gap-4 py-12" role="status" aria-label="Carregando">
      <div className="relative h-14 w-14">
        <div className="absolute inset-0 rounded-full border-4 border-primary/10" />
        <div className="absolute inset-0 animate-spin rounded-full border-4 border-transparent border-t-primary border-r-secondary" />
        <div className="absolute inset-2 animate-pulse-slow rounded-full bg-primary-light" />
      </div>
      <p className="text-sm font-medium text-slate-500">Carregando...</p>
    </div>
  )
}

export function EmptyState({ children }) {
  return (
    <div className="animate-pop relative overflow-hidden rounded-2xl border border-dashed border-primary/25 bg-primary-light/40 py-12 text-center">
      <div className="mx-auto mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-white text-lg font-bold text-primary shadow-md ring-4 ring-white/60">!</div>
      <p className="text-sm font-medium text-slate-600">{children}</p>
    </div>
  )
}

export function ErrorBox({ message, onRetry }) {
  return (
    <div className="animate-pop flex items-center justify-between gap-3 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm font-medium text-red-800 shadow-sm">
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
    <div className="mb-6 flex gap-2 overflow-x-auto rounded-2xl border border-slate-200 bg-slate-50/80 p-1.5 shadow-inner">
      {tabs.map((t) => (
        <button
          key={t.value}
          type="button"
          onClick={() => onChange(t.value)}
          className={`shrink-0 rounded-xl px-4 py-2.5 text-sm font-semibold transition-all duration-300 focus:outline-none focus-visible:ring-2 focus-visible:ring-primary/40 ${
            value === t.value
              ? 'scale-[1.02] bg-white text-primary-dark shadow-md ring-1 ring-primary/10'
              : 'text-slate-500 hover:bg-white/70 hover:text-primary'
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
    <div className="animate-fade-in fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 p-4 backdrop-blur-md" onMouseDown={onClose}>
      <div
        className="animate-pop max-h-[90vh] w-full max-w-lg overflow-y-auto rounded-2xl bg-white shadow-2xl ring-1 ring-white/60"
        onMouseDown={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
      >
        <div className="brand-gradient animate-gradient flex items-center justify-between rounded-t-2xl px-6 py-4 text-white shadow-lg">
          <h2 className="text-lg font-bold">{title}</h2>
          <button type="button" onClick={onClose} className="rounded-lg px-2 text-2xl leading-none transition-all duration-200 hover:bg-white/15 hover:rotate-90" aria-label="Fechar">
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
  'w-full rounded-xl border border-slate-300 bg-white px-4 py-2.5 text-sm transition-all duration-200 hover:border-primary/40 focus:border-primary focus:outline-none focus:ring-4 focus:ring-primary/10 disabled:cursor-not-allowed disabled:bg-slate-100 disabled:text-slate-500'

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
            <label className="mb-1.5 block text-sm font-semibold text-slate-700" htmlFor={f.name}>
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
            {fieldErrors[f.name] && <p className="mt-1 text-xs font-medium text-red-600">{fieldErrors[f.name]}</p>}
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
    return <img src={time.escudoUrl} alt={time.nome} onError={() => setFailed(true)} className={`${dim} rounded-xl object-contain drop-shadow-sm`} />
  }
  const initials = ((time?.sigla || time?.nome || '?')).slice(0, 3).toUpperCase()
  return (
    <div className={`brand-gradient animate-gradient ${dim} flex items-center justify-center rounded-xl font-bold text-white shadow-md`}>{initials}</div>
  )
}
