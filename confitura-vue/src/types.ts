export interface RootState {
  headerTheme: string;
  headerHeight: number;
  windowWidth: number;
}

export const CHANGE_HEADER_THEME = 'CHANGE_HEADER_THEME';
export const WINDOW_RESIZED = 'WINDOW_RESIZED';
