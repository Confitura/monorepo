export interface RootState {
  headerTheme: string;
  headerHeight: number;
  windowWidth: number;
  date: string;
  partners: Partner[];
}

export const CHANGE_HEADER_THEME = 'CHANGE_HEADER_THEME';
export const WINDOW_RESIZED = 'WINDOW_RESIZED';
export const LOAD_PARTNERS = 'LOAD_PARTNERS';

export interface Partner {
  id: string;
  name: string;
  description: string;
  logo: string;
  www: string;
  type: 'platinum' | 'gold' | 'silver' | 'bronze';
}
