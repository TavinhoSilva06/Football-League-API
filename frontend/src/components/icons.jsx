const base = {
  fill: 'none',
  stroke: 'currentColor',
  strokeWidth: 1.8,
  strokeLinecap: 'round',
  strokeLinejoin: 'round',
  'aria-hidden': true,
}

export function IconGrid(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><rect x="3" y="3" width="7" height="7" rx="2" /><rect x="14" y="3" width="7" height="7" rx="2" /><rect x="3" y="14" width="7" height="7" rx="2" /><rect x="14" y="14" width="7" height="7" rx="2" /></svg>
}

export function IconUsers(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><circle cx="9" cy="8" r="3" /><path d="M3 20v-1a6 6 0 0 1 12 0v1" /><path d="M16 5.5a3 3 0 0 1 0 5.5M18 20v-1a6 6 0 0 0-2-4.5" /></svg>
}

export function IconTrophy(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="M7 4h10v5a5 5 0 0 1-10 0V4Z" /><path d="M7 6H4v2a3 3 0 0 0 3 3M17 6h3v2a3 3 0 0 1-3 3" /><path d="M12 14v4M8 21h8M9 18h6" /></svg>
}

export function IconBolt(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="m13 2-8 12h6l-1 8 8-12h-6l1-8Z" /></svg>
}

export function IconLogout(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="M10 4H6a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h4" /><path d="M14 8l4 4-4 4M18 12H9" /></svg>
}

export function IconMenu(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="M4 7h16M4 12h16M4 17h16" /></svg>
}

export function IconClose(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="m6 6 12 12M18 6 6 18" /></svg>
}

export function IconArrowRight(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="M5 12h14M13 6l6 6-6 6" /></svg>
}

export function IconCalendar(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><rect x="3" y="5" width="18" height="16" rx="3" /><path d="M8 3v4M16 3v4M3 10h18" /></svg>
}

export function IconTarget(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><circle cx="12" cy="12" r="9" /><circle cx="12" cy="12" r="5" /><circle cx="12" cy="12" r="1.5" /></svg>
}

export function IconShield(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><path d="M12 3 5 6v6c0 4.5 3 7.5 7 9 4-1.5 7-4.5 7-9V6l-7-3Z" /><path d="m9 12 2 2 4-4" /></svg>
}

export function IconBall(props) {
  return <svg viewBox="0 0 24 24" {...base} {...props}><circle cx="12" cy="12" r="9" /><path d="m12 7 4 3-1.5 5h-5L8 10l4-3Z" /><path d="M12 3v4M4.5 10 8 12M8 20l1.5-5M16 20l-1.5-5M19.5 10 16 12" /></svg>
}
