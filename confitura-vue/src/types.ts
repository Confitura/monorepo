export interface RootState {
  headerTheme: string;
  headerHeight: number;
  windowWidth: number;
  date: string;
  partners: Partner[];
  token: string;
  user?: User;
}

export const CHANGE_HEADER_THEME = 'CHANGE_HEADER_THEME';
export const WINDOW_RESIZED = 'WINDOW_RESIZED';
export const LOAD_PARTNERS = 'LOAD_PARTNERS';
export const LOAD_PARTNER_BY_ID = 'LOAD_PARTNER_BY_ID';
export const TOKEN = 'TOKEN';
export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';

export interface User {
  sub: string;
  jti: string;
  isVolunteer: boolean;
  isAdmin: boolean;
  isNew: boolean;
  isSpeaker: boolean;
}

export interface Partner {
  id: string;
  name: string;
  description: string;
  logo: string;
  www: string;
  type: '' | 'platinum' | 'gold' | 'silver' | 'bronze';
}

export interface UserProfile {
  id: string;
  name: string;
  twitter: string;
  github: string;
  www: string;
  email: string;
  bio: string;
  photo: string;
  admin: boolean;
  volunteer: boolean;
  speaker: boolean;
  privacyPolicyAccepted: boolean;
  // presentations: Presentation[];
}
