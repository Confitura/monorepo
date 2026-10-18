import type * as React from 'react';

/** Programme flavours. `jam` is the house colour; the four fruits mark tracks or rooms. */
export type Tone = 'jam' | 'plum' | 'apricot' | 'gooseberry' | 'blueberry';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /** primary: jam fill, white label. secondary: ink fill. ghost: outline. Default primary. */
  variant?: 'primary' | 'secondary' | 'ghost';
  /** md is 48px tall, lg is 60px. Default md. */
  size?: 'md' | 'lg';
  /** Renders an <a> instead of a <button>. */
  href?: string;
  children?: React.ReactNode;
}
export declare function Button(props: ButtonProps): React.ReactElement;

export interface TagProps {
  /** Soft tint for flavours and status; neutral uses the line colour. Default neutral. */
  tone?: Tone | 'neutral';
  /** outline is for language codes (PL, EN) and is set in mono. Default soft. */
  variant?: 'soft' | 'outline';
  /** Show the square lid marker. Defaults to true for a flavour tone. */
  marker?: boolean;
  /** Tooltip, e.g. "In Polish" for PL. */
  title?: string;
  className?: string;
  children?: React.ReactNode;
}
export declare function Tag(props: TagProps): React.ReactElement;

export interface SectionHeadingProps {
  title: React.ReactNode;
  eyebrow?: React.ReactNode;
  lead?: React.ReactNode;
  /** Heading level, default 2. */
  level?: 1 | 2 | 3 | 4;
  align?: 'start' | 'center';
  className?: string;
}
export declare function SectionHeading(props: SectionHeadingProps): React.ReactElement;

export interface SiteHeaderLink { label: string; href: string; current?: boolean }
export interface SiteHeaderProps {
  /** The logo: an <img> of confitura-logo-reversed.svg (the header is always black). */
  logo: React.ReactNode;
  homeHref?: string;
  links?: SiteHeaderLink[];
  /** Edition dates as shown, e.g. "25–26.09.2026". */
  date?: string;
  /** Machine-readable date for <time>, e.g. "2026-09-25". */
  dateTime?: string;
  cta?: { label: string; href: string };
  className?: string;
}
export declare function SiteHeader(props: SiteHeaderProps): React.ReactElement;

export interface TabItem { label: string; value?: string }
export interface TabsProps {
  items: TabItem[];
  /** Accessible name for the tab list, e.g. "Partner tiers". */
  label?: string;
  /** Uncontrolled starting tab (value or label). Defaults to the first. */
  defaultSelected?: string;
  /** Controlled selection; pair with onSelect. */
  selected?: string;
  onSelect?: (value: string) => void;
  className?: string;
}
export declare function Tabs(props: TabsProps): React.ReactElement;

export interface Speaker { name: string; company?: string }
export interface SessionCardProps {
  /** 24-hour time, "09:00". */
  start: string;
  end?: string;
  title: React.ReactNode;
  room?: string;
  /** Flavour of the room or track. Default jam. */
  tone?: Tone;
  speakers?: Speaker[];
  lang?: 'PL' | 'EN';
  kind?: 'talk' | 'workshop' | 'keynote' | 'panel';
  level?: string;
  href?: string;
  className?: string;
}
export declare function SessionCard(props: SessionCardProps): React.ReactElement;

export interface SpeakerCardProps {
  name: string;
  company?: string;
  talk?: React.ReactNode;
  /** Square-ish portrait URL; cropped into the jar. Without it, initials show on jam-soft. */
  photo?: string;
  initials?: string;
  href?: string;
  className?: string;
}
export declare function SpeakerCard(props: SpeakerCardProps): React.ReactElement;

export interface TicketCardProps {
  name: string;
  /** "2 days", "1 day". */
  duration: string;
  price?: string;
  includes?: React.ReactNode[];
  excludes?: React.ReactNode[];
  status?: 'available' | 'few-left' | 'sold-out';
  note?: React.ReactNode;
  featured?: boolean;
  cta?: { label: string; href: string };
  className?: string;
}
export declare function TicketCard(props: TicketCardProps): React.ReactElement;

export interface CalloutProps {
  /** Default "Note!". Shown in caps. */
  title?: string;
  tone?: 'jam' | 'neutral';
  className?: string;
  children?: React.ReactNode;
}
export declare function Callout(props: CalloutProps): React.ReactElement;

export interface Stat { value: string; label: string; /** Pictogram URL (white, for the jam band). */ icon?: string }
export interface StatGridProps {
  /** Lowercase section title, e.g. "confitura'25 in numbers". */
  title?: React.ReactNode;
  lead?: React.ReactNode;
  items: Stat[];
  /** band: full-width jam band with white text (default). plain: on the page surface. */
  variant?: 'band' | 'plain';
  className?: string;
}
export declare function StatGrid(props: StatGridProps): React.ReactElement;

export interface Partner { name: string; logo?: string; href?: string }
export interface PartnerTierProps {
  /** Platinum, Gold, Silver, Bronze, Tech, Media. */
  tier: string;
  partners: Partner[];
  /** Slot width: lg 280px, md 200px, sm 140px minimum. */
  size?: 'lg' | 'md' | 'sm';
  className?: string;
}
export declare function PartnerTier(props: PartnerTierProps): React.ReactElement;

declare global {
  interface Window {
    Confitura: {
      Button: typeof Button;
      Tag: typeof Tag;
      SectionHeading: typeof SectionHeading;
      SiteHeader: typeof SiteHeader;
      Tabs: typeof Tabs;
      SessionCard: typeof SessionCard;
      SpeakerCard: typeof SpeakerCard;
      TicketCard: typeof TicketCard;
      Callout: typeof Callout;
      StatGrid: typeof StatGrid;
      PartnerTier: typeof PartnerTier;
    };
  }
}
