// The day a News Entry is dated, as shown in the news list and on its own page.
export function formatNewsDate(date: string): string {
  const options: Intl.DateTimeFormatOptions = {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  };
  return new Date(date).toLocaleDateString('en-US', options);
}
