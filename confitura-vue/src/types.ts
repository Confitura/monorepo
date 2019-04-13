import { AuthenticationState } from '@/store/authentication';
import { PartnersState } from '@/store/partners';

export interface RootState {
  headerTheme: string;
  headerHeight: number;
  windowWidth: number;
  date: string;
  partners?: PartnersState;
  authentication?: AuthenticationState;
}

export const CHANGE_HEADER_THEME = 'CHANGE_HEADER_THEME';
export const WINDOW_RESIZED = 'WINDOW_RESIZED';
export const LOAD_PARTNERS = 'LOAD_PARTNERS';
export const LOAD_PARTNER_BY_ID = 'LOAD_PARTNER_BY_ID';
export const TOKEN = 'TOKEN';
export const LOGIN = 'LOGIN';
export const LOGOUT = 'LOGOUT';
export const REMOVE_PRESENTATION = 'REMOVE_PRESENTATION';

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
  orientation?: 'horizontal' | 'vertical' | 'box';
}

export interface UserProfile {
  id?: string;
  name?: string;
  twitter?: string;
  github?: string;
  www?: string;
  email?: string;
  bio?: string;
  photo?: string;
  admin?: boolean;
  volunteer?: boolean;
  speaker?: boolean;
  privacyPolicyAccepted?: boolean;
  // presentations: Presentation[];
}

export interface EmbeddedPresentations {
  _embedded: { presentations: Presentation[] };
}

export interface EmbeddedTags {
  _embedded: { tags: Tag[] };
}

export interface Presentation {
  id?: string;
  title: string;
  language: string;
  level: string;
  speakers?: UserProfile[];
  tags: Tag[];
  shortDescription: string;
  description: string;
  status?: string;
}

export interface Tag {
  id: string;
  name: string;
}

export interface Page {
  content: string;
}
