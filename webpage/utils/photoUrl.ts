// Builds a photo URL that requests a resized variant.
// - uploaded photos (/photos/) get a ?size=<px> param (served resized by the
//   resources nginx image_filter; harmless/ignored if it isn't)
// - GitHub / Gravatar avatars use their own sizing params
// The <img> CSS dimensions still downscale in the browser if the source isn't resized.
export function photoUrl(src: string | undefined | null, size: number): string {
  if (!src) {
    return ''
  }
  if (src.includes('/photos/')) {
    return `${src}${src.includes('?') ? '&' : '?'}size=${size}`
      .replace('api.confitura.pl/api/resources', 'resources.confitura.pl')
  } else if (src.includes('githubusercontent.com')) {
    return `${src}&s=${size}`
  } else if (src.includes('gravatar.com')) {
    return src.replace('s=300', `s=${size}`)
  }
  return src
}
