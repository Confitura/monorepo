// News Entry dates are entered and shown in Europe/Warsaw time, whatever zone the browser is in.
// <input type="datetime-local"> values are 'YYYY-MM-DDTHH:mm' wall-clock strings with no zone, so
// they are converted here with Intl rather than through the browser's local zone.
const warsaw = new Intl.DateTimeFormat('sv-SE', {
  timeZone: 'Europe/Warsaw',
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  hourCycle: 'h23',
})

// 'YYYY-MM-DD HH:mm' for the Warsaw wall clock at the given instant.
function wallClock(ms: number) {
  return warsaw.format(ms)
}

export function toWarsawInput(iso: string) {
  return wallClock(new Date(iso).getTime()).replace(' ', 'T')
}

// The instant whose Warsaw wall clock reads `input`. Around a DST change a time that does not
// exist (02:30 on spring-forward day) moves an hour later, and one that happens twice (02:30 on
// fall-back day) is read as the later, winter one.
export function fromWarsawInput(input: string) {
  const wanted = input.replace('T', ' ').slice(0, 16)
  const [y, mo, d, h, mi] = wanted.split(/[- :]/).map(Number)
  const wall = Date.UTC(y, mo - 1, d, h, mi)
  // Warsaw's offset from UTC, as seen half a day before and after `wall`, covers any DST change near it.
  const offsetAt = (ms: number) => {
    const [wy, wmo, wd, wh, wmi] = wallClock(ms).split(/[- :]/).map(Number)
    return Date.UTC(wy, wmo - 1, wd, wh, wmi) - Math.floor(ms / 60000) * 60000
  }
  const candidates = [wall - offsetAt(wall - 43200000), wall - offsetAt(wall + 43200000)]
  const existing = candidates.filter((ms) => wallClock(ms) === wanted)
  return new Date(Math.max(...(existing.length ? existing : candidates))).toISOString()
}
