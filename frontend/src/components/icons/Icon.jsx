// Ícones SVG simples (traço), sem biblioteca externa.
const PATHS = {
  leaf: 'M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.5 19 2c1 2 2 4.2 2 8 0 5.5-4.8 10-10 10Z M2 21c0-3 1.9-5.4 5.3-6.6 3-1 6-1.4 8.7-4.4',
  search: 'M21 21l-4.3-4.3 M11 18a7 7 0 1 0 0-14 7 7 0 0 0 0 14Z',
  bell: 'M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9 M10.3 21a1.9 1.9 0 0 0 3.4 0',
  plus: 'M12 5v14 M5 12h14',
  chevron: 'M6 9l6 6 6-6',
  close: 'M18 6L6 18 M6 6l12 12',
  dashboard: 'M3 3h7v7H3z M14 3h7v7h-7z M14 14h7v7h-7z M3 14h7v7H3z',
  sprout: 'M12 22V11 M12 11c0-4 3-7 8-7 0 4-3 7-8 7Z M12 14c0-3-2.5-5.5-7-5.5 0 3.5 2.5 5.5 7 5.5Z',
  cost: 'M4 4h16v16H4z M12 7v10 M15 9.5H10.5a1.5 1.5 0 0 0 0 3h3a1.5 1.5 0 0 1 0 3H9',
  harvest: 'M3 7h13l3 5v5H3z M7 21a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z M16 21a2 2 0 1 0 0-4 2 2 0 0 0 0 4Z',
  tasks: 'M5 4h14v16H5z M9 9l2 2 4-4 M9 15h6',
  chart: 'M3 17l6-6 4 4 8-8 M15 7h6v6',
  history: 'M3 12a9 9 0 1 0 3-6.700L3 8 M3 3v5h5 M12 7v5l3 2',
  logout: 'M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4 M16 17l5-5-5-5 M21 12H9',
  pin: 'M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z M12 13a3 3 0 1 0 0-6 3 3 0 0 0 0 6Z',
  check: 'M20 6L9 17l-5-5',
  arrowRight: 'M5 12h14 M13 6l6 6-6 6',
  user: 'M20 21a8 8 0 0 0-16 0 M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8Z',
};

export default function Icon({ name, size = 18, strokeWidth = 1.8, className }) {
  return (
    <svg
      className={className}
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth={strokeWidth}
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      <path d={PATHS[name]} />
    </svg>
  );
}
