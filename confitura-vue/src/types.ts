import { AuthenticationState } from "@/store/authentication";
import { PartnersState } from "@/store/partners";

export interface RootState {
  headerTheme: string;
  headerHeight: number;
  windowWidth: number;
  date: string;
  partners?: PartnersState;
  authentication?: AuthenticationState;
}

export const CHANGE_HEADER_THEME = "CHANGE_HEADER_THEME";
export const WINDOW_RESIZED = "WINDOW_RESIZED";
export const LOAD_PARTNERS = "LOAD_PARTNERS";
export const LOAD_PARTNER_BY_ID = "LOAD_PARTNER_BY_ID";
export const TOKEN = "TOKEN";
export const LOGIN = "LOGIN";
export const LOGOUT = "LOGOUT";
export const REMOVE_PRESENTATION = "REMOVE_PRESENTATION";
export const PARTICIPATION_ID = "participationId";

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
  type:
    | ""
    | "platinum"
    | "path"
    | "gold"
    | "silver"
    | "bronze"
    | "media"
    | "tech";
  orientation?: "horizontal" | "vertical" | "box";
}

export interface EmbeddedUserProfiles {
  _embedded: { users: UserProfile[]; publicUsers: UserProfile[] };
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
  participationDataId?: string | null;
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
  _links: { self: { href: string } };
}

export interface WithTitle {
  id: string;
  title: string;
}

export interface Tag {
  id: string;
  name: string;
}

export interface Page {
  content: string;
}

export interface Vote {
  rate: number;
  id: string;
  order: number;
}

export interface Participant {
  id?: string;
  lastName?: string;
  firstName?: string;
  email?: string;
  voucher?: Voucher;
  privacyPolicyAccepted?: boolean;
  // t-hsirt:
  gender?: string;
  size?: string;

  isAdmin?: boolean;
  isVolunteer?: boolean;
  isSpeaker?: boolean;
  hasAcceptedPresentation?: boolean;
  isParticipant?: boolean;
}

export interface RegistrationForm {
  voucher: Voucher;

  lastName?: string;
  firstName?: string;
  email?: string;
  privacyPolicyAccepted?: boolean;

  size?: string;
  gender?: string;
  mealOption?: string;
  city?: string;
  experience?: string;
  role?: string;
}

export interface Voucher {
  id: string;
  originalBuyer?: string;
  comment?: string;
  emailSent?: boolean;
  type?: string;
  createdBy?: string;
  createdDate?: string;
  lastModifiedBy?: string;
  lastModifiedDate?: string;
}

export interface PresentationRate {
  presentation?: WithTitle;
  rate: number;
  comment?: string;
}

export interface VoteStat {
  title: string;
  presentationId: string;
  positiveVotes: number;
  negativeVotes: number;
  totalVotes: number;
}
