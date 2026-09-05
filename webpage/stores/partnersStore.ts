import {defineStore} from 'pinia'

const descriptions = import.meta.glob('~/content/partners/*.md', {
    query: '?raw',
    import: 'default',
    eager: true
}) as Record<string, string>

function descFor(id: string): string {
    return descriptions[`/content/partners/${id}.md`] ?? '';
}

export const usePartnersStore = defineStore('partners', {
    state: () => ({
        // Hardcoded list is the fallback until the backend dump is loaded.
        partners: getPartners()
    }),
    getters: {
        platinum: ({partners}): Partner[] => filterBy(partners, "platinum"),
        path: ({partners}): Partner[] => filterBy(partners, "path"),
        silver: ({partners}): Partner[] => filterBy(partners, "silver"),
        gold: ({partners}): Partner[] => filterBy(partners, "gold"),
        bronze: ({partners}): Partner[] => filterBy(partners, "bronze"),
        media: ({partners}): Partner[] => filterBy(partners, "media"),
        tech: ({partners}): Partner[] => filterBy(partners, "tech"),
        partnersMap: ({partners}): Partners => {
            return {
                platinum: filterBy(partners, "platinum"),
                path: filterBy(partners, "path"),
                gold: filterBy(partners, "gold"),
                silver: filterBy(partners, "silver"),
                bronze: filterBy(partners, "bronze"),
                media: filterBy(partners, "media"),
                tech: filterBy(partners, "tech"),
            }
        }
    },
    actions: {
        // Populate from the backend dump (/partners/list.json). Falls back to the
        // hardcoded list when the dump is empty or unavailable.
        setPartners(data: BackendPartner[] | null | undefined) {
            this.partners = (data && data.length > 0)
                ? data.map(toPartner)
                : getPartners();
        },
        getPartnerById(idOrSlug: string) {
            const key = idOrSlug.toLowerCase();
            return this.partners.find(partner =>
                partner.slug?.toLowerCase() === key || partner.id.toLowerCase() === key);
        }
    }
});

interface BackendPartner {
    id: string;
    slug?: string;
    name: string;
    type: string;
    www: string;
    logo: string;
    description: string;
    orientation?: string;
    published?: boolean;
}

function toPartner(p: BackendPartner): Partner {
    return {
        id: p.id,
        slug: p.slug,
        name: p.name,
        description: p.description ?? '',
        logo: p.logo ?? '',
        www: p.www ?? '',
        type: p.type as PartnerType,
        orientation: p.orientation as Partner['orientation'],
    };
}

function filterBy(partners: Partner[], type: string) {
    return shuffle(partners.filter(partner => partner.type === type));
}

function shuffle<T>(array: T[]): T[] {
    return array.sort(() => 0.5 - Math.random());
}

export interface PartnersState {
    partners: Partner[];
}

export type PartnerType = 'platinum' | 'gold' | 'silver' | 'media' | 'path' | 'bronze' | 'tech';

export interface Partner {
    id: string;
    slug?: string;
    name: string;
    description: string;
    logo: string;
    www: string;
    type: PartnerType;
    orientation?: "horizontal" | "vertical" | "box";
}

export interface Partners {
    platinum: Partner[];
    path: Partner[];
    gold: Partner[];
    silver: Partner[];
    bronze: Partner[];
    tech: Partner[];
    media?: Partner[];
}

function getPartners(): Partner[] {
    return [{
        id: "xtb",
        name: "XTB",
        description: descFor("xtb"),
        logo: "/assets/partners/2026/xtb.svg",
        www: "https://www.xtb.com/",
        type: "gold",
        orientation: "horizontal"
    },{
        id: "dpd",
        name: "DPD",
        description: descFor("dpd"),
        logo: "/assets/partners/2026/dpd.svg",
        www: "https://dpdgroupitsolutions.pl/",
        type: "bronze",
        orientation: "box"
    },{
        id: "cantor",
        name: "CANTOR",
        description: descFor("cantor"),
        logo: "/assets/partners/2026/cantor.png",
        www: "https://www.cantor.com/",
        type: "bronze",
        orientation: "vertical"
    }];
}